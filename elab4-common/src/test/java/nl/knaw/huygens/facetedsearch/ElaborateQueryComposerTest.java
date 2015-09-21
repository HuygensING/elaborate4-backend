package nl.knaw.huygens.facetedsearch;

/*
 * #%L
 * elab4-common
 * =======
 * Copyright (C) 2013 - 2015 Huygens ING
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

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class ElaborateQueryComposerTest {
	static final QueryComposer queryComposer = new ElaborateQueryComposer();

	@Test
	public void testcomposeQueryString1() throws Exception {
		ElaborateSearchParameters sp = new ElaborateSearchParameters();
		sp.setTextLayers(ImmutableList.of("Diplomatic"));
		String expected = "*:*";

		queryComposer.compose(sp);
		String query = queryComposer.getSearchQuery();
		assertThat(query).isEqualTo(expected);
	}

	@Test
	public void testcomposeQueryString2() throws Exception {
		ElaborateSearchParameters sp = new ElaborateSearchParameters();
		sp.setTerm("iets")//
				.setTextLayers(ImmutableList.of("Diplomatic"))//
				.setCaseSensitive(true);
		String expected = "name:iets textlayercs_diplomatic:iets";

		queryComposer.compose(sp);
		String query = queryComposer.getSearchQuery();
		assertThat(query).isEqualTo(expected);
		assertThat(queryComposer.mustHighlight()).isTrue();
		String hquery = queryComposer.getHighlightQuery();
		assertThat(hquery).isEqualTo(expected);
	}

	@Test
	public void testcomposeQueryString3() throws Exception {
		ElaborateSearchParameters sp = new ElaborateSearchParameters();
		sp.setTerm("iets anders")//
				.setTextLayers(ImmutableList.of("Diplomatic"))//
				.setCaseSensitive(true);
		String expected = "name:(iets anders) textlayercs_diplomatic:(iets anders)";

		queryComposer.compose(sp);
		String query = queryComposer.getSearchQuery();
		assertThat(query).isEqualTo(expected);
		assertThat(queryComposer.mustHighlight()).isTrue();
		String hquery = queryComposer.getHighlightQuery();
		assertThat(hquery).isEqualTo(expected);
	}

	@Test
	public void testcomposeQueryString4() throws Exception {
		ElaborateSearchParameters sp = new ElaborateSearchParameters();
		sp.setTerm("iets vaags")//
				.setFuzzy(true)//
				.setTextLayers(ImmutableList.of("Diplomatic", "Comments"))//
				.setCaseSensitive(false)//
				.setSearchInAnnotations(true);
		String expected = "name:(iets~0.75 vaags~0.75) textlayer_diplomatic:(iets~0.75 vaags~0.75) annotations_diplomatic:(iets~0.75 vaags~0.75) textlayer_comments:(iets~0.75 vaags~0.75) annotations_comments:(iets~0.75 vaags~0.75)";

		queryComposer.compose(sp);
		String query = queryComposer.getSearchQuery();
		assertThat(query).isEqualTo(expected);
		assertThat(queryComposer.mustHighlight()).isTrue();
		String hquery = queryComposer.getHighlightQuery();
		assertThat(hquery).isEqualTo(expected);
	}

	@Test
	public void testcomposeQueryString5() throws Exception {
		ElaborateSearchParameters sp = new ElaborateSearchParameters();
		sp.setTerm("iets vaags")//
				.setFuzzy(true)//
				.setCaseSensitive(false)//
				.setSearchInAnnotations(true);
		String expected = "*:*"; // Because no textlayers are indicated

		queryComposer.compose(sp);
		String query = queryComposer.getSearchQuery();
		assertThat(query).isEqualTo(expected);
		assertThat(queryComposer.mustHighlight()).isFalse();
	}

	@Test
	public void testcomposeQueryString6() throws Exception {
		//  {"searchInAnnotations":false,"searchInTranscriptions":false,"facetValues":[{"name":"metadata_folio_number","values":["199"]}],"term":"a*"}
		ElaborateSearchParameters sp = new ElaborateSearchParameters()//
				.setTerm("a*")//
				.setFuzzy(true)//
				.setCaseSensitive(false)//
				.setTextLayers(ImmutableList.of("Diplomatic"))//
				.setFacetValues(ImmutableList.of(new FacetParameter().setName("metadata_folio_number").setValues(ImmutableList.of("199"))))//
				.setSearchInAnnotations(false)//
				.setSearchInTranscriptions(false);
		String expected = "+(name:a*~0.75) +metadata_folio_number:(199)";

		queryComposer.compose(sp);
		String query = queryComposer.getSearchQuery();
		assertThat(query).isEqualTo(expected);
		assertThat(queryComposer.mustHighlight()).isTrue();
	}

	@Test
	public void testcomposeQueryStringWithRange() throws Exception {
		ElaborateSearchParameters sp = new ElaborateSearchParameters();
		sp.setTerm("iets vaags")//
				.setFuzzy(true)//
				.setTextLayers(ImmutableList.of("Diplomatic"))//
				.setCaseSensitive(false)//
				.setFacetValues(ImmutableList.of(//
						new FacetParameter().setName("metadata_folio_number").setValues(ImmutableList.of("199")), //
						new FacetParameter().setName("metadata_date").setLowerLimit(16000101).setUpperLimit(20201231)))//
				.setSearchInAnnotations(true);
		String expected = "+(name:(iets~0.75 vaags~0.75) textlayer_diplomatic:(iets~0.75 vaags~0.75) annotations_diplomatic:(iets~0.75 vaags~0.75))"//
				+ " +metadata_folio_number:(199)"//
				+ " +metadata_date_lower:[16000101 TO 20201231]"//
				+ " +metadata_date_upper:[16000101 TO 20201231]";

		queryComposer.compose(sp);
		String query = queryComposer.getSearchQuery();
		assertThat(query).isEqualTo(expected);
		assertThat(queryComposer.mustHighlight()).isTrue();
	}

}
