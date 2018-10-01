package elaborate.publication.resources;

/*
 * #%L
 * elab4-publication-backend
 * =======
 * Copyright (C) 2013 - 2018 Huygens ING
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

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableList;

import elaborate.publication.solr.SearchService;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.facetedsearch.AbstractSolrServer;
import nl.knaw.huygens.facetedsearch.ElaborateSearchParameters;
import nl.knaw.huygens.facetedsearch.SearchData;
import nl.knaw.huygens.jaxrstools.exceptions.BadRequestException;
import nl.knaw.huygens.jaxrstools.resources.UTF8MediaType;

@Path("search")
public class SearchResource {
	private static final String SEARCH_PATH_TEMPLATE = "/search/{0,number,#}";
	static final String KEY_NEXT = "_next";
	static final String KEY_PREV = "_prev";

	@Context
	SearchService searchService;
	@Context
	ServletContext context;

	@GET
	@Produces(UTF8MediaType.APPLICATION_JSON)
	public Object doSimpleSearch(@QueryParam("q") @DefaultValue("") String term) {
		ElaborateSearchParameters elaborateSearchParameters = new ElaborateSearchParameters()//
				.setTerm(term)//
				.setTextLayers(ImmutableList.of("Diplomatic"));
		searchService.setSolrDir(getSolrDir());
		SearchData search = searchService.createSearch(elaborateSearchParameters);
		Map<String, Object> searchResult = searchService.getSearchResult(search.getId(), 0, 1000);
		return searchResult;
	}

	private String getSolrDir() {
		return context.getRealPath("/WEB-INF/solr");
	}

	@POST
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@Produces(UTF8MediaType.APPLICATION_JSON)
	public Response createSearch(//
			ElaborateSearchParameters elaborateSearchParameters//
	) {
		Log.debug("elaborateSearchParameters:{}", elaborateSearchParameters);
		searchService.setSolrDir(getSolrDir());
		SearchData search = searchService.createSearch(elaborateSearchParameters);
		return Response.created(createURI(search)).build();
	}

	@GET
	@Path("{search_id:[0-9]+}")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	public Response getSearchResults(//
			@PathParam("search_id") long searchId, //
			@QueryParam("start") @DefaultValue("0") String startString, //
			@QueryParam("rows") @DefaultValue("100") String rowsString//
	) {
		if (!StringUtils.isNumeric(startString) || !StringUtils.isNumeric(rowsString)) {
			throw new BadRequestException();
		}
		int start = Integer.valueOf(startString);
		int rows = Integer.valueOf(rowsString);
		Map<String, Object> searchResult = searchService.getSearchResult(searchId, start, rows);
		addPrevNextURIs(searchResult, searchId, start, rows);
		ResponseBuilder builder = Response.ok(searchResult);
		return builder.build();
	}

	@GET
	@Path("{search_id:[0-9]+}/allids")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	public Response getAllSearchResultIds(@PathParam("search_id") long searchId) {
		List<String> searchResultIds = searchService.getAllSearchResultIds(searchId);
		ResponseBuilder builder = Response.ok(searchResultIds);
		return builder.build();
	}

	void addPrevNextURIs(Map<String, Object> searchResult, long searchId, int start, int rows) {
		int prevStart = Math.max(0, start - rows);
		Log.info("prevStart={}", prevStart);
		String path = MessageFormat.format(SEARCH_PATH_TEMPLATE, searchId);
		if (start > 0) {
			addURI(searchResult, KEY_PREV, path, prevStart, rows);
		}

		int nextStart = start + rows;
		int size = (Integer) searchResult.get(AbstractSolrServer.KEY_NUMFOUND);
		Log.info("nextStart={}, size={}", nextStart, size);
		if (nextStart < size) {
			addURI(searchResult, KEY_NEXT, path, start + rows, rows);
		}
	}

	private void addURI(Map<String, Object> searchResult, String key, String prevLink, int start, int rows) {
		UriBuilder builder = UriBuilder//
				.fromUri(searchService.getBaseURL() + "/api/")//
				.path(prevLink)//
				.queryParam("start", start)//
				.queryParam("rows", rows);
		searchResult.put(key, builder.build().toString());
	}

	protected URI createURI(SearchData e) {
		URI uri;
		try {
			uri = new URI(String.valueOf(e.getId()));
		} catch (URISyntaxException ue) {
			uri = null;
			ue.printStackTrace();
		}
		return uri;
	}

}
