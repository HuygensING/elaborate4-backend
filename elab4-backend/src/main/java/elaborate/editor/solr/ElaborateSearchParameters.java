package elaborate.editor.solr;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import nl.knaw.huygens.solr.FacetedSearchParameters;
import nl.knaw.huygens.solr.SolrUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import elaborate.util.StringUtil;

@XmlRootElement
public class ElaborateSearchParameters extends FacetedSearchParameters<ElaborateSearchParameters> {
	private long projectId = 0;
	private List<String> textLayers = Lists.newArrayList();
	private boolean searchInTranscriptions = true;
	private boolean searchInAnnotations = false;
	private String level1Field = SolrFields.NAME;
	private String level2Field = SolrFields.NAME;
	private String level3Field = SolrFields.NAME;

	public ElaborateSearchParameters setProjectId(final long projectId) {
		this.projectId = projectId;
		return this;
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

	public long getProjectId() {
		return projectId;
	}

	public ElaborateSearchParameters setTextLayers(final List<String> _textLayers) {
		this.textLayers = _textLayers;
		return this;
	}

	public List<String> getTextLayers() {
		return textLayers;
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
			String fieldname = StringUtil.normalize(textLayer);
			if (getSearchInTranscriptions()) {
				map.put(textlayerPrefix + fieldname, textLayer);
			}
			if (getSearchInAnnotations()) {
				map.put(annotationPrefix + fieldname, textLayer + " annotations");
			}
		}

		return map;
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
}
