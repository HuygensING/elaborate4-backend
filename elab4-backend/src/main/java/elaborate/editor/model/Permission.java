package elaborate.editor.model;

import java.util.Set;

import com.google.common.collect.Sets;

public class Permission {
  Set<String> allowedActions = Sets.newHashSet();
  private boolean canRead = false;
  private boolean canWrite = false;

  public Permission setCanRead(boolean b) {
    this.canRead = b;
    return this;
  }

  public Permission setCanWrite(boolean b) {
    this.canWrite = b;
    if (b) {
      setCanRead(true);
    }
    return this;
  }

  public boolean canRead() {
    return this.canRead;
  }

  public boolean canWrite() {
    return this.canWrite;
  }

  public boolean can(Action action) {
    return can(action.name());
  }

  public boolean can(String action) {
    return allowedActions.contains(action);
  }

  public Permission allow(String action) {
    allowedActions.add(action);
    return this;
  }

  public Permission allow(Action action) {
    allow(action.name());
    return this;
  }

  public Permission disallow(String action) {
    allowedActions.remove(action);
    return this;
  }

  public Permission disallow(Action action) {
    disallow(action.name());
    return this;
  }

}
