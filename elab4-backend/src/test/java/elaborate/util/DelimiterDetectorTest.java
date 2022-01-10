package elaborate.util;

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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DelimiterDetectorTest {
  @Test
  public void test1() {
    testDelimiterDetector("abcdef", "", "abcdef", "");
  }

  @Test
  public void test2() {
    testDelimiterDetector(";abcdef,", ";", "abcdef", ",");
  }

  private void testDelimiterDetector(
      String string, String expectedPre, String expectedStripped, String expectedPost) {
    DelimiterDetector d = new DelimiterDetector(string);
    assertThat(d.getPreDelimiters()).isEqualTo(expectedPre);
    assertThat(d.getStripped()).isEqualTo(expectedStripped);
    assertThat(d.getPostDelimiters()).isEqualTo(expectedPost);
  }
}
