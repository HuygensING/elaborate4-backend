package elaborate.editor.model;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class ElaborateRoles {
  public static final String ADMIN = "ADMIN";
  public static final String PROJECTLEADER = "PROJECTLEADER";
  public static final String READER = "READER";
  public static final String USER = "USER";
  public static final String[] ROLES = new String[] { READER, USER, PROJECTLEADER, ADMIN };

  public static String getRolestringFor(String role) {
    List<String> roles = Lists.newArrayList(READER);
    if (USER.equals(role)) {
      roles.add(USER);

    } else if (PROJECTLEADER.equals(role)) {
      roles.add(PROJECTLEADER);
      roles.add(USER);

    } else if (ADMIN.equals(role)) {
      roles.add(ADMIN);
      roles.add(PROJECTLEADER);
      roles.add(USER);
    }
    return Joiner.on(",").join(roles);
  }

  public static String highestRole(List<String> list) {
    if (list.contains(ADMIN)) {
      return ADMIN;
    }
    if (list.contains(PROJECTLEADER)) {
      return PROJECTLEADER;
    }
    if (list.contains(USER)) {
      return USER;
    }
    return READER;
  }

}
