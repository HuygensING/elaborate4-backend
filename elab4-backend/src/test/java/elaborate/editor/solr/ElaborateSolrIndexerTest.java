package elaborate.editor.solr;

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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import nl.knaw.huygens.facetedsearch.SolrFields;

import org.apache.solr.common.SolrInputDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import elaborate.editor.AbstractTest;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.ProjectEntryMetadataItem;
import elaborate.editor.model.orm.User;

@Ignore
public class ElaborateSolrIndexerTest extends AbstractTest {

	private ProjectEntry entry;

	@Before
	public void setUp() throws Exception {
		Project project = new Project();
		List<ProjectEntryMetadataItem> projectEntryMetadataItems = ImmutableList.<ProjectEntryMetadataItem> of();
		User creator = new User();
		entry = project.addEntry("brief 1", creator).setProjectEntryMetadataItems(projectEntryMetadataItems);
		entry.addTranscription(creator)//
				.setBody("<body><ab id=\"9085822\"/>sdgdgdgsdgsdfg<ae id=\"9085822\"/></body>")//
				.setTextLayer("layer1");
		entry.addTranscription(creator)//
				.setBody("<body>aap <i>noot</i> mies</body>")//
				.setTextLayer("layer2");
	}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void testGetSolrInputDocument() throws Exception {
		SolrInputDocument docForEditor = ElaborateSolrIndexer.getSolrInputDocument(entry, false);
		assertThat(docForEditor != null).isTrue();
		LOG.info("docForEditor={}", docForEditor);
		assertThat(docForEditor.getField(SolrFields.ID).getValue()).isEqualTo(entry.getId());
		assertThat(docForEditor.getField(SolrFields.NAME).getValue()).isEqualTo(entry.getName());
		assertThat(docForEditor.getField(SolrFields.PROJECT_ID).getValue()).isEqualTo(entry.getProject().getId());
		assertThat(docForEditor.getField(SolrFields.PUBLISHABLE).getValue()).isEqualTo(entry.isPublishable());

		SolrInputDocument docForPublication = ElaborateSolrIndexer.getSolrInputDocument(entry, true);
		assertThat(docForPublication != null).isTrue();
		LOG.info("docForPublication={}", docForPublication);
		assertThat(docForPublication.getField(SolrFields.ID).getValue()).isEqualTo(entry.getId());
		assertThat(docForPublication.getField(SolrFields.NAME).getValue()).isEqualTo(entry.getName());
		assertThat(docForPublication.getField(SolrFields.PROJECT_ID)).isEqualTo(null);
		assertThat(docForPublication.getField(SolrFields.PUBLISHABLE)).isEqualTo(null);
	}

	@Test
	public void testConvert() throws Exception {
		String xml = "";
		String expected = "";
		String out = ElaborateSolrIndexer.convert(xml);
		assertThat(out).isEqualTo(expected);
	}
}
