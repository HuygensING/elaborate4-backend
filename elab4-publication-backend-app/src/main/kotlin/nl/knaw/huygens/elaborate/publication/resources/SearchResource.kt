package nl.knaw.huygens.elaborate.publication.resources

import com.codahale.metrics.annotation.Timed
import nl.knaw.huygens.elaborate.publication.solr.SearchService
import nl.knaw.huygens.facetedsearch.AbstractSolrServer
import nl.knaw.huygens.facetedsearch.ElaborateSearchParameters
import nl.knaw.huygens.facetedsearch.SearchData
import org.apache.commons.lang.StringUtils
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URISyntaxException
import java.text.MessageFormat
import javax.servlet.ServletContext
import javax.ws.rs.*
import javax.ws.rs.core.*

@Path("/search")
class SearchResource {

    @Context
    var searchService: SearchService? = null

    @Context
    var context: ServletContext? = null

    @Context
    var uri: UriInfo? = null

    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    fun doSimpleSearch(@QueryParam("q") @DefaultValue("") term: String): Any {
        val elaborateSearchParameters = ElaborateSearchParameters()
            .setTerm(term)
            .setTextLayers(listOf("Diplomatic"))
        searchService!!.solrDir = solrDir
        val search: SearchData = searchService!!.createSearch(elaborateSearchParameters)
        return searchService!!.getSearchResult(search.id, 0, 1000)
    }

    private val solrDir: String
        get() = context!!.getRealPath("solr")

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun createSearch(elaborateSearchParameters: ElaborateSearchParameters): Response {
        LOG.debug("elaborateSearchParameters:{}", elaborateSearchParameters)
        searchService!!.solrDir = solrDir
        val search: SearchData = searchService!!.createSearch(elaborateSearchParameters)
        val createdURI = uri!!.requestUriBuilder.build(search.id)
        LOG.info("createdURI={}", createdURI.toString())
        return Response.created(createURI(search)).build()
    }

    @GET
    @Timed
    @Path("{search_id:[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getSearchResults(
        @PathParam("search_id") searchId: Long,
        @QueryParam("start") @DefaultValue("0") startString: String,
        @QueryParam("rows") @DefaultValue("100") rowsString: String
    ): Response {
        if (!StringUtils.isNumeric(startString) || !StringUtils.isNumeric(rowsString)) {
            throw BadRequestException()
        }
        val start = startString.toInt()
        val rows = rowsString.toInt()
        val searchResult: MutableMap<String, Any> =
            searchService!!.getSearchResult(searchId, start, rows).toMutableMap()
        addPrevNextURIs(searchResult, searchId, start, rows)
        return Response.ok(searchResult).build()
    }

    @GET
    @Timed
    @Path("{search_id:[0-9]+}/allids")
    @Produces(MediaType.APPLICATION_JSON)
    fun getAllSearchResultIds(@PathParam("search_id") searchId: Long): Response {
        val searchResultIds: List<String> = searchService!!.getAllSearchResultIds(searchId)!!
        return Response.ok(searchResultIds).build()
    }

    private fun addPrevNextURIs(
        searchResult: MutableMap<String, Any>,
        searchId: Long,
        start: Int,
        rows: Int
    ) {
        val prevStart = Math.max(0, start - rows)
        LOG.info("prevStart={}", prevStart)
        val path = MessageFormat.format(SEARCH_PATH_TEMPLATE, searchId)
        if (start > 0) {
            addURI(searchResult, KEY_PREV, path, prevStart, rows)
        }
        val nextStart = start + rows
        val size = searchResult[AbstractSolrServer.KEY_NUMFOUND] as Int
        LOG.info("nextStart={}, size={}", nextStart, size)
        if (nextStart < size) {
            addURI(searchResult, KEY_NEXT, path, start + rows, rows)
        }
    }

    private fun addURI(
        searchResult: MutableMap<String, Any>,
        key: String,
        prevLink: String,
        start: Int,
        rows: Int
    ) {
        val builder: UriBuilder = UriBuilder.fromUri(searchService!!.baseURL + "/api/")
            .path(prevLink)
            .queryParam("start", start)
            .queryParam("rows", rows)
        searchResult[key] = builder.build().toString()
    }

    private fun createURI(e: SearchData): URI? {
        var uri: URI?
        try {
            uri = URI(e.id.toString())
        } catch (ue: URISyntaxException) {
            uri = null
            ue.printStackTrace()
        }
        return uri
    }

    companion object {
        private const val SEARCH_PATH_TEMPLATE = "/search/{0,number,#}"
        private const val KEY_NEXT = "_next"
        private const val KEY_PREV = "_prev"

        private val LOG = LoggerFactory.getLogger(SearchResource::class.java)
    }
}
