package elaborate.editor.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class PermissionTest {
  Permission permission;

  @Before
  public void setUp() throws Exception {
    permission = new Permission();
  }

  @Test
  public void testAllow() throws Exception {
    assertThat(permission.can(Action.ADD)).isFalse();

    permission.allow(Action.ADD);
    assertThat(permission.can(Action.ADD)).isTrue();

    permission.disallow(Action.ADD);
    assertThat(permission.can(Action.ADD)).isFalse();
  }

  @Test
  public void testSetCanRead() throws Exception {
    assertThat(permission.canRead()).isFalse();

    permission.setCanRead(true);
    assertThat(permission.canRead()).isTrue();
  }

  @Test
  public void testSetCanWrite() throws Exception {
    assertThat(permission.canRead()).isFalse();
    assertThat(permission.canWrite()).isFalse();

    permission.setCanWrite(true);
    assertThat(permission.canRead()).isTrue();
    assertThat(permission.canWrite()).isTrue();

    permission.setCanWrite(false);
    assertThat(permission.canRead()).isTrue();
    assertThat(permission.canWrite()).isFalse();
  }

}
