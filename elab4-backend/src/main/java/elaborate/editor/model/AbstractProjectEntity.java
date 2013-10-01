package elaborate.editor.model;

import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;

import elaborate.editor.model.orm.Project;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class AbstractProjectEntity<T extends AbstractProjectEntity<T>> extends AbstractTrackedEntity<T> {
  private static final long serialVersionUID = -7339519088116167633L;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", columnDefinition = "int4")
  private Project project;

  public AbstractProjectEntity() {
    super();
  }

  @JsonIgnore
  public Project getProject() {
    return project;
  }

  public T setProject(Project _project) {
    this.project = _project;
    return (T) this;
  }

}