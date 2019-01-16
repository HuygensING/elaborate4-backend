package elaborate.editor.solr;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2019 Huygens ING
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

import com.google.common.collect.ImmutableList;
import elaborate.editor.AbstractTest;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.facetedsearch.SolrFields;
import org.apache.solr.common.SolrInputDocument;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ElaborateSolrIndexerTest extends AbstractTest {

	// private ProjectEntry entry;

	// // @Before
	// public void setUp() throws Exception {
	// Project project = new Project();
	// List<ProjectEntryMetadataItem> projectEntryMetadataItems = ImmutableList.<ProjectEntryMetadataItem> of();
	// User creator = new User();
	// entry = project.addEntry("brief 1", creator).setProjectEntryMetadataItems(projectEntryMetadataItems);
	// entry.addTranscription(creator)//
	// .setBody("<body><ab id=\"9085822\"/>sdgdgdgsdgsdfg<ae id=\"9085822\"/></body>")//
	// .setTextLayer("layer1");
	// entry.addTranscription(creator)//
	// .setBody("<body>aap <i>noot</i> mies</body>")//
	// .setTextLayer("layer2");
	// }

	@Test
	public void test_newlines_in_entry_metadata_are_replaced() {
		// Setup mock project
		Project mockProject = mock(Project.class);
		when(mockProject.getId()).thenReturn(2L);
		when(mockProject.getProjectEntryMetadataFieldnames()).thenReturn(ImmutableList.of("multiline"));

		// Setup mock projectentry
		ProjectEntry mockEntry = mock(ProjectEntry.class);
		when(mockEntry.getId()).thenReturn(1L);
		when(mockEntry.getName()).thenReturn("name");
		when(mockEntry.getProject()).thenReturn(mockProject);
		when(mockEntry.isPublishable()).thenReturn(true);
		when(mockEntry.getMetadataValue("multiline")).thenReturn("Metadata\nvalues\nwith\nmultiple\nlines");

		SolrInputDocument docForEditor = ElaborateSolrIndexer.getSolrInputDocument(mockEntry, false, null);
		assertThat(docForEditor != null).isTrue();
		Log.info("docForEditor={}", docForEditor);
		assertThat(docForEditor.getField(SolrFields.ID).getValue()).isEqualTo(mockEntry.getId());
		assertThat(docForEditor.getField(SolrFields.NAME).getValue()).isEqualTo(mockEntry.getName());
		assertThat(docForEditor.getField(SolrFields.PROJECT_ID).getValue()).isEqualTo(mockEntry.getProject().getId());
		assertThat(docForEditor.getField(SolrFields.PUBLISHABLE).getValue()).isEqualTo(mockEntry.isPublishable());
		assertThat(docForEditor.getField("metadata_multiline").getValue()).isEqualTo("Metadata/values/with/multiple/lines");

		when(mockEntry.getMetadataValue("multiline")).thenReturn("Metadata\rvalues\rwith\rmultiple\r\nlines");
		SolrInputDocument docForPublication = ElaborateSolrIndexer.getSolrInputDocument(mockEntry, true, Lists.<String> newArrayList());
		assertThat(docForPublication != null).isTrue();
		Log.info("docForPublication={}", docForPublication);
		assertThat(docForPublication.getField(SolrFields.ID).getValue()).isEqualTo(mockEntry.getId());
		assertThat(docForPublication.getField(SolrFields.NAME).getValue()).isEqualTo(mockEntry.getName());
		assertThat(docForPublication.getField(SolrFields.PROJECT_ID)).isEqualTo(null);
		assertThat(docForPublication.getField(SolrFields.PUBLISHABLE)).isEqualTo(null);
		assertThat(docForEditor.getField("metadata_multiline").getValue()).isEqualTo("Metadata/values/with/multiple/lines");
	}

	@Test
	public void test_multivalued_metadata_is_split_into_multiple_fields() {
		// Setup mock project
		Project mockProject = mock(Project.class);
		when(mockProject.getId()).thenReturn(2L);
		when(mockProject.getProjectEntryMetadataFieldnames()).thenReturn(ImmutableList.of("Field 1", "MultiField 1"));

		// Setup mock projectentry
		ProjectEntry mockEntry = mock(ProjectEntry.class);
		when(mockEntry.getId()).thenReturn(1L);
		when(mockEntry.getName()).thenReturn("name");
		when(mockEntry.getProject()).thenReturn(mockProject);
		when(mockEntry.isPublishable()).thenReturn(true);
		when(mockEntry.getMetadataValue("Field 1")).thenReturn("Value 1 | Value 2 | Value 3");
		when(mockEntry.getMetadataValue("MultiField 1")).thenReturn("Value 1 | Value 2 | Value 3");

		SolrInputDocument docForEditor = ElaborateSolrIndexer.getSolrInputDocument(mockEntry, false, null);
		assertThat(docForEditor != null).isTrue();
		Log.info("docForEditor={}", docForEditor);
		assertThat(docForEditor.getField(SolrFields.ID).getValue()).isEqualTo(mockEntry.getId());
		assertThat(docForEditor.getField(SolrFields.NAME).getValue()).isEqualTo(mockEntry.getName());
		assertThat(docForEditor.getField(SolrFields.PROJECT_ID).getValue()).isEqualTo(mockEntry.getProject().getId());
		assertThat(docForEditor.getField(SolrFields.PUBLISHABLE).getValue()).isEqualTo(mockEntry.isPublishable());
		assertThat(docForEditor.getField("metadata_multifield_1").getValue()).isEqualTo("Value 1 | Value 2 | Value 3");

		Collection<String> multiValuedFacetNames = ImmutableList.of("metadata_multifield_1");
		SolrInputDocument docForPublication = ElaborateSolrIndexer.getSolrInputDocument(mockEntry, true, multiValuedFacetNames);
		assertThat(docForPublication != null).isTrue();
		Log.info("docForPublication={}", docForPublication);
		assertThat(docForPublication.getField(SolrFields.ID).getValue()).isEqualTo(mockEntry.getId());
		assertThat(docForPublication.getField(SolrFields.NAME).getValue()).isEqualTo(mockEntry.getName());
		assertThat(docForPublication.getField(SolrFields.PROJECT_ID)).isEqualTo(null);
		assertThat(docForPublication.getField(SolrFields.PUBLISHABLE)).isEqualTo(null);
		assertThat(docForPublication.getField("mv_metadata_multifield_1").getValues()).containsOnly("Value 1", "Value 2", "Value 3");
	}

	@Test
	public void testConvert() {
		String xml = "";
		String expected = "";
		String out = ElaborateSolrIndexer.convert(xml);
		assertThat(out).isEqualTo(expected);
	}

	@Test
	public void testExtractCorrespondentsFromValueWithSlash() {
		String value = "Boddaert, Elisabeth Carolina/Spengler, Constantia Gerharda Heije van";
		List<String> extractCorrespondents = ElaborateSolrIndexer.extractCorrespondents(value);
		assertThat(extractCorrespondents).containsExactly("Boddaert, Elisabeth Carolina", "Spengler, Constantia Gerharda Heije van");
	}

	@Test
	public void testExtractCorrespondentsFromValueWithSlashAndHash() {
		String value = "Groesbeek, Klaas/Nijhoff, Paulus#Scheltema & Holkema's Boekhandel, Uitgeverij";
		List<String> extractCorrespondents = ElaborateSolrIndexer.extractCorrespondents(value);
		assertThat(extractCorrespondents).containsExactly("Groesbeek, Klaas", "Nijhoff, Paulus");
	}

	@Test
	public void testExtractCorrespondentsFromValueWithArrow() {
		String value = "Alberdingk Thijm, Karel Joan Lodewijk-->Verwey, Albert";
		List<String> extractCorrespondents = ElaborateSolrIndexer.extractCorrespondents(value);
		assertThat(extractCorrespondents).containsExactly("Alberdingk Thijm, Karel Joan Lodewijk", "Verwey, Albert");
	}

	@Test
	public void testExtractCorrespondentsFromValueWithSlashedAndArrow() {
		String value = "Groesbeek, Klaas/Nijhoff, Paulus-->Verwey, Albert/Alberdingk Thijm, Karel Joan Lodewijk";
		List<String> extractCorrespondents = ElaborateSolrIndexer.extractCorrespondents(value);
		assertThat(extractCorrespondents).containsExactly("Groesbeek, Klaas", "Nijhoff, Paulus", "Verwey, Albert", "Alberdingk Thijm, Karel Joan Lodewijk");
	}

	@Test
	public void testExtractCorrespondentsFromSingleValue() {
		String value = "Aert, Louis";
		List<String> extractCorrespondents = ElaborateSolrIndexer.extractCorrespondents(value);
		assertThat(extractCorrespondents).containsExactly("Aert, Louis");
	}
}
