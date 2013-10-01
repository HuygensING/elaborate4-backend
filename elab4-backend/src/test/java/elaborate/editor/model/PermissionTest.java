package elaborate.editor.model;

import static org.junit.Assert.*;

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
    assertFalse(permission.can(Action.ADD));
    permission.allow(Action.ADD);
    assertTrue(permission.can(Action.ADD));
    permission.disallow(Action.ADD);
    assertFalse(permission.can(Action.ADD));
  }

  @Test
  public void testSetCanRead() throws Exception {
    assertFalse(permission.canRead());
    permission.setCanRead(true);
    assertTrue(permission.canRead());
  }

  @Test
  public void testSetCanWrite() throws Exception {
    assertFalse(permission.canRead());
    assertFalse(permission.canWrite());
    permission.setCanWrite(true);
    assertTrue(permission.canRead());
    assertTrue(permission.canWrite());
    permission.setCanWrite(false);
    assertTrue(permission.canRead());
    assertFalse(permission.canWrite());
  }

}
