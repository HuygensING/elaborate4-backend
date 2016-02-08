package elaborate.editor.export.mvn;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import elaborate.editor.export.mvn.MVNValidator.ValidationResult;
import nl.knaw.huygens.Log;

public class MVNValidatorTest {
  @Test
  public void testValidation() throws IOException {
    String tei = FileUtils.readFileToString(new File("src/test/resources/validatortest.xml"));
    ValidationResult result = MVNValidator.validateTEI(tei);
    Log.info("result={}", result);
    assertThat(result.isValid()).isTrue();
    assertThat(result.getMessage()).isEmpty();
  }
}
