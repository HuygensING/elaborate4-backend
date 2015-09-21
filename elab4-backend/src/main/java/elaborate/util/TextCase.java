package elaborate.util;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2015 Huygens ING
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

import org.apache.commons.lang.StringUtils;

enum TextCase {
	LOWER {
		@Override
		public String applyTo(String string) {
			return string.toLowerCase();
		}
	},
	UPPER {
		@Override
		public String applyTo(String string) {
			return string.toUpperCase();
		}
	},
	CAPITALIZED {
		@Override
		public String applyTo(String string) {
			return StringUtils.capitalize(string.toLowerCase());
		}
	},
	MIXED {
		@Override
		public String applyTo(String string) {
			return string;
		}
	};

	public abstract String applyTo(String string);

	public static TextCase detectCase(String token) {
		for (TextCase stringCase : TextCase.values()) {
			if (stringCase.applyTo(token).equals(token)) {
				return stringCase;
			}
		}
		throw new RuntimeException("No suitable case detected. Check available cases.");
	}

}
