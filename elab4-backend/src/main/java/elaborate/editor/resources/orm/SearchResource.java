package elaborate.editor.resources.orm;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2021 Huygens ING
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

import com.sun.jersey.spi.resource.*;
import elaborate.editor.config.*;
import elaborate.editor.model.orm.*;
import elaborate.editor.model.orm.service.*;
import elaborate.editor.resources.*;
import elaborate.editor.solr.*;
import elaborate.jaxrs.Annotations.*;
import nl.knaw.huygens.facetedsearch.*;
import nl.knaw.huygens.jaxrstools.exceptions.*;
import nl.knaw.huygens.jaxrstools.resources.*;
import org.apache.commons.lang.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.*;
import java.text.*;
import java.util.*;

@AuthorizationRequired
@Singleton
public class SearchResource extends AbstractElaborateResource {
	private static final int DEFAULT_PORT = 80;
	static final String KEY_NEXT = "_next";
	static final String KEY_PREV = "_prev";

	private final SearchService searchService = SearchService.instance();
	private final Configuration config = Configuration.instance();

	private final User user;

	public SearchResource(User user) {
		this.user = user;
	}

	@POST
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@Produces(UTF8MediaType.APPLICATION_JSON)
	public synchronized Response createSearch(//
			@PathParam("project_id") long projectId, //
			ElaborateEditorSearchParameters elaborateSearchParameters//
	) {
		searchService.removeExpiredSearches();
		elaborateSearchParameters.setProjectId(projectId);
		StorableSearchData search = searchService.createSearch(elaborateSearchParameters, user);
		return Response.created(createURI(search)).build();
	}

	@GET
	@Path("{search_id:[0-9]+}")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	public Response getSearchResults(//
			@PathParam("project_id") long projectId, //
			@PathParam("search_id") long searchId, //
			@QueryParam("start") @DefaultValue("0") String startString, //
			@QueryParam("rows") @DefaultValue("25") String rowsString//
	// @QueryParam("verbose") @DefaultValue("false") boolean verbose//
	) {
		if (!StringUtils.isNumeric(startString) || !StringUtils.isNumeric(rowsString)) {
			throw new BadRequestException();
		}
		int start = Integer.parseInt(startString);
		int rows = Integer.parseInt(rowsString);
		Map<String, Object> searchResult = searchService.getSearchResult(projectId, searchId, start, rows, user);
		addPrevNextURIs(searchResult, projectId, searchId, start, rows);
		ResponseBuilder builder = Response.ok(searchResult);
		return builder.build();
	}

	void addPrevNextURIs(Map<String, Object> searchResult, long projectId, long searchId, int start, int rows) {
		int prevStart = Math.max(0, start - rows);
		// Log.info("prevStart={}", prevStart);
		String path = MessageFormat.format("/projects/{0,number,#}/search/{1,number,#}", projectId, searchId);
		if (start > 0) {
			addURI(searchResult, KEY_PREV, path, prevStart, rows);
		}

		int nextStart = start + rows;
		int size = (Integer) searchResult.get(AbstractSolrServer.KEY_NUMFOUND);
		// Log.info("nextStart={}, size={}", nextStart, size);
		if (nextStart < size) {
			addURI(searchResult, KEY_NEXT, path, start + rows, rows);
		}
	}

	private void addURI(Map<String, Object> searchResult, String key, String prevLink, int start, int rows) {
		UriBuilder builder = UriBuilder//
				.fromPath(prevLink)//
				.scheme(config.getStringSetting("server.scheme", "html"))//
				.host(config.getStringSetting("server.name", "127.0.0.1"))//
				.queryParam("start", start)//
				.queryParam("rows", rows);
		int port = config.getIntSetting("server.port", DEFAULT_PORT);
		if (port != DEFAULT_PORT) {
			builder.port(port);
		}
		searchResult.put(key, builder.build().toString());
	}

}
