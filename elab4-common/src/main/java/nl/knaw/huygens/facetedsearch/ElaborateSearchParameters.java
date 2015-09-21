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

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@XmlRootElement
public class ElaborateSearchParameters extends FacetedSearchParameters<ElaborateSearchParameters> {
	private List<String> textLayers = Lists.newArrayList();
	private boolean searchInTranscriptions = true;
	private boolean searchInAnnotations = false;
	private String level1Field = SolrFields.NAME;
	private String level2Field = SolrFields.NAME;
	private String level3Field = SolrFields.NAME;
	private List<RangeField> ranges = Lists.newArrayList();

	public ElaborateSearchParameters setTextLayers(final List<String> textLayers) {
		this.textLayers = textLayers;
		return this;
	}

	public List<String> getTextLayers() {
		return textLayers;
	}

	public ElaborateSearchParameters setLevelFields(String level1, String level2, String level3) {
		if (StringUtils.isNotBlank(level1)) {
			this.level1Field = SolrUtils.facetName(level1);
		}
		if (StringUtils.isNotBlank(level1)) {
			this.level2Field = SolrUtils.facetName(level2);
		}
		if (StringUtils.isNotBlank(level1)) {
			this.level3Field = SolrUtils.facetName(level3);
		}
		return this;
	}

	public ElaborateSearchParameters setSearchInTranscriptions(boolean searchInTranscriptions) {
		this.searchInTranscriptions = searchInTranscriptions;
		return this;
	}

	public boolean getSearchInTranscriptions() {
		return searchInTranscriptions;
	}

	public ElaborateSearchParameters setSearchInAnnotations(boolean searchInAnnotations) {
		this.searchInAnnotations = searchInAnnotations;
		return this;
	}

	public boolean getSearchInAnnotations() {
		return searchInAnnotations;
	}

	/* ------------------------------------------------------------------------------------------------------------------------------------ */

	@JsonIgnore
	public Map<String, String> getTextFieldsToSearch() {
		Map<String, String> map = Maps.newLinkedHashMap();
		String textlayerPrefix = isCaseSensitive() ? SolrFields.TEXTLAYERCS_PREFIX : SolrFields.TEXTLAYER_PREFIX;
		String annotationPrefix = isCaseSensitive() ? SolrFields.ANNOTATIONCS_PREFIX : SolrFields.ANNOTATION_PREFIX;
		for (String textLayer : textLayers) {
			String fieldname = SolrUtils.normalize(textLayer);
			if (getSearchInTranscriptions()) {
				map.put(textlayerPrefix + fieldname, textLayer);
			}
			if (getSearchInAnnotations()) {
				map.put(annotationPrefix + fieldname, textLayer + " annotations");
			}
		}

		return map;
	}

	public ElaborateSearchParameters setRanges(List<RangeField> ranges) {
		this.ranges = ranges;
		return this;
	}

	public List<RangeField> getRanges() {
		return ranges;
	}

	/* ------------------------------------------------------------------------------------------------------------------------------------ */

	public String getLevel1Field() {
		return level1Field;
	}

	public String getLevel2Field() {
		return level2Field;
	}

	public String getLevel3Field() {
		return level3Field;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE, false);
	}

	public boolean isLevelFieldsSet() {
		return StringUtils.isNotEmpty(level1Field) //
				&& StringUtils.isNotEmpty(level2Field) //
				&& StringUtils.isNotEmpty(level3Field);
	}
}
