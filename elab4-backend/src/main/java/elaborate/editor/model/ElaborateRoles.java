package elaborate.editor.model;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2020 Huygens ING
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

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class ElaborateRoles {
	public static final String ADMIN = "ADMIN";
	public static final String PROJECTLEADER = "PROJECTLEADER";
	public static final String READER = "READER";
	public static final String USER = "USER";
	public static final String[] ROLES = new String[] { READER, USER, PROJECTLEADER, ADMIN };

	public static String getRolestringFor(String role) {
		List<String> roles = Lists.newArrayList(READER);
		if (USER.equals(role)) {
			roles.add(USER);

		} else if (PROJECTLEADER.equals(role)) {
			roles.add(PROJECTLEADER);
			roles.add(USER);

		} else if (ADMIN.equals(role)) {
			roles.add(ADMIN);
			roles.add(PROJECTLEADER);
			roles.add(USER);
		}
		return Joiner.on(",").join(roles);
	}

	public static String highestRole(List<String> list) {
		if (list.contains(ADMIN)) {
			return ADMIN;
		}
		if (list.contains(PROJECTLEADER)) {
			return PROJECTLEADER;
		}
		if (list.contains(USER)) {
			return USER;
		}
		return READER;
	}

}
