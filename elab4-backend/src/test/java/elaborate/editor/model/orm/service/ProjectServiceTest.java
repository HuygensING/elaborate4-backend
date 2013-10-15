package elaborate.editor.model.orm.service;

import static org.junit.Assert.*;

import java.util.List;

import nl.knaw.huygens.jaxrstools.exceptions.UnauthorizedException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import elaborate.AbstractTest;
import elaborate.editor.model.ModelFactory;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.User;

@Ignore
public class ProjectServiceTest extends AbstractTest {
  private UserService userService;
  private ProjectService projectService;
  private User root;
  private User notRoot;

  @Before
  public void setUp() throws Exception {
    userService = new UserService();
    projectService = new ProjectService();
    root = new User().setRoot(true).setUsername("root");
    userService.create(root);
    notRoot = new User().setUsername("notroot");
  }

  @After
  public void tearDown() throws Exception {
    userService.delete(root.getId());
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
    assertEquals("name", read.getName());
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
