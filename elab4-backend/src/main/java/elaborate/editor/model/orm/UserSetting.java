package elaborate.editor.model.orm;

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
