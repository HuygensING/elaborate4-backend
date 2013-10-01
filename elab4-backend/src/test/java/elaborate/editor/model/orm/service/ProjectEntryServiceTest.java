package elaborate.editor.model.orm.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;

import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.User;

public class ProjectEntryServiceTest {

  private ProjectEntryService projectEntryService;

  @Before
  public void setUp() throws Exception {
    projectEntryService = new ProjectEntryService();
  }

  @After
  public void tearDown() throws Exception {}

  //  @Test
  public void testRead() throws Exception {
    User user = mock(User.class);
    when(user.getId()).thenReturn((long) 1);
    ProjectEntry pe = projectEntryService.read(1, user);
    assertEquals(1, pe.getId());
  }

}
