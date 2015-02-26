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

import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import nl.knaw.huygens.jaxrstools.exceptions.BadRequestException;
import nl.knaw.huygens.jaxrstools.resources.UTF8MediaType;
import nl.knaw.huygens.security.client.UnauthorizedException;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableMap;

import elaborate.editor.model.SessionService;
import elaborate.editor.model.SessionService.SessionUserInfo;
import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.service.UserService;
import elaborate.editor.resources.orm.PasswordData;
import elaborate.jaxrs.APIDesc;

@Path("sessions")
public class SessionResource extends AbstractElaborateResource {
	@Context
	UserService userService;

	@Inject
	SessionService sessionService;

	@POST
	@Path("login")
	@Consumes(UTF8MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Get an authentication if the username/password combo is valid")
	public Response login(@FormParam("username") String username, @FormParam("password") String password, @FormParam("hsid") String hsid) {
		sessionService.removeExpiredSessions();
		User user = null;
		String token = hsid;
		if (StringUtils.isNotBlank(hsid)) {
			// the Federated way
			try {
				user = sessionService.getSessionUser(hsid);
			} catch (UnauthorizedException e) {
				throw new nl.knaw.huygens.jaxrstools.exceptions.UnauthorizedException(e.getMessage());
			}
			if (user == null) {
				throw new BadRequestException("not a valid hsid");
			}

		} else {
			// the old way
			user = userService.getByUsernamePassword(username, password);
			if (user == null) {
				throw new BadRequestException("no user found with this username/password combination");
			}
			token = sessionService.startSession(user);
		}

		Map<String, Object> content = ImmutableMap.<String, Object> of("token", token, "user", user);
		userService.setUserIsLoggedIn(user);
		return Response.ok(content).build();
	}

	@POST
	@Path("{token}/logout")
	@Consumes(UTF8MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(UTF8MediaType.TEXT_PLAIN)
	@APIDesc("Logout the session with the given token")
	public Response logout(@PathParam("token") String sessionId) {
		User sessionUser;
		try {
			sessionUser = sessionService.getSessionUser(sessionId);
			userService.setUserIsLoggedOut(sessionUser);
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		}
		sessionService.stopSession(sessionId);
		return Response.ok().build();
	}

	@POST
	@Path("passwordresetrequest")
	@APIDesc("Sends a password reset mail for the user with the given emailAddress")
	public void sendResetPasswordMail(String emailAddress) {
		userService.sendResetPasswordMail(emailAddress);
	}

	@POST
	@Path("passwordreset")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("resets the password for the user with the given id")
	public void resetPassword(PasswordData passwordData) {
		userService.resetPassword(passwordData);
	}

	@GET
	@Path("activeusers")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("returns all users in active sessions")
	public Collection<SessionUserInfo> getActiveUsers() {
		return sessionService.getActiveSessionUsersInfo();
	}
}
