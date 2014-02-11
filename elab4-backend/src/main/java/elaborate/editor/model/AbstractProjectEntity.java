package elaborate.editor.model;

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
