package elaborate.editor.model.orm.service;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import elaborate.editor.config.Configuration;
import elaborate.editor.model.orm.User;
import elaborate.util.Emailer;

public class UserServiceTest {

	@Test
	public void testEmailToBeSentHasCorrectValues() throws Exception {
		String from_email = "from@example.org";
		String from_name = "From Name";
		String to_email = "to@example.org";
		String username = "bladiebla";

		Configuration config = mock(Configuration.class);
		when(config.getSetting(Configuration.FROM_EMAIL)).thenReturn(from_email);
		when(config.getSetting(Configuration.FROM_NAME)).thenReturn(from_name);

		Emailer emailer = mock(Emailer.class);

		User user = mock(User.class);
		when(user.getUsername()).thenReturn(username);
		when(user.getEmail()).thenReturn(to_email);

		UserService userservice = UserService.instance();
		userservice.composeAndSendEmail(config, emailer, user);
		verify(emailer).sendMail(eq(from_email), eq(from_name), eq(to_email), anyString(), contains(username));
	}
}
