package elaborate.editor.publish;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2021 Huygens ING
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.knaw.huygens.Log;
import nl.knaw.huygens.solr.FacetInfo;

import elaborate.editor.AbstractTest;
import elaborate.editor.model.ProjectMetadataFields;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.TranscriptionType;
import elaborate.editor.publish.Publication.Settings;
import elaborate.editor.publish.PublishTask.AnnotationPublishData;
import elaborate.editor.publish.PublishTask.AnnotationTypeData;
import elaborate.editor.publish.PublishTask.EntryData;
import elaborate.util.XmlUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.guava.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PublishTaskTest extends AbstractTest {

  @Before
  public void setUp() {}

  @After
  public void tearDown() {}

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
    EntryData entry1 =
        new EntryData(1L, "uno", "uno", "entry1.json", ArrayListMultimap.<String, String>create());
    EntryData entry2 =
        new EntryData(2L, "due", "due", "entry2.json", ArrayListMultimap.<String, String>create());
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
    EntryData entry1 =
        new EntryData(1L, "uno", "uno", "entry1.json", ArrayListMultimap.<String, String>create());
    EntryData entry2 =
        new EntryData(2L, "due", "due", "entry2.json", ArrayListMultimap.<String, String>create());
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
  public void testEntryFilename() {
    ProjectEntry entry = mock(ProjectEntry.class);
    when(entry.getId()).thenReturn(9999L);

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
    String[] textLayers = new String[] {TranscriptionType.DIPLOMATIC, TranscriptionType.COMMENTS};
    when(project.getTextLayers()).thenReturn(textLayers);

    List<String> projectEntryMetadataFields = Lists.newArrayList("Meta1", "Meta2");
    Map<String, Object> projectEntryData =
        publishTask.getProjectEntryData(entry, projectEntryMetadataFields);
    assertThat(projectEntryData.get("name")).isEqualTo(entryName);

    Log.info("projectEntryData={}", projectEntryData);

    String json = PublishTask.toJson(projectEntryData);
    Log.info("json={}", json);
    assertThat(json).isNotEmpty();
  }

  @Test
  public void testGetSearchConfigMap() throws Exception {
    List<String> selectedProjectEntryMetadataFields =
        ImmutableList.of("Field1", "Field2", "Field3");
    Project project =
        new Project() //
            .setProjectEntryMetadataFieldnames(
                ImmutableList.of("Field1", "Field2", "Field3", "field4")) //
            .setLevel1("Field1") //
            .setLevel2("Field3");
    Collection<String> multivaluedFacetTitles = Lists.newArrayList();
    SearchConfig searchConfig =
        new SearchConfig(project, selectedProjectEntryMetadataFields, multivaluedFacetTitles);
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
  public void testSetText() {
    AnnotationPublishData ad =
        new AnnotationPublishData()
            .setText(
                "<span class=\"annotationStub\"><span class=\"citedAnnotation\">dit is de geannoteerde tekst</span></span> dit is de annotatietekst");
    // assertThat(ad.getText()).isEqualTo("dit is de annotatietekst");
    assertThat(ad.getText()).isEqualTo("dit is de geannoteerde tekst dit is de annotatietekst");
  }

  @Test
  public void testGetTypographicalAnnotationMap() {
    Settings settings = mock(Publication.Settings.class);
    PublishTask publishTask = new PublishTask(settings);
    Project project = mock(Project.class);
    Map<String, String> metadataMap =
        ImmutableMap.of( //
            ProjectMetadataFields.ANNOTATIONTYPE_BOLD_NAME, "bold", //
            ProjectMetadataFields.ANNOTATIONTYPE_BOLD_DESCRIPTION, "Vetgedrukt", //
            ProjectMetadataFields.ANNOTATIONTYPE_ITALIC_NAME, "italic", //
            ProjectMetadataFields.ANNOTATIONTYPE_ITALIC_DESCRIPTION, "" //
            );
    when(project.getMetadataMap()).thenReturn(metadataMap);
    Map<String, String> map = publishTask.getTypographicalAnnotationMap(project);
    assertThat(map).containsOnly(entry("b", "Vetgedrukt [bold]"), entry("i", "italic"));
  }

  @Test
  public void testAnnotationTypeKey() {
    Settings settings = mock(Publication.Settings.class);
    PublishTask publishTask = new PublishTask(settings);
    AnnotationTypeData ad = new AnnotationTypeData().setName("name").setDescription("description");
    String key = publishTask.annotationTypeKey(ad);
    assertThat(key).isEqualTo("description [name]");
  }

  @Test
  public void testGetBaseURLFillsInProjectname() {
    Settings settings = mock(Publication.Settings.class);
    PublishTask publishTask = new PublishTask(settings);
    assertThat(publishTask.getBaseURL("project-name"))
        .isEqualTo("http://example.org/project-name/draft");
  }

  @Test
  public void testMultiValuedFacets() {
    Project project = mock(Project.class);
    Settings settings = mock(Publication.Settings.class);
    PublishTask publishTask = new PublishTask(settings);
    Map<String, String> metadataMap =
        ImmutableMap.of( //
            ProjectMetadataFields.MULTIVALUED_METADATA_FIELDS,
            "MultivaluedField 1;MultivaluedField 2" //
            );
    when(project.getMetadataMap()).thenReturn(metadataMap);

    Collection<String> facetsToSplit = publishTask.getFacetsToSplit(project);

    assertThat(facetsToSplit)
        .containsOnly("metadata_multivaluedfield_1", "metadata_multivaluedfield_2");
  }

  @Test
  public void testMultivalueFacetValueIndex() {
    ProjectEntry projectEntry = mock(ProjectEntry.class);
    when(projectEntry.getMetadataValue("multi")).thenReturn("a | b | c");
    when(projectEntry.getMetadataValue("single")).thenReturn("d | e | f");
    String[] multivaluedFacetNames = new String[] {"multi"};
    Multimap<String, String> multivaluedFacetValues =
        PublishTask.getMultivaluedFacetValues(multivaluedFacetNames, projectEntry);
    assertThat(multivaluedFacetValues).containsValues("a", "b", "c");
    assertThat(multivaluedFacetValues).containsKeys("multi");
    assertThat(multivaluedFacetValues).hasSize(3);
  }

  @Test
  public void testCleanupAfterWord() {
    String dirty =
        "<!-- some word shit with \n lots of newlines \n and other #$% -->This is the actual text.<i style=\"mso-bidi-font-style:\n"
            + " normal\"> </i><!-- single line -->";
    String clean = PublishTask.cleanupAfterWord(dirty);
    assertThat(clean)
        .isEqualTo("This is the actual text.<i style=\"mso-bidi-font-style:\n" + " normal\"> </i>");
  }

  @Test
  public void testCleanupAfterWord2() {
    String dirty =
        " <i>Hem dede noot</i> (&lt; Dt. \"sie dadents noit\"): <!--[if gte mso 9]><xml>\n"
            + "  <w:WordDocument>\n"
            + "   <w:View>Normal</w:View>\n"
            + "   <w:Zoom>0</w:Zoom>\n"
            + "   <w:TrackMoves/>\n"
            + "   <w:TrackFormatting/>\n"
            + "   <w:HyphenationZone>21</w:HyphenationZone>\n"
            + "   <w:PunctuationKerning/>\n"
            + "   <w:ValidateAgainstSchemas/>\n"
            + "   <w:SaveIfXMLInvalid>false</w:SaveIfXMLInvalid>\n"
            + "   <w:IgnoreMixedContent>false</w:IgnoreMixedContent>\n"
            + "   <w:AlwaysShowPlaceholderText>false</w:AlwaysShowPlaceholderText>\n"
            + "   <w:DoNotPromoteQF/>\n"
            + "   <w:LidThemeOther>DE</w:LidThemeOther>\n"
            + "   <w:LidThemeAsian>X-NONE</w:LidThemeAsian>\n"
            + "   <w:LidThemeComplexScript>X-NONE</w:LidThemeComplexScript>\n"
            + "   <w:Compatibility>\n"
            + "    <w:BreakWrappedTables/>\n"
            + "    <w:SnapToGridInCell/>\n"
            + "    <w:WrapTextWithPunct/>\n"
            + "    <w:UseAsianBreakRules/>\n"
            + "    <w:DontGrowAutofit/>\n"
            + "    <w:SplitPgBreakAndParaMark/>\n"
            + "    <w:DontVertAlignCellWithSp/>\n"
            + "    <w:DontBreakConstrainedForcedTables/>\n"
            + "    <w:DontVertAlignInTxbx/>\n"
            + "    <w:Word11KerningPairs/>\n"
            + "    <w:CachedColBalance/>\n"
            + "   </w:Compatibility>\n"
            + "   <m:mathPr>\n"
            + "    <m:mathFont m:val=\"Cambria Math\"/>\n"
            + "    <m:brkBin m:val=\"before\"/>\n"
            + "    <m:brkBinSub m:val=\"&#45;-\"/>\n"
            + "    <m:smallFrac m:val=\"off\"/>\n"
            + "    <m:dispDef/>\n"
            + "    <m:lMargin m:val=\"0\"/>\n"
            + "    <m:rMargin m:val=\"0\"/>\n"
            + "    <m:defJc m:val=\"centerGroup\"/>\n"
            + "    <m:wrapIndent m:val=\"1440\"/>\n"
            + "    <m:intLim m:val=\"subSup\"/>\n"
            + "    <m:naryLim m:val=\"undOvr\"/>\n"
            + "   </m:mathPr></w:WordDocument>\n"
            + " </xml><![endif]-->De letterlijke weergave van de Dt. tekst (Mnl. „si\n"
            + " dadent node/nooit“) levert noch het gewenste rijm op \"broot\" noch een goede zin op. Mnl. \"hem dede noot\" betekent \"ze hadden er behoefte aan, ze hadden het nodig\".<span style=\"font-size:\n"
            + " 14.0pt;line-height:115%;font-family:&quot;Times New Roman&quot;,&quot;serif&quot;;mso-fareast-font-family:\n"
            + " &quot;Times New Roman&quot;;mso-ansi-language:DE;mso-fareast-language:DE;mso-bidi-language:\n"
            + " AR-SA\"><i style=\"mso-bidi-font-style:\n"
            + " normal\"> </i></span><!--[if gte mso 9]><xml>\n"
            + "  <w:LatentStyles DefLockedState=\"false\" DefUnhideWhenUsed=\"true\"\n"
            + "   DefSemiHidden=\"true\" DefQFormat=\"false\" DefPriority=\"99\"\n"
            + "   LatentStyleCount=\"267\">\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"0\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" QFormat=\"true\" Name=\"Normal\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"9\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" QFormat=\"true\" Name=\"heading 1\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"9\" QFormat=\"true\" Name=\"heading 2\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"9\" QFormat=\"true\" Name=\"heading 3\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"9\" QFormat=\"true\" Name=\"heading 4\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"9\" QFormat=\"true\" Name=\"heading 5\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"9\" QFormat=\"true\" Name=\"heading 6\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"9\" QFormat=\"true\" Name=\"heading 7\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"9\" QFormat=\"true\" Name=\"heading 8\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"9\" QFormat=\"true\" Name=\"heading 9\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"39\" Name=\"toc 1\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"39\" Name=\"toc 2\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"39\" Name=\"toc 3\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"39\" Name=\"toc 4\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"39\" Name=\"toc 5\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"39\" Name=\"toc 6\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"39\" Name=\"toc 7\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"39\" Name=\"toc 8\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"39\" Name=\"toc 9\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"35\" QFormat=\"true\" Name=\"caption\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"10\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" QFormat=\"true\" Name=\"Title\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"1\" Name=\"Default Paragraph Font\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"11\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" QFormat=\"true\" Name=\"Subtitle\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"22\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" QFormat=\"true\" Name=\"Strong\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"20\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" QFormat=\"true\" Name=\"Emphasis\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"59\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Table Grid\"/>\n"
            + "   <w:LsdException Locked=\"false\" UnhideWhenUsed=\"false\" Name=\"Placeholder Text\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"1\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" QFormat=\"true\" Name=\"No Spacing\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"60\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light Shading\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"61\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light List\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"62\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light Grid\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"63\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Shading 1\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"64\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Shading 2\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"65\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium List 1\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"66\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium List 2\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"67\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 1\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"68\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 2\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"69\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 3\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"70\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Dark List\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"71\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful Shading\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"72\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful List\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"73\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful Grid\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"60\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light Shading Accent 1\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"61\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light List Accent 1\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"62\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light Grid Accent 1\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"63\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Shading 1 Accent 1\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"64\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Shading 2 Accent 1\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"65\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium List 1 Accent 1\"/>\n"
            + "   <w:LsdException Locked=\"false\" UnhideWhenUsed=\"false\" Name=\"Revision\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"34\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" QFormat=\"true\" Name=\"List Paragraph\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"29\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" QFormat=\"true\" Name=\"Quote\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"30\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" QFormat=\"true\" Name=\"Intense Quote\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"66\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium List 2 Accent 1\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"67\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 1 Accent 1\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"68\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 2 Accent 1\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"69\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 3 Accent 1\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"70\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Dark List Accent 1\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"71\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful Shading Accent 1\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"72\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful List Accent 1\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"73\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful Grid Accent 1\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"60\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light Shading Accent 2\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"61\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light List Accent 2\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"62\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light Grid Accent 2\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"63\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Shading 1 Accent 2\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"64\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Shading 2 Accent 2\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"65\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium List 1 Accent 2\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"66\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium List 2 Accent 2\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"67\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 1 Accent 2\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"68\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 2 Accent 2\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"69\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 3 Accent 2\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"70\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Dark List Accent 2\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"71\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful Shading Accent 2\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"72\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful List Accent 2\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"73\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful Grid Accent 2\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"60\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light Shading Accent 3\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"61\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light List Accent 3\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"62\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light Grid Accent 3\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"63\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Shading 1 Accent 3\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"64\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Shading 2 Accent 3\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"65\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium List 1 Accent 3\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"66\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium List 2 Accent 3\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"67\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 1 Accent 3\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"68\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 2 Accent 3\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"69\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 3 Accent 3\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"70\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Dark List Accent 3\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"71\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful Shading Accent 3\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"72\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful List Accent 3\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"73\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful Grid Accent 3\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"60\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light Shading Accent 4\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"61\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light List Accent 4\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"62\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light Grid Accent 4\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"63\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Shading 1 Accent 4\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"64\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Shading 2 Accent 4\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"65\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium List 1 Accent 4\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"66\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium List 2 Accent 4\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"67\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 1 Accent 4\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"68\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 2 Accent 4\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"69\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 3 Accent 4\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"70\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Dark List Accent 4\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"71\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful Shading Accent 4\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"72\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful List Accent 4\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"73\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful Grid Accent 4\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"60\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light Shading Accent 5\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"61\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light List Accent 5\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"62\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light Grid Accent 5\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"63\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Shading 1 Accent 5\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"64\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Shading 2 Accent 5\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"65\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium List 1 Accent 5\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"66\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium List 2 Accent 5\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"67\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 1 Accent 5\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"68\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 2 Accent 5\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"69\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 3 Accent 5\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"70\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Dark List Accent 5\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"71\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful Shading Accent 5\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"72\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful List Accent 5\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"73\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful Grid Accent 5\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"60\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light Shading Accent 6\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"61\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light List Accent 6\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"62\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Light Grid Accent 6\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"63\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Shading 1 Accent 6\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"64\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Shading 2 Accent 6\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"65\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium List 1 Accent 6\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"66\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium List 2 Accent 6\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"67\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 1 Accent 6\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"68\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 2 Accent 6\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"69\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Medium Grid 3 Accent 6\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"70\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Dark List Accent 6\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"71\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful Shading Accent 6\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"72\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful List Accent 6\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"73\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" Name=\"Colorful Grid Accent 6\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"19\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" QFormat=\"true\" Name=\"Subtle Emphasis\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"21\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" QFormat=\"true\" Name=\"Intense Emphasis\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"31\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" QFormat=\"true\" Name=\"Subtle Reference\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"32\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" QFormat=\"true\" Name=\"Intense Reference\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"33\" SemiHidden=\"false\"\n"
            + "    UnhideWhenUsed=\"false\" QFormat=\"true\" Name=\"Book Title\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"37\" Name=\"Bibliography\"/>\n"
            + "   <w:LsdException Locked=\"false\" Priority=\"39\" QFormat=\"true\" Name=\"TOC Heading\"/>\n"
            + "  </w:LatentStyles>\n"
            + " </xml><![endif]--><!--[if gte mso 10]>\n"
            + " <style>\n"
            + "  /* Style Definitions */\n"
            + "  table.MsoNormalTable\n"
            + "         {mso-style-name:\"Normale Tabelle\";\n"
            + "         mso-tstyle-rowband-size:0;\n"
            + "         mso-tstyle-colband-size:0;\n"
            + "         mso-style-noshow:yes;\n"
            + "         mso-style-priority:99;\n"
            + "         mso-style-qformat:yes;\n"
            + "         mso-style-parent:\"\";\n"
            + "         mso-padding-alt:0cm 5.4pt 0cm 5.4pt;\n"
            + "         mso-para-margin-top:0cm;\n"
            + "         mso-para-margin-right:0cm;\n"
            + "         mso-para-margin-bottom:10.0pt;\n"
            + "         mso-para-margin-left:0cm;\n"
            + "         line-height:115%;\n"
            + "         mso-pagination:widow-orphan;\n"
            + "         font-size:11.0pt;\n"
            + "         font-family:\"Calibri\",\"sans-serif\";\n"
            + "         mso-ascii-font-family:Calibri;\n"
            + "         mso-ascii-theme-font:minor-latin;\n"
            + "         mso-fareast-font-family:\"Times New Roman\";\n"
            + "         mso-fareast-theme-font:minor-fareast;\n"
            + "         mso-hansi-font-family:Calibri;\n"
            + "         mso-hansi-theme-font:minor-latin;}\n"
            + " </style>\n"
            + " <![endif]--> \n";
    String clean = PublishTask.cleanupAfterWord(dirty);
    assertThat(clean)
        .isEqualTo(
            " <i>Hem dede noot</i> (&lt; Dt. \"sie dadents noit\"): De letterlijke weergave van de Dt. tekst (Mnl. „si\n"
                + " dadent node/nooit“) levert noch het gewenste rijm op \"broot\" noch een goede zin op. Mnl. \"hem dede noot\" betekent \"ze hadden er behoefte aan, ze hadden het nodig\".<span style=\"font-size:\n"
                + " 14.0pt;line-height:115%;font-family:&quot;Times New Roman&quot;,&quot;serif&quot;;mso-fareast-font-family:\n"
                + " &quot;Times New Roman&quot;;mso-ansi-language:DE;mso-fareast-language:DE;mso-bidi-language:\n"
                + " AR-SA\"><i style=\"mso-bidi-font-style:\n"
                + " normal\"> </i></span> \n");
    String simpleHTML = XmlUtil.toSimpleHTML(clean);
    assertThat(simpleHTML)
        .isEqualTo(
            " <em>Hem dede noot</em> (&lt; Dt. \"sie dadents noit\"): De letterlijke weergave van de Dt. tekst (Mnl. „si\n"
                + " dadent node/nooit“) levert noch het gewenste rijm op \"broot\" noch een goede zin op. Mnl. \"hem dede noot\" betekent \"ze hadden er behoefte aan, ze hadden het nodig\".  \n");
  }
}
