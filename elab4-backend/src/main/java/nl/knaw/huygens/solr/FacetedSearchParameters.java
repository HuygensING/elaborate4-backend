package nl.knaw.huygens.solr;

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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import nl.knaw.huygens.facetedsearch.FacetInfo;
import nl.knaw.huygens.facetedsearch.SortParameter;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

@SuppressWarnings("unchecked")
public class FacetedSearchParameters<T extends FacetedSearchParameters<T>> extends AbstractSearchParameters<FacetedSearchParameters<T>> {
	private String term = "*";
	//	private final String sort = null;
	//	private final String sortDir = "asc";
	private boolean caseSensitive = false;
	private String[] facetFields = new String[] {};
	private List<FacetParameter> facetParameters = Lists.newArrayList();
	private Map<String, FacetInfo> facetInfoMap;
	private Collection<String> resultFields = Lists.newArrayList();
	private boolean fuzzy = false;
	private List<SortParameter> sortParameters = Lists.newArrayList();

	public T setTerm(final String term) {
		if (StringUtils.isNotBlank(term)) {
			this.term = term;
		}
		return (T) this;
	}

	public String getTerm() {
		return term;
	}

	//	public T setSort(final String sort) {
	//		this.sort = sort;
	//		return (T) this;
	//	}
	//
	//	public String getSort() {
	//		return sort;
	//	}
	//
	//	public T setSortDir(final String sortDir) {
	//		this.sortDir = sortDir;
	//		return (T) this;
	//	}
	//
	//	public String getSortDir() {
	//		return sortDir;
	//	}
	//
	//	public boolean isAscending() {
	//		return "asc".equals(sortDir);
	//	}

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

	public T setResultFields(Collection<String> orderLevels) {
		this.resultFields = orderLevels;
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

	public List<SortParameter> getSortParameters() {
		return sortParameters;
	}

	public FacetedSearchParameters<T> setSortParameters(List<SortParameter> sortParameters) {
		this.sortParameters = sortParameters;
		return this;
	}

}
