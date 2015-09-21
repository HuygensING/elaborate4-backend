package elaborate.editor.model;

import com.sun.jersey.api.client.Client;

import elaborate.editor.config.Configuration;
import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.service.UserService;

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

import nl.knaw.huygens.security.client.HuygensAuthorizationHandler;
import nl.knaw.huygens.security.client.UnauthorizedException;
import nl.knaw.huygens.security.client.model.SecurityInformation;

public class SecurityWrapper {
	static Configuration config = Configuration.instance();
	static UserService userservice = UserService.instance();

	static Client client = new Client();
	static HuygensAuthorizationHandler hah = new HuygensAuthorizationHandler(client, config.getSetting("security.hss.url"), config.getSetting("security.hss.credentials"));

	public static Session createSession(String sessionToken) throws UnauthorizedException {
		SecurityInformation securityInformation = hah.getSecurityInformation(sessionToken);
		String emailAddress = securityInformation.getEmailAddress();
		User user = userservice.getByEmail(emailAddress);
		if (user == null) {
			throw new UnauthorizedException("The email address given by the securityservice (" + emailAddress + ") is not registered to a user in elaborate");
		}
		return new Session(user.getId());
	}

	public static void delete(String sessionId) {
		try {
			hah.logout(sessionId);
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		}
	}
}
