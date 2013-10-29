package elaborate.editor.resources.orm;

import static org.junit.Assert.*;

import java.text.MessageFormat;
import java.util.Map;

import nl.knaw.huygens.LoggableObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import elaborate.editor.solr.AbstractSolrServer;

public class SearchResourceTest extends LoggableObject {

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testAddPrevNextURIs1() throws Exception {
    Map<String, Object> searchResult = Maps.newHashMap();
    searchResult.put(AbstractSolrServer.KEY_NUMFOUND, 100);
    SearchResource searchResource = new SearchResource(null);
    searchResource.addPrevNextURIs(searchResult, 1, 2, 0, 50);
    assertEquals("http://10.152.32.82:2013/projects/1/search/2?start=50&rows=50", searchResult.get(SearchResource.KEY_NEXT));
    assertFalse(searchResult.containsKey(SearchResource.KEY_PREV));
  }

  @Test
  public void testAddPrevNextURIs2() throws Exception {
    SearchResource searchResource = new SearchResource(null);
    Map<String, Object> searchResult = Maps.newHashMap();
    searchResult.put(AbstractSolrServer.KEY_NUMFOUND, 100);
    searchResource.addPrevNextURIs(searchResult, 1, 2, 10, 50);
    assertEquals("http://10.152.32.82:2013/projects/1/search/2?start=60&rows=50", searchResult.get(SearchResource.KEY_NEXT));
    assertEquals("http://10.152.32.82:2013/projects/1/search/2?start=0&rows=50", searchResult.get(SearchResource.KEY_PREV));
  }

  @Test
  public void testMessageFormat() {
    String path = MessageFormat.format("/projects/{0,number,#}/search/{1,number,#}", 31415, 1000000);
    assertEquals("/projects/31415/search/1000000", path);
  }

}
