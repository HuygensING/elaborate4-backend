package elaborate.editor.model;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2014 Huygens ING
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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Singleton;

import nl.knaw.huygens.LoggableObject;
import nl.knaw.huygens.security.client.UnauthorizedException;

import org.apache.commons.lang.RandomStringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.service.UserService;
import elaborate.editor.security.ElaborateSecurityContext;

@Singleton
public class SessionService extends LoggableObject {
	private static SessionService instance;
	private static final int MINUTES = 1000 * 60;
	static final int SESSION_TIMEOUT = 60 * MINUTES;
	private static final int SESSIONID_SIZE = 20;
	Map<String, Session> sessionMap = Maps.newHashMap();
	UserService userService = UserService.instance();

	private SessionService() {}

	public static SessionService instance() {
		if (instance == null) {
			instance = new SessionService();
		}
		return instance;
	}

	public String startSession(User user) {
		String sessionId = RandomStringUtils.randomAlphanumeric(SESSIONID_SIZE);
		sessionMap.put(sessionId, new Session(user.getId()));
		//    LOG.info("sessionMap={}", sessionMap);
		return sessionId;
	}

	public void stopSession(String sessionId) {
		Session session = sessionMap.remove(sessionId);
		if (session != null && session.isFederated()) {
			SecurityWrapper.delete(sessionId);
		}
		//    LOG.info("sessionMap={}", sessionMap);
	}

	public boolean isSessionActive(String sessionId) {
		return sessionMap.containsKey(sessionId) && sessionMap.get(sessionId).isActive();
	}

	public void updateSession(String sessionId) {
		if (sessionMap.containsKey(sessionId)) {
			sessionMap.get(sessionId).update();
		}
	}

	public void removeExpiredSessions() {
		List<String> sessionsToRemove = Lists.newArrayList();
		for (Entry<String, Session> entry : sessionMap.entrySet()) {
			if (!entry.getValue().isActive()) {
				sessionsToRemove.add(entry.getKey());
			}
		}
		for (String sessionId : sessionsToRemove) {
			sessionMap.remove(sessionId);
		}
	}

	public static final String SIMPLEAUTH = "SimpleAuth";
	public static final String FEDERATED = "Federated";
	public static final List<String> SCHEMES = ImmutableList.of(SIMPLEAUTH, FEDERATED);

	public ElaborateSecurityContext getSecurityContext(String scheme, String key) throws UnauthorizedException {
		if (SCHEMES.contains(scheme)) {
			Session session = sessionMap.get(key);
			if (session != null) {
				if (session.isActive()) {
					session.update();
					return getElaborateSecurityContext(session);
				}
			} else if (FEDERATED.equals(scheme)) {
				session = SecurityWrapper.createSession(key);
				if (session != null) {
					sessionMap.put(key, session);
					return getElaborateSecurityContext(session);
				}
			}
		}
		return null;
	}

	private ElaborateSecurityContext getElaborateSecurityContext(Session session) {
		long userId = session.getUserId();
		User user = userService.read(userId);
		return user != null ? new ElaborateSecurityContext(user) : null;
	}

	public User getSessionUser(String hsid) throws UnauthorizedException {
		Session session = sessionMap.get(hsid);
		if (session == null) {
			session = SecurityWrapper.createSession(hsid);
		}
		long userId = session.getUserId();
		User user = userService.read(userId);
		return user;
	}
}
