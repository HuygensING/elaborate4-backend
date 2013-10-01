package elaborate.editor.model.orm;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import elaborate.editor.model.ModelFactory;

public class UserTest extends StoredEntityTest {
  //  @Test
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
    assertEquals(1, result.size());
    for (User u : result) {
      Set<UserSetting> userSettings = u.getUserSettings();
      assertEquals(2, userSettings.size());
      UserSetting setting = userSettings.iterator().next();
      assertEquals("root", setting.getUser().getUsername());
    }
    entityManager.getTransaction().commit();
    entityManager.close();

  }

}
