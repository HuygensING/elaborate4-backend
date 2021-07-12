package elaborate.jaxrs.security;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2021 Huygens ING
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
		return (null != session) && session.isSecure();
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
			return false;
		}

	}
}
