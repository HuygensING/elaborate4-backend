package elaborate.editor.config;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2020 Huygens ING
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

class ConfigLocation {
	private static String CONFIG_XML;
	private static final String DEFAULT = "default";
	private static final String FILENAME = "config.xml";

	private ConfigLocation() {
		throw new AssertionError("Non-instantiable class");
	}

	public static String instance() {
		try {
			return createInstance(DEFAULT);
		} catch (IllegalStateException e) {
			return CONFIG_XML;
		}
	}

	private static String createInstance(String instance) throws IllegalStateException {
		if (CONFIG_XML == null) {
			CONFIG_XML = FILENAME;
		} else {
			throw new IllegalStateException();
		}
		return CONFIG_XML;
	}

}
