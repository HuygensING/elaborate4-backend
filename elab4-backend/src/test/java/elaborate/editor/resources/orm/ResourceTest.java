package elaborate.editor.resources.orm;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

public class ResourceTest extends JerseyTest {
  public Logger LOG = LoggerFactory.getLogger(getClass());
  protected static EntityManagerFactory entityManagerFactory;

  public ResourceTest() {
    super(new WebAppDescriptor.Builder()//
        .initParam("com.sun.jersey.config.property.packages", "elaborate.editor.resources;elaborate.editor.providers;nl.knaw.huygens.jaxrstools.resources;nl.knaw.huygens.jaxrstools.providers")//
        .initParam("com.sun.jersey.spi.container.ResourceFilters", "elaborate.jaxrs.filters.ElaborateResourceFilterFactory")//
        .build());
    //    super("elaborate.editor.resources", "elaborate.editor.providers");
  }

  @Before
  public void setUpRT() throws Exception {
    entityManagerFactory = Persistence.createEntityManagerFactory("nl.knaw.huygens.elaborate.test.jpa");
    //    entityManagerFactory = Persistence.createEntityManagerFactory("nl.knaw.huygens.elaborate.jpa");
  }

  @After
  public void tearDownRT() throws Exception {
    entityManagerFactory.close();
  }

}
