package elaborate.editor.solr;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class AbstractSolrServerTest {

  private TestSolrServer testSolrServer;

  @Before
  public void setUp() throws Exception {
    testSolrServer = new TestSolrServer();
  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testComposeQuery1() throws Exception {
    ElaborateSearchParameters sp = new ElaborateSearchParameters().setTextLayers(ImmutableList.of("diplomatic"));
    String expected = "(textlayer_diplomatic:*) AND project_id:0";

    String query = testSolrServer.composeQuery(sp);
    assertEquals(expected, query);
  }

  @Test
  public void testComposeQuery2() throws Exception {
    ElaborateSearchParameters sp = new ElaborateSearchParameters()//
        .setProjectId(1)//
        .setTerm("iets")//
        .setTextLayers(ImmutableList.of("Diplomatic"))//
        .setCaseSensitive(true);
    String expected = "(textlayercs_diplomatic:iets) AND project_id:1";

    String query = testSolrServer.composeQuery(sp);
    assertEquals(expected, query);
  }

  @Test
  public void testComposeQuery3() throws Exception {
    ElaborateSearchParameters sp = new ElaborateSearchParameters()//
        .setProjectId(1)//
        .setTerm("iets anders")//
        .setTextLayers(ImmutableList.of("Diplomatic"))//
        .setCaseSensitive(true);
    String expected = "(textlayercs_diplomatic:(iets anders)) AND project_id:1";

    String query = testSolrServer.composeQuery(sp);
    assertEquals(expected, query);
  }

  @Test
  public void testComposeQuery4() throws Exception {
    ElaborateSearchParameters sp = new ElaborateSearchParameters()//
        .setProjectId(1)//
        .setTerm("iets vaags")//
        .setFuzzy(true)//
        .setTextLayers(ImmutableList.of("Diplomatic", "Comments"))//
        .setCaseSensitive(false)//
        .setSearchInAnnotations(true);
    String expected = "(textlayer_diplomatic:(iets~0.75 vaags~0.75) annotations_diplomatic:(iets~0.75 vaags~0.75) textlayer_comments:(iets~0.75 vaags~0.75) annotations_comments:(iets~0.75 vaags~0.75)) AND project_id:1";

    String query = testSolrServer.composeQuery(sp);
    assertEquals(expected, query);
  }

  @Test
  public void testComposeQuery5() throws Exception {
    ElaborateSearchParameters sp = new ElaborateSearchParameters()//
        .setProjectId(1)//
        .setTerm("iets vaags")//
        .setFuzzy(true)//
        .setCaseSensitive(false)//
        .setSearchInAnnotations(true);
    String expected = "(*:*) AND project_id:1";

    String query = testSolrServer.composeQuery(sp);
    assertEquals(expected, query);
  }

  @Test
  public void testComposeQuery6() throws Exception {
    //  {"searchInAnnotations":false,"searchInTranscriptions":false,"facetValues":[{"name":"metadata_folio_number","values":["199"]}],"term":"a*"}
    ElaborateSearchParameters sp = new ElaborateSearchParameters()//
        .setProjectId(1)//
        .setTerm("a*")//
        .setFuzzy(true)//
        .setCaseSensitive(false)//
        .setTextLayers(ImmutableList.of("Diplomatic"))//
        .setFacetValues(ImmutableList.of(new FacetParameter().setName("metadata_folio_number").setValues(ImmutableList.of("199"))))//
        .setSearchInAnnotations(false)//
        .setSearchInTranscriptions(false);
    String expected = "(+(*:*) +metadata_folio_number:(199)) AND project_id:1";

    String query = testSolrServer.composeQuery(sp);
    assertEquals(expected, query);
  }

  static class TestSolrServer extends AbstractSolrServer {
    @Override
    void setServer() {}
  }

}
