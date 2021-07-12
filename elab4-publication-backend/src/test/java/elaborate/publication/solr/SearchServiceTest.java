package elaborate.publication.solr;

/*
 * #%L
 * elab4-publication-backend
 * =======
 * Copyright (C) 2013 - 2021 Huygens ING
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.assertj.core.data.MapEntry;
import org.junit.Test;

import nl.knaw.huygens.Log;
import nl.knaw.huygens.facetedsearch.RangeField;
import nl.knaw.huygens.solr.FacetInfo;
import nl.knaw.huygens.solr.FacetType;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchServiceTest {

  @Test
  public void testReadConfigMap() throws Exception {
    String a =
        ("{" //
                + "'facetInfoMap':{" //
                + "'metadata_field1':{'name':'metadata_field1','title':'Field1','type':'LIST'}," //
                + "'metadata_field2':{'name':'metadata_field2','title':'Field2','type':'LIST'}," //
                + "'metadata_field3':{'name':'metadata_field3','title':'Field3','type':'LIST'}" //
                + "}," //
                + "'facetFields':['metadata_field1','metadata_field2','metadata_field3']" //
                + "}")
            .replaceAll("'", "\\\"");
    Log.info("{}", a);
    InputStream reader = new ByteArrayInputStream(a.getBytes());
    Map<String, Object> configMap = SearchService.readConfigMap(reader);
    assertThat(configMap).isNotEmpty();

    //    assertTrue(configMap.containsKey("facetFields"));
    //    List<String> facetFields = (List<String>) configMap.get("facetFields");
    //    Log.info("facetFields={}", facetFields);
    //    assertFalse(facetFields.isEmpty());

    Map<String, Map<String, String>> facetInfoMap =
        (Map<String, Map<String, String>>) configMap.get("facetInfoMap");
    Log.info("facetInfoMap={}", facetInfoMap);
    assertThat(facetInfoMap).isNotNull();
    assertThat(facetInfoMap.keySet()).isNotEmpty();

    String[] facetFields = SearchService.toStringArray(configMap.get("facetFields"));
    String[] expected = new String[] {"metadata_field1", "metadata_field2", "metadata_field3"};
    assertThat(expected).isEqualTo(facetFields);

    Map<String, String> facetInfo = facetInfoMap.get("metadata_field1");
    assertThat(facetInfo).isNotNull();
    assertThat(facetInfo.get("type")).isEqualTo("LIST");
    assertThat(facetInfo.get("title")).isEqualTo("Field1");

    Map<String, FacetInfo> map = SearchService.toMap(configMap.get("facetInfoMap"));
    FacetInfo facetInfo2 = map.get("metadata_field2");
    assertThat(facetInfo2.getType()).isEqualTo(FacetType.LIST);
    assertThat(facetInfo2.getTitle()).isEqualTo("Field2");
    assertThat(facetInfo2.getName()).isEqualTo("metadata_field2");
  }

  @Test
  public void testMetadataFieldTitlesAreReturned() {
    List<Map<String, Object>> results = Lists.newArrayList();
    Map<String, Object> map = Maps.newHashMap();
    map.put("metadata_field1", ImmutableList.of("value"));
    map.put("metadata_field2", null);
    results.add(map);
    int size = results.size();

    SearchService searchService = SearchService.instance();
    FacetInfo facetInfo1 =
        new FacetInfo().setName("metadata_field1").setTitle("Field 1").setType(FacetType.LIST);
    FacetInfo facetInfo2 =
        new FacetInfo().setName("metadata_field2").setTitle("Field 2").setType(FacetType.LIST);
    Map<String, FacetInfo> facetInfoMap =
        ImmutableMap.of("metadata_field1", facetInfo1, "metadata_field2", facetInfo2);
    searchService.setFacetInfoMap(facetInfoMap);

    searchService.groupMetadata(results);
    assertThat(results).hasSize(size);

    Map<String, Object> first = results.get(0);
    Map<String, Object> firstMetadata = (Map<String, Object>) first.get("metadata");
    assertThat(firstMetadata)
        .containsOnly(MapEntry.entry("Field 1", "value"), MapEntry.entry("Field 2", ":empty"));
  }

  @Test
  public void testDeserializingRangeFieldListWorks() throws IOException {
    String json =
        "{\"rangeFields\": [" //
            + "{" //
            + " \"name\": \"metadata_datum\"," //
            + " \"lowerField\": \"metadata_datum_lower\"," //
            + " \"upperField\": \"metadata_datum_upper\"" //
            + "}]}";
    InputStream reader = new ByteArrayInputStream(json.getBytes());
    Map<String, Object> configMap = SearchService.readConfigMap(reader);
    List<RangeField> rangeFieldList = SearchService.toRangeFieldList(configMap.get("rangeFields"));
    assertThat(rangeFieldList).hasSize(1);
    RangeField rangeField = rangeFieldList.get(0);
    assertThat(rangeField.getName()).isEqualTo("metadata_datum");
    assertThat(rangeField.getLowerField()).isEqualTo("metadata_datum_lower");
    assertThat(rangeField.getUpperField()).isEqualTo("metadata_datum_upper");
  }
}
