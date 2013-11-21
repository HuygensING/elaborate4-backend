package elaborate.editor.resources.orm;

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

import nl.knaw.huygens.jaxrstools.resources.UTF8MediaType;

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

@AuthorizationRequired
@Singleton
public class AnnotationTypeMetadataItemResource extends AbstractElaborateResource {
	@Context
	private final User user;
	private final AnnotationTypeMetadataItemService annotationTypeMetadataItemService = AnnotationTypeMetadataItemService.instance();

	public AnnotationTypeMetadataItemResource(User user) {
		this.user = user;
	}

	@GET
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@JsonView(Views.Minimal.class)
	@APIDesc("Returns the metatada for the annotationtypeMetadataItem with the given id")
	public ImmutableList<AnnotationTypeMetadataItem> getAnnotationTypeMetadataItems(@PathParam("id") long id) {
		return annotationTypeMetadataItemService.getAll(id);
	}

	@GET
	@Path("{id}")
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

	@PUT
	@Path("{id}")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Updates the annotationtypeMetadataItem with the given id")
	public void update(@PathParam("id") long id, AnnotationTypeMetadataItemInput input) {
		input.setId(id);
		annotationTypeMetadataItemService.update(input, getUser());
	}

	@DELETE
	@Path("{id}")
	@APIDesc("Deletes the annotationtypeMetadataItem with the given id")
	public void delete(@PathParam("id") long id) {
		annotationTypeMetadataItemService.delete(id, getUser());
	}

}
