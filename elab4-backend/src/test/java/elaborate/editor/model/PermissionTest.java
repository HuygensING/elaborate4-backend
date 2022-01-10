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

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PermissionTest {
  private Permission permission;

  @Before
  public void setUp() {
    permission = new Permission();
  }

  @Test
  public void testAllow() {
    assertThat(permission.can(Action.ADD)).isFalse();

    permission.allow(Action.ADD);
    assertThat(permission.can(Action.ADD)).isTrue();

    permission.disallow(Action.ADD);
    assertThat(permission.can(Action.ADD)).isFalse();
  }

  @Test
  public void testSetCanRead() {
    assertThat(permission.canRead()).isFalse();

    permission.setCanRead(true);
    assertThat(permission.canRead()).isTrue();
  }

  @Test
  public void testSetCanWrite() {
    assertThat(permission.canRead()).isFalse();
    assertThat(permission.canWrite()).isFalse();

    permission.setCanWrite(true);
    assertThat(permission.canRead()).isTrue();
    assertThat(permission.canWrite()).isTrue();

    permission.setCanWrite(false);
    assertThat(permission.canRead()).isTrue();
    assertThat(permission.canWrite()).isFalse();
  }
}
