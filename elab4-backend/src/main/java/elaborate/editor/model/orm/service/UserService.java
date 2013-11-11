package elaborate.editor.model.orm.service;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Singleton;
import javax.persistence.NoResultException;

import nl.knaw.huygens.jaxrstools.exceptions.ConflictException;
import nl.knaw.huygens.jaxrstools.exceptions.UnauthorizedException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.sun.jersey.api.NotFoundException;

import elaborate.editor.model.AbstractStoredEntity;
import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.UserSetting;
import elaborate.util.PasswordUtil;

@Singleton
public class UserService extends AbstractStoredEntityService<User> {

  @Override
  Class<? extends AbstractStoredEntity<?>> getEntityClass() {
    return User.class;
  }

  @Override
  String getEntityName() {
    return "User";
  }

  /* CRUD methods */
  public User create(User user, User creator) {
    beginTransaction();

    if (creator.getPermission(user).canWrite()) {
      try {
        getEntityManager().createQuery("from User as u where u.username=?1").setParameter(1, user.getUsername()).getSingleResult();
        rollbackTransaction();
        throw new ConflictException("a user with username " + user.getUsername() + " already exists. Usernames must be unique");
      } catch (NoResultException e) {
        // user doesn't already exist, that's good
      }

      User create = super.create(user);
      commitTransaction();
      return create;

    } else {
      rollbackTransaction();
      throw new UnauthorizedException("user " + creator.getUsername() + " is not authorized to create new users");
    }
  }

  @Override
  public User read(long userId) {
    openEntityManager();
    User user = super.read(userId);
    closeEntityManager();
    return user;
  }

  public User update(User user, User modifier) {
    beginTransaction();
    User updated = super.update(user);
    commitTransaction();
    return updated;
  }

  public void delete(long id, User modifier) {
    beginTransaction();
    super.delete(id);
    commitTransaction();
  }

  /* */
  public ImmutableMap<String, String> getSettings(long id) {
    Builder<String, String> settings = ImmutableMap.<String, String> builder();

    openEntityManager();

    User user = find(User.class, id);
    if (user != null) {
      for (UserSetting setting : user.getUserSettings()) {
        settings.put(setting.getKey(), setting.getValue());
      }
    }

    closeEntityManager();

    if (user == null) {
      throw new NotFoundException("No user found with id " + id);
    }
    return settings.build();
  }

  public User getByUsernamePassword(String username, String password) {
    openEntityManager();

    byte[] encodedPassword = PasswordUtil.encode(password);
    User user;
    try {
      user = (User) getEntityManager().createQuery("from User as u where u.username=?1 and u.encodedpassword=?2").setParameter(1, username).setParameter(2, encodedPassword).getSingleResult();
    } catch (NoResultException e) {
      user = null;
    }

    closeEntityManager();

    return user;
  }

  public User getUser(long id) {
    openEntityManager();
    User user = find(User.class, id);
    closeEntityManager();
    return user;
  }

  @Override
  public ImmutableList<User> getAll() {
    openEntityManager();
    ImmutableList<User> all = super.getAll();
    closeEntityManager();
    return all;
  }

  public void setSetting(long userId, String key, String value, User modifier) {
    beginTransaction();
    User user = read(userId);
    if (!modifier.getUsername().equals(user.getUsername())) {
      rollbackTransaction();
      throw new UnauthorizedException(MessageFormat.format("{0} is not allowed to change settings for {1}", modifier.getUsername(), user.getUsername()));
    }
    UserSetting userSetting = user.setUserSetting(key, value);
    persist(userSetting);
    commitTransaction();
  }

  public void updateSettings(long userId, Map<String, String> newSettingsMap, User modifier) {
    beginTransaction();
    User user = read(userId);
    if (!modifier.getUsername().equals(user.getUsername())) {
      rollbackTransaction();
      throw new UnauthorizedException(MessageFormat.format("{0} is not allowed to change settings for {1}", modifier.getUsername(), user.getUsername()));
    }

    for (Entry<String, String> entry : newSettingsMap.entrySet()) {
      UserSetting userSetting = user.setUserSetting(entry.getKey(), entry.getValue());
      persist(userSetting);
    }

    persist(user);
    commitTransaction();
  }

}
