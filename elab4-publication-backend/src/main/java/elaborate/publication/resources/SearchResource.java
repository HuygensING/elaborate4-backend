package elaborate.publication.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
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

import nl.knaw.huygens.jaxrstools.resources.UTF8MediaType;

import com.google.common.collect.ImmutableList;

import elaborate.LoggableObject;
import elaborate.publication.solr.AbstractSolrServer;
import elaborate.publication.solr.ElaborateSearchParameters;
import elaborate.publication.solr.SearchData;
import elaborate.publication.solr.SearchService;

@Path("search")
public class SearchResource extends LoggableObject {
  static final String KEY_NEXT = "_next";
  static final String KEY_PREV = "_prev";

  @Context
  SearchService searchService;
  @Context
  ServletContext context;

  @GET
  @Produces(UTF8MediaType.APPLICATION_JSON)
  public Object doSimpleSearch(@QueryParam("q") @DefaultValue("") String term) {
    ElaborateSearchParameters elaborateSearchParameters = new ElaborateSearchParameters().setTerm(term).setTextLayers(ImmutableList.of("Diplomatic"));
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
    searchService.setSolrDir(getSolrDir());
    SearchData search = searchService.createSearch(elaborateSearchParameters);
    return Response.created(createURI(search)).build();
  }

  @GET
  @Path("{search_id}")
  @Produces(UTF8MediaType.APPLICATION_JSON)
  public Response getSearchResults(//
      @PathParam("search_id") long searchId,//
      @QueryParam("start") @DefaultValue("0") int start,//
      @QueryParam("rows") @DefaultValue("100") int rows//
  ) {
    Map<String, Object> searchResult = searchService.getSearchResult(searchId, start, rows);

    addPrevNextURIs(searchResult, searchId, start, rows);

    ResponseBuilder builder = Response.ok(searchResult);
    return builder.build();
  }

  void addPrevNextURIs(Map<String, Object> searchResult, long searchId, int start, int rows) {
    int prevStart = Math.max(0, start - rows);
    LOG.info("prevStart={}", prevStart);
    String path = MessageFormat.format("/search/{0,number,#}", searchId);
    if (start > 0) {
      addURI(searchResult, KEY_PREV, path, prevStart, rows);
    }

    int nextStart = start + rows;
    int size = (Integer) searchResult.get(AbstractSolrServer.KEY_NUMFOUND);
    LOG.info("nextStart={}, size={}", nextStart, size);
    if (nextStart < size) {
      addURI(searchResult, KEY_NEXT, path, start + rows, rows);
    }
  }

  private void addURI(Map<String, Object> searchResult, String key, String prevLink, int start, int rows) {
    UriBuilder builder = UriBuilder//
        .fromPath(prevLink)//
        //    .scheme(config.getStringSetting("server.scheme", "html"))//
        //    .host(config.getStringSetting("server.name", "127.0.0.1"))//
        .queryParam("start", start)//
        .queryParam("rows", rows);
    //    int port = config.getIntSetting("server.port", DEFAULT_PORT);
    //    if (port != DEFAULT_PORT) {
    //      builder.port(port);
    //    }
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
