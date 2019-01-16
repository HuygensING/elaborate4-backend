package elaborate.editor.providers;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2019 Huygens ING
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
			// session = sessionRepository.findOne(sessionId);

			// Load associated user from session
			// if (session != null) {
			// // user = userRepository.findOne(session.getUserId());
			// }
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
