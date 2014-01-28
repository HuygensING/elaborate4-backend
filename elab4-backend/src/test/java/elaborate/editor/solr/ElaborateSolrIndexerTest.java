package elaborate.editor.solr;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import elaborate.AbstractTest;
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
