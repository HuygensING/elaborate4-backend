package elaborate.editor.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import elaborate.editor.model.orm.User;

public class ElaborateSecurityContextTest {
  private User user;
  private ElaborateSecurityContext elaborateSecurityContext;

  @Before
  public void setUp() {
    user = mock(User.class);
    when(user.getUsername()).thenReturn("username");
    when(user.hasRole("ADMIN")).thenReturn(true);

    elaborateSecurityContext = new ElaborateSecurityContext(user);
  }

  @Test
  public void testGetUser() throws Exception {
    assertEquals(user, elaborateSecurityContext.getUser());
  }

  @Test
  public void testGetUserPrincipal() throws Exception {
    assertEquals(user.getUsername(), elaborateSecurityContext.getUserPrincipal().getName());
  }

  @Test
  public void testIsUserInRole() throws Exception {
    assertTrue(elaborateSecurityContext.isUserInRole(ElaborateRoles.ADMIN));
  }

  @Test
  public void testGetAuthenticationScheme() throws Exception {
    assertEquals("SimpleAuth", elaborateSecurityContext.getAuthenticationScheme());
  }

  @Test
  public void testIsSecure() throws Exception {
    assertFalse(elaborateSecurityContext.isSecure());
  }

}
