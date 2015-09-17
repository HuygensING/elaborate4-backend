package elaborate.editor.publish;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import elaborate.editor.model.orm.Project;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.solr.FacetInfo;

public class SearchConfigTest {

	@Test
	public void testSearchConfigWithMultivaluedFacets() {
		Project project = new Project()//
				.setProjectEntryMetadataFieldnames(ImmutableList.of("Field 1", "Field 2", "Field 3", "field 4", "MultiField 1", "MultiField 2"))//
				.setLevel1("Field1")//
				.setLevel2("Field3");
		List<String> metadataFieldsForFacets = ImmutableList.of("Field 1", "Field 2", "MultiField 1", "MultiField 2");
		Collection<String> multivaluedFacetNames = ImmutableList.of("metadata_multifield_1", "metadata_multifield_2");

		SearchConfig ac = new SearchConfig(project, metadataFieldsForFacets, multivaluedFacetNames);
		Map<String, FacetInfo> facetInfoMap = ac.getFacetInfoMap();

		assertThat(facetInfoMap).containsKeys("metadata_field_1", "metadata_field_2", "mv_metadata_multifield_1", "mv_metadata_multifield_2");
		FacetInfo facetInfo = facetInfoMap.get("mv_metadata_multifield_2");
		Log.info("facetInfo={}", facetInfo);
		assertThat(facetInfo.getTitle()).isEqualTo("MultiField 2");
		assertThat(facetInfo.getName()).isEqualTo("mv_metadata_multifield_2");
	}

}
