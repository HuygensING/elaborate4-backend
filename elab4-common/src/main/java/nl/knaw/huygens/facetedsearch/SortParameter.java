package nl.knaw.huygens.facetedsearch;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/*
 * #%L
 * elab4-common
 * =======
 * Copyright (C) 2013 - 2016 Huygens ING
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

public class SortParameter {
	private String fieldname;
	private String direction = "asc";

	public String getFieldname() {
		return fieldname;
	}

	public SortParameter setFieldname(String fieldname) {
		this.fieldname = fieldname;
		return this;
	}

	public String getDirection() {
		return direction;
	}

	public SortParameter setDirection(String direction) {
		this.direction = direction;
		return this;
	}

	@Override
	public boolean equals(Object other) {
		return EqualsBuilder.reflectionEquals(this, other);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
