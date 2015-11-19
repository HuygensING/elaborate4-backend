package elaborate.editor.resources;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2015 Huygens ING
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;

import com.sun.jersey.spi.container.ContainerRequest;

import elaborate.editor.model.AbstractStoredEntity;
import elaborate.editor.model.orm.User;
import elaborate.editor.security.ElaborateSecurityContext;

public abstract class AbstractElaborateResource {
	public AbstractElaborateResource() {
		java.util.logging.Logger.getLogger("com.sun.jersey").setLevel(Level.WARNING);
	}

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
