package elaborate.util;

import static org.junit.Assert.*;

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
    assertEquals(expectedPre, d.getPreDelimiters());
    assertEquals(expectedStripped, d.getStripped());
    assertEquals(expectedPost, d.getPostDelimiters());
  }

}
