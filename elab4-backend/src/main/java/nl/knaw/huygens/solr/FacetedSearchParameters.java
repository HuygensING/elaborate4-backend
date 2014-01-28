package nl.knaw.huygens.solr;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

@SuppressWarnings("unchecked")
public class FacetedSearchParameters<T extends FacetedSearchParameters<T>> extends AbstractSearchParameters<FacetedSearchParameters<T>> {
  private String term = "*";
  private String sort = null;
  private String sortDir = "asc";
  private boolean caseSensitive = false;
  private String[] facetFields = new String[] {};
  private List<FacetParameter> facetParameters = Lists.newArrayList();
  private Map<String, FacetInfo> facetInfoMap;
  private List<String> orderLevels = Lists.newArrayList();
  private boolean fuzzy = false;

  public T setTerm(final String term) {
    if (StringUtils.isNotBlank(term)) {
      this.term = term;
    }
    return (T) this;
  }

  public String getTerm() {
    return term;
  }

  public T setSort(final String sort) {
    this.sort = sort;
    return (T) this;
  }

  public String getSort() {
    return sort;
  }

  public T setSortDir(final String sortDir) {
    this.sortDir = sortDir;
    return (T) this;
  }

  public String getSortDir() {
    return sortDir;
  }

  public boolean isAscending() {
    return "asc".equals(sortDir);
  }

  public T setCaseSensitive(boolean matchCase) {
    this.caseSensitive = matchCase;
    return (T) this;
  }

  public boolean isCaseSensitive() {
    return caseSensitive;
  }

  public T setFacetFields(String[] _facetFields) {
    this.facetFields = _facetFields;
    return (T) this;
  }

  public String[] getFacetFields() {
    return facetFields;
  }

  public T setOrderLevels(List<String> orderLevels) {
    this.orderLevels = orderLevels;
    return (T) this;
  }

  public List<String> getOrderLevels() {
    return orderLevels;
  }

  public boolean isFuzzy() {
    return fuzzy;
  }

  public T setFuzzy(Boolean fuzzy) {
    this.fuzzy = fuzzy;
    return (T) this;
  }

  public List<FacetParameter> getFacetValues() {
    return facetParameters;
  }

  public T setFacetValues(List<FacetParameter> fp) {
    this.facetParameters = fp;
    return (T) this;
  }

  public Map<String, FacetInfo> getFacetInfoMap() {
    return facetInfoMap;
  }

  public T setFacetInfoMap(Map<String, FacetInfo> facetInfoMap) {
    this.facetInfoMap = facetInfoMap;
    return (T) this;
  }

}
