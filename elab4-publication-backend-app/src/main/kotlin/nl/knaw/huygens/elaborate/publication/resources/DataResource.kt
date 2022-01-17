package nl.knaw.huygens.elaborate.publication.resources

import java.io.File
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import nl.knaw.huygens.elaborate.publication.AppConfig

@Path("/data")
class DataResource(config: AppConfig) {

    private val dataDir = config.dataDir

    @GET
    @Path("{baseName:.+}.json")
    @Produces(MediaType.APPLICATION_JSON)
    fun getJsonFile(@PathParam("baseName") baseName: String): Any {
        val filePath = "${dataDir}/${baseName}.json"
        val file = File(filePath)
        return if (file.exists()) {
            val stream = file.inputStream().readBytes().toString(Charsets.UTF_8)
            Response.ok(stream).build()
        } else {
            throw NotFoundException()
        }
    }
}