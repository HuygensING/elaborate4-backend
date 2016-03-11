package elaborate.editor.resources.orm;

import static org.mockito.Mockito.mock;

import org.junit.Test;

import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.service.ProjectService;
import elaborate.editor.resources.orm.wrappers.TranscriptionWrapper;

public class ProjectEntriesResourceTest {
  @Test
  public void testcheckForWellFormedBodyHasNoProblemWithNBSPEntity() {
    ProjectEntriesResource r = new ProjectEntriesResource(mockUser(), mockProjectService(), 1L);
    String body = "<body>bla bla bla<br>&nbsp;more&nbsp;bla&nbsp;gaga</body>";
    TranscriptionWrapper transcriptionWrapper = new TranscriptionWrapper().setBody(body);
    r.checkForWellFormedBody(transcriptionWrapper);
  }

  private ProjectService mockProjectService() {
    return mock(ProjectService.class);
  }

  private User mockUser() {
    return mock(User.class);
  }
}
