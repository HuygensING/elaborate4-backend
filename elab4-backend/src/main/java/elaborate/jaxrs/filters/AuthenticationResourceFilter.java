package elaborate.jaxrs.filters;

import java.util.List;

import javax.ws.rs.core.SecurityContext;

import nl.knaw.huygens.LoggableObject;
import nl.knaw.huygens.jaxrstools.exceptions.UnauthorizedException;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;

import elaborate.editor.model.SessionService;

public class AuthenticationResourceFilter extends LoggableObject implements ResourceFilter, ContainerRequestFilter {
  public static final String SCHEME = "SimpleAuth";
  public static final String HEADER = "Authorization";

  SessionService sessionService = SessionService.instance();

  @Override
  public ContainerRequest filter(ContainerRequest request) {
    String authentication = request.getHeaderValue(HEADER);
    //    LOG.info("authentication={}", authentication);
    if (StringUtils.isNotBlank(authentication)) {
      List<String> parts = Lists.newArrayList(Splitter.on(" ").split(authentication));
      if (parts.size() == 2 && SCHEME.equals(parts.get(0))) {
        String key = parts.get(1);
        SecurityContext securityContext = sessionService.getSecurityContext(key);
        if (securityContext != null) {
          request.setSecurityContext(securityContext);
          return request;
        }
      }
    }
    throw new UnauthorizedException("No valid " + HEADER + " header in request");
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