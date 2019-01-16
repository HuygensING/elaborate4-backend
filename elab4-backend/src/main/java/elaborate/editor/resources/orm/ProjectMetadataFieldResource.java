package elaborate.editor.resources.orm;

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

import java.util.List;

import javax.annotation.security.RolesAllowed;
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

import com.fasterxml.jackson.annotation.JsonView;

import elaborate.editor.model.Views;
import elaborate.editor.model.orm.ProjectMetadataField;
import elaborate.editor.model.orm.service.ProjectMetadataFieldService;
import elaborate.editor.resources.AbstractElaborateResource;
import elaborate.jaxrs.APIDesc;
import elaborate.jaxrs.Annotations.AuthorizationRequired;
import nl.knaw.huygens.jaxrstools.resources.UTF8MediaType;

@Path("projectmetadatafields")
@AuthorizationRequired
public class ProjectMetadataFieldResource extends AbstractElaborateResource {
	@Context
	private ProjectMetadataFieldService projectMetadataFieldService;

	@GET
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns a list of all projectmetadatafields")
	@JsonView(Views.Minimal.class)
	public List<ProjectMetadataField> getAll() {
		return projectMetadataFieldService.getAll(getUser());
	}

	@GET
	@Path("{field_id: [0-9]+}")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the projectmetadatafield with the given field_id")
	@JsonView(Views.Minimal.class)
	public ProjectMetadataField getProjectMetadataField(@PathParam("field_id") long field_id) {
		return projectMetadataFieldService.read(field_id);
	}

	@POST
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@RolesAllowed("ADMIN")
	@APIDesc("Adds a new projecmetadatafield")
	public Response createProjectMetadataField(ProjectMetadataField pmField) {
		projectMetadataFieldService.create(pmField, getUser());
		return Response.created(createURI(pmField)).build();
	}

	@PUT
	@Path("{field_id: [0-9]+}")
	@RolesAllowed("ADMIN")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Updates the projectmetadatafield with the given field_id")
	public void updateProject(ProjectMetadataField pmField) {
		projectMetadataFieldService.update(pmField, getUser());
	}

	@DELETE
	@Path("{field_id: [0-9]+}")
	@RolesAllowed("ADMIN")
	@APIDesc("Deletes the projectmetadatafield with the given field_id")
	public void deleteProject(@PathParam("field_id") long field_id) {
		projectMetadataFieldService.delete(field_id, getUser());
	}

}
