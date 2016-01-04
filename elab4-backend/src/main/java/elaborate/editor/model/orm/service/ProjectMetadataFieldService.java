package elaborate.editor.model.orm.service;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2016 Huygens ING
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
import java.util.List;

import com.google.common.collect.ImmutableList;

import elaborate.editor.model.orm.ProjectMetadataField;
import elaborate.editor.model.orm.User;
import nl.knaw.huygens.jaxrstools.exceptions.UnauthorizedException;

public class ProjectMetadataFieldService extends AbstractStoredEntityService<ProjectMetadataField> {
	private static ProjectMetadataFieldService instance;

	private ProjectMetadataFieldService() {}

	public static ProjectMetadataFieldService instance() {
		if (instance == null) {
			instance = new ProjectMetadataFieldService();
		}
		return instance;
	}

	@Override
	Class<ProjectMetadataField> getEntityClass() {
		return ProjectMetadataField.class;
	}

	@Override
	String getEntityName() {
		return "ProjectMetadataFields";
	}

	/* CRUD methods */
	@Override
	public ProjectMetadataField read(long entry_id) {
		openEntityManager();
		ProjectMetadataField projectMetadataField;
		try {
			projectMetadataField = super.read(entry_id);
		} finally {
			closeEntityManager();
		}
		return projectMetadataField;
	}

	public void update(ProjectMetadataField projectMetadataField, User user) {
		if (rootOrAdmin(user)) {
			beginTransaction();
			try {
				projectMetadataField.setModifiedBy(user);
				super.update(projectMetadataField);
			} finally {
				commitTransaction();
			}

		} else {
			throw new UnauthorizedException("user " + user.getUsername() + " has no admin rights");
		}
	}

	public void delete(long entry_id, User user) {
		if (rootOrAdmin(user)) {
			beginTransaction();
			try {
				super.delete(entry_id);
			} finally {
				commitTransaction();
			}

		} else {
			throw new UnauthorizedException("user " + user.getUsername() + " has no admin rights");
		}
	}

	/* */
	public List<ProjectMetadataField> getAll(User user) {
		if (rootOrAdmin(user)) {
			openEntityManager();
			ImmutableList<ProjectMetadataField> all;
			try {
				all = super.getAll();
			} finally {
				closeEntityManager();
			}
			return all;

		} else {
			throw new UnauthorizedException("user " + user.getUsername() + " has no admin rights");
		}
	}

	public void create(ProjectMetadataField pmField, User user) {
		if (rootOrAdmin(user)) {
			beginTransaction();
			try {
				pmField.setCreator(user);
				pmField.setCreatedOn(new Date());
				pmField.setModifier(user);
				pmField.setModifiedOn(new Date());
				super.create(pmField);
			} finally {
				commitTransaction();
			}

		} else {
			throw new UnauthorizedException("user " + user.getUsername() + " has no admin rights");
		}
	}

}
