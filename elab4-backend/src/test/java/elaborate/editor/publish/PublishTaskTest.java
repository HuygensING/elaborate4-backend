package elaborate.editor.publish;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import elaborate.AbstractTest;
import elaborate.editor.model.FacetInfo;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.publish.Publication.Settings;
import elaborate.editor.publish.PublishTask.AnnotationData;

public class PublishTaskTest extends AbstractTest {

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testGetProjectData() throws Exception {
    Settings settings = mock(Publication.Settings.class);
    Project mockProject = mock(Project.class);
    String projectTitle = "titel";
    when(mockProject.getTitle()).thenReturn(projectTitle);

    PublishTask publishTask = new PublishTask(settings);
    List<String> entries = ImmutableList.of("entry1.json", "entry2.json");

    Map<Long, List<String>> thumbnails = Maps.newHashMap();
    Map<String, Object> projectData = publishTask.getProjectData(mockProject, entries, thumbnails);
    assertEquals(projectTitle, projectData.get("title"));

    LOG.info("projectData={}", projectData);

    String json = PublishTask.toJson(projectData);
    LOG.info("json={}", json);
    assertTrue(StringUtils.isNotBlank(json));
  }

  @Test
  public void testEntryFilename() throws Exception {
    ProjectEntry entry = mock(ProjectEntry.class);
    when(entry.getId()).thenReturn(9999l);

    assertEquals("entry9999.json", PublishTask.entryFilename(9999));
  }

  @Test
  public void testGetProjectEntryData() throws Exception {
    Settings settings = mock(Publication.Settings.class);

    PublishTask publishTask = new PublishTask(settings);

    ProjectEntry entry = mock(ProjectEntry.class);
    Project project = mock(Project.class);
    String entryName = "entryname";
    when(entry.getName()).thenReturn(entryName);
    when(entry.getProject()).thenReturn(project);
    String[] textLayers = new String[] { "Diplomatic", "Comments" };
    when(project.getTextLayers()).thenReturn(textLayers);

    List<String> projectEntryMetadataFields = Lists.newArrayList("Meta1", "Meta2");
    Map<String, Object> projectEntryData = publishTask.getProjectEntryData(entry, projectEntryMetadataFields);
    assertEquals(entryName, projectEntryData.get("name"));

    LOG.info("projectEntryData={}", projectEntryData);

    String json = PublishTask.toJson(projectEntryData);
    LOG.info("json={}", json);
    assertTrue(StringUtils.isNotBlank(json));
  }

  @Test
  public void testGetSearchConfigMap() throws Exception {
    List<String> selectedProjectEntryMetadataFields = ImmutableList.of("Field1", "Field2", "Field3");
    Project project = new Project()//
        .setProjectEntryMetadataFieldnames(ImmutableList.of("Field1", "Field2", "Field3", "field4"))//
        .setLevel1("Field1")//
        .setLevel2("Field3");
    SearchConfig searchConfig = new SearchConfig(project, selectedProjectEntryMetadataFields);
    LOG.info("searchConfig={}", searchConfig);
    assertNotNull(searchConfig);

    Map<String, FacetInfo> map = searchConfig.getFacetInfoMap();
    assertNotNull(map);
    assertTrue(map.containsKey("metadata_field1"));
    assertFalse(map.containsKey("publishable"));
    String json = PublishTask.toJson(searchConfig);
    LOG.info(json);
    assertNotNull(json);
  }

  @Test
  public void testSetText() throws Exception {
    AnnotationData ad = new AnnotationData().setText("<span class=\"annotationStub\"><span class=\"citedAnnotation\">dit is de geannoteerde tekst</span></span> dit is de annotatietekst");
    assertEquals("dit is de annotatietekst", ad.getText());
  }
}
