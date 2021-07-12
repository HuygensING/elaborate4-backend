package elaborate.editor.model.orm.service;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2021 Huygens ING
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

import org.junit.Test;

import elaborate.editor.config.Configuration;
import elaborate.editor.model.orm.User;
import elaborate.util.Emailer;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
