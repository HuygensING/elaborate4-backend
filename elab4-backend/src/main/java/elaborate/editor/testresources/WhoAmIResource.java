package elaborate.editor.testresources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nl.knaw.huygens.jaxrstools.resources.UTF8MediaType;
import elaborate.editor.model.orm.User;
import elaborate.editor.resources.AbstractElaborateResource;
import elaborate.jaxrs.Annotations.AuthorizationRequired;

@Path("whoami")
@AuthorizationRequired
public class WhoAmIResource extends AbstractElaborateResource {

  @GET
  @Produces(UTF8MediaType.TEXT_PLAIN)
  public String whoami() {
    User user = getUser();
    if (user != null) {
      return "You are user " + user.getUsername();
    }
    return "no authorization header found";
  }

}
