package elaborate.editor.model.orm.service;

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


import java.util.Date;

import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.User;

public class ExtendedProjectEntry {
	ProjectEntry projectEntry;
	private String l1;
	private String l2;
	private String l3;

	public ProjectEntry getProjectEntry() {
		return projectEntry;
	}

	public ProjectEntry setProject(Project _project) {
		return projectEntry.setProject(_project);
	}

	public ProjectEntry setId(long id) {
		return projectEntry.setId(id);
	}

	public ProjectEntry setCreator(User user) {
		return projectEntry.setCreator(user);
	}

	public ProjectEntry setCreatedOn(Date date) {
		return projectEntry.setCreatedOn(date);
	}

	public ProjectEntry setModifier(User user) {
		return projectEntry.setModifier(user);
	}

	public ProjectEntry setModifiedOn(Date date) {
		return projectEntry.setModifiedOn(date);
	}

	public ProjectEntry setName(String name) {
		return projectEntry.setName(name);
	}

	public void setModifiedBy(User _modifier) {
		projectEntry.setModifiedBy(_modifier);
	}

	public void setCreatedBy(User creator) {
		projectEntry.setCreatedBy(creator);
	}

	public ProjectEntry setPublishable(boolean publishable) {
		return projectEntry.setPublishable(publishable);
	}

	public String getL1() {
		return l1;
	}

	public ExtendedProjectEntry setL1(String l1) {
		this.l1 = l1;
		return this;
	}

	public String getL2() {
		return l2;
	}

	public ExtendedProjectEntry setL2(String l2) {
		this.l2 = l2;
		return this;
	}

	public String getL3() {
		return l3;
	}

	public ExtendedProjectEntry setL3(String l3) {
		this.l3 = l3;
		return this;
	}

}
