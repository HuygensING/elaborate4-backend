package elaborate.editor.resources;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;

import nl.knaw.huygens.LoggableObject;

import com.sun.jersey.spi.container.ContainerRequest;

import elaborate.editor.model.AbstractStoredEntity;
import elaborate.editor.model.orm.User;
import elaborate.editor.security.ElaborateSecurityContext;

public abstract class AbstractElaborateResource extends LoggableObject {

  @Context
  Request request;

  public User getUser() {
    User user = null;
    ContainerRequest cr = (ContainerRequest) request;
    SecurityContext securityContext = cr.getSecurityContext();
    if (securityContext instanceof ElaborateSecurityContext) {
      ElaborateSecurityContext esc = (ElaborateSecurityContext) securityContext;
      if (esc != null) {
        user = esc.getUser();
      }
    }
    return user;
  }

  protected URI createURI(AbstractStoredEntity<?> e) {
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
