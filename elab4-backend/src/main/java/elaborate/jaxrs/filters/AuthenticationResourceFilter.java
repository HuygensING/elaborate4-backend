package elaborate.jaxrs.filters;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2018 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.List;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;

import elaborate.editor.model.SessionService;
import nl.knaw.huygens.jaxrstools.exceptions.UnauthorizedException;

public class AuthenticationResourceFilter implements ResourceFilter, ContainerRequestFilter {
	public static final String HEADER = "Authorization";

	SessionService sessionService = SessionService.instance();

	@Override
	public ContainerRequest filter(ContainerRequest request) {
		// OPTIONS calls don't need authorization, apparently
		if ("OPTIONS".equals(request.getMethod())) {
			return request;
		}

		// Authentication via header or cookie
		String authentication = request.getHeaderValue(HEADER);
		if (StringUtils.isBlank(authentication)) {
			Cookie cookie = request.getCookies().get(HEADER);
			if (cookie != null) {
				authentication = cookie.getValue();
			}
		}
		// Log.info("authentication={}", authentication);
		if (StringUtils.isNotBlank(authentication)) {
			List<String> parts = Lists.newArrayList(Splitter.on(" ").split(authentication));
			if (parts.size() == 2) {
				String scheme = parts.get(0);
				String key = parts.get(1);
				try {
					SecurityContext securityContext = sessionService.getSecurityContext(scheme, key);
					if (securityContext != null) {
						request.setSecurityContext(securityContext);
						return request;
					}
				} catch (nl.knaw.huygens.security.client.UnauthorizedException e) {
					throw new UnauthorizedException(e.getMessage());
				}
			}
		} else {

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
