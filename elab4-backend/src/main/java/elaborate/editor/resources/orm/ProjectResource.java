package elaborate.editor.resources.orm;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;

import nl.knaw.huygens.jaxrstools.exceptions.BadRequestException;
import nl.knaw.huygens.jaxrstools.resources.UTF8MediaType;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;

import elaborate.editor.config.Configuration;
import elaborate.editor.model.AnnotationInputWrapper;
import elaborate.editor.model.Views;
import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.AnnotationType;
import elaborate.editor.model.orm.Facsimile;
import elaborate.editor.model.orm.LogEntry;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.SearchData;
import elaborate.editor.model.orm.Transcription;
import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.service.ProjectEntryService;
import elaborate.editor.model.orm.service.ProjectService;
import elaborate.editor.model.orm.service.SearchService;
import elaborate.editor.model.orm.service.TranscriptionService;
import elaborate.editor.publish.Publication;
import elaborate.editor.resources.AbstractElaborateResource;
import elaborate.editor.resources.orm.wrappers.TranscriptionWrapper;
import elaborate.editor.solr.AbstractSolrServer;
import elaborate.editor.solr.ElaborateSearchParameters;
import elaborate.jaxrs.APIDesc;
import elaborate.jaxrs.Annotations.AuthorizationRequired;

@Path("projects")
@AuthorizationRequired
public class ProjectResource extends AbstractElaborateResource {
  private static final int DEFAULT_PORT = 80;
  static final String KEY_NEXT = "_next";
  static final String KEY_PREV = "_prev";

  Configuration config = Configuration.instance();

  @Context
  private ProjectService projectService;

  @Context
  private SearchService searchService;

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

  /* TEI export */
  @GET
  @Path("{project_id}/tei")
  @Produces(MediaType.APPLICATION_XML)
  @APIDesc("Returns the project with the given project_id as tei")
  public String exportTei(@PathParam("project_id") long project_id) {
    return projectService.exportTei(project_id, getUser());
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
  public Object getProjectAnnotationTypes(@PathParam("project_id") long project_id) {
    return projectService.getProjectAnnotationTypes(project_id, getUser());
  }

  @PUT
  @Path("{project_id}/annotationtypes")
  @Consumes(UTF8MediaType.APPLICATION_JSON)
  @JsonView(Views.Minimal.class)
  @APIDesc("Updates the annotation types for the project with the given project_id")
  public void setProjectAnnotationTypes(@PathParam("project_id") long project_id, Set<AnnotationType> annotationTypes) {
    projectService.setProjectAnnotationTypes(project_id, annotationTypes, getUser());
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
    return projectService.getProjectUsersIds(project_id, getUser());
  }

  @PUT
  @Path("{project_id}/projectusers/{user_id}")
  @Consumes(UTF8MediaType.APPLICATION_JSON)
  @RolesAllowed("ADMIN")
  @APIDesc("Adds an existing user to the project with the given project_id")
  public Response addProjectUser(@PathParam("project_id") long project_id, @PathParam("user_id") long user_id) {
    User created = projectService.addProjectUser(project_id, user_id, getUser());
    return Response.created(createURI(created)).build();
  }

  @DELETE
  @Path("{project_id}/projectusers/{user_id}")
  @Consumes(UTF8MediaType.APPLICATION_JSON)
  @RolesAllowed("ADMIN")
  @APIDesc("Removes the user with the given user_id from the project with the given project_id")
  public void deleteProjectUser(@PathParam("project_id") long project_id, @PathParam("user_id") long user_id) {
    projectService.deleteProjectUser(project_id, user_id, getUser());
  }

  /* project statistics */
  @GET
  @Path("{project_id}/statistics")
  @Produces(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("Returns the statistics of the project with the given project_id")
  public Object getProjectStatistics(@PathParam("project_id") long project_id) {
    return projectService.getProjectStatistics(project_id, getUser());
  }

  /* project entries */
  @GET
  @Path("{project_id}/entries")
  @Produces(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("Returns the entries of the project with the given project_id")
  @JsonView(Views.Minimal.class)
  public Collection<ProjectEntry> getProjectEntries(@PathParam("project_id") long project_id) {
    return projectService.getProjectEntries(project_id, getUser());
  }

  @POST
  @Path("{project_id}/entries")
  @Consumes(UTF8MediaType.APPLICATION_JSON)
  @RolesAllowed("USER")
  @APIDesc("Adds an entry to the project with the given project_id")
  public Response createProjectEntry(@PathParam("project_id") long project_id, ProjectEntry projectEntry) {
    ProjectEntry created = projectService.createProjectEntry(project_id, projectEntry, getUser());
    return Response.created(createURI(created)).build();
  }

  @GET
  @Path("{project_id}/entries/{entry_id}")
  @Produces(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("Returns the projectentry with the given entry_id")
  @JsonView(Views.Extended.class)
  public ProjectEntry getProjectEntry(@PathParam("entry_id") long entry_id) {
    return projectEntryService.read(entry_id, getUser());
  }

  @PUT
  @Path("{project_id}/entries/{entry_id}")
  @RolesAllowed("ADMIN")
  @Consumes(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("Updates the projectentry with the given entry_id")
  public void updateProjectEntry(@PathParam("entry_id") long entry_id, ProjectEntry projectEntry) {
    projectEntryService.update(entry_id, projectEntry, getUser());
  }

  @DELETE
  @Path("{project_id}/entries/{entry_id}")
  @RolesAllowed("ADMIN")
  @APIDesc("Deletes the projectentry with the given entry_id")
  public void deleteProjectEntry(@PathParam("entry_id") long entry_id) {
    projectEntryService.delete(entry_id, getUser());
  }

  /* entry settings */
  @GET
  @Path("{project_id}/entries/{entry_id}/settings")
  @Produces(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("Returns the settings of the project entry with the given entry_id")
  public Map<String, String> getProjectEntrySettings(@PathParam("project_id") long project_id, @PathParam("entry_id") long entry_id) {
    return projectEntryService.getProjectEntrySettings(entry_id, getUser());
  }

  /* update multiple entry settings */
  @PUT
  @Path("{project_id}/multipleentrysettings")
  @Produces(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("Updates the settings of the project entries with the given entry_ids")
  public void updateMultipleProjectEntrySettings(@PathParam("project_id") long project_id, MultipleProjectEntrySettings mpes) {
    projectEntryService.updateMultipleProjectEntrySettings(project_id, mpes, getUser());
  }

  /* facsimiles */
  @GET
  @Path("{project_id}/entries/{entry_id}/facsimiles")
  @Produces(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("Returns the facsimiles of the project entry with the given entry_id")
  @JsonView(Views.Minimal.class)
  public Collection<Facsimile> getFacsimiles(@PathParam("entry_id") long entry_id) {
    return projectEntryService.getFacsimiles(entry_id, getUser());
  }

  @POST
  @Path("{project_id}/entries/{entry_id}/facsimiles")
  @Consumes(UTF8MediaType.APPLICATION_JSON)
  @RolesAllowed("USER")
  @APIDesc("Adds a facsimile to the project entry with the given entry_id")
  public Response addFacsimile(@PathParam("entry_id") long entry_id, Facsimile input) {
    Facsimile facsimile = projectEntryService.addFacsimile(entry_id, input, getUser());
    return Response.created(createURI(facsimile)).build();
  }

  @GET
  @Path("{project_id}/entries/{entry_id}/facsimiles/{facsimile_id}")
  @Produces(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("Returns the facsimile with the given facsimile_id of the project entry with the given entry_id of the project with the given project_id")
  @JsonView(Views.Minimal.class)
  public Facsimile getFacsimile(@PathParam("project_id") long project_id, @PathParam("facsimile_id") long facsimile_id) {
    Facsimile facsimile = projectEntryService.readFacsimile(project_id, facsimile_id, getUser());
    return facsimile;
  }

  @PUT
  @Path("{project_id}/entries/{entry_id}/facsimiles/{facsimile_id}")
  @Consumes(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("Updates the facsimile with the given facsimile_id of the project entry with the given entry_id of the project with the given project_id")
  public void updateFacsimile(@PathParam("project_id") long project_id, @PathParam("facsimile_id") long facsimile_id, Facsimile newFacsimileData) {
    projectEntryService.updateFacsimile(project_id, facsimile_id, newFacsimileData, getUser());
  }

  @DELETE
  @Path("{project_id}/entries/{entry_id}/facsimiles/{facsimile_id}")
  @APIDesc("Deletes the facsimile with the given facsimile_id of the project entry with the given entry_id of the project with the given project_id, and all its dependencies (annotations")
  public void deleteFacsimile(@PathParam("project_id") long project_id, @PathParam("facsimile_id") long facsimile_id) {
    projectEntryService.deleteFacsimile(project_id, facsimile_id, getUser());
  }

  /* transcriptions*/
  @GET
  @Path("{project_id}/entries/{entry_id}/transcriptions")
  @Produces(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("Returns the transcriptions of the project entry with the given entry_id")
  public Collection<TranscriptionWrapper> getTranscriptions(@PathParam("entry_id") long entry_id) {
    Collection<Transcription> transcriptions = projectEntryService.getTranscriptions(entry_id, getUser());
    List<TranscriptionWrapper> wrappers = Lists.newArrayListWithCapacity(transcriptions.size());
    for (Transcription transcription : transcriptions) {
      wrappers.add(new TranscriptionWrapper(transcription));
    }
    return wrappers;
  }

  @POST
  @Path("{project_id}/entries/{entry_id}/transcriptions")
  @Consumes(UTF8MediaType.APPLICATION_JSON)
  @RolesAllowed("USER")
  @APIDesc("Adds a transcription to the project entry with the given entry_id")
  public Response addTranscription(@PathParam("entry_id") long entry_id, TranscriptionWrapper transcriptionWrapper) {
    Transcription transcription = projectEntryService.addTranscription(entry_id, transcriptionWrapper, getUser());
    return Response.created(createURI(transcription)).build();
  }

  @GET
  @Path("{project_id}/entries/{entry_id}/transcriptions/{transcription_id}")
  @Produces(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("Returns the transcription with the given transcription_id of the project entry with the given entry_id of the project with the given project_id")
  public TranscriptionWrapper getTranscription(@PathParam("project_id") long project_id, @PathParam("transcription_id") long transcription_id) {
    Transcription transcription = transcriptionService.read(project_id, transcription_id, getUser());
    return new TranscriptionWrapper(transcription);
  }

  @PUT
  @Path("{project_id}/entries/{entry_id}/transcriptions/{transcription_id}")
  @Consumes(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("Updates the transcription with the given transcription_id of the project entry with the given entry_id of the project with the given project_id")
  public void updateTranscription(@PathParam("project_id") long project_id, @PathParam("transcription_id") long transcription_id, TranscriptionWrapper transcriptionWrapper) {
    LOG.info("transcription in={}", transcriptionWrapper);
    transcriptionService.update(project_id, transcription_id, transcriptionWrapper, getUser());
  }

  @DELETE
  @Path("{project_id}/entries/{entry_id}/transcriptions/{transcription_id}")
  @APIDesc("Deletes the transcription with the given transcription_id of the project entry with the given entry_id of the project with the given project_id, and all its dependencies (annotations")
  public void deleteTranscription(@PathParam("project_id") long project_id, @PathParam("transcription_id") long transcription_id) {
    transcriptionService.delete(project_id, transcription_id, getUser());
  }

  /* annotations*/
  @GET
  @Path("{project_id}/entries/{entry_id}/transcriptions/{transcription_id}/annotations")
  @Produces(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("Returns the annotations of the transcription with the given transcription_id of the project entry with the given entry_id")
  public Collection<Annotation> getAnnotations(@PathParam("project_id") long project_id, @PathParam("transcription_id") long transcription_id) {
    return transcriptionService.getAnnotations(project_id, transcription_id, getUser());
  }

  @POST
  @Path("{project_id}/entries/{entry_id}/transcriptions/{transcription_id}/annotations")
  @Consumes(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("Add an annotation to the transcription with the given transcription_id of the project entry with the given entry_id")
  public Response addAnnotation(@PathParam("project_id") long project_id, @PathParam("transcription_id") long transcription_id, AnnotationInputWrapper annotationInput) {
    Annotation created = transcriptionService.addAnnotation(project_id, transcription_id, annotationInput, getUser());
    return Response.created(createURI(created)).build();
  }

  @GET
  @Path("{project_id}/entries/{entry_id}/transcriptions/{transcription_id}/annotations/{annotation_id}")
  @Produces(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("Returns the annotation with the given annotation_id of the transcription with the given transcription_id of the project entry with the given entry_id")
  public Annotation getAnnotation(@PathParam("project_id") long project_id, @PathParam("annotation_id") long annotation_id) {
    return transcriptionService.readAnnotation(project_id, annotation_id, getUser());
  }

  @PUT
  @Path("{project_id}/entries/{entry_id}/transcriptions/{transcription_id}/annotations/{annotation_id}")
  @Consumes(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("updates the annotation with the given annotation_id of the transcription with the given transcription_id of the project entry with the given entry_id")
  public void updateAnnotation(@PathParam("project_id") long project_id, @PathParam("annotation_id") long annotation_id, AnnotationInputWrapper annotationInput) {
    transcriptionService.updateAnnotation(project_id, annotation_id, annotationInput, getUser());
  }

  @DELETE
  @Path("{project_id}/entries/{entry_id}/transcriptions/{transcription_id}/annotations/{annotation_id}")
  @APIDesc("updates the annotation with the given annotation_id of the transcription with the given transcription_id of the project entry with the given entry_id")
  public void deleteAnnotation(@PathParam("project_id") long project_id, @PathParam("annotation_id") long annotation_id) {
    transcriptionService.deleteAnnotation(project_id, annotation_id, getUser());
  }

  /* project loglines */
  @GET
  @Path("{project_id}/logentries")
  @Produces(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("Returns the logentries of the project with the given project_id")
  public List<LogEntry> getLogEntries(@PathParam("project_id") long project_id) {
    return projectService.getLogEntries(project_id, getUser());
  }

  /* publish */
  @POST
  @Path("{project_id}/publication")
  @Consumes(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("begin the publication of the project")
  public Response startPublication(@PathParam("project_id") long project_id, Publication.Settings settings) {
    Publication.Status status = projectService.createPublicationStatus(project_id, settings, getUser());
    return Response.created(status.getURI()).build();
  }

  @GET
  @Path("{project_id}/publication/{status_id}")
  @Produces(UTF8MediaType.APPLICATION_JSON)
  @APIDesc("Returns the status of the publication with the given status_id")
  public Publication.Status getPublicationStatus(@PathParam("status_id") String status_id) {
    return projectService.getPublicationStatus(status_id);
  }

  /* search */
  @POST
  @Path("{project_id}/search")
  @Consumes(UTF8MediaType.APPLICATION_JSON)
  @Produces(UTF8MediaType.APPLICATION_JSON)
  public Response createSearch(//
      @PathParam("project_id") long projectId,//
      ElaborateSearchParameters elaborateSearchParameters//
  ) {
    elaborateSearchParameters.setProjectId(projectId);
    SearchData search = searchService.createSearch(elaborateSearchParameters, getUser());
    return Response.created(createURI(search)).build();
  }

  @GET
  @Path("{project_id}/search/{search_id}")
  @Produces(UTF8MediaType.APPLICATION_JSON)
  public Response getSearchResults(//
      @PathParam("project_id") long projectId,//
      @PathParam("search_id") long searchId,//
      @QueryParam("start") @DefaultValue("0") int start,//
      @QueryParam("rows") @DefaultValue("25") int rows//
  //      @QueryParam("verbose") @DefaultValue("false") boolean verbose//
  ) {
    Map<String, Object> searchResult = searchService.getSearchResult(projectId, searchId, start, rows, getUser());
    addPrevNextURIs(searchResult, projectId, searchId, start, rows);
    ResponseBuilder builder = Response.ok(searchResult);
    return builder.build();
  }

  void addPrevNextURIs(Map<String, Object> searchResult, long projectId, long searchId, int start, int rows) {
    int prevStart = Math.max(0, start - rows);
    LOG.info("prevStart={}", prevStart);
    String path = MessageFormat.format("/projects/{0,number,#}/search/{1,number,#}", projectId, searchId);
    if (start > 0) {
      addURI(searchResult, KEY_PREV, path, prevStart, rows);
    }

    int nextStart = start + rows;
    int size = (Integer) searchResult.get(AbstractSolrServer.KEY_NUMFOUND);
    LOG.info("nextStart={}, size={}", nextStart, size);
    if (nextStart < size) {
      addURI(searchResult, KEY_NEXT, path, start + rows, rows);
    }
  }

  private void addURI(Map<String, Object> searchResult, String key, String prevLink, int start, int rows) {
    UriBuilder builder = UriBuilder//
        .fromPath(prevLink)//
        .scheme(config.getStringSetting("server.scheme", "html"))//
        .host(config.getStringSetting("server.name", "127.0.0.1"))//
        .queryParam("start", start)//
        .queryParam("rows", rows);
    int port = config.getIntSetting("server.port", DEFAULT_PORT);
    if (port != DEFAULT_PORT) {
      builder.port(port);
    }
    searchResult.put(key, builder.build().toString());
  }

}
