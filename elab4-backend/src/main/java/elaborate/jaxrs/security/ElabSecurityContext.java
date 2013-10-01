package elaborate.jaxrs.security;

import java.security.Principal;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public class ElabSecurityContext implements javax.ws.rs.core.SecurityContext {

  private final SessionUser user;
  private final Session session;

  public ElabSecurityContext(Session session, SessionUser user) {
    this.session = session;
    this.user = user;
  }

  @Override
  public String getAuthenticationScheme() {
    return SecurityContext.BASIC_AUTH;
  }

  @Override
  public Principal getUserPrincipal() {
    return user;
  }

  @Override
  public boolean isSecure() {
    return (null != session) ? session.isSecure() : false;
  }

  @Override
  public boolean isUserInRole(String role) {
    if (null == session || !session.isActive()) {
      Response denied = Response.status(Response.Status.FORBIDDEN).entity("Permission Denied").build();
      throw new WebApplicationException(denied);
    }

    try {
      return user.getRoles().contains(SessionUser.Role.valueOf(role));
    } catch (Exception e) {

    }

    return false;
  }
}