package elaborate.editor.model;

import com.sun.jersey.api.client.Client;

import nl.knaw.huygens.security.client.HuygensAuthorizationHandler;
import nl.knaw.huygens.security.client.UnauthorizedException;
import nl.knaw.huygens.security.client.model.SecurityInformation;

import elaborate.editor.config.Configuration;
import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.service.UserService;

public class SecurityWrapper {
	static final Configuration config = Configuration.instance();
	static final UserService userservice = UserService.instance();

	static final Client client = new Client();
	static final HuygensAuthorizationHandler hah = new HuygensAuthorizationHandler(client, config.getSetting("security.hss.url"), config.getSetting("security.hss.credentials"));

	public static Session createSession(String sessionToken) throws UnauthorizedException {
		SecurityInformation securityInformation = hah.getSecurityInformation(sessionToken);
		String emailAddress = securityInformation.getEmailAddress();
		User user = userservice.getByEmail(emailAddress);
		if (user == null) {
			throw new UnauthorizedException("The email address given by the securityservice (" + emailAddress + ") is not registered to a user in elaborate");
		}
		return new Session(user.getId());
	}

	public static void delete(String sessionId) {
		try {
			hah.logout(sessionId);
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		}
	}
}
