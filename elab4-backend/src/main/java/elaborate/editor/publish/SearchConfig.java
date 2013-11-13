package elaborate.editor.publish;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import elaborate.editor.model.FacetInfo;
import elaborate.editor.model.orm.Project;

public class SearchConfig {
  List<String> facetFields = Lists.newArrayList();
  Map<String, FacetInfo> facetInfoMap = Maps.newLinkedHashMap();
  List<String> defaultSortOrder = Lists.newArrayList();

  public SearchConfig(Project project, List<String> selectedMetadataFields) {
    for (Entry<String, FacetInfo> entry : project.getFacetInfoMap().entrySet()) {
      String key = entry.getKey();
      FacetInfo value = entry.getValue();
      if (selectedMetadataFields.contains(value.getTitle())) {
        facetInfoMap.put(key, value);
      }
    }
    facetFields = ImmutableList.copyOf(facetInfoMap.keySet());
    defaultSortOrder = ImmutableList.of(//
        fieldOf(project.getLevel1()),//
        fieldOf(project.getLevel2()),//
        fieldOf(project.getLevel3())//
        );
  }

  private String fieldOf(String level) {
    return StringUtils.defaultIfEmpty(level, "");
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

}
