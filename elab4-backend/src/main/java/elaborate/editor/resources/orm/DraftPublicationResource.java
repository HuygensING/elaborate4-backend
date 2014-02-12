package elaborate.editor.resources.orm;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2014 Huygens ING
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

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import nl.knaw.huygens.jaxrstools.resources.UTF8MediaType;

import com.sun.jersey.spi.resource.Singleton;

import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.service.ProjectService;
import elaborate.editor.publish.Publication;
import elaborate.editor.resources.AbstractElaborateResource;
import elaborate.jaxrs.APIDesc;
import elaborate.jaxrs.Annotations.AuthorizationRequired;

@AuthorizationRequired
@Singleton
public class DraftPublicationResource extends AbstractElaborateResource {
  private final ProjectService projectService;
  private final User user;

  public DraftPublicationResource(User user, ProjectService projectService) {
    this.user = user;
    this.projectService = projectService;
  }

  @POST
  @APIDesc("begin the publication of the project")
  public Response startPublication(@PathParam("project_id") long project_id) {
    Publication.Status status = projectService.createPublicationStatus(project_id, user);
    return Response.created(status.getURI()).build();
  }

  @GET
  @Path("{status_id}")
  @Produces(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("Returns the status of the publication with the given status_id")
  public Publication.Status getPublicationStatus(@PathParam("status_id") String status_id) {
    return projectService.getPublicationStatus(status_id);
  }

}
