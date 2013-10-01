package elaborate.editor.providers;

import javax.ws.rs.ext.Provider;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;

import elaborate.jaxrs.security.ElabSecurityContext;
import elaborate.jaxrs.security.Session;
import elaborate.jaxrs.security.SessionUser;

@Provider
public class SecurityContextFilterFactory implements ResourceFilter, ContainerRequestFilter {

  @Override
  public ContainerRequest filter(ContainerRequest request) {
    // Get session id from request header
    final String sessionId = request.getHeaderValue("session-id");

    SessionUser user = null;
    Session session = null;

    if (sessionId != null && sessionId.length() > 0) {
      // Load session object from repository
      //      session = sessionRepository.findOne(sessionId);

      // Load associated user from session
      //      if (session != null) {
      //        //        user = userRepository.findOne(session.getUserId());
      //      }
    }

    request.setSecurityContext(new ElabSecurityContext(session, user));
    return request;
  }

  @Override
  public ContainerRequestFilter getRequestFilter() {
    return this;
  }

  @Override
  public ContainerResponseFilter getResponseFilter() {
    return null;
  }
}