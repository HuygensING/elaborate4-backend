package elaborate.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class DelimiterDetectorTest {
  @Test
  public void test1() {
    testDelimiterDetector("abcdef", "", "abcdef", "");
  }

  @Test
  public void test2() {
    testDelimiterDetector(";abcdef,", ";", "abcdef", ",");
  }

  private void testDelimiterDetector(String string, String expectedPre, String expectedStripped, String expectedPost) {
    DelimiterDetector d = new DelimiterDetector(string);
    assertThat(d.getPreDelimiters()).isEqualTo(expectedPre);
    assertThat(d.getStripped()).isEqualTo(expectedStripped);
    assertThat(d.getPostDelimiters()).isEqualTo(expectedPost);
  }

}
