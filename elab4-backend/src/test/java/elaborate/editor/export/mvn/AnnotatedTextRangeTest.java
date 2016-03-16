package elaborate.editor.export.mvn;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import nl.knaw.huygens.Log;

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
