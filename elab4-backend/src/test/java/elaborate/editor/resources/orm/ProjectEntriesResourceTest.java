package elaborate.editor.resources.orm;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2021 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.junit.Test;

import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.service.ProjectService;
import elaborate.editor.resources.orm.wrappers.TranscriptionWrapper;

import static org.mockito.Mockito.mock;

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
