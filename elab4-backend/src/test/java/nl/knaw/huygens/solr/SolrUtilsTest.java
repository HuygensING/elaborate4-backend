package nl.knaw.huygens.solr;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

public class SolrUtilsTest {

  @Test
  public void testEscapeFacetValue() throws Exception {
    String in = "This? is a 'test' (#@!$)";
    String expected = "This\\?\\ is\\ a\\ 'test'\\ \\(#@\\!$\\)";
    assertThat(SolrUtils.escapeFacetValue(in)).isEqualTo(expected);
  }

  @Test
  public void testEmptyFacetValue() throws Exception {
    String in = ":empty";
    String expected = "\\:empty";
    assertThat(SolrUtils.escapeFacetValue(in)).isEqualTo(expected);
  }

  @Test
  public void testSplitTerms1() throws Exception {
    String terms = "losse woorden \"gekoppelde woorden\"";
    List<String> splitTerms = SolrUtils.splitTerms(terms);
    assertThat(splitTerms.size()).isEqualTo(3);
    assertThat(splitTerms.get(0)).isEqualTo("losse");
    assertThat(splitTerms.get(1)).isEqualTo("woorden");
    assertThat(splitTerms.get(2)).isEqualTo("\"gekoppelde woorden\"");
  }

  @Test
  public void testSplitTerms2() throws Exception {
    String terms = "\"drie gekoppelde woorden\" twee losse woorden";
    List<String> splitTerms = SolrUtils.splitTerms(terms);
    assertThat(splitTerms.size()).isEqualTo(4);
    assertThat(splitTerms.get(0)).isEqualTo("\"drie gekoppelde woorden\"");
    assertThat(splitTerms.get(1)).isEqualTo("twee");
    assertThat(splitTerms.get(2)).isEqualTo("losse");
    assertThat(splitTerms.get(3)).isEqualTo("woorden");
  }

  @Test
  public void testSpecialCharacter() {
    assertThat(SolrUtils.fuzzy("+groot")).isEqualTo("+groot");
    assertThat(SolrUtils.fuzzy("-groot")).isEqualTo("-groot");
    assertThat(SolrUtils.fuzzy("groo*")).isEqualTo("groo*");
    assertThat(SolrUtils.fuzzy("gro?t")).isEqualTo("gro?t");
    assertThat(SolrUtils.fuzzy("groot~")).isEqualTo("groot~");
    assertThat(SolrUtils.fuzzy("\"groot\"")).isEqualTo("\"groot\"");
    assertThat(SolrUtils.fuzzy("(groot)")).isEqualTo("(groot)");
  }

  @Test
  public void testNoTerm() {
    assertThat(SolrUtils.fuzzy(" ")).isEqualTo(" ");
  }

  @Test
  public void testOneTerm() {
    assertThat(SolrUtils.fuzzy("groot")).isEqualTo("groot~0.7");
  }

  @Test
  public void testTwoTerms() {
    assertThat(SolrUtils.fuzzy("hugo groot")).isEqualTo("hugo~0.7 AND groot~0.7");
  }

  @Test
  public void testThreeTerms() {
    assertThat(SolrUtils.fuzzy("hugo de groot")).isEqualTo("hugo~0.7 AND de~0.5 AND groot~0.7");
  }

}
