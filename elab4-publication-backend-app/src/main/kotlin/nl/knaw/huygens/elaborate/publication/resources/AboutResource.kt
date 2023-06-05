package nl.knaw.huygens.elaborate.publication.resources

import java.io.File
import java.util.*
import javax.servlet.ServletContext
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.UriInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import nl.knaw.huygens.elaborate.publication.AppConfig

@Path("/about")
class AboutResource(private val config: AppConfig) {
    val log: Logger = LoggerFactory.getLogger(AboutResource::class.java)

    private val stream = File("${config.publicationDir}/about.properties").inputStream()

    private val propertyResourceBundle: PropertyResourceBundle by lazy {
        PropertyResourceBundle(stream)
    }

    private val properties = listOf("commitId", "buildDate", "version", "scmBranch", "publishdate")

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    fun get(
        @Context context: ServletContext,
        @Context uriInfo: UriInfo
    ): Any {
        val data: MutableMap<String, String> = mutableMapOf()
        val propertyKeys = propertyResourceBundle.keys.toList()
        for (field: String in properties) {
            if (propertyKeys.contains(field)) {
                data[field] = getProperty(field)
            }
        }
        data["serverInfo"] = context.serverInfo
        data["contextPath"] = context.contextPath
        data["baseUri"] = uriInfo.baseUri.toString()
        data["projectName"] = config.projectName
        data["publicationDir"] = config.publicationDir
        data["PROXY_URL"] = System.getenv("PROXY_URL") ?: "not defined"
        return data
    }

    @Synchronized
    private fun getProperty(key: String): String =
        propertyResourceBundle.getString(key)
}