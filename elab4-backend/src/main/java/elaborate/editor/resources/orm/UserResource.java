package elaborate.editor.resources.orm;

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

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.service.UserService;
import elaborate.editor.resources.AbstractElaborateResource;
import elaborate.editor.resources.orm.wrappers.UserInput;
import elaborate.jaxrs.APIDesc;
import elaborate.jaxrs.Annotations.AuthorizationRequired;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.jaxrstools.resources.UTF8MediaType;

@Path("users")
@AuthorizationRequired
public class UserResource extends AbstractElaborateResource {
	@Context
	private UserService userService;

	@GET
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns a list of all users")
	public ImmutableList<User> getAll() {
		return userService.getAll();
	}

	@GET
	@Path("{id: [0-9]+}")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the user with the given id")
	public User getUser(@PathParam("id") long id) {
		return userService.read(id);
	}

	@POST
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	// @RolesAllowed("ADMIN")
	@APIDesc("Adds a new user")
	public Response create(UserInput userInput) {
		User user = userInput.getUser();
		userService.create(user, getUser());
		return Response.created(createURI(user)).build();
	}

	@PUT
	@Path("{id: [0-9]+}")
	// @RolesAllowed("ADMIN")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Updates the user with the given id")
	public void update(@PathParam("id") long id, UserInput userInput) {
		userInput.id = id;
		userService.update(userInput.getUser(), getUser());
	}

	@DELETE
	@Path("{id: [0-9]+}")
	// @RolesAllowed("ADMIN")
	@APIDesc("Deletes the user with the given id")
	public void delete(@PathParam("id") long id) {
		userService.delete(id, getUser());
	}

	@GET
	@Path("{id: [0-9]+}/settings")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns a map of the settings for the user with the given id")
	public ImmutableMap<String, String> getUserSettings(@PathParam("id") long id) {
		Log.info("getUserSettings({})", new Object[] { id });
		return userService.getSettings(id);
	}

	@PUT
	@Path("{id: [0-9]+}/settings")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Updates the map of the settings for the user with the given id")
	public void updateUserSettings(@PathParam("id") long id, Map<String, String> newSettings) {
		userService.updateSettings(id, newSettings, getUser());
	}

	@POST
	@Path("{id: [0-9]+}/settings/{field}")
	@Consumes(UTF8MediaType.TEXT_PLAIN)
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Adds or sets a setting for the given field for the user with the given id, returns the new settings")
	public Object setUserSetting(@PathParam("id") long id, @PathParam("field") String field, String value) {
		Log.info("setUserSetting({},{},{})", new Object[] { id, field, value });
		userService.setSetting(id, field, value, getUser());
		return userService.getSettings(id);
	}

}
