package elaborate.jaxrs.security;

import java.util.Set;

public class SessionUser implements java.security.Principal {
  public enum Role {
    User, Admin, Projectleader, Reader
  };

  private long userId;
  private String name;
  private String emailAddress;
  private Set<Role> roles;

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

}