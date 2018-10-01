package elaborate.editor.model.orm.service;

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

import static elaborate.editor.model.orm.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.NotFoundException;

import elaborate.editor.AbstractTest;
import elaborate.editor.model.ElaborateRoles;
import elaborate.editor.model.orm.AnnotationType;
import elaborate.editor.model.orm.User;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.jaxrstools.exceptions.UnauthorizedException;

@Ignore
public class AnnotationTypeServiceTest extends AbstractTest {
	static AnnotationTypeService service;
	private static User projectleader;
	private static User user;
	private static User root;
	private static User reader;
	private static User admin;

	@BeforeClass
	public static void setupClass() {
		Log.info("setupClass - start");
		service = AnnotationTypeService.instance();

		projectleader = new User().setRoleString(ElaborateRoles.PROJECTLEADER);
		user = new User().setRoleString(ElaborateRoles.USER);
		reader = new User().setRoleString(ElaborateRoles.READER);
		admin = new User().setRoleString(ElaborateRoles.ADMIN);
		root = new User().setRoot(true);

		// testdb();

		service.beginTransaction();
		service.persist(root);
		service.persist(user);
		service.persist(reader);
		service.persist(admin);
		service.persist(projectleader);
		service.commitTransaction();

		ImmutableList<AnnotationType> all = service.getAll();
		assertThat(all).isEmpty();
		Log.info("setupClass - end");
	}

	// private static void testdb() {
	// try {
	// Class.forName("org.hsqldb.jdbcDriver");
	// Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
	// Statement s = conn.createStatement();
	// s.execute("SELECT * FROM *");
	// ResultSet rs = s.getResultSet();
	// ResultSetMetaData metaData = rs.getMetaData();
	// int columnCount = metaData.getColumnCount();
	// for (int i = 1; i < columnCount; i++) {
	// System.out.print(metaData.getColumnName(i) + " | ");
	// }
	// Log.info("");
	//
	// while (rs.next()) {
	// for (int i = 1; i < columnCount; i++) {
	// System.out.print(rs.getString(i) + " | ");
	// }
	// Log.info("");
	// }
	// conn.close();
	// } catch (ClassNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

	@AfterClass
	public static void teardownClass() {
		Log.info("teardownClass - start");
		service = null;
		Log.info("teardownClass - end");
	}

	@Test
	public void testCRUD_asProjectLeader() throws Exception {
		testAsAuthorizedUser("bladiebla", projectleader);
	}

	@Test
	public void testCRUD_asRoot() throws Exception {
		testAsAuthorizedUser("ofallevil", root);
	}

	@Test
	public void testCRUD_asAdmin() throws Exception {
		testAsAuthorizedUser("administer", admin);
	}

	private void testAsAuthorizedUser(String name, User user) {
		AnnotationType placeholder = new AnnotationType().setName(name).setDescription("description");
		assertThat(placeholder.getId()).isEqualTo(0); // default

		// Create
		AnnotationType created = service.create(placeholder, user);
		long id = created.getId();
		assertThat(id).isNotNull();

		ImmutableList<AnnotationType> all = service.getAll();
		assertThat(all).hasSize(1);

		// Read
		AnnotationType read = service.read(id, user);
		assertThat(read).hasName(name);
		assertThat(read).hasDescription("description");

		// Update
		read.setName("newName").setDescription("newDescription");
		service.update(read, user);
		AnnotationType updated = service.read(id, user);
		assertThat(updated).hasName("newName");
		assertThat(updated).hasDescription("newDescription");

		// Delete
		service.delete(id, user);
		try {
			service.read(id, user);
			fail("NotFoundException expected");
		} catch (NotFoundException e) {}
	}

	private void createAsUnauthorizedUser(User _user) {
		AnnotationType annotationType = new AnnotationType().setName("user").setDescription("description");
		service.create(annotationType, _user);
		fail("an UnauthorizedException should've been thrown");
	}

	@Test(expected = UnauthorizedException.class)
	public void testCreateAsUser() throws Exception {
		createAsUnauthorizedUser(user);
	}

	@Test(expected = UnauthorizedException.class)
	public void testCreateAsReader() throws Exception {
		createAsUnauthorizedUser(reader);
	}

	private void updateAsUnauthorizedUser(User _user) {
		AnnotationType annotationType = new AnnotationType().setName("user").setDescription("description");
		AnnotationType created = service.create(annotationType, root);

		created.setName("newName");
		boolean exceptionThrown = false;
		try {
			service.update(created, _user);
		} catch (UnauthorizedException e) {
			exceptionThrown = true;
		}
		service.delete(created.getId(), root);
		assertThat(exceptionThrown).isEqualTo(true);
	}

	@Test
	public void testUpdateAsUser() throws Exception {
		updateAsUnauthorizedUser(user);
	}

	@Test
	public void testUpdateAsReader() throws Exception {
		updateAsUnauthorizedUser(reader);
	}

	private void deleteAsUnauthorizedUser(User _user) {
		AnnotationType annotationType = new AnnotationType().setName("user").setDescription("description");
		AnnotationType created = service.create(annotationType, root);

		boolean exceptionThrown = false;
		try {
			service.delete(created.getId(), _user);
		} catch (UnauthorizedException e) {
			exceptionThrown = true;
		}
		service.delete(created.getId(), root);
		assertThat(exceptionThrown).isEqualTo(true);
	}

	@Test
	public void testDeleteAsUser() throws Exception {
		deleteAsUnauthorizedUser(user);
	}

	@Test
	public void testDeleteAsReader() throws Exception {
		deleteAsUnauthorizedUser(reader);
	}

}
