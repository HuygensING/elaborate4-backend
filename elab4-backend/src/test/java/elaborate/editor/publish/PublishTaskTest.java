package elaborate.editor.publish;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2016 Huygens ING
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
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.guava.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import elaborate.editor.AbstractTest;
import elaborate.editor.model.ProjectMetadataFields;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.TranscriptionType;
import elaborate.editor.publish.Publication.Settings;
import elaborate.editor.publish.PublishTask.AnnotationPublishData;
import elaborate.editor.publish.PublishTask.AnnotationTypeData;
import elaborate.editor.publish.PublishTask.EntryData;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.solr.FacetInfo;

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
    when(mockProject.getName()).thenReturn("project-name");
    when(mockProject.getLevel1()).thenReturn("level1");
    when(mockProject.getLevel2()).thenReturn("level2");
    when(mockProject.getLevel3()).thenReturn("level3");

    PublishTask publishTask = new PublishTask(settings);
    EntryData entry1 = new EntryData(1l, "uno", "uno", "entry1.json", ArrayListMultimap.<String, String> create());
    EntryData entry2 = new EntryData(2l, "due", "due", "entry2.json", ArrayListMultimap.<String, String> create());
    List<EntryData> entries = ImmutableList.of(entry1, entry2);

    Map<Long, List<String>> thumbnails = Maps.newHashMap();
    Map<String, Object> projectData = publishTask.getProjectData(mockProject, entries, thumbnails);
    assertThat(projectData.get("title")).isEqualTo(projectTitle);

    Log.info("projectData={}", projectData);

    String json = PublishTask.toJson(projectData);
    Log.info("json={}", json);
    assertThat(json).isNotEmpty();
  }

  @Test
  public void testGetProjectData_WithPublicationTitle() throws Exception {
    Settings settings = mock(Publication.Settings.class);
    Project mockProject = mock(Project.class);
    when(mockProject.getName()).thenReturn("project-name");
    when(mockProject.getLevel1()).thenReturn("level1");
    when(mockProject.getLevel2()).thenReturn("level2");
    when(mockProject.getLevel3()).thenReturn("level3");
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
    EntryData entry1 = new EntryData(1l, "uno", "uno", "entry1.json", ArrayListMultimap.<String, String> create());
    EntryData entry2 = new EntryData(2l, "due", "due", "entry2.json", ArrayListMultimap.<String, String> create());
    List<EntryData> entries = ImmutableList.of(entry1, entry2);

    Map<Long, List<String>> thumbnails = Maps.newHashMap();
    Map<String, Object> projectData = publishTask.getProjectData(mockProject, entries, thumbnails);
    assertThat(projectData.get("title")).isEqualTo(publicationTitle);

    @SuppressWarnings("unchecked")
    Map<String, List<String>> map = (Map<String, List<String>>) projectData.get("metadata");
    assertThat(map).doesNotContainKey(ProjectMetadataFields.ANNOTATIONTYPE_BOLD_NAME);

    boolean containsKey = map.containsKey(ProjectMetadataFields.ANNOTATIONTYPE_BOLD_NAME);
    assertThat(containsKey).isFalse();
    assertThat(projectData).doesNotContainKey("entryTermSingular");
    assertThat(projectData.get("textFont")).isEqualTo("comicsans");

    Log.info("projectData={}", projectData);

    String json = PublishTask.toJson(projectData);
    Log.info("json={}", json);
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
    String[] textLayers = new String[] { TranscriptionType.DIPLOMATIC, TranscriptionType.COMMENTS };
    when(project.getTextLayers()).thenReturn(textLayers);

    List<String> projectEntryMetadataFields = Lists.newArrayList("Meta1", "Meta2");
    Map<String, String> map = Maps.newHashMap();
    Map<String, Object> projectEntryData = publishTask.getProjectEntryData(entry, projectEntryMetadataFields, map);
    assertThat(projectEntryData.get("name")).isEqualTo(entryName);

    Log.info("projectEntryData={}", projectEntryData);

    String json = PublishTask.toJson(projectEntryData);
    Log.info("json={}", json);
    assertThat(json).isNotEmpty();
  }

  @Test
  public void testGetSearchConfigMap() throws Exception {
    List<String> selectedProjectEntryMetadataFields = ImmutableList.of("Field1", "Field2", "Field3");
    Project project = new Project()//
        .setProjectEntryMetadataFieldnames(ImmutableList.of("Field1", "Field2", "Field3", "field4"))//
        .setLevel1("Field1")//
        .setLevel2("Field3");
    Collection<String> multivaluedFacetTitles = Lists.newArrayList();
    SearchConfig searchConfig = new SearchConfig(project, selectedProjectEntryMetadataFields, multivaluedFacetTitles);
    Log.info("searchConfig={}", searchConfig);
    assertThat(searchConfig).isNotNull();

    Map<String, FacetInfo> map = searchConfig.getFacetInfoMap();
    assertThat(map).isNotNull();
    assertThat(map).containsKey("metadata_field1");
    assertThat(map).doesNotContainKey("publishable");
    String json = PublishTask.toJson(searchConfig);
    Log.info(json);
    assertThat(json).isNotNull();
  }

  @Test
  public void testSetText() throws Exception {
    AnnotationPublishData ad = new AnnotationPublishData().setText("<span class=\"annotationStub\"><span class=\"citedAnnotation\">dit is de geannoteerde tekst</span></span> dit is de annotatietekst");
    // assertThat(ad.getText()).isEqualTo("dit is de annotatietekst");
    //		assertThat(ad.getText()).isEqualTo("dit is de geannoteerde tekst dit is de annotatietekst");
    assertThat(ad.getText()).isEqualTo("<span class=\"annotationStub\"><span class=\"citedAnnotation\">dit is de geannoteerde tekst</span></span> dit is de annotatietekst");
  }

  @Test
  public void testGetTypographicalAnnotationMap() throws Exception {
    Settings settings = mock(Publication.Settings.class);
    PublishTask publishTask = new PublishTask(settings);
    Project project = mock(Project.class);
    Map<String, String> metadataMap = ImmutableMap.of(//
        ProjectMetadataFields.ANNOTATIONTYPE_BOLD_NAME, "bold", //
        ProjectMetadataFields.ANNOTATIONTYPE_BOLD_DESCRIPTION, "Vetgedrukt", //
        ProjectMetadataFields.ANNOTATIONTYPE_ITALIC_NAME, "italic", //
        ProjectMetadataFields.ANNOTATIONTYPE_ITALIC_DESCRIPTION, ""//
    );
    when(project.getMetadataMap()).thenReturn(metadataMap);
    Map<String, String> map = publishTask.getTypographicalAnnotationMap(project);
    assertThat(map).containsOnly(entry("b", "Vetgedrukt [bold]"), entry("i", "italic"));
  }

  @Test
  public void testAnnotationTypeKey() throws Exception {
    Settings settings = mock(Publication.Settings.class);
    PublishTask publishTask = new PublishTask(settings);
    AnnotationTypeData ad = new AnnotationTypeData().setName("name").setDescription("description");
    String key = publishTask.annotationTypeKey(ad);
    assertThat(key).isEqualTo("description [name]");
  }

  @Test
  public void testGetBaseURLFillsInProjectname() throws Exception {
    Settings settings = mock(Publication.Settings.class);
    PublishTask publishTask = new PublishTask(settings);
    assertThat(publishTask.getBaseURL("project-name")).isEqualTo("http://example.org/project-name/draft");
  }

  @Test
  public void testMultiValuedFacets() {
    Project project = mock(Project.class);
    Settings settings = mock(Publication.Settings.class);
    PublishTask publishTask = new PublishTask(settings);
    Map<String, String> metadataMap = ImmutableMap.of(//
        ProjectMetadataFields.MULTIVALUED_METADATA_FIELDS, "MultivaluedField 1;MultivaluedField 2"//
    );
    when(project.getMetadataMap()).thenReturn(metadataMap);

    Collection<String> facetsToSplit = publishTask.getFacetsToSplit(project);

    assertThat(facetsToSplit).containsOnly("metadata_multivaluedfield_1", "metadata_multivaluedfield_2");
  }

  @Test
  public void testMultivalueFacetValueIndex() {
    ProjectEntry projectEntry = mock(ProjectEntry.class);
    when(projectEntry.getMetadataValue("multi")).thenReturn("a | b | c");
    when(projectEntry.getMetadataValue("single")).thenReturn("d | e | f");
    String[] multivaluedFacetNames = new String[] { "multi" };
    Multimap<String, String> multivaluedFacetValues = PublishTask.getMultivaluedFacetValues(multivaluedFacetNames, projectEntry);
    assertThat(multivaluedFacetValues).containsValues("a", "b", "c");
    assertThat(multivaluedFacetValues).containsKeys("multi");
    assertThat(multivaluedFacetValues).hasSize(3);

  }
}
