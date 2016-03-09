package elaborate.editor.publish;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2016 Huygens ING
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

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import elaborate.editor.model.ProjectTypes;
import elaborate.editor.model.orm.AnnotationType;
import elaborate.editor.model.orm.User;

public class Publication {

  public static class Settings {
    private Long projectId;
    private List<String> projectEntryMetadataFields = Lists.newArrayList();
    private List<Long> annotationTypeIds = Lists.newArrayList();
    private String projectType = ProjectTypes.COLLECTION; // || "work"
    private User user;
    private List<String> textLayers = Lists.newArrayList();
    private List<String> facetFields = Lists.newArrayList();

    public Long getProjectId() {
      return projectId;
    }

    public Settings setProjectId(Long projectId) {
      this.projectId = projectId;
      return this;
    }

    public boolean includeAnnotationType(AnnotationType annotationType) {
      return getAnnotationTypeIds().isEmpty() ? true : getAnnotationTypeIds().contains(annotationType.getId());
    }

    public List<String> getProjectEntryMetadataFields() {
      return projectEntryMetadataFields;
    }

    public Settings setProjectEntryMetadataFields(List<String> projectEntryMetadataFields) {
      this.projectEntryMetadataFields = projectEntryMetadataFields;
      return this;
    }

    public List<Long> getAnnotationTypeIds() {
      return annotationTypeIds;
    }

    public Settings setAnnotationTypeIds(List<Long> annotationTypeIds) {
      this.annotationTypeIds = annotationTypeIds;
      return this;
    }

    public String getProjectType() {
      return projectType;
    }

    public Settings setProjectType(String projectType) {
      this.projectType = projectType;
      return this;
    }

    public User getUser() {
      return user;
    }

    public Settings setUser(User user) {
      this.user = user;
      return this;
    }

    public Settings setTextLayers(List<String> publishableTextLayers) {
      this.textLayers = publishableTextLayers;
      return this;
    }

    public List<String> getTextLayers() {
      return textLayers;
    }

    public Settings setFacetFields(List<String> facetFields) {
      this.facetFields = facetFields;
      return this;
    }

    public List<String> getFacetFields() {
      return facetFields;
    }

  }

  public static class Status {
    private final String id;
    private String url;
    private final List<String> loglines = Lists.newArrayList();
    private final List<String> errors = Lists.newArrayList();
    private boolean done = false;
    private boolean fail = false;

    public Status(long projectId) {
      this.id = new DateTime().toString("yyyyMMddHHmmss") + projectId;
    }

    @JsonIgnore
    public URI getURI() {
      URI uri;
      try {
        // uri = new URIBuilder().setPath(MessageFormat.format("/projects/{0}/publicationstatus/{1}", String.valueOf(projectId), id)).build();
        // uri = new URI(MessageFormat.format("status/{0}", id));
        uri = new URI(id);
      } catch (URISyntaxException ue) {
        uri = null;
        ue.printStackTrace();
      }
      return uri;
    }

    public void addLogline(String line) {
      String timestamp = new DateTime().toString("yyyy-MM-dd HH:mm:ss");
      getLoglines().add(MessageFormat.format("{0} | {1}", timestamp, line));
    }

    public List<String> getLoglines() {
      return loglines;
    }

    public void addError(String error) {
      errors.add(error);
      addLogline("ERROR: " + error);
    }

    public List<String> getErrors() {
      return errors;
    }

    public void setDone() {
      done = true;
    }

    public boolean isDone() {
      return done;
    }

    public void setFail() {
      this.fail = true;
    }

    public boolean isFail() {
      return fail || !errors.isEmpty();
    }

    public String getId() {
      return id;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getUrl() {
      return url;
    }

  }

}
