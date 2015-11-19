package elaborate.editor.resources.orm;

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

import javax.persistence.EntityManager;
import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import elaborate.editor.model.ModelFactory;
import elaborate.editor.model.orm.User;

@Ignore
public class UserResourceTest extends ResourceTest {
	private static EntityManager entityManager;

	public UserResourceTest() {
		super();
	}

	@Before
	public void setUp1() {
		entityManager = entityManagerFactory.createEntityManager();
		User user1 = ModelFactory.create(User.class).setUsername("john").setFirstName("John").setLastName("Doe");
		User user2 = ModelFactory.create(User.class).setUsername("butch").setFirstName("Butcher").setLastName("Baker");
		entityManager.getTransaction().begin();
		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Test
	public void testGetUsers() {
		String responseMsg = resource().path("users").get(String.class);
		assertThat(responseMsg.contains("\"John\"")).isTrue();
		assertThat(responseMsg.contains("\"Butcher\"")).isTrue();
	}

	@Test
	public void testAddUser() {
		resource().path("users").accept(MediaType.APPLICATION_JSON).post("{\"username\":\"bla\"}");
	}

}
