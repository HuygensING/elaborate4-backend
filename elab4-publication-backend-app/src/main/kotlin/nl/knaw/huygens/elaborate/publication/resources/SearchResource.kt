package nl.knaw.huygens.elaborate.publication.resources

import java.io.File
import java.net.URI
import javax.ws.rs.*
import javax.ws.rs.core.*
import kotlin.math.max
import com.codahale.metrics.annotation.Timed
import org.apache.commons.lang.StringUtils
import org.slf4j.LoggerFactory
import nl.knaw.huygens.elaborate.publication.solr.SearchService
import nl.knaw.huygens.facetedsearch.AbstractSolrServer.KEY_NUMFOUND
import nl.knaw.huygens.facetedsearch.ElaborateSearchParameters
import nl.knaw.huygens.facetedsearch.SearchData

@Path("/search")
class SearchResource(publicationDir: String) {

    private val searchService = SearchService("$publicationDir/solr", File("$publicationDir/config.json"))

    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    fun doSimpleSearch(@QueryParam("q") @DefaultValue("") term: String): Any {
        val elaborateSearchParameters = ElaborateSearchParameters()
                .setTerm(term)
                .setTextLayers(listOf("Diplomatic"))
        val search: SearchData = searchService.createSearch(elaborateSearchParameters)
        return searchService.getSearchResult(search.id, 0, 1000)
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun createSearch(@Context uriInfo: UriInfo,
                     elaborateSearchParameters: ElaborateSearchParameters
    ): Response {
        LOG.debug("elaborateSearchParameters:{}", elaborateSearchParameters)
        val search: SearchData = searchService.createSearch(elaborateSearchParameters)
        val createdURI = searchUriBuilder(uriInfo).path(search.id.toString()).build()
        LOG.info("createdURI={}", createdURI.toString())
        return Response.created(createdURI).build()
    }

    @GET
    @Timed
    @Path("{search_id:[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getSearchResults(
            @Context uriInfo: UriInfo,
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
                searchService.getSearchResult(searchId, start, rows).toMutableMap()
        if (searchResult.isEmpty()) {
            throw NotFoundException()
        }
        searchResult.addPrevNextURIs(searchUriBuilder(uriInfo, searchId), start, rows)
        return Response.ok(searchResult).build()
    }

    private fun searchUriBuilder(uriInfo: UriInfo, searchId: Long? = null): UriBuilder {
        val proxyURL = System.getenv("PROXY_URL")
        return if (proxyURL == null || proxyURL.isBlank()) {
            uriInfo.requestUriBuilder
        } else {
            val builder = UriBuilder.fromUri(URI.create(proxyURL)).path("search")
            if (searchId != null) {
                builder.path(searchId.toString())
            } else {
                builder
            }
        }
    }

    @GET
    @Timed
    @Path("{search_id:[0-9]+}/allids")
    @Produces(MediaType.APPLICATION_JSON)
    fun getAllSearchResultIds(@PathParam("search_id") searchId: Long): Response {
        val searchResultIds: List<String> = searchService.getAllSearchResultIds(searchId)!!
        return Response.ok(searchResultIds).build()
    }

    private fun MutableMap<String, Any>.addPrevNextURIs(
            uriBuilder: UriBuilder,
            start: Int,
            rows: Int
    ) {
        val prevStart = max(0, start - rows)
        LOG.info("prevStart={}", prevStart)
        if (start > 0) {
            this[KEY_PREV] = createURI(uriBuilder, prevStart, rows)
        }
        val nextStart = start + rows
        val size = this[KEY_NUMFOUND] as Int
        LOG.info("nextStart={}, size={}", nextStart, size)
        if (nextStart < size) {
            this[KEY_NEXT] = createURI(uriBuilder, start + rows, rows)
        }
    }

    private fun createURI(
            uriBuilder: UriBuilder,
            start: Int,
            rows: Int
    ): String =
            uriBuilder
                    .replaceQueryParam("start", start)
                    .replaceQueryParam("rows", rows)
                    .build()
                    .toString()

    companion object {
        private const val KEY_NEXT = "_next"
        private const val KEY_PREV = "_prev"

        private val LOG = LoggerFactory.getLogger(SearchResource::class.java)
    }
}
