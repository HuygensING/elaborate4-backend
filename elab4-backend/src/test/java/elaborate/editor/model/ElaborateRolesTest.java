package elaborate.editor.model;

import static com.google.common.collect.Lists.*;
import static elaborate.editor.model.ElaborateRoles.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import org.junit.Test;

public class ElaborateRolesTest {

  @Test
  public void testHighestRole1() throws Exception {
    assertThat(highestRole(newArrayList(ADMIN, USER))).isEqualTo(ADMIN);
  }

  @Test
  public void testHighestRole2() throws Exception {
    assertThat(highestRole(newArrayList(USER, READER))).isEqualTo(USER);
  }

  @Test
  public void testHighestRole3() throws Exception {
    assertThat(highestRole(newArrayList(USER, READER, PROJECTLEADER))).isEqualTo(PROJECTLEADER);
  }

  @Test
  public void testHighestRole4() throws Exception {
    assertThat(highestRole(newArrayList(READER))).isEqualTo(READER);
  }

  @Test
  public void testGetRolestringForReader() throws Exception {
    assertThat(getRolestringFor(READER)).isEqualTo("READER");
  }

  @Test
  public void testGetRolestringForUser() throws Exception {
    assertThat(getRolestringFor(USER)).isEqualTo("READER,USER");
  }

  @Test
  public void testGetRolestringForProjectLeader() throws Exception {
    assertThat(getRolestringFor(PROJECTLEADER)).isEqualTo("READER,PROJECTLEADER,USER");
  }

  @Test
  public void testGetRolestringForAdmin() throws Exception {
    assertThat(getRolestringFor(ADMIN)).isEqualTo("READER,ADMIN,PROJECTLEADER,USER");
  }

}
