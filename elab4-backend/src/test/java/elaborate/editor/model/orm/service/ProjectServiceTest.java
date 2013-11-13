package elaborate.editor.model.orm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.persistence.EntityManager;

import nl.knaw.huygens.jaxrstools.exceptions.UnauthorizedException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import elaborate.editor.model.ModelFactory;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.StoredEntityTest;
import elaborate.editor.model.orm.User;

@Ignore
public class ProjectServiceTest extends StoredEntityTest {
  private static UserService userService;
  private static ProjectService projectService;
  private static User root;
  private static User notRoot;

  @Before
  public void setUp() throws Exception {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    userService = new UserService();
    userService.setEntityManager(entityManager);

    projectService = new ProjectService();
    projectService.setEntityManager(entityManager);

    root = new User().setRoot(true).setUsername("root");
    userService.beginTransaction();
    userService.create(root);
    notRoot = new User().setUsername("notroot");
  }

  @After
  public void tearDown() throws Exception {
    userService.delete(root.getId());
    userService.rollbackTransaction();
  }

  @Test(expected = UnauthorizedException.class)
  public void testCreateAsNotRoot() throws Exception {
    Project project = ModelFactory.createTrackedEntity(Project.class, notRoot).setName("name");
    Project created = projectService.create(project, notRoot);
    assertNotNull(created);
  }

  @Test
  public void testCreateAsRoot() throws Exception {
    Project project = ModelFactory.createTrackedEntity(Project.class, root).setName("name");
    Project created = projectService.create(project, root);
    long project_id = created.getId();
    Project read = projectService.read(project_id, root);
    assertThat(read.getName()).isEqualTo("name");
  }

  @Test
  public void testGetAll() throws Exception {
    List<Project> all = projectService.getAll(root);
    assertFalse(all.isEmpty());
    LOG.info("{}", all.size());
  }

  //  @Test
  //  public void testExportPdf() throws Exception {
  //    projectService.exportPdf(1, root, "editie.pdf");
  //  }
}
