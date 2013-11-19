package elaborate.editor.model.orm.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import nl.knaw.huygens.jaxrstools.exceptions.InternalServerErrorException;

import org.joda.time.DateTime;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import elaborate.editor.model.AbstractStoredEntity;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.SearchData;
import elaborate.editor.model.orm.User;
import elaborate.editor.solr.ElaborateSearchParameters;
import elaborate.util.ResourceUtil;

@Singleton
public class SearchService extends AbstractStoredEntityService<SearchData> {
  private static SearchService instance;
  ProjectService projectService = ProjectService.instance();

  private SearchService() {}

  public static SearchService instance() {
    if (instance == null) {
      instance = new SearchService();
    }
    return instance;
  }

  public SearchData createSearch(ElaborateSearchParameters elaborateSearchParameters, User user) {
    beginTransaction();
    projectService.setEntityManager(getEntityManager());
    Project project = projectService.getProjectIfUserIsAllowed(elaborateSearchParameters.getProjectId(), user);
    elaborateSearchParameters.setLevelFields(project.getLevel1(), project.getLevel2(), project.getLevel3());
    if (elaborateSearchParameters.getSearchInTranscriptions() && elaborateSearchParameters.getTextLayers().isEmpty()) {
      elaborateSearchParameters.setTextLayers(ImmutableList.copyOf(project.getTextLayers()));
    }

    elaborateSearchParameters.setFacetFields(project.getFacetFields());
    elaborateSearchParameters.setFacetInfoMap(project.getFacetInfoMap());
    try {
      Map<String, Object> result = getSolrServer().search(elaborateSearchParameters);
      SearchData searchData = new SearchData().setCreatedOn(new Date()).setResults(result);
      persist(searchData);
      commitTransaction();
      return searchData;

    } catch (Exception e) {
      e.printStackTrace();
      if (getEntityManager().getTransaction().isActive()) {
        rollbackTransaction();
      }
      LOG.error(e.getMessage());
      LOG.error("e={}", e);
      throw new InternalServerErrorException(e.getMessage());
    }
  }

  public Map<String, Object> getSearchResult(long projectId, long searchId, int start, int rows, User user) {
    Map<String, Object> resultsMap = Maps.newHashMap();
    //    try {
    openEntityManager();
    SearchData searchData = find(SearchData.class, searchId);
    checkEntityFound(searchData, searchId);
    Project project = getEntityManager().find(Project.class, projectId);
    closeEntityManager();

    if (searchData != null) {
      List<String> sortableFields = Lists.newArrayList("id", "name");
      sortableFields.addAll(ImmutableList.copyOf(project.getFacetFields()));

      resultsMap = searchData.getResults();

      List<String> ids = (List<String>) resultsMap.remove("ids");
      List<Map<String, Object>> results = (List<Map<String, Object>>) resultsMap.remove("results");

      int lo = ResourceUtil.toRange(start, 0, ids.size());
      int hi = ResourceUtil.toRange(lo + rows, 0, ids.size());
      ids = ids.subList(lo, hi);
      results = results.subList(lo, hi);

      resultsMap.put("ids", ids);
      resultsMap.put("results", results);
      resultsMap.put("start", lo);
      resultsMap.put("rows", hi - lo);

      resultsMap.put("sortableFields", sortableFields);
    }
    //    } catch (Exception e) {
    //      throw new RuntimeException(e);
    //    }
    return resultsMap;
  }

  public void removeExpiredSearches() {
    String cutoffDate = new DateTime().minusDays(1).toString("YYYY-MM-dd HH:mm:ss");
    beginTransaction();
    getEntityManager()//
        .createQuery("delete SearchData where created_on < '" + cutoffDate + "'")//
        .executeUpdate();
    commitTransaction();
  }

  @Override
  Class<? extends AbstractStoredEntity<?>> getEntityClass() {
    return SearchData.class;
  }

  @Override
  String getEntityName() {
    return "SearchData";
  }
}
