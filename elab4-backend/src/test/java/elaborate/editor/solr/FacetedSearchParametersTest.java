package elaborate.editor.solr;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FacetedSearchParametersTest<T> {

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testSetTerm() throws Exception {
    FacetedSearchParameters<?> facetedSearchParameters = new FacetedSearchParameters().setCaseSensitive(true).setFacetFields(new String[] {});
    assertTrue(facetedSearchParameters.isCaseSensitive());
  }
}
