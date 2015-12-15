package elaborate.editor.publish;

import java.util.Collection;
import java.util.Date;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2015 Huygens ING
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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import elaborate.editor.model.orm.Project;
import nl.knaw.huygens.facetedsearch.RangeField;
import nl.knaw.huygens.solr.FacetInfo;
import nl.knaw.huygens.solr.FacetType;

public class SearchConfig {
  private static final String MULTIVALUED_PREFIX = "mv_";
  List<String> facetFields = Lists.newArrayList();
  Map<String, FacetInfo> facetInfoMap = Maps.newLinkedHashMap();
  List<String> defaultSortOrder = Lists.newArrayList();
  String baseURL;
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

  public List<RangeField> getRangeFields() {
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
