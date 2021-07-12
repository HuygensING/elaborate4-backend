package elaborate.editor.publish;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;

import nl.knaw.huygens.facetedsearch.RangeField;
import nl.knaw.huygens.solr.FacetInfo;
import nl.knaw.huygens.solr.FacetType;

import elaborate.editor.model.orm.Project;

public class SearchConfig {
  private static final String MULTIVALUED_PREFIX = "mv_";
  private List<String> facetFields = Lists.newArrayList();
  private Map<String, FacetInfo> facetInfoMap = Maps.newLinkedHashMap();
  private List<String> defaultSortOrder = Lists.newArrayList();
  private String baseURL;
  private final List<RangeField> rangeFields = Lists.newArrayList();

  public SearchConfig(Project project, List<String> metadataFieldsForFacets, Collection<String> multivaluedFacetNames) {
    // TODO: refactor CNW Kludge
    if (44 == project.getId()) {
      getRangeFields().add(new RangeField("metadata_datum", "metadata_datum_lower", "metadata_datum_upper"));
    }
    for (Entry<String, FacetInfo> entry : project.getFacetInfoMap().entrySet()) {
      String facetName = entry.getKey();
      FacetInfo facetInfo = entry.getValue();
      String facetTitle = facetInfo.getTitle();

      if (metadataFieldsForFacets.contains(facetTitle)) {
        if (multivaluedFacetNames.contains(facetName)) {
          facetName = MULTIVALUED_PREFIX + facetName;
          facetInfo.setName(facetName);
        }
        facetInfoMap.put(facetName, facetInfo);
        // TODO: refactor or remove! kludge for CNW
        insertFacetsForCNW(project, facetName);
      }
    }

    facetFields = ImmutableList.copyOf(facetInfoMap.keySet());

    defaultSortOrder = ImmutableList.of(//
        fieldOf(project.getLevel1()), //
        fieldOf(project.getLevel2()), //
        fieldOf(project.getLevel3())//
    );
  }

  private void insertFacetsForCNW(Project project, String key) {
    if (project.getId() == 44) {
      if (key.equals("metadata_ontvanger")) {
        String name = MULTIVALUED_PREFIX + "metadata_correspondents";
        FacetInfo facetInfo = new FacetInfo()//
            .setName(name)//
            .setTitle("Correspondent")//
            .setType(FacetType.LIST);
        facetInfoMap.put(name, facetInfo);
      }
    }
  }

  private String fieldOf(String level) {
    return StringUtils.defaultIfEmpty(level, "");
  }

  private List<RangeField> getRangeFields() {
    return rangeFields;
  }

  public List<String> getFacetFields() {
    return facetFields;
  }

  public void setFacetFields(List<String> facetFields) {
    this.facetFields = facetFields;
  }

  public Map<String, FacetInfo> getFacetInfoMap() {
    return facetInfoMap;
  }

  public void setFacetInfoMap(Map<String, FacetInfo> facetInfoMap) {
    this.facetInfoMap = facetInfoMap;
  }

  public List<String> getDefaultSortOrder() {
    return defaultSortOrder;
  }

  public void setDefaultSortOrder(List<String> defaultSortOrder) {
    this.defaultSortOrder = defaultSortOrder;
  }

  public String getBaseURL() {
    return baseURL;
  }

  public SearchConfig setBaseURL(String baseURL) {
    this.baseURL = baseURL;
    return this;
  }

  public Long getGenerated() {
    return new Date().getTime();
  }
}
