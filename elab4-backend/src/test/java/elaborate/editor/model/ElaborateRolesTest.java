package elaborate.editor.model;

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

import static com.google.common.collect.Lists.newArrayList;
import static elaborate.editor.model.ElaborateRoles.ADMIN;
import static elaborate.editor.model.ElaborateRoles.PROJECTLEADER;
import static elaborate.editor.model.ElaborateRoles.READER;
import static elaborate.editor.model.ElaborateRoles.USER;
import static elaborate.editor.model.ElaborateRoles.getRolestringFor;
import static elaborate.editor.model.ElaborateRoles.highestRole;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ElaborateRolesTest {

	@Test
	public void testHighestRole1() {
		assertThat(highestRole(newArrayList(ADMIN, USER))).isEqualTo(ADMIN);
	}

	@Test
	public void testHighestRole2() {
		assertThat(highestRole(newArrayList(USER, READER))).isEqualTo(USER);
	}

	@Test
	public void testHighestRole3() {
		assertThat(highestRole(newArrayList(USER, READER, PROJECTLEADER))).isEqualTo(PROJECTLEADER);
	}

	@Test
	public void testHighestRole4() {
		assertThat(highestRole(newArrayList(READER))).isEqualTo(READER);
	}

	@Test
	public void testGetRolestringForReader() {
		assertThat(getRolestringFor(READER)).isEqualTo("READER");
	}

	@Test
	public void testGetRolestringForUser() {
		assertThat(getRolestringFor(USER)).isEqualTo("READER,USER");
	}

	@Test
	public void testGetRolestringForProjectLeader() {
		assertThat(getRolestringFor(PROJECTLEADER)).isEqualTo("READER,PROJECTLEADER,USER");
	}

	@Test
	public void testGetRolestringForAdmin() {
		assertThat(getRolestringFor(ADMIN)).isEqualTo("READER,ADMIN,PROJECTLEADER,USER");
	}

}
