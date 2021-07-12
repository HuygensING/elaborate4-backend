package elaborate.editor.export.mvn;

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

import java.util.Set;

import org.junit.Test;

import nl.knaw.huygens.Log;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnotatedTextRangeTest {

  @Test
  public void testAnnotatedTextRange() {
    String unicodeChar = "\u00D8";
    String xml = "<xml><text xml:id=\"text-1\" auth=\"bla\" bar=\"foo\">" + unicodeChar + " Hello <b><i>Happy</i><i>" + unicodeChar + "</i></b> World\nGóödbye <b>" + unicodeChar + "Cruel</b> World</text></xml>";
    String expectedText = unicodeChar + " Hello Happy" + unicodeChar + " World\nGóödbye " + unicodeChar + "Cruel World";
    AnnotatedTextRange annotatedTextRange = new AnnotatedTextRange(xml);

    String text = annotatedTextRange.getText();
    assertThat(text).isEqualTo(expectedText);
    Set<RangeAnnotation> rangeAnnotations = annotatedTextRange.getRangeAnnotations();
    assertThat(rangeAnnotations).hasSize(6);
    for (RangeAnnotation rangeAnnotation : rangeAnnotations) {
      String rangedText = text.substring(rangeAnnotation.getStartOffset(), rangeAnnotation.getEndOffset());
      String name = rangeAnnotation.getElement().getName();
      Log.info("<{}>{}</{}>", name, rangedText, name);
    }

    String reassembled = annotatedTextRange.reassemble();
    Log.info("reassembled=[{}]", reassembled);
    assertThat(reassembled).isEqualTo(xml);
  }

  @Test
  public void testAnnotatedTextRangeWithDuplicateAnnotationsRemovesDuplicates() {
    String xml = "<xml><xml><xml>WTF</xml></xml></xml>";
    String expectedText = "WTF";
    AnnotatedTextRange annotatedTextRange = new AnnotatedTextRange(xml);

    String text = annotatedTextRange.getText();
    assertThat(text).isEqualTo(expectedText);
    Set<RangeAnnotation> rangeAnnotations = annotatedTextRange.getRangeAnnotations();
    assertThat(rangeAnnotations).hasSize(1);

    String reassembled = annotatedTextRange.reassemble();
    Log.info("reassembled=[{}]", reassembled);
    assertThat(reassembled).isEqualTo("<xml>WTF</xml>");
  }

}
