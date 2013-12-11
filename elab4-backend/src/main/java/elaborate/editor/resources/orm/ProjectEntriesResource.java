package elaborate.editor.resources.orm;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import nl.knaw.huygens.jaxrstools.resources.UTF8MediaType;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;
import com.sun.jersey.spi.resource.Singleton;

import elaborate.editor.model.AnnotationInputWrapper;
import elaborate.editor.model.Views;
import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.Facsimile;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.Transcription;
import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.service.PrevNext;
import elaborate.editor.model.orm.service.ProjectEntryService;
import elaborate.editor.model.orm.service.ProjectService;
import elaborate.editor.model.orm.service.TranscriptionService;
import elaborate.editor.resources.AbstractElaborateResource;
import elaborate.editor.resources.orm.wrappers.TranscriptionWrapper;
import elaborate.jaxrs.APIDesc;
import elaborate.jaxrs.Annotations.AuthorizationRequired;

@AuthorizationRequired
@Singleton
public class ProjectEntriesResource extends AbstractElaborateResource {
	private final ProjectService projectService;
	private final ProjectEntryService projectEntryService = ProjectEntryService.instance();
	private final TranscriptionService transcriptionService = TranscriptionService.instance();
	private final User user;

	public ProjectEntriesResource(User user, ProjectService projectService) {
		this.user = user;
		this.projectService = projectService;
	}

	@GET
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the entries of the project with the given project_id")
	@JsonView(Views.Extended.class)
	public Collection<ProjectEntry> getProjectEntries(@PathParam("project_id") long project_id) {
		return projectService.getProjectEntries(project_id, user);
	}

	@POST
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@RolesAllowed("USER")
	@APIDesc("Adds an entry to the project with the given project_id")
	public Response createProjectEntry(@PathParam("project_id") long project_id, ProjectEntry projectEntry) {
		ProjectEntry created = projectService.createProjectEntry(project_id, projectEntry, user);
		return Response.created(createURI(created)).build();
	}

	@GET
	@Path("{entry_id}")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the projectentry with the given entry_id")
	@JsonView(Views.Extended.class)
	public ProjectEntry getProjectEntry(@PathParam("entry_id") long entry_id) {
		return projectEntryService.read(entry_id, user);
	}

	@GET
	@Path("{entry_id}/prevnext")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the id of the previous projectentry")
	@JsonView(Views.Extended.class)
	public PrevNext getPreviousProjectEntryId(@PathParam("entry_id") long entry_id) {
		return projectEntryService.getPrevNextProjectEntryIds(entry_id);
	}

	@PUT
	@Path("{entry_id}")
	@RolesAllowed("ADMIN")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Updates the projectentry with the given entry_id")
	public void updateProjectEntry(@PathParam("entry_id") long entry_id, ProjectEntry projectEntry) {
		projectEntryService.update(entry_id, projectEntry, user);
	}

	@DELETE
	@Path("{entry_id}")
	@RolesAllowed("ADMIN")
	@APIDesc("Deletes the projectentry with the given entry_id")
	public void deleteProjectEntry(@PathParam("entry_id") long entry_id) {
		projectEntryService.delete(entry_id, user);
	}

	/* entry settings */
	@GET
	@Path("{entry_id}/settings")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the settings of the project entry with the given entry_id")
	public Map<String, String> getProjectEntrySettings(@PathParam("project_id") long project_id, @PathParam("entry_id") long entry_id) {
		return projectEntryService.getProjectEntrySettings(entry_id, user);
	}

	@PUT
	@Path("{entry_id}/settings")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Updates the settings of the project entry with the given entry_id")
	public void updateProjectEntrySettings(@PathParam("project_id") long project_id, @PathParam("entry_id") long entry_id, Map<String, String> projectEntrySettings) {
		projectEntryService.updateProjectEntrySettings(project_id, entry_id, projectEntrySettings, user);
	}

	/* facsimiles */
	@GET
	@Path("{entry_id}/facsimiles")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the facsimiles of the project entry with the given entry_id")
	@JsonView(Views.Minimal.class)
	public Collection<Facsimile> getFacsimiles(@PathParam("entry_id") long entry_id) {
		return projectEntryService.getFacsimiles(entry_id, user);
	}

	@POST
	@Path("{entry_id}/facsimiles")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@RolesAllowed("USER")
	@APIDesc("Adds a facsimile to the project entry with the given entry_id")
	public Response addFacsimile(@PathParam("entry_id") long entry_id, Facsimile input) {
		Facsimile facsimile = projectEntryService.addFacsimile(entry_id, input, user);
		return Response.created(createURI(facsimile)).build();
	}

	@GET
	@Path("{entry_id}/facsimiles/{facsimile_id}")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the facsimile with the given facsimile_id of the project entry with the given entry_id of the project with the given project_id")
	@JsonView(Views.Minimal.class)
	public Facsimile getFacsimile(@PathParam("project_id") long project_id, @PathParam("facsimile_id") long facsimile_id) {
		Facsimile facsimile = projectEntryService.readFacsimile(project_id, facsimile_id, user);
		return facsimile;
	}

	@PUT
	@Path("{entry_id}/facsimiles/{facsimile_id}")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Updates the facsimile with the given facsimile_id of the project entry with the given entry_id of the project with the given project_id")
	public void updateFacsimile(@PathParam("project_id") long project_id, @PathParam("facsimile_id") long facsimile_id, Facsimile newFacsimileData) {
		projectEntryService.updateFacsimile(project_id, facsimile_id, newFacsimileData, user);
	}

	@DELETE
	@Path("{entry_id}/facsimiles/{facsimile_id}")
	@APIDesc("Deletes the facsimile with the given facsimile_id of the project entry with the given entry_id of the project with the given project_id, and all its dependencies (annotations")
	public void deleteFacsimile(@PathParam("project_id") long project_id, @PathParam("facsimile_id") long facsimile_id) {
		projectEntryService.deleteFacsimile(project_id, facsimile_id, user);
	}

	/* transcriptions*/
	@GET
	@Path("{entry_id}/transcriptions")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the transcriptions of the project entry with the given entry_id")
	public Collection<TranscriptionWrapper> getTranscriptions(@PathParam("entry_id") long entry_id) {
		Collection<Transcription> transcriptions = projectEntryService.getTranscriptions(entry_id, user);
		List<TranscriptionWrapper> wrappers = Lists.newArrayListWithCapacity(transcriptions.size());
		for (Transcription transcription : transcriptions) {
			wrappers.add(new TranscriptionWrapper(transcription));
		}
		return wrappers;
	}

	@POST
	@Path("{entry_id}/transcriptions")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@RolesAllowed("USER")
	@APIDesc("Adds a transcription to the project entry with the given entry_id")
	public Response addTranscription(@PathParam("entry_id") long entry_id, TranscriptionWrapper transcriptionWrapper) {
		Transcription transcription = projectEntryService.addTranscription(entry_id, transcriptionWrapper, user);
		return Response.created(createURI(transcription)).build();
	}

	@GET
	@Path("{entry_id}/transcriptions/{transcription_id}")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the transcription with the given transcription_id of the project entry with the given entry_id of the project with the given project_id")
	public TranscriptionWrapper getTranscription(@PathParam("project_id") long project_id, @PathParam("transcription_id") long transcription_id) {
		Transcription transcription = transcriptionService.read(project_id, transcription_id, user);
		return new TranscriptionWrapper(transcription);
	}

	@PUT
	@Path("{entry_id}/transcriptions/{transcription_id}")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Updates the transcription with the given transcription_id of the project entry with the given entry_id of the project with the given project_id")
	public void updateTranscription(@PathParam("project_id") long project_id, @PathParam("transcription_id") long transcription_id, TranscriptionWrapper transcriptionWrapper) {
		LOG.info("transcription in={}", transcriptionWrapper);
		transcriptionService.update(project_id, transcription_id, transcriptionWrapper, user);
	}

	@DELETE
	@Path("{entry_id}/transcriptions/{transcription_id}")
	@APIDesc("Deletes the transcription with the given transcription_id of the project entry with the given entry_id of the project with the given project_id, and all its dependencies (annotations")
	public void deleteTranscription(@PathParam("project_id") long project_id, @PathParam("transcription_id") long transcription_id) {
		transcriptionService.delete(project_id, transcription_id, user);
	}

	/* annotations*/
	@GET
	@Path("{entry_id}/transcriptions/{transcription_id}/annotations")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the annotations of the transcription with the given transcription_id of the project entry with the given entry_id")
	public Collection<Annotation> getAnnotations(@PathParam("project_id") long project_id, @PathParam("transcription_id") long transcription_id) {
		return transcriptionService.getAnnotations(project_id, transcription_id, user);
	}

	@POST
	@Path("{entry_id}/transcriptions/{transcription_id}/annotations")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Add an annotation to the transcription with the given transcription_id of the project entry with the given entry_id")
	public Response addAnnotation(@PathParam("project_id") long project_id, @PathParam("transcription_id") long transcription_id, AnnotationInputWrapper annotationInput) {
		Annotation created = transcriptionService.addAnnotation(project_id, transcription_id, annotationInput, user);
		return Response.created(createURI(created)).build();
	}

	@GET
	@Path("{entry_id}/transcriptions/{transcription_id}/annotations/{annotation_id}")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the annotation with the given annotation_id of the transcription with the given transcription_id of the project entry with the given entry_id")
	public Annotation getAnnotation(@PathParam("project_id") long project_id, @PathParam("annotation_id") long annotation_id) {
		return transcriptionService.readAnnotation(project_id, annotation_id, user);
	}

	@PUT
	@Path("{entry_id}/transcriptions/{transcription_id}/annotations/{annotation_id}")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("updates the annotation with the given annotation_id of the transcription with the given transcription_id of the project entry with the given entry_id")
	public void updateAnnotation(@PathParam("project_id") long project_id, @PathParam("annotation_id") long annotation_id, AnnotationInputWrapper annotationInput) {
		transcriptionService.updateAnnotation(project_id, annotation_id, annotationInput, user);
	}

	@DELETE
	@Path("{entry_id}/transcriptions/{transcription_id}/annotations/{annotation_id}")
	@APIDesc("updates the annotation with the given annotation_id of the transcription with the given transcription_id of the project entry with the given entry_id")
	public void deleteAnnotation(@PathParam("project_id") long project_id, @PathParam("annotation_id") long annotation_id) {
		transcriptionService.deleteAnnotation(project_id, annotation_id, user);
	}

}
