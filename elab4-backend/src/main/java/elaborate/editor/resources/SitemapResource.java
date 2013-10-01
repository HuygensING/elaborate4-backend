package elaborate.editor.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import nl.knaw.huygens.jaxrstools.resources.UTF8MediaType;
import elaborate.editor.model.Sitemap;
import elaborate.jaxrs.APIDesc;

@Path("api")
public class SitemapResource extends AbstractElaborateResource {

  @GET
  @Produces(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("Generates a structured sitemap.")
  public Sitemap getSitemap(@Context Application app) {
    return new Sitemap(app);
  }

}
