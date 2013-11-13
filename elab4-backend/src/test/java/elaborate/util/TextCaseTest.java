package elaborate.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TextCaseTest {
  @Test
  public void testDetectLowerCase() {
    assertThat(TextCase.detectCase("lower")).isEqualTo(TextCase.LOWER);
    assertThat(TextCase.detectCase("UPPER")).isEqualTo(TextCase.UPPER);
    assertThat(TextCase.detectCase("Capitalized")).isEqualTo(TextCase.CAPITALIZED);
    assertThat(TextCase.detectCase("mIxEd")).isEqualTo(TextCase.MIXED);
  }

  @Test
  public void testApplyTo() {
    String string = "tEsT";
    assertThat(TextCase.LOWER.applyTo(string)).isEqualTo("test");
    assertThat(TextCase.UPPER.applyTo(string)).isEqualTo("TEST");
    assertThat(TextCase.CAPITALIZED.applyTo(string)).isEqualTo("Test");
    assertThat(TextCase.MIXED.applyTo(string)).isEqualTo(string);
  }
}
