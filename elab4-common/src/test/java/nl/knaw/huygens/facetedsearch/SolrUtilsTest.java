package nl.knaw.huygens.facetedsearch;

/*
 * #%L
 * elab4-common
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

import java.util.List;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SolrUtilsTest {

  @Test
  public void testNormalize() {
    assertThat(SolrUtils.normalize("Abc Def")).isEqualTo("abc_def");
  }

  @Test
  public void testEscapeFacetValue() {
    String in = "This? is a 'test' (#@!$)";
    String expected = "This\\?\\ is\\ a\\ 'test'\\ \\(#@\\!$\\)";
    assertThat(SolrUtils.escapeFacetValue(in)).isEqualTo(expected);
  }

  @Test
  public void testEmptyFacetValue() {
    String in = ":empty";
    String expected = "\\:empty";
    assertThat(SolrUtils.escapeFacetValue(in)).isEqualTo(expected);
  }

  @Test
  public void testSplitTerms1() {
    String terms = "losse woorden \"gekoppelde woorden\"";
    List<String> splitTerms = SolrUtils.splitTerms(terms);
    assertThat(splitTerms.size()).isEqualTo(3);
    assertThat(splitTerms.get(0)).isEqualTo("losse");
    assertThat(splitTerms.get(1)).isEqualTo("woorden");
    assertThat(splitTerms.get(2)).isEqualTo("\"gekoppelde woorden\"");
  }

  @Test
  public void testSplitTerms2() {
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

  @Test
  public void testIfFieldNameUsedReturnFieldname() {
    assertThat(SolrUtils.facetName("metadata_date")).isEqualTo("metadata_date");
  }

  @Test
  public void testIfFieldTitleUsedReturnFacetFieldName() {
    assertThat(SolrUtils.facetName("Date")).isEqualTo("metadata_date");
  }
}
