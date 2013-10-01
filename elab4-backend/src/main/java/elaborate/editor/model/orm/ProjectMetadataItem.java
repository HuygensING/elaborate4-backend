package elaborate.editor.model.orm;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import elaborate.editor.model.AbstractTrackedEntity;

@Entity
@Table(name = "project_metadata_items")
@XmlRootElement
public class ProjectMetadataItem extends AbstractTrackedEntity<ProjectMetadataItem> {
  private static final long serialVersionUID = 1L;

  String field;
  String data;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", columnDefinition = "int4")
  private Project project;

  public Project getProject() {
    return project;
  }

  public ProjectMetadataItem setProject(Project project) {
    this.project = project;
    return this;
  }

  public String getField() {
    return field;
  }

  public ProjectMetadataItem setField(String field) {
    this.field = field;
    return this;
  }

  public String getData() {
    return data;
  }

  public ProjectMetadataItem setData(String data) {
    this.data = data;
    return this;
  }

}
