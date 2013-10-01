package elaborate.editor.model;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

import elaborate.editor.model.orm.User;
import elaborate.jaxrs.filters.AuthenticationResourceFilter;

public class ElaborateSecurityContext implements SecurityContext {
  private final User user;

  public ElaborateSecurityContext(User user) {
    this.user = user;
  }

  public User getUser() {
    return user;
  }

  @Override
  public Principal getUserPrincipal() {
    return new Principal() {
      @Override
      public String getName() {
        return user.getUsername();
      }
    };
  }

  @Override
  public boolean isUserInRole(String role) {
    return user.hasRole(role);
  }

  @Override
  public boolean isSecure() {
    return false;
  }

  @Override
  public String getAuthenticationScheme() {
    return AuthenticationResourceFilter.SCHEME;
  }

}
