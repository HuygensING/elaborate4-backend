package elaborate.editor.model.orm;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2021 Huygens ING
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import elaborate.editor.model.AbstractStoredEntity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Entity
@Table(name = "log_entries")
@XmlRootElement
public class LogEntry extends AbstractStoredEntity<LogEntry>implements Comparable<LogEntry> {
	private static final long serialVersionUID = 1L;

	private String comment;

	// @Column(name = "project_title")
  private String projectTitle;

	// @Column(name = "user_name")
  private String userName;

	@Temporal(TemporalType.TIMESTAMP)
  private
  // @Column(name = "created_on")
	Date createdOn;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", columnDefinition = "int4")
	private Project project;

	@JsonIgnore
	public Project getProject() {
		return project;
	}

	public LogEntry setProject(Project project) {
		this.project = project;
		setProjectTitle(project.getTitle());
		return this;
	}

	public String getComment() {
		return comment;
	}

	public LogEntry setComment(String comment) {
		this.comment = comment;
		return this;
	}

	@JsonIgnore
	public String getProjectTitle() {
		return projectTitle;
	}

	private void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
  }

	public String getUserName() {
		return userName;
	}

	public LogEntry setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public LogEntry setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	@Override
	public int compareTo(LogEntry o) {
		return o.getCreatedOn().compareTo(this.getCreatedOn());
	}
}
