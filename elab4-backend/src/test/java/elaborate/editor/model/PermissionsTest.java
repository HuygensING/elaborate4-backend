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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import elaborate.editor.model.orm.User;

public class PermissionsTest {
	User root;
	User admin;
	User projectleader;
	User plainUser;
	private ImmutableList<User> allusers;
	private ImmutableList<User> writeusers;

	@Before
	public void setUp() throws Exception {
		root = new User().setId(0).setRoot(true);
		admin = new User().setId(1).setRoleString(ElaborateRoles.getRolestringFor(ElaborateRoles.ADMIN));
		projectleader = new User().setId(3).setRoleString(ElaborateRoles.getRolestringFor(ElaborateRoles.PROJECTLEADER));
		plainUser = new User().setId(2).setRoleString(ElaborateRoles.getRolestringFor(ElaborateRoles.USER));
		allusers = ImmutableList.of(root, admin, projectleader, plainUser);
		writeusers = ImmutableList.of(root, admin, projectleader);
	}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void testGetPermission1() throws Exception {
		for (User writeuser : writeusers) {
			for (User user : allusers) {
				Permission permission = Permissions.getPermission(writeuser, user);
				assertThat(permission.canWrite()).isTrue();
			}
		}
	}

	@Test
	public void testGetPermission2() throws Exception {
		Permission permission = Permissions.getPermission(plainUser, plainUser);
		assertThat(permission.canWrite()).isTrue();
	}

	@Test
	public void testGetPermission3() throws Exception {
		Permission permission = Permissions.getPermission(plainUser, admin);
		assertThat(permission.canWrite()).isFalse();
	}

	@Test
	public void testGetPermission4() throws Exception {
		Permission permission = Permissions.getPermission(null, plainUser);
		assertThat(permission.canWrite()).isFalse();
	}
}
