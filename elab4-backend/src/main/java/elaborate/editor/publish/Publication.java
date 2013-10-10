package elaborate.editor.publish;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import elaborate.editor.model.ProjectTypes;
import elaborate.editor.model.orm.AnnotationType;

public class Publication {

  public static class Settings {
    private Long projectId;
    private List<String> projectEntryMetadataFields = Lists.newArrayList();
    private List<Long> annotationTypeIds = Lists.newArrayList();
    private String projectType = ProjectTypes.COLLECTION; // || "work"

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
  }

  public static class Status {
    private final String id;
    private String url;
    private final List<String> loglines = Lists.newArrayList();
    boolean done = false;
    private final long projectId;

    public Status(long projectId) {
      this.projectId = projectId;
      this.id = new DateTime().toString("yyyyMMddHHmmss") + projectId;
    }

    @JsonIgnore
    public URI getURI() {
      URI uri;
      try {
        //        uri = new URIBuilder().setPath(MessageFormat.format("/projects/{0}/publicationstatus/{1}", String.valueOf(projectId), id)).build();
        //        uri = new URI(MessageFormat.format("status/{0}", id));
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

    public void setDone() {
      done = true;
    }

    public boolean isDone() {
      return done;
    }

    public String getId() {
      return id;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }
  }

}
