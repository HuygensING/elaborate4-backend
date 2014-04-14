package elaborate.editor.model.orm;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import elaborate.editor.model.AbstractStoredEntity;
import elaborate.editor.model.ElaborateRoles;
import elaborate.editor.model.ModelFactory;
import elaborate.editor.model.Permission;
import elaborate.editor.model.Permissions;
import elaborate.editor.model.UserSettings;

@Entity
@Table(name = "users")
@XmlRootElement(name = "user")
public class User extends AbstractStoredEntity<User> {
	private static final long serialVersionUID = 1L;
	private static final String STATUS_ONLINE = "online";
	private static final String STATUS_OFFLINE = "offline";

	/* properties to persist */
	@Column(unique = true)
	private String username;

	private String firstname;
	private String lastname;
	private String title;
	private String email;

	private byte[] encodedpassword;

	private String rolestring = ElaborateRoles.READER;
	private boolean isRoot;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
	private Set<UserSetting> userSettings;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(//
	name = "project_users",//
	joinColumns = { @JoinColumn(name = "user_id", columnDefinition = "int4", nullable = false, updatable = false) },//
	inverseJoinColumns = { @JoinColumn(name = "project_id", columnDefinition = "int4", nullable = false, updatable = false) }//
	)
	private List<Project> projects;

	/* persistent properties getters and setters */
	public String getUsername() {
		return username;
	};

	public User setUsername(String name) {
		username = name;
		return this;
	};

	public String getTitle() {
		return title;
	};

	public User setTitle(String title) {
		this.title = title;
		return this;
	};

	public String getEmail() {
		return email;
	};

	public User setEmail(String email) {
		this.email = email;
		return this;
	};

	public String getFirstName() {
		return firstname;
	}

	public User setFirstName(String firstName) {
		this.firstname = firstName;
		return this;
	}

	public String getLastName() {
		return lastname;
	}

	public User setLastName(String lastName) {
		this.lastname = lastName;
		return this;
	}

	@JsonIgnore
	public byte[] getEncodedPassword() {
		return encodedpassword;
	}

	public User setEncodedPassword(byte[] encodedPassword) {
		this.encodedpassword = encodedPassword;
		return this;
	}

	public String getRoleString() {
		return rolestring;
	}

	//comma-seperated list of roles (Roles.USER,Roles.ADMIN)
	public User setRoleString(String roleString) {
		this.rolestring = roleString;
		return this;
	}

	public String getRole() {
		return ElaborateRoles.highestRole(getRoles());
	}

	@JsonIgnore
	public boolean isRoot() {
		return isRoot;
	}

	public User setRoot(boolean isRoot) {
		this.isRoot = isRoot;
		return this;
	};

	@JsonIgnore
	public Set<UserSetting> getUserSettings() {
		return userSettings;
	}

	public User setUserSettings(Set<UserSetting> userSettings) {
		this.userSettings = userSettings;
		return this;
	}

	@JsonIgnore
	public List<Project> getProjects() {
		return projects;
	}

	public User setProjects(List<Project> projects) {
		this.projects = projects;
		return this;
	}

	/* transient methods */

	@Transient
	private List<String> getRoles() {
		return Lists.newArrayList(Splitter.on(',').split(getRoleString()));
	}

	/**
	 * @param role The string representation of the role to check for
	 * @return true if this User has the given role, false otherwise
	 */
	public boolean hasRole(String role) {
		if (role.equals(ElaborateRoles.READER)) {
			return true;
		}
		return isRoot() || getRoles().contains(role);
	}

	public boolean hasHighestRole(String role) {
		return (getRole().equals(role));
	}

	/* UserSettings */
	public UserSetting setUserSetting(final String key, String value) {
		if (hasUserSetting(key)) {
			UserSetting setting = Iterables.find(Lists.newArrayList(getUserSettings()), userSettingWithKey(key));
			setting.setValue(value);
			return setting;

		} else {
			return addUserSetting(key, value);
		}
	}

	public UserSetting addUserSetting(String key, String value) {
		UserSetting setting = ModelFactory.create(UserSetting.class).setUser(this).setKey(key).setValue(value);
		return setting;
	}

	public void removeUserSetting(String key) {
		if (hasUserSetting(key)) {
			Iterables.find(Lists.newArrayList(getUserSettings()), userSettingWithKey(key));
		}
	}

	public String getUserSetting(final String key) {
		return getUserSetting(key, null);
	}

	public String getUserSetting(final String key, final String defaultValue) {
		return (hasUserSetting(key) && userSetting(key) != null) ? userSetting(key) : defaultValue;
	}

	private String userSetting(final String key) {
		UserSetting setting = Iterables.find(Lists.newArrayList(getUserSettings()), userSettingWithKey(key));
		return setting.getValue();
	}

	private Predicate<UserSetting> userSettingWithKey(final String key) {
		Predicate<UserSetting> predicate = new Predicate<UserSetting>() {
			@Override
			public boolean apply(UserSetting userSetting) {
				return key.equals(userSetting.getKey());
			}
		};
		return predicate;
	}

	public boolean hasUserSetting(final String key) {
		return Iterables.any(Lists.newArrayList(getUserSettings()), userSettingWithKey(key));
	}

	public String[] getProjectLevels(String project_id) {
		String level1 = getUserSetting(UserSettings.projectLevel(project_id, 1), "");
		String level2 = getUserSetting(UserSettings.projectLevel(project_id, 2), "");
		String level3 = getUserSetting(UserSettings.projectLevel(project_id, 3), "");
		return new String[] { level1, level2, level3 };
	}

	public void updateLoginStatus(boolean loggingOff) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (loggingOff && isLoggedIn()) {
			setUserSetting(UserSettings.ONLINE_STATUS, STATUS_OFFLINE);
			setUserSetting(UserSettings.LOGOUT_TIME, simpleDateFormat.format(new Date()));
		} else if (!loggingOff && !isLoggedIn()) {
			setUserSetting(UserSettings.ONLINE_STATUS, STATUS_ONLINE);
			setUserSetting(UserSettings.LOGIN_TIME, simpleDateFormat.format(new Date()));
		}
	}

	public boolean isLoggedIn() {
		return STATUS_ONLINE.equals(getUserSetting(UserSettings.ONLINE_STATUS, STATUS_OFFLINE));
	}

	// Permissions

	public Permission getPermissionFor(Object object) {
		return Permissions.getPermission(this, object);
	}

}
