package elaborate.editor.resources.orm;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2020 Huygens ING
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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.ImmutableList;
import com.sun.jersey.spi.resource.Singleton;

import elaborate.editor.model.Views;
import elaborate.editor.model.orm.AnnotationTypeMetadataItem;
import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.service.AnnotationTypeMetadataItemService;
import elaborate.editor.resources.AbstractElaborateResource;
import elaborate.editor.resources.orm.wrappers.AnnotationTypeMetadataItemInput;
import elaborate.jaxrs.APIDesc;
import elaborate.jaxrs.Annotations.AuthorizationRequired;
import nl.knaw.huygens.jaxrstools.resources.UTF8MediaType;

@AuthorizationRequired
@Singleton
public class AnnotationTypeMetadataItemResource extends AbstractElaborateResource {
  private final AnnotationTypeMetadataItemService annotationTypeMetadataItemService = AnnotationTypeMetadataItemService.instance();

	public AnnotationTypeMetadataItemResource(User user) {
	}

	@GET
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@JsonView(Views.Minimal.class)
	@APIDesc("Returns the metatada for the annotationtypeMetadataItem with the given id")
	public ImmutableList<AnnotationTypeMetadataItem> getAnnotationTypeMetadataItems(@PathParam("id") long id) {
		return annotationTypeMetadataItemService.getAll(id);
	}

	@GET
	@Path("{id: [0-9]+}")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@JsonView(Views.Minimal.class)
	@APIDesc("Returns the annotationtypeMetadataItem with the given id")
	public AnnotationTypeMetadataItem getAnnotationType(@PathParam("id") long id) {
		return annotationTypeMetadataItemService.read(id, getUser());
	}

	@POST
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Adds a new AnnotationTypeMetadataItem")
	public Response create(AnnotationTypeMetadataItemInput input) {
		AnnotationTypeMetadataItem created = annotationTypeMetadataItemService.create(input, getUser());
		return Response.created(createURI(created)).build();
	}

	// @PUT
	// @Path("{id: [0-9]+}")
	// @Consumes(UTF8MediaType.APPLICATION_JSON)
	// @APIDesc("Updates the annotationtypeMetadataItem with the given id")
	// public void update(@PathParam("id") long id, AnnotationTypeMetadataItemInput input) {
	// input.setId(id);
	// annotationTypeMetadataItemService.update(input, getUser());
	// }

	@DELETE
	@Path("{id: [0-9]+}")
	@APIDesc("Deletes the annotationtypeMetadataItem with the given id")
	public void delete(@PathParam("id") long id) {
		annotationTypeMetadataItemService.delete(id, getUser());
	}

}
