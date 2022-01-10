package elaborate.editor.resources.orm;

/*-
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2022 Huygens ING
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ProjectResourceTest {

  @Test
  public void testAddingOneProjectUserIsAllowed() {
    List<Long> oldUserIds = new ArrayList<Long>(asList(1L, 2L, 3L));
    List<Long> newUserIds = new ArrayList<Long>(asList(1L, 2L, 3L, 4L));
    boolean ok = ProjectResource.updateIsAllowed(oldUserIds, newUserIds);
    assertThat(ok).isTrue();
  }

  @Test
  public void testDeletingOneProjectUserIsAllowed() {
    List<Long> oldUserIds = new ArrayList<Long>(asList(1L, 2L, 3L));
    List<Long> newUserIds = new ArrayList<Long>(asList(1L, 3L));
    boolean ok = ProjectResource.updateIsAllowed(oldUserIds, newUserIds);
    assertThat(ok).isTrue();
  }

  @Test
  public void testAddingTwoProjectUsersIsNotAllowed() {
    List<Long> oldUserIds = new ArrayList<Long>(asList(1L, 2L, 3L));
    List<Long> newUserIds = new ArrayList<Long>(asList(1L, 2L, 3L, 4L, 5L));
    boolean ok = ProjectResource.updateIsAllowed(oldUserIds, newUserIds);
    assertThat(ok).isFalse();
  }

  @Test
  public void testDeletingTwoProjectUsersIsNotAllowed() {
    List<Long> oldUserIds = new ArrayList<Long>(asList(1L, 2L, 3L));
    List<Long> newUserIds = new ArrayList<Long>(Collections.singletonList(3L));
    boolean ok = ProjectResource.updateIsAllowed(oldUserIds, newUserIds);
    assertThat(ok).isFalse();
  }

  @Test
  public void testChangingNothingIsAllowed() {
    List<Long> oldUserIds = new ArrayList<Long>(asList(1L, 2L, 3L));
    List<Long> newUserIds = new ArrayList<Long>(asList(1L, 2L, 3L));
    boolean ok = ProjectResource.updateIsAllowed(oldUserIds, newUserIds);
    assertThat(ok).isTrue();
  }

  @Test
  public void testChangingEverythingIsNotAllowed() {
    List<Long> oldUserIds = new ArrayList<Long>(asList(1L, 2L, 3L));
    List<Long> newUserIds = new ArrayList<Long>(asList(4L, 5L, 6L));

    boolean ok = ProjectResource.updateIsAllowed(oldUserIds, newUserIds);
    assertThat(ok).isFalse();
  }
}
