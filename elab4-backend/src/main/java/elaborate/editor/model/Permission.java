package elaborate.editor.model;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2022 Huygens ING
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

import java.util.Set;

import com.google.common.collect.Sets;

public class Permission {
  final Set<String> allowedActions = Sets.newHashSet();
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
