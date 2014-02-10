package elaborate.editor.solr;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2014 Huygens ING
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
import nl.knaw.huygens.facetedsearch.FacetParameter;
import nl.knaw.huygens.solr.LocalSolrServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class AbstractSolrServerTest {

	private LocalSolrServer testSolrServer;

	@Before
	public void setUp() throws Exception {
		testSolrServer = new LocalSolrServer("", "");
	}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void testComposeQuery1() throws Exception {
		ElaborateSearchParameters sp = new ElaborateSearchParameters().setTextLayers(ImmutableList.of("diplomatic"));
		String expected = "(*:*) AND project_id:0";

		String query = testSolrServer.composeQuery(sp);
		assertThat(query).isEqualTo(expected);
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
		assertThat(query).isEqualTo(expected);
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
		assertThat(query).isEqualTo(expected);
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
		assertThat(query).isEqualTo(expected);
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
		assertThat(query).isEqualTo(expected);
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
		assertThat(query).isEqualTo(expected);
	}

}
