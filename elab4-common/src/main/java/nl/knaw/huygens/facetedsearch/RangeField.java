package nl.knaw.huygens.facetedsearch;

/*
 * #%L
 * elab4-common
 * =======
 * Copyright (C) 2013 - 2015 Huygens ING
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


public class RangeField {
	public String name;
	public String lowerField;
	public String upperField;

	public RangeField() {};

	public RangeField(String name, String lower, String upper) {
		this.name = name;
		lowerField = lower;
		upperField = upper;
	}

	public String getName() {
		return name;
	}

	public RangeField setName(String name) {
		this.name = name;
		return this;
	}

	public String getLowerField() {
		return lowerField;
	}

	public RangeField setLowerField(String lowerField) {
		this.lowerField = lowerField;
		return this;
	}

	public String getUpperField() {
		return upperField;
	}

	public RangeField setUpperField(String upperField) {
		this.upperField = upperField;
		return this;
	}

}
