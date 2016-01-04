package elaborate.editor.model;

import elaborate.editor.model.orm.Project;

public class ProjectPrototype {
  private String type = ProjectTypes.COLLECTION;
  private final Project project;

  public ProjectPrototype() {
    project = new Project();
  }

  public ProjectPrototype setTitle(String title) {
    project.setTitle(title);
    return this;
  }

  public ProjectPrototype setType(String type) {
    this.type = type;
    return this;
  }

  public Project getProject() {
    return project;
  }

  public String getType() {
    return type;
  }

}
