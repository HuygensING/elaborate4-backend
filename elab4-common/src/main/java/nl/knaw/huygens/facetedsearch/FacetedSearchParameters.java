package nl.knaw.huygens.facetedsearch;

/*
 * #%L
 * elab4-common
 * =======
 * Copyright (C) 2013 - 2014 Huygens ING
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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@SuppressWarnings("unchecked")
public class FacetedSearchParameters<T extends FacetedSearchParameters<T>> {
	private String term = "*";
	private boolean caseSensitive = false;
	private String[] facetFields = new String[] {};
	private List<FacetParameter> facetParameters = Lists.newArrayList();
	private Map<String, FacetInfo> facetInfoMap;
	private Collection<String> resultFields = Lists.newArrayList();
	private boolean fuzzy = false;
	private LinkedHashSet<SortParameter> sortParameters = Sets.newLinkedHashSet();

	public T setTerm(final String term) {
		if (StringUtils.isNotBlank(term)) {
			this.term = term;
		}
		return (T) this;
	}

	public String getTerm() {
		return term;
	}

	public T setCaseSensitive(boolean matchCase) {
		this.caseSensitive = matchCase;
		return (T) this;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public T setFacetFields(String[] _facetFields) {
		this.facetFields = _facetFields;
		return (T) this;
	}

	public String[] getFacetFields() {
		return facetFields;
	}

	public T setResultFields(Collection<String> _resultFields) {
		this.resultFields = Sets.newHashSet(_resultFields);
		this.resultFields.removeAll(Arrays.asList("", null));
		return (T) this;
	}

	public Collection<String> getResultFields() {
		return resultFields;
	}

	public boolean isFuzzy() {
		return fuzzy;
	}

	public T setFuzzy(Boolean fuzzy) {
		this.fuzzy = fuzzy;
		return (T) this;
	}

	public List<FacetParameter> getFacetValues() {
		return facetParameters;
	}

	public T setFacetValues(List<FacetParameter> fp) {
		this.facetParameters = fp;
		return (T) this;
	}

	public Map<String, FacetInfo> getFacetInfoMap() {
		return facetInfoMap;
	}

	public T setFacetInfoMap(Map<String, FacetInfo> facetInfoMap) {
		this.facetInfoMap = facetInfoMap;
		return (T) this;
	}

	public LinkedHashSet<SortParameter> getSortParameters() {
		return sortParameters;
	}

	public FacetedSearchParameters<T> setSortParameters(List<SortParameter> sortParameters) {
		this.sortParameters = new LinkedHashSet<SortParameter>(sortParameters);
		return this;
	}

	private String sort = null;
	private String sortDir = "asc";

	/**
	 * @deprecated use {@link setSortParameters()} instead.  
	 */
	@Deprecated
	public T setSort(final String sort) {
		this.sort = sort;
		return (T) this;
	}

	/**
	 * @deprecated use {@link getSortParameters()} instead.  
	 */
	@Deprecated
	public String getSort() {
		return sort;
	}

	/**
	 * @deprecated use {@link setSortParameters()} instead.  
	 */
	@Deprecated
	public T setSortDir(final String sortDir) {
		this.sortDir = sortDir;
		return (T) this;
	}

	/**
	 * @deprecated use {@link getSortParameters()} instead.  
	 */
	@Deprecated
	public String getSortDir() {
		return sortDir;
	}

	/**
	 * @deprecated use {@link getSortParameters()} instead.  
	 */
	@Deprecated
	public boolean isAscending() {
		return "asc".equals(sortDir);
	}

}
