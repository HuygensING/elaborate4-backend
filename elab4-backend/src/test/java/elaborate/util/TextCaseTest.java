package elaborate.util;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2014 Huygens ING
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
