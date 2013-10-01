package elaborate.editor.publish;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import elaborate.editor.model.orm.AnnotationType;

public class Publication {

  public static class Settings {
    private Long projectId;
    private List<String> projectEntryMetadataFields = Lists.newArrayList();
    private List<Long> annotationTypeIds = Lists.newArrayList();

    public Long getProjectId() {
      return projectId;
    }

    public void setProjectId(Long projectId) {
      this.projectId = projectId;
    }

    public boolean includeAnnotationType(AnnotationType annotationType) {
      return getAnnotationTypeIds().isEmpty() ? true : getAnnotationTypeIds().contains(annotationType.getId());
    }

    public List<String> getProjectEntryMetadataFields() {
      return projectEntryMetadataFields;
    }

    public void setProjectEntryMetadataFields(List<String> projectEntryMetadataFields) {
      this.projectEntryMetadataFields = projectEntryMetadataFields;
    }

    public List<Long> getAnnotationTypeIds() {
      return annotationTypeIds;
    }

    public void setAnnotationTypeIds(List<Long> annotationTypeIds) {
      this.annotationTypeIds = annotationTypeIds;
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
