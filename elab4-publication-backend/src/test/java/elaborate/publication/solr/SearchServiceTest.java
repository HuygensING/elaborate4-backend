package elaborate.publication.solr;

import static org.junit.Assert.*;

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
    assertFalse(configMap.isEmpty());

    //    assertTrue(configMap.containsKey("facetFields"));
    //    List<String> facetFields = (List<String>) configMap.get("facetFields");
    //    LOG.info("facetFields={}", facetFields);
    //    assertFalse(facetFields.isEmpty());

    Map<String, Map<String, String>> facetInfoMap = (Map<String, Map<String, String>>) configMap.get("facetInfoMap");
    assertNotNull(facetInfoMap);
    LOG.info("facetInfoMap={}", facetInfoMap);
    assertFalse(facetInfoMap.keySet().isEmpty());

    String[] facetFields = SearchService.toStringArray(configMap.get("facetFields"));
    String[] expected = new String[] { "metadata_field1", "metadata_field2", "metadata_field3" };
    assertArrayEquals(expected, facetFields);
    Map<String, String> facetInfo = facetInfoMap.get("metadata_field1");
    assertNotNull(facetInfo);
    assertEquals("LIST", facetInfo.get("type"));
    assertEquals("Field1", facetInfo.get("title"));

    Map<String, FacetInfo> map = SearchService.toMap(configMap.get("facetInfoMap"));
    FacetInfo facetInfo2 = map.get("metadata_field2");
    assertEquals(FacetType.LIST, facetInfo2.getType());
    assertEquals("Field2", facetInfo2.getTitle());
    assertEquals("metadata_field2", facetInfo2.getName());

  }
}
