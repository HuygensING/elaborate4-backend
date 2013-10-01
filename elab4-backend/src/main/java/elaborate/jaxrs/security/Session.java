package elaborate.jaxrs.security;

import java.io.Serializable;
import java.util.Date;

public class Session implements Serializable {
  private static final long serialVersionUID = -309056773365000560L;

  private String sessionId;
  private long userId;
  private boolean active;
  private boolean secure;

  private Date createTime;
  private Date lastAccessedTime;

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public boolean isSecure() {
    return secure;
  }

  public void setSecure(boolean secure) {
    this.secure = secure;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getLastAccessedTime() {
    return lastAccessedTime;
  }

  public void setLastAccessedTime(Date lastAccessedTime) {
    this.lastAccessedTime = lastAccessedTime;
  }
}
