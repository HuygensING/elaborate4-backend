package elaborate.editor.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Singleton;

import org.apache.commons.lang.RandomStringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.service.UserService;

@Singleton
public class SessionService extends LoggableObject {
  private static SessionService instance;

  UserService userService = new UserService();

  private static final int MINUTES = 1000 * 60;
  private static final int SESSION_TIMEOUT = 60 * MINUTES;
  private static final int SESSIONID_SIZE = 20;
  Map<String, Session> sessionMap = Maps.newHashMap();

  public String startSession(User user) {
    String sessionId = RandomStringUtils.randomAlphanumeric(SESSIONID_SIZE);
    sessionMap.put(sessionId, new Session(user.getId()));
    //    LOG.info("sessionMap={}", sessionMap);
    return sessionId;
  }

  public void stopSession(String sessionId) {
    sessionMap.remove(sessionId);
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

  public ElaborateSecurityContext getSecurityContext(String key) {
    Session session = sessionMap.get(key);
    if (session != null && session.isActive()) {
      session.update();
      long userId = session.getUserId();
      User user = userService.read(userId);
      if (user != null) {
        return new ElaborateSecurityContext(user);
      }
    }
    return null;
  }

  public static class Session {
    Date lastAccessed;
    long userId;

    public Session(long userId) {
      this.userId = userId;
      lastAccessed = new Date();
    }

    public long getUserId() {
      return userId;
    }

    public void update() {
      lastAccessed = new Date();
    }

    public boolean isActive() {
      long diff = new Date().getTime() - lastAccessed.getTime();
      return (diff < SESSION_TIMEOUT);
    }
  }

  public static SessionService instance() {
    if (instance == null) {
      instance = new SessionService();
    }
    return instance;
  }

}
