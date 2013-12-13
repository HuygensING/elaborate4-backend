package elaborate.editor.model;

import nl.knaw.huygens.security.client.HuygensAuthorizationHandler;
import nl.knaw.huygens.security.client.UnauthorizedException;
import nl.knaw.huygens.security.client.model.SecurityInformation;

import com.sun.jersey.api.client.Client;

import elaborate.editor.config.Configuration;
import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.service.UserService;

public class SecurityWrapper {
	static Configuration config = Configuration.instance();
	static UserService userservice = UserService.instance();

	static Client client = new Client();
	static HuygensAuthorizationHandler hah = new HuygensAuthorizationHandler(client, config.getSetting("security.hss.url"), config.getSetting("security.hss.credentials"));

	public static Session createSession(String sessionToken) throws UnauthorizedException {
		SecurityInformation securityInformation = hah.getSecurityInformation(sessionToken);
		String emailAddress = securityInformation.getEmailAddress();
		User user = userservice.getByEmail(emailAddress);
		if (user == null) {
			throw new UnauthorizedException("The email address given by the securityservice (" + emailAddress + ") is not registered to a user in elaborate");
		}
		return new Session(user.getId());
	}
}
