package elaborate.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class TextCaseTest {
  @Test
  public void testDetectLowerCase() {
    assertEquals(TextCase.LOWER, TextCase.detectCase("lower"));
    assertEquals(TextCase.UPPER, TextCase.detectCase("UPPER"));
    assertEquals(TextCase.CAPITALIZED, TextCase.detectCase("Capitalized"));
    assertEquals(TextCase.MIXED, TextCase.detectCase("mIxEd"));
  }

  @Test
  public void testApplyTo() {
    String string = "tEsT";
    assertEquals("test", TextCase.LOWER.applyTo(string));
    assertEquals("TEST", TextCase.UPPER.applyTo(string));
    assertEquals("Test", TextCase.CAPITALIZED.applyTo(string));
    assertEquals(string, TextCase.MIXED.applyTo(string));
  }
}
