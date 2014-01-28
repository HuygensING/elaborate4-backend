package elaborate.editor.security;

import javax.ws.rs.core.SecurityContext;

import nl.knaw.huygens.security.client.SecurityContextCreator;
import nl.knaw.huygens.security.client.model.SecurityInformation;

public class ElaborateSecurityContextCreator implements SecurityContextCreator {

	@Override
	public SecurityContext createSecurityContext(SecurityInformation securityInformation) {
		//TODO: implement
		return null;
	}

}
