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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.knaw.huygens.jaxrstools.exceptions.BadRequestException;
import nl.knaw.huygens.jaxrstools.resources.UTF8MediaType;

import org.apache.xalan.xsltc.compiler.util.InternalError;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.ImmutableList;

import elaborate.editor.config.Configuration;
import elaborate.editor.model.Views;
import elaborate.editor.model.orm.LogEntry;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.service.ProjectEntryService;
import elaborate.editor.model.orm.service.ProjectService;
import elaborate.editor.model.orm.service.ReindexStatus;
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
		try {
			return projectService.getAll(getUser());
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	@GET
	@Path("{project_id: [0-9]+}")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the project with the given project_id")
	public Project getProject(@PathParam("project_id") long project_id) {
		try {
			return projectService.read(project_id, getUser());
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	@POST
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@RolesAllowed("ADMIN")
	@APIDesc("Adds a new project")
	public Response createProject(Project project) {
		try {
			projectService.create(project, getUser());
			return Response.created(createURI(project)).build();
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	//	@PUT
	//	@Path("{project_id: [0-9]+}")
	//	@RolesAllowed("ADMIN")
	//	@Consumes(UTF8MediaType.APPLICATION_JSON)
	//	@APIDesc("Updates the project with the given project_id")
	//	public void updateProject(Project project) {
	//		projectService.update(project, getUser());
	//	}

	@DELETE
	@Path("{project_id: [0-9]+}")
	@RolesAllowed("ADMIN")
	@APIDesc("Deletes the project with the given project_id")
	public void deleteProject(@PathParam("project_id") long project_id) {
		try {
			projectService.delete(project_id, getUser());
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	/* project settings */

	@PUT
	@Path("{project_id: [0-9]+}/sortlevels")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@RolesAllowed("ADMIN")
	@APIDesc("Updates level1,2,3 of the project with the given project_id")
	public void updateProjectSortLevels(@PathParam("project_id") long project_id, List<String> levels) {
		try {
			if (levels != null) {
				projectService.setProjectSortLevels(project_id, levels, getUser());
			} else {
				throw new BadRequestException("no sortlevels sent");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	@GET
	@Path("{project_id: [0-9]+}/settings")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the settings of the project with the given project_id")
	public Map<String, String> getProjectSettings(@PathParam("project_id") long project_id) {
		try {
			return projectService.getProjectSettings(project_id, getUser());
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	@PUT
	@Path("{project_id: [0-9]+}/settings")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@RolesAllowed("ADMIN")
	@APIDesc("Updates the settings of the project with the given project_id")
	public void updateProjectSettings(@PathParam("project_id") long project_id, Map<String, String> settingsMap) {
		try {
			if (settingsMap != null) {
				projectService.setProjectSettings(project_id, settingsMap, getUser());
			} else {
				throw new BadRequestException("no settingsMap sent");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	@PUT
	@Path("{project_id: [0-9]+}/textlayers")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@RolesAllowed("ADMIN")
	@APIDesc("Updates the textlayers settings of the project with the given project_id")
	public void updateTextlayersSettings(@PathParam("project_id") long project_id, List<String> textLayers) {
		try {
			projectService.setTextlayers(project_id, textLayers, getUser());
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	//  /* project facets */
	//  @GET
	//  @Path("{project_id: [0-9]+}/facets")
	//  @Produces(UTF8MediaType.APPLICATION_JSON)
	//  @APIDesc("Returns facet info of the project with the given project_id")
	//  public List<FacetInfo> getFacetInfo(@PathParam("project_id") long project_id) {
	//    return projectService.getFacetInfo(project_id, getUser());
	//  }

	@GET
	@Path("{project_id: [0-9]+}/entrymetadata")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the metadata of the entries of the project with the given project_id")
	@JsonView(Views.Extended.class)
	public Collection<Map<String, String>> getProjectEntryMetadata(@PathParam("project_id") long project_id) {
		try {
			return projectService.getProjectEntryMetadata(project_id, getUser());
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	/* project entry metadata */
	@GET
	@Path("{project_id: [0-9]+}/entrymetadatafields")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the metadatafields for the project entries of the project with the given project_id")
	public Collection<String> getProjectEntryMetadataFields(@PathParam("project_id") long project_id) {
		try {
			return ImmutableList.copyOf(projectService.getProjectEntryMetadataFields(project_id, getUser()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	@PUT
	@Path("{project_id: [0-9]+}/entrymetadatafields")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Updates the metadatafields for the project entries of the project with the given project_id")
	public Response updateProjectEntryMetadataFields(@PathParam("project_id") long project_id, List<String> fields) {
		try {
			projectService.setProjectEntryMetadataFields(project_id, fields, getUser());
			ReindexStatus status = projectService.createReindexStatus(project_id);
			return Response.created(status.getURI()).build();
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}

	}

	/* project annotationtypes */
	@GET
	@Path("{project_id: [0-9]+}/annotationtypes")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@JsonView(Views.Minimal.class)
	@APIDesc("Returns the annotation types for the project with the given project_id")
	public List<Long> getProjectAnnotationTypes(@PathParam("project_id") long project_id) {
		try {
			return projectService.getProjectAnnotationTypeIds(project_id, getUser());
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	@PUT
	@Path("{project_id: [0-9]+}/annotationtypes")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@JsonView(Views.Minimal.class)
	@APIDesc("Updates the annotation types for the project with the given project_id")
	public void setProjectAnnotationTypes(@PathParam("project_id") long project_id, List<Long> annotationTypeIds) {
		try {
			projectService.setProjectAnnotationTypes(project_id, annotationTypeIds, getUser());
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	/* project users */
	@GET
	@Path("{project_id: [0-9]+}/users")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the users assigned to the project with the given project_id, extended view")
	public Object getProjectUsersExtended(@PathParam("project_id") long project_id) {
		try {
			return projectService.getProjectUsersFull(project_id, getUser());
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	@GET
	@Path("{project_id: [0-9]+}/projectusers")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the user ids assigned to the project with the given project_id")
	public List<Long> getProjectUsers(@PathParam("project_id") long project_id) {
		try {
			return projectService.getProjectUserIds(project_id, getUser());
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	@PUT
	@Path("{project_id: [0-9]+}/projectusers")
	@Consumes(UTF8MediaType.APPLICATION_JSON)
	@RolesAllowed("ADMIN")
	@APIDesc("Updates the user ids assigned to the project with the given project_id")
	public void updateProjectUsers(@PathParam("project_id") long project_id, List<Long> userIds) {
		try {
			projectService.updateProjectUserIds(project_id, userIds, getUser());
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	//  @PUT
	//  @Path("{project_id: [0-9]+}/projectusers/{user_id}")
	//  @Consumes(UTF8MediaType.APPLICATION_JSON)
	//  @RolesAllowed("ADMIN")
	//  @APIDesc("Adds an existing user to the project with the given project_id")
	//  public Response addProjectUser(@PathParam("project_id") long project_id, @PathParam("user_id") long user_id) {
	//    User created = projectService.addProjectUser(project_id, user_id, getUser());
	//    return Response.created(createURI(created)).build();
	//  }

	//  @DELETE
	//  @Path("{project_id: [0-9]+}/projectusers/{user_id}")
	//  @Consumes(UTF8MediaType.APPLICATION_JSON)
	//  @RolesAllowed("ADMIN")
	//  @APIDesc("Removes the user with the given user_id from the project with the given project_id")
	//  public void deleteProjectUser(@PathParam("project_id") long project_id, @PathParam("user_id") long user_id) {
	//    projectService.deleteProjectUser(project_id, user_id, getUser());
	//  }

	/* project statistics */
	@GET
	@Path("{project_id: [0-9]+}/statistics")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the statistics of the project with the given project_id")
	public Object getProjectStatistics(@PathParam("project_id") long project_id) {
		try {
			return projectService.getProjectStatistics(project_id, getUser());
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	/* project entries */

	@Path("{project_id: [0-9]+}/entries")
	public ProjectEntriesResource getProjectEntriesResource() {
		try {
			return new ProjectEntriesResource(getUser(), projectService);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	/* update multiple entry settings */
	@PUT
	@Path("{project_id: [0-9]+}/multipleentrysettings")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Updates the settings of the project entries with the given entry_ids")
	public void updateMultipleProjectEntrySettings(@PathParam("project_id") long project_id, MultipleProjectEntrySettings mpes) {
		try {
			LOG.info("in:{}", mpes);
			projectEntryService.updateMultipleProjectEntrySettings(project_id, mpes, getUser());
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	/* project loglines */
	@GET
	@Path("{project_id: [0-9]+}/logentries")
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Returns the logentries of the project with the given project_id")
	public List<LogEntry> getLogEntries(@PathParam("project_id") long project_id) {
		try {
			return projectService.getLogEntries(project_id, getUser());
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	/* TEI export */
	@GET
	@Path("{project_id: [0-9]+}/tei")
	@Produces(MediaType.APPLICATION_XML)
	@APIDesc("Returns the project with the given project_id as tei")
	public String exportTei(@PathParam("project_id") long project_id) {
		try {
			return projectService.exportTei(project_id, getUser());
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	/* publish */

	@Path("{project_id: [0-9]+}/draft")
	public DraftPublicationResource getDraftResource() {
		try {
			return new DraftPublicationResource(getUser(), projectService);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

	//  @Path("{project_id: [0-9]+}/publicationrequest")
	//  public void requestPublication() {
	//    ;
	//  }

	/* search */

	@Path("{project_id: [0-9]+}/search")
	public SearchResource getSearchResource() {
		try {
			return new SearchResource(getUser());
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.getMessage());
		}
	}

}
