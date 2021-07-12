package elaborate.editor.publish;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2021 Huygens ING
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import nl.knaw.huygens.Log;
import nl.knaw.huygens.solr.FacetInfo;

import elaborate.editor.model.orm.Project;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchConfigTest {

  @Test
  public void testSearchConfigWithMultivaluedFacets() {
    Project project =
        new Project()
            .setProjectEntryMetadataFieldnames(
                ImmutableList.of(
                    "Field 1", "Field 2", "Field 3", "field 4", "MultiField 1", "MultiField 2"))
            .setLevel1("Field1")
            .setLevel2("Field3");
    List<String> metadataFieldsForFacets =
        ImmutableList.of("Field 1", "Field 2", "MultiField 1", "MultiField 2");
    Collection<String> multivaluedFacetNames =
        ImmutableList.of("metadata_multifield_1", "metadata_multifield_2");

    SearchConfig ac = new SearchConfig(project, metadataFieldsForFacets, multivaluedFacetNames);
    Map<String, FacetInfo> facetInfoMap = ac.getFacetInfoMap();

    assertThat(facetInfoMap)
        .containsKeys(
            "metadata_field_1",
            "metadata_field_2",
            "mv_metadata_multifield_1",
            "mv_metadata_multifield_2");
    FacetInfo facetInfo = facetInfoMap.get("mv_metadata_multifield_2");
    Log.info("facetInfo={}", facetInfo);
    assertThat(facetInfo.getTitle()).isEqualTo("MultiField 2");
    assertThat(facetInfo.getName()).isEqualTo("mv_metadata_multifield_2");
  }
}
