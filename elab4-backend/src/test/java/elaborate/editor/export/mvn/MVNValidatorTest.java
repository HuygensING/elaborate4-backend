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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.glassfish.grizzly.utils.Charsets;
import org.junit.Test;

import nl.knaw.huygens.Log;

import elaborate.editor.export.mvn.MVNValidator.ValidationResult;

import static org.assertj.core.api.Assertions.assertThat;

public class MVNValidatorTest {
  @Test
  public void testValidation() throws IOException {
    String tei = FileUtils.readFileToString(new File("src/test/resources/validatortest.xml"), Charsets.UTF8_CHARSET);
    ValidationResult result = MVNValidator.validateTEI(tei);
    Log.info("result={}", result);
    assertThat(result.isValid()).isTrue();
    assertThat(result.getMessage()).isEmpty();
  }
}
