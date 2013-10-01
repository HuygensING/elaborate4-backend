package elaborate.editor.model.orm;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;

public class StoredEntityTest {

  protected EntityManagerFactory entityManagerFactory;

  @Before
  public void setUp() throws Exception {
    //    entityManagerFactory = Persistence.createEntityManagerFactory("nl.knaw.huygens.elaborate.test.jpa");
    entityManagerFactory = Persistence.createEntityManagerFactory("nl.knaw.huygens.elaborate.test.psql.jpa");
  }

  @After
  public void tearDown() throws Exception {
    if (entityManagerFactory != null) {
      entityManagerFactory.close();
    }
  }

}
