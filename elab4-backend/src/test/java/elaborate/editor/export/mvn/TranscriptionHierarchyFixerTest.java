package elaborate.editor.export.mvn;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Ignore;
import org.junit.Test;

public class TranscriptionHierarchyFixerTest {
  TranscriptionHierarchyFixer thf = new TranscriptionHierarchyFixer();

  @Ignore
  @Test
  public void testFixBoldTagsAnnotationTag() {
    String xml = "<i><b><ab id=\"1\"/>¶<ae id=\"1\"/></b></i>";
    String expected = "<ab id=\"1\"/><i><b>¶</b></i><ae id=\"1\"/>";
    String fixed = thf.fix(xml);
    assertThat(fixed).isEqualTo(expected);
  }

  @Test
  public void testTextDecorationForAnnotationTagIsIgnored() {
    String xml = "<i><b><ab id=\"1\"/></b></i>";
    String expected = "<ab id=\"1\"/>";
    String fixed = thf.fix(xml);
    assertThat(fixed).isEqualTo(expected);
  }

  //  @Ignore
  @Test
  public void testIncorrectAnnotationBeginEndHierarchyIsFixed() {
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

}
