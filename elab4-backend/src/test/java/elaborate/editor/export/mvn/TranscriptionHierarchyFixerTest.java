package elaborate.editor.export.mvn;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2019 Huygens ING
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


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TranscriptionHierarchyFixerTest {
  final TranscriptionHierarchyFixer thf = new TranscriptionHierarchyFixer();

  @Test
  public void testFixBoldTagsAnnotationTag() {
    String xml = "<body><i><b><ab id=\"1\"/>¶<ae id=\"1\"/></b></i></body>";
    String expected = "<body><ab id=\"1\"/><i><b>¶</b></i><ae id=\"1\"/></body>";
    String fixed = thf.fix(xml);
    assertThat(fixed).isEqualTo(expected);
  }

  @Test
  public void testTextDecorationForAnnotationTagIsIgnored() {
    String xml = "<body><i><b><ab id=\"1\"/></b>A<ae id=\"1\"/></i></body>";
    String expected = "<body><ab id=\"1\"/><i>A</i><ae id=\"1\"/></body>";
    String fixed = thf.fix(xml);
    assertThat(fixed).isEqualTo(expected);
  }

  @Test
  public void testIncorrectAnnotationBeginEndHierarchyIsFixed1() {
    String xml = "<body>"//
        + "<ab id=\"1\"/>"//
        + "<ab id=\"2\"/>"//
        + "<ab id=\"3\"/>"//
        + "<ab id=\"4\"/>"//
        + "<b>D</b>"//
        + "<ae id=\"4\"/>"//
        + "<ae id=\"3\"/>"//
        + "<ab id=\"5\"/>"//
        + "e"//
        + "<ae id=\"2\"/>"//
        + "<ae id=\"5\"/>"//
        + " tali conuiuio"//
        + "<ae id=\"1\"/>"//
        + "</body>";
    String expected = "<body>"//
        + "<ab id=\"1\"/>"//
        + "<ab id=\"2\"/>"//
        + "<ab id=\"3\"/>"//
        + "<ab id=\"4\"/>"//
        + "<b>D</b>"//
        + "<ae id=\"4\"/>"//
        + "<ae id=\"3\"/>"//
        + "<ab id=\"5\"/>"//
        + "e"//
        + "<ae id=\"5\"/>"//
        + "<ae id=\"2\"/>"//
        + " tali conuiuio"//
        + "<ae id=\"1\"/>"//
        + "</body>";
    String fixed = thf.fix(xml);
    assertThat(fixed).isEqualTo(expected);
  }

  @Test
  public void testIncorrectAnnotationBeginEndHierarchyIsFixed2() {
    String xml = "<body>"//
        + "<ab id=\"1\"/>"//
        + "<ab id=\"2\"/>"//
        + "<ab id=\"3\"/>"//
        + "<ab id=\"4\"/>"//
        + "<b>V</b>"//
        + "<ae id=\"4\"/>"//
        + "<ae id=\"1\"/>"//
        + "<ab id=\"5\"/>a<ae id=\"5\"/>n"//
        + "<ae id=\"2\"/>"//
        + " jherusalem zeghelijn"//
        + "<ae id=\"3\"/>"//
        + "</body>";
    String expected = "<body>"//
        + "<ab id=\"3\"/>"//
        + "<ab id=\"2\"/>"//
        + "<ab id=\"1\"/>"//
        + "<ab id=\"4\"/>"//
        + "<b>V</b>"//
        + "<ae id=\"4\"/>"//
        + "<ae id=\"1\"/>"//
        + "<ab id=\"5\"/>a<ae id=\"5\"/>n"//
        + "<ae id=\"2\"/>"//
        + " jherusalem zeghelijn"//
        + "<ae id=\"3\"/>"//
        + "</body>";
    String fixed = thf.fix(xml);
    assertThat(fixed).isEqualTo(expected);
  }

  @Test
  public void testIncorrectAnnotationBeginEndHierarchyIsFixed3() {
    String xml = "<body>"//
        + "<ab id=\"3\"/>"//
        + "<ab id=\"2\"/>"//
        + "e"//
        + "<ae id=\"3\"/>"//
        + "<ae id=\"2\"/>"//
        + "</body>";
    String expected = "<body>"//
        + "<ab id=\"2\"/>"//
        + "<ab id=\"3\"/>e<ae id=\"3\"/>"//
        + "<ae id=\"2\"/>"//
        + "</body>";
    String fixed = thf.fix(xml);
    assertThat(fixed).isEqualTo(expected);
  }

  @Test
  public void testIncorrectAnnotationBeginEndHierarchyIsFixed3a() {
    String xml = "<body>"//
        + "<lb/>"//
        + "<ab id=\"6\"/>"//
        + "<ab id=\"3\"/>"//
        + "<ab id=\"1\"/>"//
        + "<ab id=\"7\"/>"//
        + "<b>D</b>"//
        + "<ae id=\"7\"/>"//
        + "<ae id=\"1\"/>"//
        + "<ab id=\"2\"/>"//
        + "e"//
        + "<ae id=\"3\"/>"//
        + "<ae id=\"2\"/>"//
        + " tali conuiuio"//
        + "<ae id=\"6\"/>"//
        + " "//
        + "<ab id=\"5\"/>"//
        + "<ab id=\"4\"/>B<ae id=\"4\"/>"//
        + "enedicamus"//
        + "<ae id=\"5\"/>"//
        + " domino"//
        + "<le/>"//
        + "</body>";
    String expected = "<body>"//
        + "<lb/>"//
        + "<ab id=\"6\"/>"//
        + "<ab id=\"3\"/>"//
        + "<ab id=\"1\"/>"//
        + "<ab id=\"7\"/><b>D</b><ae id=\"7\"/>"//
        + "<ae id=\"1\"/>"//
        + "<ab id=\"2\"/>e<ae id=\"2\"/>"//
        + "<ae id=\"3\"/>"//
        + " tali conuiuio"//
        + "<ae id=\"6\"/>"//
        + " "//
        + "<ab id=\"5\"/>"//
        + "<ab id=\"4\"/>B<ae id=\"4\"/>"//
        + "enedicamus"//
        + "<ae id=\"5\"/>"//
        + " domino"//
        + "<le/></body>";
    String fixed = thf.fix(xml);
    assertThat(fixed).isEqualTo(expected);
  }

  @Test
  public void testIncorrectAnnotationBeginEndHierarchyIsFixed4() {
    String xml = "<body>"//
        + "<b><ab id=\"1\"/></b>"//
        + "<b><ab id=\"3\"/><ab id=\"6\"/></b>"//
        + "<b><ab id=\"7\"/>D<ae id=\"7\"/></b>"//
        + "<b><ae id=\"1\"/></b>"//
        + "<ab id=\"2\"/>"//
        + "e"//
        + "<ae id=\"3\"/>"//
        + "<ae id=\"2\"/>"//
        + " tali conuiuio<ae id=\"6\"/>"//
        + " "//
        + "<ab id=\"4\"/>"//
        + "<ab id=\"5\"/>"//
        + "B"//
        + "<ae id=\"4\"/>"//
        + "enedicamus"//
        + "<ae id=\"5\"/>"//
        + " domino"//
        + "</body>";
    String expected = "<body>"//
        + "<ab id=\"6\"/>"//
        + "<ab id=\"3\"/>"//
        + "<ab id=\"1\"/><ab id=\"7\"/><b>D</b><ae id=\"7\"/><ae id=\"1\"/>"//
        + "<ab id=\"2\"/>e<ae id=\"2\"/>"//
        + "<ae id=\"3\"/>"//
        + " tali conuiuio<ae id=\"6\"/>"//
        + " "//
        + "<ab id=\"5\"/>"//
        + "<ab id=\"4\"/>"//
        + "B"//
        + "<ae id=\"4\"/>"//
        + "enedicamus"//
        + "<ae id=\"5\"/>"//
        + " domino"//
        + "</body>";
    String fixed = thf.fix(xml);
    assertThat(fixed).isEqualTo(expected);
  }

}
