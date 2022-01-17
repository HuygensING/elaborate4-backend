package nl.knaw.huygens.elaborate.publication.resources

import nl.knaw.huygens.elaborate.publication.AppConfig
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.*
import javax.servlet.ServletContext
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.UriInfo

@Path("/about")
class AboutResource(private val config: AppConfig) {
    private var propertyResourceBundle: PropertyResourceBundle? = null
    val log = LoggerFactory.getLogger(AboutResource::class.java)

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    fun get(@Context context: ServletContext, @Context uriInfo: UriInfo): Any {
        val data: MutableMap<String, String> = mutableMapOf()
        for (field: String in listOf("commitId", "buildDate", "version", "scmBranch", "publishdate")) {
            data[field] = getProperty(field)
        }
        data["serverInfo"] = context.serverInfo
        data["contextPath"] = context.contextPath
        data["baseUri"] = uriInfo.baseUri.toString()
        data["absolutePath"] = uriInfo.absolutePath.toString()
        data["projectName"] = config.projectName
        data["dataDir"] = config!!.dataDir
        data["solrDir"] = config!!.solrDir
        return data
    }


    @Synchronized
    private fun getProperty(key: String): String {
        if (propertyResourceBundle == null) {
            try {
                propertyResourceBundle = PropertyResourceBundle(
                    Thread.currentThread()
                        .contextClassLoader
                        .getResourceAsStream("about.properties")
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return propertyResourceBundle!!.getString(key)
    }
}