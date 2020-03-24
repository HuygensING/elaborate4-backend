package elaborate.editor.resources.orm.wrappers;

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

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;

import elaborate.editor.model.ElaborateRoles;
import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.service.UserService;
import elaborate.util.PasswordUtil;

@XmlRootElement
public class UserInput {
	// {
	// "id": 123, // only for PUT/update
	// "username": "marijke_boter",
	// "email": "marijke.boter@huygens.knaw.nl",
	// "firstName": "Marijke",
	// "lastName": "Boter",
	// "password": "whatever",
	// "role": "USER"
	// },

	private static final long NULL_ID = -1L;
	public long id = NULL_ID;
	public String username;
	public String email;
	public String firstName;
	public String lastName;
	public String role;
	public String password;

	public User getUser() {
		User user = (id == NULL_ID) ? new User() : UserService.instance().getUser(id);
		user.setUsername(StringUtils.defaultIfEmpty(username, ""))//
				.setEmail(StringUtils.defaultIfEmpty(email, ""))//
				.setFirstName(StringUtils.defaultIfEmpty(firstName, ""))//
				.setLastName(StringUtils.defaultIfEmpty(lastName, ""));

		if (StringUtils.isNotBlank(role)) {
			user.setRoleString(ElaborateRoles.getRolestringFor(role));
		}

		if (StringUtils.isNotBlank(password)) {
			user.setEncodedPassword(PasswordUtil.encode(password));
		}

		if (StringUtils.isNotBlank(firstName) || StringUtils.isNotBlank(lastName)) {
			user.setTitle(Joiner.on(", ").skipNulls().join(new String[] { user.getLastName(), user.getFirstName() }));
		} else {
			user.setTitle(user.getUsername().replaceAll("_", " "));
		}
		return user;
	}
}
