package elaborate.publication.solr;

/*
 * #%L
 * elab4-publication-backend
 * =======
 * Copyright (C) 2013 - 2014 Huygens ING
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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import elaborate.LoggableObject;

public class SearchServiceTest extends LoggableObject {

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testReadConfigMap() throws Exception {
    String a = ("{"//
        + "'facetInfoMap':{"//
        + "'metadata_field1':{'name':'metadata_field1','title':'Field1','type':'LIST'},"//
        + "'metadata_field2':{'name':'metadata_field2','title':'Field2','type':'LIST'},"//
        + "'metadata_field3':{'name':'metadata_field3','title':'Field3','type':'LIST'}"//
        + "},"//
        + "'facetFields':['metadata_field1','metadata_field2','metadata_field3']"//  
        + "}").replaceAll("'", "\\\"");
    LOG.info("{}", a);
    InputStream reader = new ByteArrayInputStream(a.getBytes());
    Map<String, Object> configMap = SearchService.readConfigMap(reader);
    assertThat(configMap).isNotEmpty();

    //    assertTrue(configMap.containsKey("facetFields"));
    //    List<String> facetFields = (List<String>) configMap.get("facetFields");
    //    LOG.info("facetFields={}", facetFields);
    //    assertFalse(facetFields.isEmpty());

    Map<String, Map<String, String>> facetInfoMap = (Map<String, Map<String, String>>) configMap.get("facetInfoMap");
    LOG.info("facetInfoMap={}", facetInfoMap);
    assertThat(facetInfoMap).isNotNull();
    assertThat(facetInfoMap.keySet()).isNotEmpty();

    String[] facetFields = SearchService.toStringArray(configMap.get("facetFields"));
    String[] expected = new String[] { "metadata_field1", "metadata_field2", "metadata_field3" };
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
}
