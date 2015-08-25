package elaborate.editor.solr;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2015 Huygens ING
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

import nl.knaw.huygens.facetedsearch.FacetParameter;
import nl.knaw.huygens.facetedsearch.QueryComposer;

public class ElaborateEditorQueryComposerTest {
	static final QueryComposer queryComposer = new ElaborateEditorQueryComposer();

	@Test
	public void testComposeQuery1() throws Exception {
		ElaborateEditorSearchParameters sp = new ElaborateEditorSearchParameters();
		sp.setTextLayers(ImmutableList.of("diplomatic"));
		String expected = "(*:*) AND project_id:0";

		queryComposer.compose(sp);
		assertThat(queryComposer.getSearchQuery()).isEqualTo(expected);
		assertThat(queryComposer.mustHighlight()).isFalse();
	}

	@Test
	public void testComposeQuery2() throws Exception {
		ElaborateEditorSearchParameters sp = new ElaborateEditorSearchParameters()//
				.setProjectId(1);
		sp.setTerm("iets")//
				.setTextLayers(ImmutableList.of("Diplomatic"))//
				.setCaseSensitive(true);
		String expected = "(name:iets textlayercs_diplomatic:iets) AND project_id:1";
		String expectedh = "name:iets textlayercs_diplomatic:iets";

		queryComposer.compose(sp);
		assertThat(queryComposer.getSearchQuery()).isEqualTo(expected);
		assertThat(queryComposer.mustHighlight()).isTrue();
		assertThat(queryComposer.getHighlightQuery()).isEqualTo(expectedh);
	}

	@Test
	public void testComposeQuery3() throws Exception {
		ElaborateEditorSearchParameters sp = new ElaborateEditorSearchParameters()//
				.setProjectId(1);
		sp.setTerm("iets anders")//
				.setTextLayers(ImmutableList.of("Diplomatic"))//
				.setCaseSensitive(true);
		String expected = "(name:(iets anders) textlayercs_diplomatic:(iets anders)) AND project_id:1";
		String expectedh = "name:(iets anders) textlayercs_diplomatic:(iets anders)";

		queryComposer.compose(sp);
		assertThat(queryComposer.getSearchQuery()).isEqualTo(expected);
		assertThat(queryComposer.mustHighlight()).isTrue();
		assertThat(queryComposer.getHighlightQuery()).isEqualTo(expectedh);
	}

	@Test
	public void testComposeQuery4() throws Exception {
		ElaborateEditorSearchParameters sp = new ElaborateEditorSearchParameters()//
				.setProjectId(1);
		sp.setTerm("iets vaags")//
				.setFuzzy(true)//
				.setTextLayers(ImmutableList.of("Diplomatic", "Comments"))//
				.setCaseSensitive(false)//
				.setSearchInAnnotations(true);
		String expected = "(name:(iets~0.75 vaags~0.75) textlayer_diplomatic:(iets~0.75 vaags~0.75) annotations_diplomatic:(iets~0.75 vaags~0.75) textlayer_comments:(iets~0.75 vaags~0.75) annotations_comments:(iets~0.75 vaags~0.75)) AND project_id:1";
		String expectedh = "name:(iets~0.75 vaags~0.75) textlayer_diplomatic:(iets~0.75 vaags~0.75) annotations_diplomatic:(iets~0.75 vaags~0.75) textlayer_comments:(iets~0.75 vaags~0.75) annotations_comments:(iets~0.75 vaags~0.75)";

		queryComposer.compose(sp);
		assertThat(queryComposer.getSearchQuery()).isEqualTo(expected);
		assertThat(queryComposer.mustHighlight()).isTrue();
		assertThat(queryComposer.getHighlightQuery()).isEqualTo(expectedh);
	}

	@Test
	public void testComposeQuery5() throws Exception {
		ElaborateEditorSearchParameters sp = new ElaborateEditorSearchParameters()//
				.setProjectId(1);
		sp.setTerm("iets vaags")//
				.setFuzzy(true)//
				.setCaseSensitive(false)//
				.setSearchInAnnotations(true);
		String expected = "(*:*) AND project_id:1";

		queryComposer.compose(sp);
		assertThat(queryComposer.getSearchQuery()).isEqualTo(expected);
		assertThat(queryComposer.mustHighlight()).isFalse();
	}

	@Test
	public void testComposeQuery6() throws Exception {
		//  {"searchInAnnotations":false,"searchInTranscriptions":false,"facetValues":[{"name":"metadata_folio_number","values":["199"]}],"term":"a*"}
		ElaborateEditorSearchParameters sp = new ElaborateEditorSearchParameters()//
				.setProjectId(1);//
		sp.setTerm("a*")//
				.setFuzzy(true)//
				.setCaseSensitive(false)//
				.setTextLayers(ImmutableList.of("Diplomatic"))//
				.setFacetValues(ImmutableList.of(new FacetParameter().setName("metadata_folio_number").setValues(ImmutableList.of("199"))))//
				.setSearchInAnnotations(false)//
				.setSearchInTranscriptions(false);
		String expected = "(+(name:a*~0.75) +metadata_folio_number:(199)) AND project_id:1";

		queryComposer.compose(sp);
		assertThat(queryComposer.getSearchQuery()).isEqualTo(expected);
		assertThat(queryComposer.mustHighlight()).isTrue();
	}

}
