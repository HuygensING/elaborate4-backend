package elaborate.editor.publish;

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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.knaw.huygens.facetedsearch.FacetInfo;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import elaborate.editor.model.orm.Project;

public class SearchConfig {
	List<String> facetFields = Lists.newArrayList();
	Map<String, FacetInfo> facetInfoMap = Maps.newLinkedHashMap();
	List<String> defaultSortOrder = Lists.newArrayList();
	String baseURL;

	public SearchConfig(Project project, List<String> metadataFieldsForFacets) {
		for (Entry<String, FacetInfo> entry : project.getFacetInfoMap().entrySet()) {
			String key = entry.getKey();
			FacetInfo value = entry.getValue();
			if (metadataFieldsForFacets.contains(value.getTitle())) {
				facetInfoMap.put(key, value);
			}
		}
		facetFields = ImmutableList.copyOf(facetInfoMap.keySet());
		defaultSortOrder = ImmutableList.of(//
				fieldOf(project.getLevel1()),//
				fieldOf(project.getLevel2()),//
				fieldOf(project.getLevel3())//
				);
	}

	private String fieldOf(String level) {
		return StringUtils.defaultIfEmpty(level, "");
	}

	public List<String> getFacetFields() {
		return facetFields;
	}

	public void setFacetFields(List<String> facetFields) {
		this.facetFields = facetFields;
	}

	public Map<String, FacetInfo> getFacetInfoMap() {
		return facetInfoMap;
	}

	public void setFacetInfoMap(Map<String, FacetInfo> facetInfoMap) {
		this.facetInfoMap = facetInfoMap;
	}

	public List<String> getDefaultSortOrder() {
		return defaultSortOrder;
	}

	public void setDefaultSortOrder(List<String> defaultSortOrder) {
		this.defaultSortOrder = defaultSortOrder;
	}

	public String getBaseURL() {
		return baseURL;
	}

	public SearchConfig setBaseURL(String baseURL) {
		this.baseURL = baseURL;
		return this;
	}
}
