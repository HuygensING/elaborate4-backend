package elaborate.editor.export.tei;

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

import java.util.Map;

import com.google.common.collect.Maps;

public class TagInfo {
	private String name = "";
	private Map<String, String> attributes = Maps.newHashMap();
	private boolean skipNewlineAfter = false;

	public String getName() {
		return name;
	}

	public TagInfo setName(String name1) {
		this.name = name1;
		return this;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public TagInfo setAttributes(Map<String, String> attributes1) {
		this.attributes = attributes1;
		return this;
	}

	public void addAttribute(String key, String value) {
		attributes.put(key, value);
	}

	public boolean skipNewlineAfter() {
		return skipNewlineAfter;
	}

	public TagInfo setSkipNewlineAfter(boolean skipNewlineAfter1) {
		this.skipNewlineAfter = skipNewlineAfter1;
		return this;
	}
}
