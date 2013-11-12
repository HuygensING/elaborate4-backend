package elaborate.editor.resources.orm;

import javax.persistence.EntityManagerFactory;

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
  }

}
