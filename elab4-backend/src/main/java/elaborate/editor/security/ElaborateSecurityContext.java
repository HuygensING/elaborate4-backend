package elaborate.editor.security;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2022 Huygens ING
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

import java.security.Principal;
import javax.ws.rs.core.SecurityContext;

import elaborate.editor.model.SessionService;
import elaborate.editor.model.orm.User;

public class ElaborateSecurityContext implements SecurityContext {
  private final User user;

  public ElaborateSecurityContext(User user) {
    this.user = user;
  }

  public User getUser() {
    return user;
  }

  @Override
  public Principal getUserPrincipal() {
    return () -> user.getUsername();
  }

  @Override
  public boolean isUserInRole(String role) {
    return user.hasRole(role);
  }

  @Override
  public boolean isSecure() {
    return false;
  }

  @Override
  public String getAuthenticationScheme() {
    return SessionService.SIMPLEAUTH;
  }
}
