package elaborate.editor.resources.orm;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.knaw.huygens.jaxrstools.exceptions.BadRequestException;
import nl.knaw.huygens.jaxrstools.resources.UTF8MediaType;

import com.fasterxml.jackson.annotation.JsonView;

import elaborate.editor.config.Configuration;
import elaborate.editor.model.Views;
import elaborate.editor.model.orm.LogEntry;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.service.ProjectEntryService;
import elaborate.editor.model.orm.service.ProjectService;
import elaborate.editor.model.orm.service.TranscriptionService;
import elaborate.editor.resources.AbstractElaborateResource;
import elaborate.jaxrs.APIDesc;
import elaborate.jaxrs.Annotations.AuthorizationRequired;

@Path("projects")
@AuthorizationRequired
public class ProjectResource extends AbstractElaborateResource {
	Configuration config = Configuration.instance();

	@Context
	private ProjectService projectService;

	@Context
	private ProjectEntryService projectEntryService;

	@Context
	private TranscriptionService transcriptionService;

	@GET
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns a list of all projects accessible to user")
	public List<Project> getAll() {
		return projectService.getAll(getUser());
	}

	@GET
	@Path("{project_id}")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the project with the given project_id")
	public Project getProject(@PathParam("project_id") long project_id) {
		return projectService.read(project_id, getUser());
	}

	@POST
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@RolesAllowed("ADMIN")
	@APIDesc("Adds a new project")
	public Response createProject(Project project) {
		projectService.create(project, getUser());
		return Response.created(createURI(project)).build();
	}

	@PUT
	@Path("{project_id}")
	@RolesAllowed("ADMIN")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Updates the project with the given project_id")
	public void updateProject(Project project) {
		projectService.update(project, getUser());
	}

	@DELETE
	@Path("{project_id}")
	@RolesAllowed("ADMIN")
	@APIDesc("Deletes the project with the given project_id")
	public void deleteProject(@PathParam("project_id") long project_id) {
		projectService.delete(project_id, getUser());
	}

	/* project settings */
	@GET
	@Path("{project_id}/settings")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the settings of the project with the given project_id")
	public Map<String, String> getProjectSettings(@PathParam("project_id") long project_id) {
		return projectService.getProjectSettings(project_id, getUser());
	}

	@PUT
	@Path("{project_id}/settings")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@RolesAllowed("ADMIN")
	@APIDesc("Updates the settings of the project with the given project_id")
	public void updateProjectSettings(@PathParam("project_id") long project_id, Map<String, String> settingsMap) {
		if (settingsMap != null) {
			projectService.setProjectSettings(project_id, settingsMap, getUser());
		} else {
			throw new BadRequestException("no settingsMap sent");
		}
	}

	@PUT
	@Path("{project_id}/textlayers")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@RolesAllowed("ADMIN")
	@APIDesc("Updates the textlayers settings of the project with the given project_id")
	public void updateTextlayersSettings(@PathParam("project_id") long project_id, List<String> textLayers) {
		projectService.setTextlayers(project_id, textLayers, getUser());
	}

	//  /* project facets */
	//  @GET
	//  @Path("{project_id}/facets")
	//  @Produces(UTF8MediaType.APPLICATION_JSON)
	//  @APIDesc("Returns facet info of the project with the given project_id")
	//  public List<FacetInfo> getFacetInfo(@PathParam("project_id") long project_id) {
	//    return projectService.getFacetInfo(project_id, getUser());
	//  }

	/* project entry metadata */
	@GET
	@Path("{project_id}/entrymetadatafields")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the metadatafields for the project entries of the project with the given project_id")
	public String[] getProjectEntryMetadataFields(@PathParam("project_id") long project_id) {
		return projectService.getProjectEntryMetadataFields(project_id, getUser());
	}

	@PUT
	@Path("{project_id}/entrymetadatafields")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Updates the metadatafields for the project entries of the project with the given project_id")
	public void updateProjectEntryMetadataFields(@PathParam("project_id") long project_id, List<String> fields) {
		projectService.setProjectEntryMetadataFields(project_id, fields, getUser());
	}

	/* project annotationtypes */
	@GET
	@Path("{project_id}/annotationtypes")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@JsonView(Views.Minimal.class)
	@APIDesc("Returns the annotation types for the project with the given project_id")
	public List<Long> getProjectAnnotationTypes(@PathParam("project_id") long project_id) {
		return projectService.getProjectAnnotationTypeIds(project_id, getUser());
	}

	// TODO
	@PUT
	@Path("{project_id}/annotationtypes")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@JsonView(Views.Minimal.class)
	@APIDesc("Updates the annotation types for the project with the given project_id")
	public void setProjectAnnotationTypes(@PathParam("project_id") long project_id, List<Long> annotationTypeIds) {
		projectService.setProjectAnnotationTypes(project_id, annotationTypeIds, getUser());
	}

	/* project users */
	@GET
	@Path("{project_id}/users")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the users assigned to the project with the given project_id, extended view")
	public Object getProjectUsersExtended(@PathParam("project_id") long project_id) {
		return projectService.getProjectUsersFull(project_id, getUser());
	}

	@GET
	@Path("{project_id}/projectusers")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the user ids assigned to the project with the given project_id")
	public List<Long> getProjectUsers(@PathParam("project_id") long project_id) {
		return projectService.getProjectUserIds(project_id, getUser());
	}

	@PUT
	@Path("{project_id}/projectusers")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@RolesAllowed("ADMIN")
	@APIDesc("Updates the user ids assigned to the project with the given project_id")
	public void updateProjectUsers(@PathParam("project_id") long project_id, List<Long> userIds) {
		projectService.updateProjectUserIds(project_id, userIds, getUser());
	}

	//  @PUT
	//  @Path("{project_id}/projectusers/{user_id}")
	//  @Consumes(UTF8MediaType.APPLICATION_JSON)
	//  @RolesAllowed("ADMIN")
	//  @APIDesc("Adds an existing user to the project with the given project_id")
	//  public Response addProjectUser(@PathParam("project_id") long project_id, @PathParam("user_id") long user_id) {
	//    User created = projectService.addProjectUser(project_id, user_id, getUser());
	//    return Response.created(createURI(created)).build();
	//  }

	//  @DELETE
	//  @Path("{project_id}/projectusers/{user_id}")
	//  @Consumes(UTF8MediaType.APPLICATION_JSON)
	//  @RolesAllowed("ADMIN")
	//  @APIDesc("Removes the user with the given user_id from the project with the given project_id")
	//  public void deleteProjectUser(@PathParam("project_id") long project_id, @PathParam("user_id") long user_id) {
	//    projectService.deleteProjectUser(project_id, user_id, getUser());
	//  }

	/* project statistics */
	@GET
	@Path("{project_id}/statistics")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the statistics of the project with the given project_id")
	public Object getProjectStatistics(@PathParam("project_id") long project_id) {
		return projectService.getProjectStatistics(project_id, getUser());
	}

	/* project entries */

	@Path("{project_id}/entries")
	public ProjectEntriesResource getProjectEntriesResource() {
		return new ProjectEntriesResource(getUser(), projectService);
	}

	/* update multiple entry settings */
	@PUT
	@Path("{project_id}/multipleentrysettings")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Updates the settings of the project entries with the given entry_ids")
	public void updateMultipleProjectEntrySettings(@PathParam("project_id") long project_id, MultipleProjectEntrySettings mpes) {
		LOG.info("in:{}", mpes);
		projectEntryService.updateMultipleProjectEntrySettings(project_id, mpes, getUser());
	}

	/* project loglines */
	@GET
	@Path("{project_id}/logentries")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the logentries of the project with the given project_id")
	public List<LogEntry> getLogEntries(@PathParam("project_id") long project_id) {
		return projectService.getLogEntries(project_id, getUser());
	}

	/* TEI export */
	@GET
	@Path("{project_id}/tei")
	@Produces(MediaType.APPLICATION_XML)
	@APIDesc("Returns the project with the given project_id as tei")
	public String exportTei(@PathParam("project_id") long project_id) {
		return projectService.exportTei(project_id, getUser());
	}

	/* publish */

	@Path("{project_id}/draft")
	public DraftPublicationResource getDraftResource() {
		return new DraftPublicationResource(getUser(), projectService);
	}

	//  @Path("{project_id}/publicationrequest")
	//  public void requestPublication() {
	//    ;
	//  }

	/* search */

	@Path("{project_id}/search")
	public SearchResource getSearchResource() {
		return new SearchResource(getUser());
	}

}
