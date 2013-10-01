package elaborate.editor.model;

import static com.google.common.collect.Lists.*;
import static elaborate.editor.model.ElaborateRoles.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class ElaborateRolesTest {

  @Test
  public void testHighestRole1() throws Exception {
    assertEquals(ADMIN, highestRole(newArrayList(ADMIN, USER)));
  }

  @Test
  public void testHighestRole2() throws Exception {
    assertEquals(USER, highestRole(newArrayList(USER, READER)));
  }

  @Test
  public void testHighestRole3() throws Exception {
    assertEquals(PROJECTLEADER, highestRole(newArrayList(USER, READER, PROJECTLEADER)));
  }

  @Test
  public void testHighestRole4() throws Exception {
    assertEquals(READER, highestRole(newArrayList(READER)));
  }

  @Test
  public void testGetRolestringForReader() throws Exception {
    assertEquals("READER", getRolestringFor(READER));
  }

  @Test
  public void testGetRolestringForUser() throws Exception {
    assertEquals("READER,USER", getRolestringFor(USER));
  }

  @Test
  public void testGetRolestringForProjectLeader() throws Exception {
    assertEquals("READER,PROJECTLEADER,USER", getRolestringFor(PROJECTLEADER));
  }

  @Test
  public void testGetRolestringForAdmin() throws Exception {
    assertEquals("READER,ADMIN,PROJECTLEADER,USER", getRolestringFor(ADMIN));
  }

}
