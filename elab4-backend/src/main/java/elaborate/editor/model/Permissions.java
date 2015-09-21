package elaborate.editor.model;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2015 Huygens ING
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

import elaborate.editor.model.orm.AnnotationType;
import elaborate.editor.model.orm.AnnotationTypeMetadataItem;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.User;

public class Permissions {
	private static final Permission NO_PERMISSION = new Permission();
	private static final Permission ROOT_PERMISSION;

	static {
		ROOT_PERMISSION = new Permission() {
			@Override
			public boolean can(Action action) {
				return true;
			}

			@Override
			public boolean can(String action) {
				return true;
			}

			@Override
			public boolean canRead() {
				return true;
			}

			@Override
			public boolean canWrite() {
				return true;
			}
		};
	}

	public static Permission getPermission(User user, Object object) {
		if (user == null) {
			return NO_PERMISSION;
		}
		if (user.isRoot()) {
			return ROOT_PERMISSION;
		}
		Permission permission = new Permission();
		if (object instanceof Project) {
			Project project = (Project) object;
			permission = permissionForProject(user, project);

		} else if (object instanceof User) {
			User otheruser = (User) object;
			permission = permissionForUser(user, otheruser);

		} else if (object instanceof AnnotationType || object instanceof AnnotationTypeMetadataItem) {
			permission = permissionForAnnotationType(user);

		}
		return permission;
	}

	private static Permission permissionForAnnotationType(User user) {
		Permission permission = new Permission().setCanRead(true);

		boolean userIsAdmin = user.getRoleString().contains(ElaborateRoles.ADMIN);
		boolean userIsProjectLeader = user.getRoleString().contains(ElaborateRoles.PROJECTLEADER);

		if (userIsProjectLeader || userIsAdmin) {
			permission.setCanWrite(true);
		}

		return permission;
	}

	private static Permission permissionForUser(User user, User otheruser) {
		Permission permission = new Permission().setCanRead(true);

		boolean userIsAdmin = user.getRoleString().contains(ElaborateRoles.ADMIN);
		boolean userIsProjectLeader = user.getRoleString().contains(ElaborateRoles.PROJECTLEADER);

		if (userIsProjectLeader || userIsAdmin || user.getId() == otheruser.getId()) {
			permission.setCanWrite(true);
		}

		return permission;
	}

	private static Permission permissionForProject(User user, Project project) {
		Permission permission = new Permission();

		boolean userIsAdmin = user.getRoleString().contains(ElaborateRoles.ADMIN);
		boolean userIsProjectLeader = (user.getId() == project.getProjectLeaderId());

		if (userIsProjectLeader || userIsAdmin) {
			permission.setCanRead(true);
			permission.setCanWrite(true);
			permission.allow(Action.SELECT_PROJECT_ANNOTATION_TYPES);
			permission.allow(Action.SELECT_PROJECT_ENTRY_METADATA_FIELDS);
			permission.allow(Action.DELETE_PROJECT_ENTRIES);
			permission.allow(Action.DELETE_PROJECT);
			permission.allow(Action.EDIT_PROJECT_USERS);
			permission.allow(Action.EDIT_PROJECT_SETTINGS);
			permission.allow(Action.PUBLISH);

		} else {
			for (User projectUser : project.getUsers()) {
				if (user.equals(projectUser)) {
					permission.setCanRead(true);
					permission.setCanWrite(user.getRoleString().contains(ElaborateRoles.USER));
					return permission;
				}
			}
		}
		return permission;
	}
}
