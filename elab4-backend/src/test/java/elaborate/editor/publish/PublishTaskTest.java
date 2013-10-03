package elaborate.editor.publish;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import elaborate.AbstractTest;
import elaborate.editor.model.FacetInfo;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.publish.Publication.Settings;
import elaborate.editor.publish.PublishTask.ThumbnailInfo;

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

    List<ThumbnailInfo> thumbnails = Lists.newArrayList();
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
    String entryName = "entryname";
    when(entry.getName()).thenReturn(entryName);

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
}
