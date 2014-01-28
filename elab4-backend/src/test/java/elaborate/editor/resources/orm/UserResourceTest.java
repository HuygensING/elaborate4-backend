package elaborate.editor.resources.orm;

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
