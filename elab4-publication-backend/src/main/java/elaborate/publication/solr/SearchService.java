package elaborate.publication.solr;

/*
 * #%L
 * elab4-publication-backend
 * =======
 * Copyright (C) 2013 - 2018 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.facetedsearch.*;
import nl.knaw.huygens.jaxrstools.exceptions.InternalServerErrorException;
import nl.knaw.huygens.solr.FacetInfo;
import nl.knaw.huygens.solr.FacetType;
import org.joda.time.DateTime;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static nl.knaw.huygens.facetedsearch.SolrUtils.EMPTYVALUE_SYMBOL;

@Singleton
public class SearchService {
  private static final SearchService instance = new SearchService();

  private final Map<Long, SearchData> searchDataIndex = Maps.newHashMap();
  private SolrServerWrapper solrServer;
  private String solrDir;
  private Map<String, FacetInfo> facetInfoMap;
  private List<RangeField> rangeFields;
  private String[] facetFields;
  private String[] defaultSortOrder;

  private String hostname;

  private SearchService() {
    super();
    loadConfig();
  }

  public static SearchService instance() {
    return instance;
  }

  public SearchData createSearch(ElaborateSearchParameters elaborateSearchParameters) {
    elaborateSearchParameters//
        .setFacetFields(getFacetFields())//
        .setFacetInfoMap(getFacetInfoMap())//
        .setRanges(getRangeFields())//
        .setLevelFields(defaultSortOrder[0], defaultSortOrder[1], defaultSortOrder[2]);
    try {
      Log.info("searchParameters={}", elaborateSearchParameters);
      Map<String, Object> result = getSolrServer().search(elaborateSearchParameters);
      Log.info("result={}", result);
      SearchData searchData = new SearchData().setResults(result);
      searchDataIndex.put(searchData.getId(), searchData);
      return searchData;

    } catch (Exception e) {
      Log.error(e.getMessage());
      Log.error("e={}", e);
      e.printStackTrace();
      throw new InternalServerErrorException(e.getMessage());
    }
  }

  public Map<String, Object> getSearchResult(long searchId, int start, int rows) {
    Map<String, Object> resultsMap = Maps.newHashMap();
    SearchData searchData = searchDataIndex.get(searchId);
    //		Map<String, String> fieldnameMap = getFieldnameMap();

    if (searchData != null) {
      List<String> sortableFields = Lists.newArrayList("id", "name");
      sortableFields.addAll(ImmutableList.copyOf(getFacetFields()));

      resultsMap = searchData.getResults();

      List<String> ids = (List<String>) resultsMap.remove("ids");
      List<Map<String, Object>> results = (List<Map<String, Object>>) resultsMap.remove("results");

      Log.info("start={}, rows={}", start, rows);
      int lo = toRange(start, 0, ids.size());
      int hi = toRange(lo + rows, 0, ids.size());
      Log.info("lo={}, hi={}", lo, hi);
      results = results.subList(lo, hi);
      Log.info("results={}", results);
      groupMetadata(results);
      Log.info("after groupMetadata: results={}", results);

      resultsMap.put("ids", ids);
      resultsMap.put("results", results);
      resultsMap.put("start", lo);
      resultsMap.put("rows", hi - lo);

      resultsMap.put("sortableFields", sortableFields);
    }
    return resultsMap;
  }

  void groupMetadata(List<Map<String, Object>> results) {
    for (Map<String, Object> resultmap : results) {
      Map<String, String> metadata = Maps.newHashMap();
      List<String> keys = ImmutableList.copyOf(resultmap.keySet());
      for (String key : keys) {
        if (key.startsWith(SolrUtils.METADATAFIELD_PREFIX)) {
          Object valueObject = resultmap.remove(key);
          FacetInfo facetInfo = facetInfoMap.get(key);
          if (facetInfo != null) {
            String name = facetInfo.getTitle();
            if (valueObject == null) {
              metadata.put(name, EMPTYVALUE_SYMBOL);
            } else if (valueObject instanceof List) {
              List<String> values = (List<String>) valueObject;
              if (values.isEmpty()) {
                metadata.put(name, EMPTYVALUE_SYMBOL);
              } else if (values.size() == 1) {
                metadata.put(name, values.get(0));
              } else if (values.size() > 1) {
                Log.warn("unexpected: multiple values: {}", values);
                metadata.put(name, values.get(0));
              }
            }
          }
        }
      }
      Log.info("metadata:{}", metadata);
      resultmap.put("metadata", metadata);
    }
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
      solrServer = new LocalSolrServer(getSolrDir(), "entries", new ElaborateQueryComposer());
    }
    return solrServer;
  }

  private String[] getFacetFields() {
    return facetFields;
  }

  private Map<String, FacetInfo> getFacetInfoMap() {
    return facetInfoMap;
  }

  private List<RangeField> getRangeFields() {
    return rangeFields;
  }

  void setFacetInfoMap(Map<String, FacetInfo> _facetInfoMap) {
    facetInfoMap = _facetInfoMap;
  }

  void setRangeFields(List<RangeField> rangeFields) {
    this.rangeFields = rangeFields;
  }

  void loadConfig() {
    //		Log.info("{}", Thread.currentThread().getContextClassLoader().getResource(".").getPath());
    try {
      InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.json");
      Map<String, Object> configMap = readConfigMap(inputStream);
      setFacetInfoMap(toMap(configMap.get("facetInfoMap")));
      setRangeFields(toRangeFieldList(configMap.get("rangeFields")));
      facetFields = toStringArray(configMap.get("facetFields"));
      defaultSortOrder = toStringArray(configMap.get("defaultSortOrder"));
      hostname = (String) configMap.get("baseURL");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("unchecked")
  static List<RangeField> toRangeFieldList(Object object) {
    List<RangeField> list = Lists.newArrayList();
    if (object == null) {
      return list;
    }
    List<Map<String, Object>> mapList = (List<Map<String, Object>>) object;
    for (Map<String, Object> map : mapList) {
      list.add(new RangeField((String) map.get("name"), (String) map.get("lowerField"), (String) map.get("upperField")));
    }
    return list;
  }

  @SuppressWarnings("unchecked")
  static String[] toStringArray(Object object) {
    return ((List<String>) object).toArray(new String[]{});
  }

  @SuppressWarnings("unchecked")
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

  public static Map<String, Object> readConfigMap(InputStream inputStream) throws IOException {
    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
    Map<String, Object> configMap = new ObjectMapper().readValue(inputStreamReader, Map.class);
    if (configMap == null) {
      configMap = Maps.newHashMap();
    }
    return configMap;
  }

  public List<String> getAllSearchResultIds(long searchId) {
    try {
      SearchData searchData = searchDataIndex.get(searchId);
      if (searchData != null) {
        Map<String, Object> resultsMap = searchData.getResults();
        return (List<String>) resultsMap.remove("ids");
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return ImmutableList.of();
  }

  public String getBaseURL() {
    return hostname;
  }

}
