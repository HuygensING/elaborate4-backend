package elaborate.util;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2021 Huygens ING
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

import com.google.common.base.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PasswordUtilTest {

  @Ignore
  @Test
  public void testPassword() {
    final String password = "aap-noot-mies";
    final byte[] passwordDigest2 = PasswordUtil.encode(password);
    String encodedPassword = new String(Base64.encodeBase64(passwordDigest2), Charsets.UTF_8);
    assertThat(PasswordUtil.matches(password, encodedPassword)).isTrue();
    assertThat(PasswordUtil.matches("somethingelse", encodedPassword)).isFalse();
  }
}
