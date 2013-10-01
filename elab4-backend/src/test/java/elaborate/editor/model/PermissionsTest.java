package elaborate.editor.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import elaborate.editor.model.orm.User;

public class PermissionsTest {
  User root;
  User admin;
  User projectleader;
  User plainUser;
  private ImmutableList<User> allusers;
  private ImmutableList<User> writeusers;

  @Before
  public void setUp() throws Exception {
    root = new User().setId(0).setRoot(true);
    admin = new User().setId(1).setRoleString(ElaborateRoles.getRolestringFor(ElaborateRoles.ADMIN));
    projectleader = new User().setId(3).setRoleString(ElaborateRoles.getRolestringFor(ElaborateRoles.PROJECTLEADER));
    plainUser = new User().setId(2).setRoleString(ElaborateRoles.getRolestringFor(ElaborateRoles.USER));
    allusers = ImmutableList.of(root, admin, projectleader, plainUser);
    writeusers = ImmutableList.of(root, admin, projectleader);
  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testGetPermission1() throws Exception {
    for (User writeuser : writeusers) {
      for (User user : allusers) {
        Permission permission = Permissions.getPermission(writeuser, user);
        assertTrue(permission.canWrite());
      }
    }
  }

  @Test
  public void testGetPermission2() throws Exception {
    Permission permission = Permissions.getPermission(plainUser, plainUser);
    assertTrue(permission.canWrite());
  }

  @Test
  public void testGetPermission3() throws Exception {
    Permission permission = Permissions.getPermission(plainUser, admin);
    assertFalse(permission.canWrite());
  }

  @Test
  public void testGetPermission4() throws Exception {
    Permission permission = Permissions.getPermission(null, plainUser);
    assertFalse(permission.canWrite());
  }
}
