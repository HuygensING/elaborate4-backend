package elaborate.editor.resources;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;

import com.sun.jersey.spi.container.ContainerRequest;

import elaborate.editor.model.AbstractStoredEntity;
import elaborate.editor.model.ElaborateSecurityContext;
import elaborate.editor.model.LoggableObject;
import elaborate.editor.model.orm.User;

public abstract class AbstractElaborateResource extends LoggableObject {

  @Context
  Request request;

  public User getUser() {
    User user = null;
    ContainerRequest cr = (ContainerRequest) request;
    ElaborateSecurityContext esc = (ElaborateSecurityContext) cr.getSecurityContext();
    if (esc != null) {
      user = esc.getUser();
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
