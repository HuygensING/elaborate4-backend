package elaborate.editor.model.orm;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import elaborate.AbstractTest;

public class StoredEntityTest extends AbstractTest {

  protected static EntityManagerFactory entityManagerFactory;

  @BeforeClass
  public static void setUpClass() throws Exception {
    entityManagerFactory = Persistence.createEntityManagerFactory("nl.knaw.huygens.elaborate.test.jpa");
    //    entityManagerFactory = Persistence.createEntityManagerFactory("nl.knaw.huygens.elaborate.test.psql.jpa");
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
    if (entityManagerFactory != null) {
      entityManagerFactory.close();
    }
  }

}
