package elaborate.editor.resources.orm;

import java.util.ArrayList;
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
    List<Long> newUserIds = new ArrayList<Long>(asList(3L));
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
