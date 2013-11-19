package elaborate.editor.solr;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nl.knaw.huygens.solr.AbstractSolrServer;
import nl.knaw.huygens.solr.FacetCount;
import nl.knaw.huygens.solr.FacetedSearchParameters;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.HighlightParams;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import elaborate.editor.model.FacetInfo;

public abstract class AbstractElaborateSolrServer extends AbstractSolrServer {
  public static final String KEY_NUMFOUND = "numFound";
  private static final int HIGHLIGHT_FRAGSIZE = 100;
  private static final int ROWS = 50000;
  private static final int FACET_LIMIT = 10000;
  private static final String FUZZY = "~0.75";

  @Override
  public Map<String, Object> search(FacetedSearchParameters<?> fsp) throws IndexException {
    ElaborateSearchParameters sp = (ElaborateSearchParameters) fsp;
    LOG.info("searchparameters={}", sp);
    String queryString = composeQuery(sp);
    String[] facetFields = sp.getFacetFields();
    LOG.debug("search({},{})", queryString, sp.getSort());
    Map<String, String> textFieldMap = sp.getTextFieldsToSearch();

    SolrQuery query = new SolrQuery();
    String[] fieldsToReturn = getIndexFieldToReturn(sp.getOrderLevels());
    query.setQuery(queryString)//
        .setFields(fieldsToReturn)//
        .setRows(ROWS)//
        .addFacetField(facetFields)//
        .setFacetMinCount(1)//
        .setFacetLimit(FACET_LIMIT)//
        .setHighlight(true)//
        .setHighlightFragsize(HIGHLIGHT_FRAGSIZE);
    query.set(HighlightParams.MAX_CHARS, -1);
    query.set(HighlightParams.FIELDS, textFieldMap.keySet().toArray(new String[textFieldMap.size()]));
    query.set(HighlightParams.Q, queryString);
    query = setSort(query, sp);

    Map<String, Object> data = getSearchData(sp, facetFields, query, fieldsToReturn);
    return data;
  }

  private Map<String, Object> getSearchData(ElaborateSearchParameters sp, String[] facetFields, SolrQuery query, String[] fieldsToReturn) throws IndexException {
    Map<String, Object> data = Maps.newHashMap();
    data.put("term", query.getQuery());
    try {
      LOG.info("query=\n{}", query);
      QueryResponse response = server.query(query);
      LOG.debug("response='{}'", response);

      SolrDocumentList documents = response.getResults();
      data.put(KEY_NUMFOUND, documents.getNumFound());

      Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();

      List<String> ids = Lists.newArrayList();
      List<Map<String, Object>> results = Lists.newArrayList();
      for (SolrDocument document : documents) {
        String docId = document.getFieldValue(SolrFields.DOC_ID).toString();
        ids.add(docId);
        results.add(entryView(document, fieldsToReturn, highlighting.get(docId), sp.getTextFieldsToSearch()));
      }
      data.put("ids", ids);
      data.put("results", results);
      data.put("facets", getFacetCountList(sp, facetFields, response));

    } catch (SolrServerException e) {
      LOG.error(e.getMessage());
      throw new IndexException(e.getMessage());
    }
    data.put("solrquery", query.toString());
    return data;
  }

  private List<FacetCount> getFacetCountList(ElaborateSearchParameters sp, String[] facetFields, QueryResponse response) {
    List<FacetCount> facets = Lists.newArrayList();
    for (String facetField : facetFields) {
      FacetInfo facetInfo = sp.getFacetInfoMap().get(facetField);
      if (facetInfo != null) {
        FacetCount facetCount = convertFacet(response.getFacetField(facetField), facetInfo.getTitle(), facetInfo.getType());
        if (!facetCount.getOptions().isEmpty()) {
          facets.add(facetCount);
        }
      }
    }
    return facets;
  }

  private Map<String, Object> entryView(SolrDocument document, String[] fieldsToReturn, Map<String, List<String>> kwicMap, Map<String, String> fieldMap) {
    Map<String, Object> view = Maps.newHashMap();
    for (String field : fieldsToReturn) {
      if (field.startsWith(SolrFields.METADATAFIELD_PREFIX)) {
        view.put(field, document.getFieldValues(field));
      } else {
        view.put(field, document.getFieldValue(field));
      }
    }

    Map<String, List<String>> newKwicMap = Maps.newLinkedHashMap();
    Set<Entry<String, List<String>>> entrySet = kwicMap.entrySet();
    for (Entry<String, List<String>> entry : entrySet) {
      String fieldName = entry.getKey();
      String fieldTitle = fieldMap.get(fieldName);
      newKwicMap.put(fieldTitle, entry.getValue());
    }

    view.put("_kwic", newKwicMap);
    return view;
  }

  private String[] getIndexFieldToReturn(List<String> orderLevels) {
    List<String> list = Lists.newArrayList(SolrFields.DOC_ID, SolrFields.NAME);
    for (String level : orderLevels) {
      list.add(SolrUtils.facetName(level));
    }
    return list.toArray(new String[list.size()]);
  }

  public String composeQuery(ElaborateSearchParameters sp) {
    List<String> textLayers = sp.getTextLayers();
    String joinedTermQuery = "";
    if (textLayers.isEmpty()) {
      joinedTermQuery = "*:*";

    } else {
      List<String> terms = getTerms(sp);
      String termQuery = (terms.size() == 1) ? terms.get(0) : MessageFormat.format("({0})", Joiner.on(" ").join(terms));

      List<String> fieldQueries = Lists.newArrayList();
      for (String field : sp.getTextFieldsToSearch().keySet()) {
        fieldQueries.add(MessageFormat.format("{0}:{1}", field, termQuery));
      }
      joinedTermQuery = Joiner.on(" ").join(fieldQueries);
      if (StringUtils.isEmpty(joinedTermQuery)) {
        joinedTermQuery = "*:*";
      }
    }

    List<String> facetQueries = Lists.newArrayList();
    for (FacetParameter fp : sp.getFacetValues()) {
      String values = Joiner.on(" ").join(fp.getEscapedValues());
      String facetQuery = MessageFormat.format("+{0}:({1})", fp.getName(), values);
      facetQueries.add(facetQuery);
    }

    if (!facetQueries.isEmpty()) {
      joinedTermQuery = MessageFormat.format("+({0}) {1}", joinedTermQuery, Joiner.on(" ").join(facetQueries));
    }

    return StringUtils.isEmpty(joinedTermQuery) ? MessageFormat.format("{0}:{1,number,#}", SolrFields.PROJECT_ID, sp.getProjectId()) //
        : MessageFormat.format("({0}) AND {1}:{2,number,#}", joinedTermQuery, SolrFields.PROJECT_ID, sp.getProjectId());
  }

  private List<String> getTerms(ElaborateSearchParameters sp) {
    List<String> terms = SolrUtils.splitTerms(sp.getTerm());
    if (sp.isFuzzy()) {
      List<String> fuzzyTerms = Lists.newArrayList();
      for (String term : terms) {
        term += FUZZY;
        fuzzyTerms.add(term);
      }
      return fuzzyTerms;
    }

    return terms;
  }

  /**
   * Sets the sort criteria for the query.
   * @return 
   */
  private SolrQuery setSort(SolrQuery query, ElaborateSearchParameters sp) {
    boolean ascending = sp.isAscending();
    String sortField = sp.getSort();
    ORDER sortOrder = ascending ? SolrQuery.ORDER.asc : SolrQuery.ORDER.desc;

    if (SolrFields.SCORE.equals(sortField)) {
      query.addSort(SolrFields.SCORE, ascending ? SolrQuery.ORDER.desc : SolrQuery.ORDER.asc);

    } else if (sortField != null) {
      query.addSort(sortField, sortOrder);
    }

    query.addSort(sp.getLevel1Field(), SolrQuery.ORDER.asc);
    query.addSort(sp.getLevel2Field(), SolrQuery.ORDER.asc);
    query.addSort(sp.getLevel3Field(), SolrQuery.ORDER.asc);
    query.addSort(SolrFields.NAME, SolrQuery.ORDER.asc);
    return query;
  }

}