package elaborate.editor.resources.orm;

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

import java.text.MessageFormat;
import java.util.Map;

import nl.knaw.huygens.LoggableObject;
import nl.knaw.huygens.facetedsearch.AbstractSolrServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

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
		assertThat(searchResult.get(SearchResource.KEY_NEXT)).isEqualTo("http://server.example.com:2013/projects/1/search/2?start=50&rows=50");
		assertThat(searchResult).doesNotContainKey(SearchResource.KEY_PREV);
	}

	@Test
	public void testAddPrevNextURIs2() throws Exception {
		SearchResource searchResource = new SearchResource(null);
		Map<String, Object> searchResult = Maps.newHashMap();
		searchResult.put(AbstractSolrServer.KEY_NUMFOUND, 100);
		searchResource.addPrevNextURIs(searchResult, 1, 2, 10, 50);
		assertThat(searchResult.get(SearchResource.KEY_NEXT)).isEqualTo("http://server.example.com:2013/projects/1/search/2?start=60&rows=50");
		assertThat(searchResult.get(SearchResource.KEY_PREV)).isEqualTo("http://server.example.com:2013/projects/1/search/2?start=0&rows=50");
	}

	@Test
	public void testMessageFormat() {
		String path = MessageFormat.format("/projects/{0,number,#}/search/{1,number,#}", 31415, 1000000);
		assertThat(path).isEqualTo("/projects/31415/search/1000000");
	}

}
