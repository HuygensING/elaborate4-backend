package elaborate.editor.model.orm;

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

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import elaborate.editor.model.ModelFactory;

class UserTest extends StoredEntityTest {
	// @Test
	public void test2() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		entityManager.getTransaction().begin();
		User user = ModelFactory.create(User.class).setUsername("root").setFirstName("firstName").setLastName("last");
		entityManager.persist(user);
		UserSetting setting1 = user.addUserSetting("key1", "value1");
		entityManager.persist(setting1);
		UserSetting setting2 = user.addUserSetting("key2", "value2");
		entityManager.persist(setting2);
		entityManager.getTransaction().commit();
		entityManager.close();

		entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		List<User> result = entityManager.createQuery("from User", User.class).getResultList();
		assertThat(result).hasSize(1);

		for (User u : result) {
			Set<UserSetting> userSettings = u.getUserSettings();
			assertThat(userSettings).hasSize(2);

			UserSetting setting = userSettings.iterator().next();
			assertThat(setting.getUser()).hasUsername("root");
		}
		entityManager.getTransaction().commit();
		entityManager.close();

	}

}
