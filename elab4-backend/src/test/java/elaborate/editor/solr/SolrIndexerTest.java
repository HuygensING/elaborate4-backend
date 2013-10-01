package elaborate.editor.solr;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.junit.After;
import org.junit.Before;

import com.google.common.collect.ImmutableList;

import elaborate.AbstractTest;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.ProjectEntryMetadataItem;
import elaborate.editor.model.orm.User;

public class SolrIndexerTest extends AbstractTest {

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

  //  @Test
  public void testGetSolrInputDocument() throws Exception {
    SolrInputDocument docForEditor = SolrIndexer.getSolrInputDocument(entry, false);
    assertNotNull(docForEditor);
    LOG.info("docForEditor={}", docForEditor);
    assertEquals(entry.getId(), docForEditor.getField(SolrFields.ID).getValue());
    assertEquals(entry.getName(), docForEditor.getField(SolrFields.NAME).getValue());
    assertEquals(entry.getProject().getId(), docForEditor.getField(SolrFields.PROJECT_ID).getValue());
    assertEquals(entry.isPublishable(), docForEditor.getField(SolrFields.PUBLISHABLE).getValue());

    SolrInputDocument docForPublication = SolrIndexer.getSolrInputDocument(entry, true);
    assertNotNull(docForPublication);
    LOG.info("docForPublication={}", docForPublication);
    assertEquals(entry.getId(), docForPublication.getField(SolrFields.ID).getValue());
    assertEquals(entry.getName(), docForPublication.getField(SolrFields.NAME).getValue());
    assertEquals(null, docForPublication.getField(SolrFields.PROJECT_ID));
    assertEquals(null, docForPublication.getField(SolrFields.PUBLISHABLE));
  }

  //  @Test
  public void testConvert() throws Exception {
    String xml = "";
    String expected = "";
    String out = SolrIndexer.convert(xml);
    assertEquals(expected, out);
  }
}
