package nl.knaw.huygens.elaborate.publication.resources

import javax.ws.rs.GET
import javax.ws.rs.Path

@Path("/")
class RootResource {
    @GET
    fun get(): Any {
        return "elaborate4-publication-backend"
    }
}