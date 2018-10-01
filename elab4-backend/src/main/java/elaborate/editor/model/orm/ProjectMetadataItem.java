package elaborate.editor.model.orm;

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
