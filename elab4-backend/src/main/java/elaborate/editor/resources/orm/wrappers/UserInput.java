package elaborate.editor.resources.orm.wrappers;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;

import elaborate.editor.model.ElaborateRoles;
import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.service.UserService;
import elaborate.util.PasswordUtil;

@XmlRootElement
public class UserInput {
  //  {
  //    "id": 123, // only for PUT/update
  //    "username": "marijke_boter",
  //    "email": "marijke.boter@huygens.knaw.nl",
  //    "firstName": "Marijke",
  //    "lastName": "Boter",
  //    "password": "whatever",
  //    "role": "USER"
  //  },

  private static final long NULL_ID = -1l;
  public long id = NULL_ID;
  public String username;
  public String email;
  public String firstName;
  public String lastName;
  public String role;
  public String password;

  public User getUser() {
    User user = (id == NULL_ID) ? new User() : UserService.instance().getUser(id);
    if (StringUtils.isNotBlank(username)) {
      user.setUsername(username);
    }

    if (StringUtils.isNotBlank(email)) {
      user.setEmail(email);
    }

    if (StringUtils.isNotBlank(firstName)) {
      user.setFirstName(firstName);
    }

    if (StringUtils.isNotBlank(lastName)) {
      user.setLastName(lastName);
    }

    if (StringUtils.isNotBlank(role)) {
      user.setRoleString(ElaborateRoles.getRolestringFor(role));
    }

    if (StringUtils.isNotBlank(password)) {
      user.setEncodedPassword(PasswordUtil.encode(password));
    }

    if (StringUtils.isNotBlank(firstName) || StringUtils.isNotBlank(lastName)) {
      user.setTitle(Joiner.on(", ").skipNulls().join(new String[] { user.getLastName(), user.getFirstName() }));
    }
    return user;
  }
}
