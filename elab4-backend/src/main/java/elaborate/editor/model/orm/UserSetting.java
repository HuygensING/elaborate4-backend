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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import elaborate.editor.model.AbstractStoredEntity;

@Entity
@Table(name = "user_settings")
@XmlRootElement
public class UserSetting extends AbstractStoredEntity<UserSetting> {
  private static final long serialVersionUID = 1L;

  /* properties to persist */
  @ManyToOne
  @JoinColumn(name = "user_id", columnDefinition = "int4")
  User user;

  @Column(name = "setting_key")
  String key;

  @Column(name = "setting_value")
  String value;

  /* persistent properties getters and setters */
  public User getUser() {
    return user;
  }

  public UserSetting setUser(User user) {
    this.user = user;
    return this;
  }

  public String getKey() {
    return key;
  }

  public UserSetting setKey(String key) {
    this.key = key;
    return this;
  }

  public String getValue() {
    return value;
  }

  public UserSetting setValue(String value) {
    this.value = value;
    return this;
  }

}
