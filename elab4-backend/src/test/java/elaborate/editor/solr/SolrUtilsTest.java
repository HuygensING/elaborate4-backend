package elaborate.editor.solr;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class SolrUtilsTest {

  @Test
  public void testEscapeFacetValue() throws Exception {
    String in = "This? is a 'test' (#@!$)";
    String expected = "This\\?\\ is\\ a\\ 'test'\\ \\(#@\\!$\\)";
    assertEquals(expected, SolrUtils.escapeFacetValue(in));
  }

  @Test
  public void testEmptyFacetValue() throws Exception {
    String in = ":empty";
    String expected = "\\:empty";
    assertEquals(expected, SolrUtils.escapeFacetValue(in));
  }

  @Test
  public void testSplitTerms1() throws Exception {
    String terms = "losse woorden \"gekoppelde woorden\"";
    List<String> splitTerms = SolrUtils.splitTerms(terms);
    assertEquals(3, splitTerms.size());
    assertEquals("losse", splitTerms.get(0));
    assertEquals("woorden", splitTerms.get(1));
    assertEquals("\"gekoppelde woorden\"", splitTerms.get(2));
  }

  @Test
  public void testSplitTerms2() throws Exception {
    String terms = "\"drie gekoppelde woorden\" twee losse woorden";
    List<String> splitTerms = SolrUtils.splitTerms(terms);
    assertEquals(4, splitTerms.size());
    assertEquals("\"drie gekoppelde woorden\"", splitTerms.get(0));
    assertEquals("twee", splitTerms.get(1));
    assertEquals("losse", splitTerms.get(2));
    assertEquals("woorden", splitTerms.get(3));
  }

  @Test
  public void testSpecialCharacter() {
    assertEquals("+groot", SolrUtils.fuzzy("+groot"));
    assertEquals("-groot", SolrUtils.fuzzy("-groot"));
    assertEquals("groo*", SolrUtils.fuzzy("groo*"));
    assertEquals("gro?t", SolrUtils.fuzzy("gro?t"));
    assertEquals("groot~", SolrUtils.fuzzy("groot~"));
    assertEquals("\"groot\"", SolrUtils.fuzzy("\"groot\""));
    assertEquals("(groot)", SolrUtils.fuzzy("(groot)"));
  }

  @Test
  public void testNoTerm() {
    assertEquals(" ", SolrUtils.fuzzy(" "));
  }

  @Test
  public void testOneTerm() {
    assertEquals("groot~0.7", SolrUtils.fuzzy("groot"));
  }

  @Test
  public void testTwoTerms() {
    assertEquals("hugo~0.7 AND groot~0.7", SolrUtils.fuzzy("hugo groot"));
  }

  @Test
  public void testThreeTerms() {
    assertEquals("hugo~0.7 AND de~0.5 AND groot~0.7", SolrUtils.fuzzy("hugo de groot"));
  }

}
