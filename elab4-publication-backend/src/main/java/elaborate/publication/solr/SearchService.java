package elaborate.publication.solr;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Singleton;

import nl.knaw.huygens.jaxrstools.exceptions.InternalServerErrorException;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import elaborate.LoggableObject;

@Singleton
public class SearchService extends LoggableObject {

  private final Map<Long, SearchData> searchDataIndex = Maps.newHashMap();
  private SolrServerWrapper solrServer;
  private String solrDir;
  private Map<String, FacetInfo> facetInfoMap;
  private String[] facetFields;
  private String[] defaultSortOrder;

  public SearchService() {
    super();
    setFacetData();
  }

  public SearchData createSearch(ElaborateSearchParameters elaborateSearchParameters) {
    elaborateSearchParameters.setFacetFields(getFacetFields());
    elaborateSearchParameters.setFacetInfoMap(getFacetInfoMap());
    elaborateSearchParameters.setLevelFields(defaultSortOrder[0], defaultSortOrder[1], defaultSortOrder[2]);
    try {
      Map<String, Object> result = getSolrServer().search(elaborateSearchParameters);
      SearchData searchData = new SearchData().setResults(result);
      searchDataIndex.put(searchData.getId(), searchData);
      return searchData;

    } catch (Exception e) {
      LOG.error(e.getMessage());
      LOG.error("e={}", e);
      e.printStackTrace();
      throw new InternalServerErrorException(e.getMessage());
    }
  }

  public Map<String, Object> getSearchResult(long searchId, int start, int rows) {
    Map<String, Object> resultsMap = Maps.newHashMap();
    try {
      SearchData searchData = searchDataIndex.get(searchId);

      if (searchData != null) {
        List<String> sortableFields = Lists.newArrayList("id", "name");
        sortableFields.addAll(ImmutableList.copyOf(getFacetFields()));

        resultsMap = searchData.getResults();

        List<String> ids = (List<String>) resultsMap.remove("ids");
        List<Map<String, Object>> results = (List<Map<String, Object>>) resultsMap.remove("results");

        int lo = toRange(start, 0, ids.size());
        int hi = toRange(lo + rows, 0, ids.size());
        ids = ids.subList(lo, hi);
        results = results.subList(lo, hi);

        resultsMap.put("ids", ids);
        resultsMap.put("results", results);
        resultsMap.put("start", lo);
        resultsMap.put("rows", hi - lo);

        resultsMap.put("sortableFields", sortableFields);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return resultsMap;
  }

  public void removeExpiredSearches() {
    long cutoffDate = new DateTime().minusDays(1).getMillis();
    Set<Long> keySet = searchDataIndex.keySet();
    for (Long key : keySet) {
      if (key < cutoffDate) {
        searchDataIndex.remove(key);
      }
    }
  }

  public int toRange(int value, int minValue, int maxValue) {
    return Math.min(Math.max(value, minValue), maxValue);
  }

  public String getSolrDir() {
    return solrDir;
  }

  public void setSolrDir(String solrDir) {
    this.solrDir = solrDir;
  }

  private SolrServerWrapper getSolrServer() {
    if (solrServer == null) {
      solrServer = new LocalSolrServer(getSolrDir());
    }
    return solrServer;
  }

  private String[] getFacetFields() {
    return facetFields;
  }

  private Map<String, FacetInfo> getFacetInfoMap() {
    return facetInfoMap;
  }

  private void setFacetData() {
    LOG.info("{}", Thread.currentThread().getContextClassLoader().getResource(".").getPath());
    InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.json");
    try {
      Map<String, Object> configMap = readConfigMap(inputStream);
      facetInfoMap = toMap(configMap.get("facetInfoMap"));
      facetFields = toStringArray(configMap.get("facetFields"));
      defaultSortOrder = toStringArray(configMap.get("defaultSortOrder"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static String[] toStringArray(Object object) {
    return ((List<String>) object).toArray(new String[] {});
  }

  static Map<String, FacetInfo> toMap(Object object) {
    Map<String, Map<String, String>> inMap = (Map<String, Map<String, String>>) object;
    Map<String, FacetInfo> outMap = Maps.newHashMapWithExpectedSize(inMap.size());
    for (Entry<String, Map<String, String>> entry : inMap.entrySet()) {
      String key = entry.getKey();
      Map<String, String> value = entry.getValue();
      outMap.put(key, new FacetInfo().setName(value.get("name")).setTitle(value.get("title")).setType(FacetType.valueOf(value.get("type"))));
    }
    return outMap;
  }

  static Map<String, Object> readConfigMap(InputStream inputStream) throws IOException, JsonParseException, JsonMappingException {
    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
    Map<String, Object> configMap = new ObjectMapper().readValue(inputStreamReader, Map.class);
    if (configMap == null) {
      configMap = Maps.newHashMap();
    }
    return configMap;
  }

}
