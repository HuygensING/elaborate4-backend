package elaborate.editor.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import elaborate.editor.model.ElaborateRoles;
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
		assertThat(elaborateSecurityContext.getUser()).isEqualTo(user);
	}

	@Test
	public void testGetUserPrincipal() throws Exception {
		assertThat(elaborateSecurityContext.getUserPrincipal().getName()).isEqualTo(user.getUsername());
	}

	@Test
	public void testIsUserInRole() throws Exception {
		assertThat(elaborateSecurityContext.isUserInRole(ElaborateRoles.ADMIN)).isTrue();
	}

	@Test
	public void testGetAuthenticationScheme() throws Exception {
		assertThat(elaborateSecurityContext.getAuthenticationScheme()).isEqualTo("SimpleAuth");
	}

	@Test
	public void testIsSecure() throws Exception {
		assertThat(elaborateSecurityContext.isSecure()).isFalse();
	}

}
