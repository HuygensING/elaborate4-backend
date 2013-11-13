package elaborate.publication.solr;

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
