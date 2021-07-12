package elaborate.editor.model;

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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Singleton;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.RandomStringUtils;

import nl.knaw.huygens.security.client.UnauthorizedException;

import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.service.UserService;
import elaborate.editor.security.ElaborateSecurityContext;

@Singleton
public class SessionService {
  private static SessionService instance;
  private static final int MINUTES = 1000 * 60;
  static final int SESSION_TIMEOUT = 8 * 60 * MINUTES;
  private static final int SESSIONID_SIZE = 20;
  final Map<String, Session> sessionMap = Maps.newHashMap();
  final UserService userService = UserService.instance();

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
    // Log.info("sessionMap={}", sessionMap);
    return sessionId;
  }

  public void stopSession(String sessionId) {
    Session session = sessionMap.remove(sessionId);
    if (session != null && session.isFederated()) {
      SecurityWrapper.delete(sessionId);
    }
    // Log.info("sessionMap={}", sessionMap);
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

  public ElaborateSecurityContext getSecurityContext(String scheme, String key)
      throws UnauthorizedException {
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

  public User getSessionUser(String sessionId) throws UnauthorizedException {
    Session session = sessionMap.get(sessionId);
    if (session == null) {
      session = SecurityWrapper.createSession(sessionId);
      sessionMap.put(sessionId, session);
    }
    long userId = session.getUserId();
    return userService.read(userId);
  }

  private static final Comparator<SessionUserInfo> ON_LAST_ACCESSED =
      new Comparator<SessionUserInfo>() {
        @Override
        public int compare(SessionUserInfo i0, SessionUserInfo i1) {
          return i1.lastAccessed.compareTo(i0.lastAccessed);
        }
      };

  public Collection<SessionUserInfo> getActiveSessionUsersInfo() {
    removeExpiredSessions();
    Collection<Session> values = sessionMap.values();
    Map<Long, SessionUserInfo> userinfo = Maps.newHashMap();
    for (Session session : values) {
      long userId = session.getUserId();
      if (userinfo.containsKey(userId)) {
        SessionUserInfo info = userinfo.get(userId);
        if (info.lastAccessed.before(session.getLastAccessed())) {
          info.lastAccessed = session.getLastAccessed();
        }
      } else {
        SessionUserInfo info = new SessionUserInfo();
        User user = userService.read(userId);
        info.email = user.getEmail();
        info.lastAccessed = session.getLastAccessed();
        info.username = user.getUsername();
        userinfo.put(userId, info);
      }
    }
    List<SessionUserInfo> activeSessionUserInfoCollection = Lists.newArrayList(userinfo.values());
    Collections.sort(activeSessionUserInfoCollection, ON_LAST_ACCESSED);
    return activeSessionUserInfoCollection;
  }

  public static class SessionUserInfo {
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd HH:mm:ss",
        timezone = "Europe/Amsterdam")
    public Date lastAccessed;

    public String email;
    public String username;
  }
}
