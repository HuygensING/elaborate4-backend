package elaborate.editor.model;

import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.User;

public class Permissions {
  private static final Permission NO_PERMISSION = new Permission();
  private static final Permission ROOT_PERMISSION;
  static {
    ROOT_PERMISSION = new Permission() {
      @Override
      public boolean can(Action action) {
        return true;
      }

      @Override
      public boolean can(String action) {
        return true;
      }

      @Override
      public boolean canRead() {
        return true;
      }

      @Override
      public boolean canWrite() {
        return true;
      }
    };
  }

  public static Permission getPermission(User user, Object object) {
    if (user == null) {
      return NO_PERMISSION;
    }
    if (user.isRoot()) {
      return ROOT_PERMISSION;
    }
    Permission permission = new Permission();
    if (object instanceof Project) {
      Project project = (Project) object;
      permission = permissionForProject(user, project);
    } else if (object instanceof User) {
      User otheruser = (User) object;
      permission = permissionForUser(user, otheruser);
    }
    return permission;
  }

  private static Permission permissionForUser(User user, User otheruser) {
    Permission permission = new Permission().setCanRead(true);

    boolean userIsAdmin = user.getRoleString().contains(ElaborateRoles.ADMIN);
    boolean userIsProjectLeader = user.getRoleString().contains(ElaborateRoles.PROJECTLEADER);

    if (userIsProjectLeader || userIsAdmin || user.getId() == otheruser.getId()) {
      permission.setCanWrite(true);
    }

    return permission;
  }

  private static Permission permissionForProject(User user, Project project) {
    Permission permission = new Permission();

    boolean userIsAdmin = user.getRoleString().contains(ElaborateRoles.ADMIN);
    boolean userIsProjectLeader = (user.getId() == project.getProjectLeaderId());

    if (userIsProjectLeader || userIsAdmin) {
      permission.setCanRead(true);
      permission.setCanWrite(true);
      permission.allow(Action.SELECT_PROJECT_ANNOTATION_TYPES);
      permission.allow(Action.SELECT_PROJECT_ENTRY_METADATA_FIELDS);
      permission.allow(Action.DELETE_PROJECT_ENTRIES);
      permission.allow(Action.DELETE_PROJECT);
      permission.allow(Action.EDIT_PROJECT_USERS);
      permission.allow(Action.PUBLISH);

    } else {
      for (User projectUser : project.getUsers()) {
        if (user.equals(projectUser)) {
          permission.setCanRead(true);
          permission.setCanWrite(user.getRoleString().contains(ElaborateRoles.USER));
          return permission;
        }
      }
    }
    return permission;
  }
}
