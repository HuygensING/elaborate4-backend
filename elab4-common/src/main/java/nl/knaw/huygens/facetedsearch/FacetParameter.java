package nl.knaw.huygens.facetedsearch;

/*
 * #%L
 * elab4-common
 * =======
 * Copyright (C) 2013 - 2019 Huygens ING
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

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;

public class FacetParameter {
	public enum AndOr {
		and, or
	}

	private String name = "";
	private List<String> values = Lists.newArrayList();
	private AndOr combineValuesWith = AndOr.or;

	public String getName() {
		return name;
	}

	public FacetParameter setName(String name) {
		this.name = name;
		return this;
	}

	private List<String> getValues() {
		return values;
	}

	public FacetParameter setValues(List<String> values) {
		this.values = values;
		return this;
	}

	public List<String> getEscapedValues() {
		Builder<String> builder = ImmutableList.builder();
		for (String value : getValues()) {
			builder.add(SolrUtils.escapeFacetValue(value));
		}
		return builder.build();
	}

	public AndOr getCombineValuesWith() {
		return combineValuesWith;
	}

	public FacetParameter setCombineValuesWith(String combineValuesWith) {
		this.combineValuesWith = AndOr.valueOf(combineValuesWith);
		return this;
	}

	public boolean combineValuesWithAnd() {
		return combineValuesWith.equals(AndOr.and);
	}

	private long lowerLimit = -1;
	private long upperLimit = -1;

	public FacetParameter setLowerLimit(long lowerLimit) {
		this.lowerLimit = lowerLimit;
		return this;
	}

	public long getLowerLimit() {
		return lowerLimit;
	}

	public FacetParameter setUpperLimit(long upperLimit) {
		this.upperLimit = upperLimit;
		return this;
	}

	public long getUpperLimit() {
		return upperLimit;
	}

	public boolean isRangeFacetParameter() {
		return lowerLimit != -1 && upperLimit != -1;
	}

}
