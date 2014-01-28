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

import elaborate.editor.model.Views;
import elaborate.editor.model.orm.AnnotationType;
import elaborate.editor.model.orm.service.AnnotationTypeService;
import elaborate.editor.resources.AbstractElaborateResource;
import elaborate.editor.resources.orm.wrappers.AnnotationTypeMetadataItemInput;
import elaborate.jaxrs.APIDesc;
import elaborate.jaxrs.Annotations.AuthorizationRequired;

@Path("annotationtypes")
@AuthorizationRequired
public class AnnotationTypeResource extends AbstractElaborateResource {
	@Context
	private AnnotationTypeService annotationTypeService;

	@GET
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns a list of all annotationtypes")
	@JsonView(Views.Minimal.class)
	public ImmutableList<AnnotationType> getAll() {
		return annotationTypeService.getAll();
	}

	@GET
	@Path("{id: [0-9]+}")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@JsonView(Views.Minimal.class)
	@APIDesc("Returns the annotationtype with the given id")
	public AnnotationType getAnnotationType(@PathParam("id") long id) {
		return annotationTypeService.read(id, getUser());
	}

	@POST
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Adds a new AnnotationType")
	public Response create(AnnotationTypeMetadataItemInput input) {
		AnnotationType annotationType = input.getAnnotationType();
		annotationTypeService.create(annotationType, getUser());
		return Response.created(createURI(annotationType)).build();
	}

	@PUT
	@Path("{id: [0-9]+}")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Updates the annotationtype with the given id")
	public void update(@PathParam("id") long id, AnnotationTypeMetadataItemInput input) {
		input.setId(id);
		annotationTypeService.update(input.getAnnotationType(), getUser());
	}

	@DELETE
	@Path("{id: [0-9]+}")
	@APIDesc("Deletes the annotationtype with the given id")
	public void delete(@PathParam("id") long id) {
		annotationTypeService.delete(id, getUser());
	}

	@Path("{id: [0-9]+}/metadataitems")
	public AnnotationTypeMetadataItemResource getMetadataItemResource() {
		return new AnnotationTypeMetadataItemResource(getUser());
	}
}
