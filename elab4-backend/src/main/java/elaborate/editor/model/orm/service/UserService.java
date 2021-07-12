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

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Singleton;
import javax.mail.MessagingException;
import javax.persistence.NoResultException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;
import com.sun.jersey.api.NotFoundException;
import org.apache.commons.lang.RandomStringUtils;

import nl.knaw.huygens.Log;
import nl.knaw.huygens.jaxrstools.exceptions.BadRequestException;
import nl.knaw.huygens.jaxrstools.exceptions.ConflictException;
import nl.knaw.huygens.jaxrstools.exceptions.UnauthorizedException;

import elaborate.editor.config.Configuration;
import elaborate.editor.model.AbstractStoredEntity;
import elaborate.editor.model.ElaborateRoles;
import elaborate.editor.model.UserSettings;
import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.UserSetting;
import elaborate.editor.resources.orm.PasswordData;
import elaborate.freemarker.FreeMarker;
import elaborate.util.Emailer;
import elaborate.util.PasswordUtil;

@Singleton
public class UserService extends AbstractStoredEntityService<User> {
  private static final UserService instance = new UserService();
  private final Map<Long, String> tokenMap = Maps.newHashMap();

  private UserService() {}

  public static UserService instance() {
    return instance;
  }

  @Override
  Class<? extends AbstractStoredEntity<?>> getEntityClass() {
    return User.class;
  }

  @Override
  String getEntityName() {
    return "User";
  }

  /* CRUD methods */
  public void create(User user, User creator) {
    beginTransaction();

    if (creator.getPermissionFor(user).canWrite()) {
      try {
        getEntityManager()
            .createQuery("from User as u where u.username=?1")
            .setParameter(1, user.getUsername())
            .getSingleResult();
        rollbackTransaction();
        throw new ConflictException(
            "a user with username "
                + user.getUsername()
                + " already exists. Usernames must be unique");
      } catch (NoResultException e) {
        // user doesn't already exist, that's good
      }

      normalizeEmailAddress(user);
      User create = super.create(user);
      commitTransaction();

    } else {
      rollbackTransaction();
      throw new UnauthorizedException(
          "user " + creator.getUsername() + " is not authorized to create new users");
    }
  }

  private void normalizeEmailAddress(User user) {
    if (user.getEmail() != null) {
      user.setEmail(user.getEmail().toLowerCase());
    }
  }

  @Override
  public User read(long userId) {
    openEntityManager();
    User user;
    try {
      user = super.read(userId);
    } finally {
      closeEntityManager();
    }
    return user;
  }

  public void update(User user, User modifier) {
    beginTransaction();
    User updated;
    try {
      normalizeEmailAddress(user);
      updated = super.update(user);
    } finally {
      commitTransaction();
    }
  }

  public void delete(long id, User modifier) {
    beginTransaction();
    try {
      super.delete(id);
    } finally {
      commitTransaction();
    }
  }

  /* */
  public ImmutableMap<String, String> getSettings(long id) {
    Builder<String, String> settings = ImmutableMap.builder();

    openEntityManager();
    User user;
    try {
      user = find(User.class, id);
      if (user != null) {
        for (UserSetting setting : user.getUserSettings()) {
          settings.put(setting.getKey(), setting.getValue());
        }
      }

    } finally {
      closeEntityManager();
    }

    if (user == null) {
      throw new NotFoundException("No user found with id " + id);
    }
    return settings.build();
  }

  public User getByUsernamePassword(String username, String password) {
    openEntityManager();
    User user;
    try {
      byte[] encodedPassword = PasswordUtil.encode(password);
      user =
          (User)
              getEntityManager()
                  .createQuery("from User as u where u.username=?1 and u.encodedpassword=?2")
                  .setParameter(1, username)
                  .setParameter(2, encodedPassword)
                  .getSingleResult();

    } catch (NoResultException e) {
      user = null;

    } finally {
      closeEntityManager();
    }

    return user;
  }

  public User getByEmail(String emailAddress) {
    openEntityManager();
    User user;
    try {
      user =
          (User)
              getEntityManager()
                  .createQuery("from User as u where u.email=?1")
                  .setParameter(1, emailAddress.toLowerCase())
                  .getSingleResult();

    } catch (NoResultException e) {
      user = null;

    } finally {
      closeEntityManager();
    }

    return user;
  }

  public User getUser(long id) {
    openEntityManager();
    User user;
    try {
      user = find(User.class, id);
    } finally {
      closeEntityManager();
    }
    return user;
  }

  @Override
  public ImmutableList<User> getAll() {
    openEntityManager();
    ImmutableList<User> all;
    try {
      all = super.getAll();
    } finally {
      closeEntityManager();
    }
    return all;
  }

  public void setSetting(long userId, String key, String value, User modifier) {
    beginTransaction();
    try {
      User user = read(userId);
      if (!modifier.getUsername().equals(user.getUsername())) {
        rollbackTransaction();
        throw new UnauthorizedException(
            MessageFormat.format(
                "{0} is not allowed to change settings for {1}",
                modifier.getUsername(), user.getUsername()));
      }
      UserSetting userSetting = user.setUserSetting(key, value);
      persist(userSetting);
    } finally {
      commitTransaction();
    }
  }

  public void updateSettings(long userId, Map<String, String> newSettingsMap, User modifier) {
    beginTransaction();
    try {
      User user = read(userId);
      if (!modifier.getUsername().equals(user.getUsername())) {
        rollbackTransaction();
        throw new UnauthorizedException(
            MessageFormat.format(
                "{0} is not allowed to change settings for {1}",
                modifier.getUsername(), user.getUsername()));
      }

      for (Entry<String, String> entry : newSettingsMap.entrySet()) {
        UserSetting userSetting = user.setUserSetting(entry.getKey(), entry.getValue());
        persist(userSetting);
      }

      persist(user);
    } finally {
      commitTransaction();
    }
  }

  public void sendResetPasswordMail(String emailAddress) {
    User user = getByEmail(emailAddress);
    if (user == null) {
      throw new BadRequestException("unknown e-mail address: " + emailAddress);
    }
    Configuration config = Configuration.instance();
    Emailer emailer = new Emailer(config.getSetting(Configuration.MAILHOST));
    composeAndSendEmail(config, emailer, user);
  }

  void composeAndSendEmail(Configuration config, Emailer emailer, User user) {
    String from_email = config.getSetting(Configuration.FROM_EMAIL);
    String from_name = config.getSetting(Configuration.FROM_NAME);
    String to_email = user.getEmail();
    String subject = "Elaborate4 Password reset";
    Map<String, Object> map = Maps.newHashMap();
    map.put("user", user.getUsername());
    String token = RandomStringUtils.randomAlphanumeric(20);
    Log.info("token={}", token);
    tokenMap.put(user.getId(), token);
    map.put(
        "url",
        MessageFormat.format(
            "{0}/resetpassword?emailaddress={1}&token={2}", //
            config.getSetting(Configuration.WORK_URL), //
            user.getEmail(), //
            token //
            ));
    String body = FreeMarker.templateToString("email.ftl", map, getClass());
    try {
      emailer.sendMail(from_email, from_name, to_email, subject, body);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }

  public void resetPassword(PasswordData passwordData) {
    String emailAddress = passwordData.getEmailAddress();
    User user = getByEmail(emailAddress);
    if (user == null) {
      throw new BadRequestException("unknown e-mail address: " + emailAddress);
    }
    long userId = user.getId();
    String expectedToken = tokenMap.get(userId);
    if (!passwordData.getToken().equals(expectedToken)) {
      throw new BadRequestException("token and e-mail address don't match");
    }

    tokenMap.remove(userId);
    beginTransaction();
    try {
      user = super.read(userId);
      byte[] encodedPassword = PasswordUtil.encode(passwordData.getNewPassword());
      user.setEncodedPassword(encodedPassword);
      persist(user);
    } finally {
      commitTransaction();
    }
  }

  public void makeProjectLeader(Long userId, User user) {
    beginTransaction();
    User projectLeader = super.read(userId);
    if (!projectLeader.hasRole(ElaborateRoles.PROJECTLEADER)) {
      String roleString = projectLeader.getRoleString() + "," + ElaborateRoles.PROJECTLEADER;
      projectLeader.setRoleString(roleString);
      commitTransaction();
    } else {
      rollbackTransaction();
    }
  }

  public void setUserIsLoggedIn(User user) {
    updateLoginStatus(user, false);
  }

  public void setUserIsLoggedOut(User user) {
    updateLoginStatus(user, true);
  }

  private void updateLoginStatus(User user, boolean loggingOff) {
    beginTransaction();
    user = super.read(user.getId());
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    if (loggingOff && user.isLoggedIn()) {
      persist(user.setUserSetting(UserSettings.ONLINE_STATUS, User.STATUS_OFFLINE));
      persist(user.setUserSetting(UserSettings.LOGOUT_TIME, simpleDateFormat.format(new Date())));
    } else if (!loggingOff && !user.isLoggedIn()) {
      persist(user.setUserSetting(UserSettings.ONLINE_STATUS, User.STATUS_ONLINE));
      persist(user.setUserSetting(UserSettings.LOGIN_TIME, simpleDateFormat.format(new Date())));
    }
    commitTransaction();
  }
}
