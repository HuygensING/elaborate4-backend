package elaborate.editor.model;

import elaborate.editor.model.orm.Project;

public class ProjectPrototype {
  private String type = ProjectTypes.COLLECTION;
  private final Project project;

  public ProjectPrototype() {
    project = new Project();
  }

  public void setTitle(String title) {
    project.setTitle(title);
  }

  public void setType(String type) {
    this.type = type;
  }

  public Project getProject() {
    return project;
  }

  public String getType() {
    return type;
  }

}
