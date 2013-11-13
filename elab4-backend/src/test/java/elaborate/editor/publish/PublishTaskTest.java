package elaborate.editor.publish;

import static org.assertj.core.api.Assertions.assertThat;
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
import elaborate.editor.model.ProjectMetadataFields;
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
  public void testGetProjectData_WithProjectTitle() throws Exception {
    Settings settings = mock(Publication.Settings.class);
    Project mockProject = mock(Project.class);
    String projectTitle = "titel";
    when(mockProject.getTitle()).thenReturn(projectTitle);

    PublishTask publishTask = new PublishTask(settings);
    List<String> entries = ImmutableList.of("entry1.json", "entry2.json");

    Map<Long, List<String>> thumbnails = Maps.newHashMap();
    Map<String, Object> projectData = publishTask.getProjectData(mockProject, entries, thumbnails);
    assertThat(projectData.get("title")).isEqualTo(projectTitle);

    LOG.info("projectData={}", projectData);

    String json = PublishTask.toJson(projectData);
    LOG.info("json={}", json);
    assertThat(StringUtils.isNotBlank(json)).isTrue();
  }

  @Test
  public void testGetProjectData_WithPublicationTitle() throws Exception {
    Settings settings = mock(Publication.Settings.class);
    Project mockProject = mock(Project.class);
    String projectTitle = "Project title";
    when(mockProject.getTitle()).thenReturn(projectTitle);

    String publicationTitle = "Publication title";
    Map<String, String> metadataMap = Maps.newHashMap();
    metadataMap.put(ProjectMetadataFields.PUBLICATION_TITLE, publicationTitle);
    metadataMap.put(ProjectMetadataFields.ANNOTATIONTYPE_BOLD_NAME, "bold");
    metadataMap.put(ProjectMetadataFields.TEXT_FONT, "comicsans");
    metadataMap.put("extra", "extra");
    when(mockProject.getMetadataMap()).thenReturn(metadataMap);

    PublishTask publishTask = new PublishTask(settings);
    List<String> entries = ImmutableList.of("entry1.json", "entry2.json");

    Map<Long, List<String>> thumbnails = Maps.newHashMap();
    Map<String, Object> projectData = publishTask.getProjectData(mockProject, entries, thumbnails);
    assertThat(projectData.get("title")).isEqualTo(publicationTitle);
    Map<String, List<String>> map = (Map<String, List<String>>) projectData.get("metadata");
    assertThat(map).doesNotContainKey(ProjectMetadataFields.ANNOTATIONTYPE_BOLD_NAME);
    boolean containsKey = map.containsKey(ProjectMetadataFields.ANNOTATIONTYPE_BOLD_NAME);
    assertThat(containsKey).isFalse();
    assertThat(projectData.containsKey("entryTermSingular")).isFalse();
    assertThat(projectData.get("textFont")).isEqualTo("comicsans");

    LOG.info("projectData={}", projectData);

    String json = PublishTask.toJson(projectData);
    LOG.info("json={}", json);
    assertThat(StringUtils.isNotBlank(json)).isTrue();
  }

  @Test
  public void testEntryFilename() throws Exception {
    ProjectEntry entry = mock(ProjectEntry.class);
    when(entry.getId()).thenReturn(9999l);

    assertThat(PublishTask.entryFilename(9999)).isEqualTo("entry9999.json");
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
    assertThat(projectEntryData.get("name")).isEqualTo(entryName);

    LOG.info("projectEntryData={}", projectEntryData);

    String json = PublishTask.toJson(projectEntryData);
    LOG.info("json={}", json);
    assertThat(StringUtils.isNotBlank(json)).isTrue();
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
    assertThat(searchConfig).isNotNull();

    Map<String, FacetInfo> map = searchConfig.getFacetInfoMap();
    assertThat(map).isNotNull();
    assertThat(map).containsKey("metadata_field1");
    assertThat(map).doesNotContainKey("publishable");
    String json = PublishTask.toJson(searchConfig);
    LOG.info(json);
    assertThat(json).isNotNull();
  }

  @Test
  public void testSetText() throws Exception {
    AnnotationData ad = new AnnotationData().setText("<span class=\"annotationStub\"><span class=\"citedAnnotation\">dit is de geannoteerde tekst</span></span> dit is de annotatietekst");
    assertThat(ad.getText()).isEqualTo("dit is de annotatietekst");
  }
}
