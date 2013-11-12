package elaborate.editor.model.orm.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import elaborate.AbstractTest;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.User;

public class ProjectEntryServiceTest extends AbstractTest {

  private ProjectEntryService projectEntryService;

  @Before
  public void setUp() throws Exception {
    projectEntryService = new ProjectEntryService();
  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testRead() throws Exception {
    User user = mock(User.class);
    when(user.getId()).thenReturn((long) 1);
    ProjectEntry pe = projectEntryService.read(1, user);
    assertEquals(1, pe.getId());
  }

}
