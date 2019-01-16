package elaborate.editor.security;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2019 Huygens ING
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import elaborate.editor.model.ElaborateRoles;
import elaborate.editor.model.orm.User;

public class ElaborateSecurityContextTest {
	private User user;
	private ElaborateSecurityContext elaborateSecurityContext;

	@Before
	public void setUp() {
		user = mock(User.class);
		when(user.getUsername()).thenReturn("username");
		when(user.hasRole("ADMIN")).thenReturn(true);

		elaborateSecurityContext = new ElaborateSecurityContext(user);
	}

	@Test
	public void testGetUser() {
		assertThat(elaborateSecurityContext.getUser()).isEqualTo(user);
	}

	@Test
	public void testGetUserPrincipal() {
		assertThat(elaborateSecurityContext.getUserPrincipal().getName()).isEqualTo(user.getUsername());
	}

	@Test
	public void testIsUserInRole() {
		assertThat(elaborateSecurityContext.isUserInRole(ElaborateRoles.ADMIN)).isTrue();
	}

	@Test
	public void testGetAuthenticationScheme() {
		assertThat(elaborateSecurityContext.getAuthenticationScheme()).isEqualTo("SimpleAuth");
	}

	@Test
	public void testIsSecure() {
		assertThat(elaborateSecurityContext.isSecure()).isFalse();
	}

}
