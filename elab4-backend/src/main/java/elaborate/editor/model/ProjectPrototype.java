package elaborate.editor.model;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2018 Huygens ING
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
