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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import com.google.common.io.Files;
import com.sun.jersey.api.client.ClientResponse;
import elaborate.editor.config.Configuration;
import elaborate.editor.export.mvn.MVNClient;
import elaborate.editor.export.mvn.MVNConversionData;
import elaborate.editor.export.mvn.MVNConversionResult;
import elaborate.editor.export.mvn.MVNConverter;
import elaborate.editor.model.ProjectMetadataFields;
import elaborate.editor.model.ProjectTypes;
import elaborate.editor.model.orm.*;
import elaborate.editor.model.orm.service.AnnotationService;
import elaborate.editor.model.orm.service.ProjectService;
import elaborate.editor.model.orm.service.ProjectService.AnnotationData;
import elaborate.editor.resources.orm.wrappers.TranscriptionWrapper;
import elaborate.editor.solr.ElaborateSolrIndexer;
import elaborate.freemarker.FreeMarker;
import elaborate.util.HibernateUtil;
import elaborate.util.StringUtil;
import elaborate.util.XmlUtil;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.facetedsearch.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class PublishTask implements Runnable {
  private static final String MVN_BASE_URL = "http://test.mvn.huygens.knaw.nl/";
  private static final SimpleDateFormat SIMPLE_DATE_FORMAT =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static final String THUMBNAIL_URL =
      "https://tomcat.tiler01.huygens.knaw.nl/adore-djatoka/resolver?url_ver=Z39.88-2004&svc_id=info:lanl-repo/svc/getRegion&svc_val_fmt=info:ofi/fmt:kev:mtx:jpeg2000&svc.format=image/jpeg&svc.level=1&rft_id=";
  private static final String ZOOM_URL =
      "https://tomcat.tiler01.huygens.knaw.nl/adore-djatoka/viewer2.1.html?rft_id=";
  private static final String PUBLICATION_URL = "publicationURL";
  private static final String PUBLICATION_TOMCAT_WEBAPPDIR = "publication.tomcat.webappdir";
  private static final String ANNOTATION_INDEX_JSON = "annotation_index.json";

  private final Publication.Status status;
  private final Publication.Settings settings;
  private final Long projectId;
  private final AnnotationService annotationService = AnnotationService.instance();
  private File rootDir;
  private File distDir;
  private File jsonDir;
  private SolrServerWrapper solrServer;

  final Configuration config = Configuration.instance();
  private EntityManager entityManager;
  //	private Map<Integer, String> publishableAnnotationTypes;
  //	private Map<Integer, Map<String, String>> publishableAnnotationParameters;
  private Map<Integer, AnnotationData> annotationDataMap;
  private final MVNClient mvnClient =
      new MVNClient(config.getSetting(Configuration.MVN_SERVER_URL));
  private final String baseURL = config.getSetting(Configuration.WORK_URL);

  public PublishTask(Publication.Settings settings) {
    this.settings = settings;
    this.projectId = settings.getProjectId();
    this.status = new Publication.Status(projectId);
  }

  @Override
  public void run() {
    // TODO: refactor entityManager/projectService interaction
    status.addLogline("started");

    entityManager = HibernateUtil.getEntityManager();
    final Project project = entityManager.find(Project.class, projectId);
    final ProjectService ps = ProjectService.instance();
    boolean projectIsMVN =
        ProjectTypes.MVN.equals(project.getMetadataMap().get(ProjectMetadataFields.TYPE));
    String url = "";
    try {
      url = projectIsMVN ? createMVNDraft(project) : createRegularDraft(project, ps);
      status.setUrl(url);

    } catch (Exception e) {
      status.addError(e.getMessage());
    }
    status.addLogline("finished");
    status.setDone();

    entityManager = HibernateUtil.getEntityManager();
    ps.setEntityManager(entityManager);
    ps.setMetadata(projectId, PUBLICATION_URL, url, settings.getUser());
  }

  private String createMVNDraft(Project project) {
    MVNConversionData data = MVNConverter.getConversionData(project.getId(), status);
    MVNConverter mvnConverter = new MVNConverter(project, data, status, baseURL);
    MVNConversionResult report = mvnConverter.convert();
    if (report.isOK()) {
      String tei = report.getTEI();
      status.addLogline("publishing TEI to MVN server");
      ClientResponse response = mvnClient.putTEI(project.getName(), tei);
      Log.info("responseStatus = {}", response.getClientResponseStatus());
      if (!response.getClientResponseStatus().equals(ClientResponse.Status.CREATED)) {
        String error =
            MessageFormat.format( //
                "MVN server returned error: <br/>{0}<br/>On generated TEI: <hr/><pre>{1}</pre><hr/>", //
                response
                    .getEntity(String.class) //
                    .replaceAll("\n", "<br/>"), //
                tei.replace("&", "&amp;") //
                    .replace("<", "&lt;") //
                    .replace(">", "&gt;") //
                    .replaceAll("\n", "<br/>") //
                );
        status.addError(error);
      }
    }
    return MVN_BASE_URL + project.getName().toUpperCase();
  }

  private String createRegularDraft(final Project project, final ProjectService ps) {
    prepareDirectories();

    status.addLogline("setting up new solr index");
    prepareSolr();

    // these 2 use transaction explicitly
    final List<String> projectEntryMetadataFields = getProjectEntryMetadataFields(ps);
    annotationDataMap =
        filterOnPublishableAnnotationTypes(
            ps.getAnnotationDataForProject(projectId), settings.getAnnotationTypeIds());

    // the rest don't (see TODO)
    ps.setEntityManager(entityManager);
    final Map<String, String> typographicalAnnotationMap = getTypographicalAnnotationMap(project);
    final Collection<String> multivaluedFacetNames = getFacetsToSplit(project);
    final List<ProjectEntry> projectEntriesInOrder = ps.getProjectEntriesInOrder(projectId);

    int entryNum = 1;
    final List<EntryData> entryData = Lists.newArrayList();
    final Map<Long, List<String>> thumbnails = Maps.newHashMap();
    final Multimap<String, AnnotationIndexData> annotationIndex = ArrayListMultimap.create();
    final String value =
        project.getMetadataMap().get(ProjectMetadataFields.MULTIVALUED_METADATA_FIELDS);
    final String[] multivaluedMetadataFields = value != null ? value.split(";") : new String[] {};
    for (final ProjectEntry projectEntry : projectEntriesInOrder) {
      if (projectEntry.isPublishable()) {
        status.addLogline(
            MessageFormat.format(
                "exporting entry {0,number,#}: \"{1}\"", entryNum, projectEntry.getName()));
        final ExportedEntryData eed =
            exportEntryData(
                projectEntry, entryNum++, projectEntryMetadataFields, typographicalAnnotationMap);
        final long id = projectEntry.getId();
        final Multimap<String, String> multivaluedFacetValues =
            getMultivaluedFacetValues(multivaluedMetadataFields, projectEntry);
        final String datafile = id + ".json";
        entryData.add(
            new EntryData(
                id,
                projectEntry.getName(),
                projectEntry.getShortName(),
                datafile,
                multivaluedFacetValues));
        thumbnails.put(id, eed.thumbnailUrls);
        annotationIndex.putAll(eed.annotationDataMap);
        indexEntry(projectEntry, multivaluedFacetNames);
      }
    }
    commitAndCloseSolr();
    exportProjectData(entryData, thumbnails, annotationIndex);

    final String basename = getBasename(project);
    final String url = getBaseURL(project.getName());
    final List<String> facetableProjectEntryMetadataFields =
        getFacetableProjectEntryMetadataFields(ps);
    exportSearchConfig(project, facetableProjectEntryMetadataFields, multivaluedFacetNames, url);
    exportBuildDate();
    exportLoggingProperties(basename);
    // FIXME: fix, error bij de ystroom
    if (entityManager.isOpen()) {
      entityManager.close();
    }

    status.addLogline("generating war file " + basename + ".war");
    final File war = new WarMaker(basename, distDir, rootDir).make();
    status.addLogline("deploying war to " + url);
    deploy(war);
    status.addLogline("cleaning up temporary directories");
    clearDirectories();
    return url;
  }

  private void exportLoggingProperties(String basename) {
    Map<String, String> map = ImmutableMap.of("prefix", basename);
    File destFile = new File(distDir, "WEB-INF/classes/logging.properties");
    FreeMarker.templateToFile("logging.properties.ftl", destFile, map, getClass());
  }

  static Multimap<String, String> getMultivaluedFacetValues(
      String[] multivaluedFacetNames, ProjectEntry projectEntry) {
    Multimap<String, String> multivaluedFacetValues = ArrayListMultimap.create();
    for (String multivaluedFacet : multivaluedFacetNames) {
      String multivalue = projectEntry.getMetadataValue(multivaluedFacet);
      if (StringUtils.isNotEmpty(multivalue)) {
        multivaluedFacetValues.putAll(multivaluedFacet, StringUtil.getValues(multivalue));
      }
    }
    return multivaluedFacetValues;
  }

  private Map<Integer, AnnotationData> filterOnPublishableAnnotationTypes(
      Map<Integer, AnnotationData> annotationDataMap, List<Long> publishableAnnotationTypeIds) {
    if (publishableAnnotationTypeIds == null || publishableAnnotationTypeIds.isEmpty()) {
      // default action: use all annotations
      return annotationDataMap;
    }

    // publishableAnnotationTypeIds set in project_metadata_items
    Map<Integer, AnnotationData> filteredAnnotationDataMap = Maps.newHashMap();
    for (Entry<Integer, AnnotationData> entry : annotationDataMap.entrySet()) {
      Integer annotationId = entry.getKey();
      AnnotationData annotationData = entry.getValue();
      if (publishableAnnotationTypeIds.contains(annotationData.getTypeId())) {
        filteredAnnotationDataMap.put(annotationId, annotationData);
      }
    }
    return filteredAnnotationDataMap;
  }

  Collection<String> getFacetsToSplit(Project project) {
    Collection<String> facetsToSplit = Sets.newHashSet();
    String value = project.getMetadataMap().get(ProjectMetadataFields.MULTIVALUED_METADATA_FIELDS);
    if (StringUtils.isNotBlank(value)) {
      for (String fieldName : Splitter.on(";").split(value)) {
        facetsToSplit.add(SolrUtils.facetName(fieldName));
      }
    }
    return facetsToSplit;
  }

  public long getProjectId() {
    return projectId;
  }

  Map<String, String> getTypographicalAnnotationMap(Project project) {
    Map<String, String> typographicalAnnotationMap = Maps.newHashMap();
    Map<String, String> metadataMap = project.getMetadataMap();
    addMapping(
        typographicalAnnotationMap,
        metadataMap,
        "b",
        ProjectMetadataFields.ANNOTATIONTYPE_BOLD_NAME,
        ProjectMetadataFields.ANNOTATIONTYPE_BOLD_DESCRIPTION);
    addMapping(
        typographicalAnnotationMap,
        metadataMap,
        "i",
        ProjectMetadataFields.ANNOTATIONTYPE_ITALIC_NAME,
        ProjectMetadataFields.ANNOTATIONTYPE_ITALIC_DESCRIPTION);
    addMapping(
        typographicalAnnotationMap,
        metadataMap,
        "u",
        ProjectMetadataFields.ANNOTATIONTYPE_UNDERLINE_NAME,
        ProjectMetadataFields.ANNOTATIONTYPE_UNDERLINE_DESCRIPTION);
    addMapping(
        typographicalAnnotationMap,
        metadataMap,
        "strike",
        ProjectMetadataFields.ANNOTATIONTYPE_STRIKE_NAME,
        ProjectMetadataFields.ANNOTATIONTYPE_STRIKE_DESCRIPTION);
    return typographicalAnnotationMap;
  }

  private void addMapping(
      Map<String, String> typographicalAnnotationMap,
      Map<String, String> metadataMap,
      String key,
      String nameKey,
      String descriptionKey) {
    if (metadataMap.containsKey(nameKey)) {
      String name = metadataMap.get(nameKey);
      String description = metadataMap.get(descriptionKey);
      String annotationTypeLabel;
      if (StringUtils.isNotBlank(description)) {
        annotationTypeLabel = description + " [" + name + "]";
      } else {
        annotationTypeLabel = name;
      }
      typographicalAnnotationMap.put(key, annotationTypeLabel);
    }
  }

  String getBaseURL(String basename) {
    return config.getSetting("publication.draft.url").replace("#", basename);
  }

  private String getBasename(Project project) {
    return "elab4-" + project.getName();
  }

  private void exportSearchConfig(
      Project project,
      List<String> facetFields,
      Collection<String> multivaluedFacetNames,
      String baseurl) {
    File json = new File(distDir, "WEB-INF/classes/config.json");
    exportJson(
        json, new SearchConfig(project, facetFields, multivaluedFacetNames).setBaseURL(baseurl));
  }

  private void exportBuildDate() {
    File properties = new File(distDir, "WEB-INF/classes/about.properties");
    try {
      FileUtils.write(
          properties, "publishdate=" + SIMPLE_DATE_FORMAT.format(new Date()), Charsets.UTF_8, true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private List<String> getProjectEntryMetadataFields(ProjectService ps) {
    List<String> projectEntryMetadataFields = settings.getProjectEntryMetadataFields();
    if (projectEntryMetadataFields.isEmpty()) {
      User rootUser = new User().setRoot(true);
      projectEntryMetadataFields =
          ImmutableList.copyOf(ps.getProjectEntryMetadataFields(projectId, rootUser));
    }
    return projectEntryMetadataFields;
  }

  private List<String> getFacetableProjectEntryMetadataFields(ProjectService ps) {
    List<String> facetFields = settings.getFacetFields();
    if (facetFields.isEmpty()) {
      User rootUser = new User().setRoot(true);
      facetFields = ImmutableList.copyOf(ps.getProjectEntryMetadataFields(projectId, rootUser));
    }
    return facetFields;
  }

  public Publication.Status getStatus() {
    return status;
  }

  static String toJson(Object data) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(data);
  }

  static String entryFilename(int num) {
    return MessageFormat.format("entry{0,number,#}.json", num);
  }

  Map<String, Object> getProjectData(
      Project project, List<EntryData> entries, Map<Long, List<String>> thumbnails) {
    Map<String, String> metadataMap = project.getMetadataMap();
    for (String key : ProjectMetadataFields.ANNOTATIONTYPE_FIELDS) {
      metadataMap.remove(key);
    }
    Map<String, Object> map = Maps.newHashMap();
    map.put("id", project.getId());
    map.put(
        "title",
        StringUtils.defaultIfBlank(
            metadataMap.remove(ProjectMetadataFields.PUBLICATION_TITLE), project.getTitle()));
    map.put("publicationDate", new DateTime().toString("yyyy-MM-dd HH:mm"));
    map.put("entries", entries);

    map.put(
        "levels", ImmutableList.of(project.getLevel1(), project.getLevel2(), project.getLevel3()));
    List<String> publishableTextLayers = settings.getTextLayers();
    map.put(
        "textLayers",
        publishableTextLayers.isEmpty() ? project.getTextLayers() : publishableTextLayers);

    map.put("thumbnails", thumbnails);
    // map.put("entryMetadataFields", project.getProjectEntryMetadataFieldnames());
    // map.put("baseURL", getBaseURL(getBasename(project)));
    map.put("baseURL", getBaseURL(project.getName()));
    map.put("annotationIndex", ANNOTATION_INDEX_JSON);
    map.put("multivaluedFacetIndex", calculateMultivaluedFacetIndex(entries));
    map.put("facetDefaultOrder", facetDefaultOrder(project));

    addIfNotNull(map, "textFont", metadataMap.remove(ProjectMetadataFields.TEXT_FONT));
    addIfNotNull(
        map, "entryTermSingular", metadataMap.remove(ProjectMetadataFields.ENTRYTERM_SINGULAR));
    addIfNotNull(
        map, "entryTermPlural", metadataMap.remove(ProjectMetadataFields.ENTRYTERM_PLURAL));
    map.put("metadata", metadataMap);

    // Map<String, String> settingsMap =
    // ProjectService.instance().getProjectSettings(project.getId(), project.getModifier());
    // Map<String, Object> projectSettings = Maps.newHashMap();
    // projectSettings.putAll(settingsMap);
    // projectSettings.put("levels", ImmutableList.of(project.getLevel1(), project.getLevel2(),
    // project.getLevel3()));
    //
    // List<String> publishableTextLayers = settings.getTextLayers();
    // projectSettings.put("textLayers", publishableTextLayers.isEmpty() ? project.getTextLayers() :
    // publishableTextLayers);
    //
    // map.put("settings", projectSettings);
    return map;
  }

  private Map<String, String> facetDefaultOrder(Project project) {
    final Map<String, String> facetDefaultOrder = new HashMap<String, String>();
    for (String f : settings.getFacetFields()) {
      facetDefaultOrder.put(f, "az");
    }
    return facetDefaultOrder;
  }

  private Map<String, Map<String, List<Long>>> calculateMultivaluedFacetIndex(
      List<EntryData> entries) {
    Map<String, ListMultimap<String, Long>> tmpindex = Maps.newHashMap();
    for (EntryData entryData : entries) {
      for (Entry<String, String> entry : entryData.multivaluedFacetValues.entries()) {
        String facetName = entry.getKey();
        String facetValue = entry.getValue();
        if (!tmpindex.containsKey(facetName)) {
          tmpindex.put(facetName, ArrayListMultimap.<String, Long>create());
        }
        tmpindex.get(facetName).put(facetValue, entryData.entryId);
      }
    }
    Map<String, Map<String, List<Long>>> index = Maps.newHashMap();
    for (String metadataField : tmpindex.keySet()) {
      index.put(metadataField, Multimaps.asMap(tmpindex.get(metadataField)));
    }
    return index;
  }

  private void addIfNotNull(Map<String, Object> map, String key, String value) {
    if (value != null) {
      map.put(key, value);
    }
  }

  // private Map<String, Object> getMetadata(Project project) {
  // Map<String, Object> metamap = Maps.newHashMap();
  // for (ProjectMetadataItem projectMetadataItem : project.getProjectMetadataItems()) {
  // metamap.put(projectMetadataItem.getField(), projectMetadataItem.getData());
  // }
  // return metamap;
  // }

  private static final Comparator<Facsimile> SORT_ON_NAME =
      new Comparator<Facsimile>() {
        @Override
        public int compare(Facsimile f1, Facsimile f2) {
          return f1.getName().compareTo(f2.getName());
        }
      };

  Map<String, Object> getProjectEntryData(
      ProjectEntry projectEntry, List<String> projectMetadataFields) {
    Map<String, TextlayerData> texts = getTexts(projectEntry);
    Multimap<String, AnnotationIndexData> annotationDataMap = ArrayListMultimap.create();
    for (String textLayer : projectEntry.getProject().getTextLayers()) {
      int order = 1;
      TextlayerData textlayerData = texts.get(textLayer);
      if (textlayerData != null) {
        for (AnnotationPublishData ad : textlayerData.getAnnotationData()) {
          AnnotationIndexData annotationIndexData =
              new AnnotationIndexData() //
                  .setEntryId(projectEntry.getId()) //
                  .setEntryName(projectEntry.getName()) //
                  .setN(ad.getN()) //
                  .setAnnotatedText(ad.getAnnotatedText()) //
                  .setAnnotationText(ad.getText()) //
                  .setTextLayer(textLayer) //
                  .setAnnotationOrder(order++);
          String atype = annotationTypeKey(ad.getType());
          annotationDataMap.put(atype, annotationIndexData);
        }
      }
    }

    Map<String, Object> map = Maps.newHashMap();
    map.put("name", projectEntry.getName());
    map.put("shortName", projectEntry.getShortName());
    map.put("id", projectEntry.getId());
    map.put("facsimiles", getFacsimileURLs(projectEntry));
    map.put("annotationDataMap", annotationDataMap);
    map.put("paralleltexts", texts);
    map.put("metadata", getMetadata(projectEntry, projectMetadataFields));
    return map;
  }

  String annotationTypeKey(AnnotationTypeData atd) {
    if (StringUtils.isNotBlank(atd.description)) {
      return atd.getDescription() + " [" + atd.getName() + "]";
    }
    return atd.getName();
  }

  private Map<String, TextlayerData> getTexts(ProjectEntry projectEntry) {
    Map<String, TextlayerData> map = Maps.newHashMap();
    for (Transcription transcription : projectEntry.getTranscriptions()) {
      if (projectEntry.getProject().getId() == 44) {
        // CNW kludge
        fixPageBreaks(transcription);
      }
      try {
        TextlayerData textlayerData = getTextlayerData(transcription);
        if (textlayerData.getText().length() < 20) {
          Log.warn(
              "empty {} transcription for entry {}",
              transcription.getTextLayer(),
              projectEntry.getId());
        }
        map.put(transcription.getTextLayer(), textlayerData);
      } catch (Exception e) {
        Log.error(
            "Error '{}' for transcription {}, body: '{}'",
            e.getMessage(),
            transcription.getId(),
            transcription.getBody());
        e.printStackTrace();
      }
    }
    return map;
  }

  private void fixPageBreaks(Transcription transcription) {
    EntityManager entityManager = HibernateUtil.getEntityManager();
    String body = cleanupAfterWord(transcription.getBody());
    String fixed =
        body //
            .replace("<strong>", "<b>") //
            .replace("</strong>", "</b>") //
            .replaceAll(
                "(?s)<b>([^<]*?)¶([^<]*?)¶([^<]*?)¶([^<]*?)</b>",
                "$1<b>¶</b>$2<b>¶</b>$3<b>¶</b>$4") //
            .replaceAll("(?s)<b>([^<]*?)¶([^<]*?)¶([^<]*?)</b>", "$1<b>¶</b>$2<b>¶</b>$3") //
            .replaceAll("(?s)<b>([^<]*?)¶([^<]*?)</b>", "$1<b>¶</b>$2") //
            .replaceAll("¶", "<b>¶</b>") //
            .replaceAll("<b><b>¶</b></b>", "<b>¶</b>") //
            .replaceAll("<b><b>¶</b></b>", "<b>¶</b>") //
            .replaceAll("<b><b>¶</b></b>", "<b>¶</b>");
    transcription.setBody(fixed);
    if (!fixed.equals(body)) {
      Log.info("fixed transcription {}:\nraw={}\nfix={}", transcription.getId(), fixed);
    }
    entityManager.merge(transcription);
    entityManager.close();
  }

  private TextlayerData getTextlayerData(Transcription transcription) {
    TranscriptionWrapper tw = new TranscriptionWrapper(transcription, annotationDataMap);
    return new TextlayerData() //
        .setText(cleanupAfterWord(tw.getBody())) //
        .setAnnotations(getAnnotationData(tw.annotationNumbers));
  }

  private List<AnnotationPublishData> getAnnotationData(List<Integer> annotationNumbers) {
    List<AnnotationPublishData> list = Lists.newArrayList();
    for (Integer integer : annotationNumbers) {
      Annotation annotation = annotationService.getAnnotationByAnnotationNo(integer, entityManager);
      if (annotation != null) {
        AnnotationType annotationType = annotation.getAnnotationType();
        if (settings.includeAnnotationType(annotationType)) {
          AnnotationPublishData ad2 =
              new AnnotationPublishData() //
                  .setN(annotation.getAnnotationNo()) //
                  .setText(cleanupAfterWord(annotation.getBody())) //
                  .setAnnotatedText(annotation.getAnnotatedText()) //
                  .setType(
                      getAnnotationTypeData(
                          annotationType, annotation.getAnnotationMetadataItems()));
          list.add(ad2);
        }
      }
    }
    return list;
  }

  // private List<Map<String, Object>> getAnnotationData(Transcription transcription) {
  // List<Map<String, Object>> list = Lists.newArrayList();
  // List<Annotation> annotations = transcription.getAnnotations();
  // for (Annotation annotation : annotations) {
  // AnnotationType annotationType = annotation.getAnnotationType();
  // if (settings.includeAnnotationType(annotationType)) {
  // Map<String, Object> map = Maps.newHashMap();
  // map.put("n", annotation.getAnnotationNo());
  // map.put("text", annotation.getBody());
  // map.put("type", getAnnotationTypeData(annotationType,
  // annotation.getAnnotationMetadataItems()));
  // list.add(map);
  // }
  // }
  // return list;
  // }

  private AnnotationTypeData getAnnotationTypeData(
      AnnotationType annotationType, Set<AnnotationMetadataItem> meta) {
    Map<String, Object> metadata = getMetadataMap(meta);
    return new AnnotationTypeData() //
        .setId(annotationType.getId()) //
        .setName(annotationType.getName()) //
        .setDescription(annotationType.getDescription()) //
        .setMetadata(metadata);
  }

  private Map<String, Object> getMetadataMap(Set<AnnotationMetadataItem> meta) {
    Map<String, Object> map = Maps.newHashMap();
    for (AnnotationMetadataItem annotationMetadataItem : meta) {
      map.put(
          annotationMetadataItem.getAnnotationTypeMetadataItem().getName(),
          annotationMetadataItem.getData());
    }
    return map;
  }

  private List<Metadata> getMetadata(ProjectEntry projectEntry, List<String> metadataFields) {
    Map<String, String> metamap = Maps.newHashMap();
    for (ProjectEntryMetadataItem projectEntryMetadataItem :
        projectEntry.getProjectEntryMetadataItems()) {
      String key = projectEntryMetadataItem.getField();
      String value = projectEntryMetadataItem.getData();
      metamap.put(key, value);
    }

    List<Metadata> list = Lists.newArrayListWithCapacity(metadataFields.size());
    for (String field : metadataFields) {
      list.add(new Metadata(field, metamap.get(field)));
    }
    return list;
  }

  private List<Map<String, String>> getFacsimileURLs(ProjectEntry projectEntry) {
    List<Facsimile> facsimiles = Lists.newArrayList(projectEntry.getFacsimiles());
    Collections.sort(facsimiles, SORT_ON_NAME);
    List<Map<String, String>> facsimileURLs = Lists.newArrayList();
    for (Facsimile facsimile : facsimiles) {
      Map<String, String> facsimileData = getFacsimileData(facsimile.getZoomableUrl());
      facsimileData.put("title", facsimile.getName());
      facsimileURLs.add(facsimileData);
    }
    return facsimileURLs;
  }

  private Map<String, String> getFacsimileData(String zoomableUrl) {
    Map<String, String> map = Maps.newHashMap();
    map.put("zoom", ZOOM_URL + zoomableUrl);
    map.put("thumbnail", THUMBNAIL_URL + zoomableUrl);
    return map;
  }

  private void prepareDirectories() {
    rootDir = Files.createTempDir();
    Log.info("directory={}", rootDir);
    distDir = new File(rootDir, "dist");
    URL resource = Thread.currentThread().getContextClassLoader().getResource("publication");
    try {
      File publicationResourceDir = new File(resource.toURI());
      FileUtils.copyDirectory(publicationResourceDir, distDir);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    jsonDir = new File(distDir, "data");
    jsonDir.mkdir();
  }

  private void exportJson(File jsonFile, Object data) {
    FileWriterWithEncoding fw = null;
    try {
      fw = new FileWriterWithEncoding(jsonFile, Charsets.UTF_8);
      fw.write(toJson(data));
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (fw != null) {
        try {
          fw.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void exportProjectData(
      List<EntryData> entryData,
      Map<Long, List<String>> thumbnails,
      Multimap<String, AnnotationIndexData> annotationIndex) {
    File json = new File(jsonDir, "config.json");
    EntityManager entityManager = HibernateUtil.getEntityManager();
    Project project = entityManager.find(Project.class, projectId);
    Map<String, Object> projectData = getProjectData(project, entryData, thumbnails);
    List<String> projectEntryMetadataFields = settings.getProjectEntryMetadataFields();
    projectData.put("entryMetadataFields", projectEntryMetadataFields);
    projectData.put("generated", new Date().getTime());
    cnwKludge(project, projectData, projectEntryMetadataFields);

    entityManager.close();
    exportJson(json, projectData);

    json = new File(jsonDir, ANNOTATION_INDEX_JSON);
    exportJson(json, annotationIndex.asMap());

    // String indexfilename = "index-" + settings.getProjectType() + ".html.ftl";
    String indexfilename = "index.html.ftl";
    File destIndex = new File(distDir, "index.html");
    String projectType = settings.getProjectType();
    Configuration configuration = Configuration.instance();
    String version = configuration.getSetting("publication.version." + projectType);
    String cdnBaseURL = configuration.getSetting("publication.cdn");
    Map<String, Object> fmRootMap =
        ImmutableMap.of( //
            "BASE_URL", projectData.get("baseURL"), //
            "TYPE", projectType, //
            "ELABORATE_CDN", cdnBaseURL, //
            "VERSION", version //
            );
    FreeMarker.templateToFile(indexfilename, destIndex, fmRootMap, getClass());
  }

  private void cnwKludge(
      Project project, Map<String, Object> projectData, List<String> projectEntryMetadataFields) {
    if (project.getId() == 44) {
      List<String> fieldnames =
          Lists.newArrayListWithExpectedSize(projectEntryMetadataFields.size());
      for (String fieldTitle : projectEntryMetadataFields) {
        fieldnames.add(SolrUtils.facetName(fieldTitle));
      }
      projectData.put("entryMetadataFields", fieldnames);

      List<String> levelTitles = (List<String>) projectData.get("levels");
      List<String> levelFieldNames = Lists.newArrayListWithExpectedSize(levelTitles.size());
      for (String levelTitle : levelTitles) {
        levelFieldNames.add(SolrUtils.facetName(levelTitle));
      }
      projectData.put("levels", levelFieldNames);

      projectData.put(
          "personMetadataFields",
          ImmutableList.of( //
              "dynamic_s_koppelnaam",
              "dynamic_s_altname",
              "dynamic_s_gender", //
              "dynamic_i_birthyear",
              "dynamic_i_deathyear",
              "dynamic_s_networkdomain", //
              "dynamic_s_characteristic",
              "dynamic_s_subdomain",
              "dynamic_s_domain",
              "dynamic_s_combineddomain", //
              "dynamic_s_periodical",
              "dynamic_s_membership" //
              ));
      projectData.put(
          "personLevels",
          ImmutableList.of( //
              "dynamic_sort_name",
              "dynamic_k_birthDate",
              "dynamic_k_deathDate",
              "dynamic_sort_networkdomain",
              "dynamic_sort_gender" //
              ));
    }
  }

  private ExportedEntryData exportEntryData(
      ProjectEntry projectEntry,
      int entryNum,
      List<String> projectEntryMetadataFields,
      Map<String, String> typographicalAnnotationMap) {
    // String entryFilename = entryFilename(entryNum);
    String entryFilename = projectEntry.getId() + ".json";
    File json = new File(jsonDir, entryFilename);
    EntityManager entityManager = HibernateUtil.getEntityManager();
    entityManager.merge(projectEntry);
    Map<String, Object> entryData = getProjectEntryData(projectEntry, projectEntryMetadataFields);
    Multimap<String, AnnotationIndexData> annotationDataMap =
        (Multimap<String, AnnotationIndexData>) entryData.remove("annotationDataMap");
    entityManager.close();
    exportJson(json, entryData);

    List<String> thumbnailUrls = Lists.newArrayList();
    for (Map<String, String> map : (List<Map<String, String>>) entryData.get("facsimiles")) {
      thumbnailUrls.add(map.get("thumbnail"));
    }

    ExportedEntryData exportedEntryData = new ExportedEntryData();
    exportedEntryData.thumbnailUrls = thumbnailUrls;
    exportedEntryData.annotationDataMap = annotationDataMap;
    return exportedEntryData;
  }

  private void deploy(File war) {
    File destDir = new File(config.getSetting(PUBLICATION_TOMCAT_WEBAPPDIR));
    try {
      FileUtils.copyFileToDirectory(war, destDir);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void clearDirectories() {
    FileUtils.deleteQuietly(rootDir);
  }

  private void prepareSolr() {
    String solrDir = distDir + "/WEB-INF/solr";
    solrServer = new LocalSolrServer(solrDir, "entries", new ElaborateQueryComposer());
    try {
      solrServer.initialize();
    } catch (IndexException e) {
      e.printStackTrace();
    }
  }

  private void indexEntry(ProjectEntry projectEntry, Collection<String> facetsToSplit) {
    SolrInputDocument doc =
        ElaborateSolrIndexer.getSolrInputDocument(projectEntry, true, facetsToSplit);
    try {
      solrServer.add(doc);
    } catch (IndexException e) {
      e.printStackTrace();
    }
  }

  private void commitAndCloseSolr() {
    try {
      solrServer.shutdown();
    } catch (IndexException e) {
      e.printStackTrace();
    }
  }

  static class ExportedEntryData {
    public List<String> thumbnailUrls;
    public Multimap<String, AnnotationIndexData> annotationDataMap;
  }

  static class AnnotationIndexData {
    private long entryId = 0L;
    private String textLayer = "";
    private String annotatedText = "";
    private String annotationText = "";
    private int annotationOrder = 0;
    private int n;
    private String entryName;

    public String getAnnotationText() {
      return annotationText;
    }

    public AnnotationIndexData setEntryName(String name) {
      this.entryName = name;
      return this;
    }

    public AnnotationIndexData setN(int n) {
      this.n = n;
      return this;
    }

    public int getN() {
      return n;
    }

    public AnnotationIndexData setAnnotationText(String annotationText) {
      this.annotationText = annotationText;
      return this;
    }

    public long getEntryId() {
      return entryId;
    }

    public AnnotationIndexData setEntryId(long entryId) {
      this.entryId = entryId;
      return this;
    }

    public String getTextLayer() {
      return textLayer;
    }

    public AnnotationIndexData setTextLayer(String textLayer) {
      this.textLayer = textLayer;
      return this;
    }

    public String getAnnotatedText() {
      return annotatedText;
    }

    public AnnotationIndexData setAnnotatedText(String annotatedText) {
      this.annotatedText = annotatedText;
      return this;
    }

    public int getAnnotationOrder() {
      return annotationOrder;
    }

    public AnnotationIndexData setAnnotationOrder(int annotationOrder) {
      this.annotationOrder = annotationOrder;
      return this;
    }

    public String getEntryName() {
      return entryName;
    }
  }

  public static class AnnotationPublishData {
    private int annotationNo = 0;
    private String body = "";
    private AnnotationTypeData annotationTypeData = null;
    private String annotatedText = "";

    public AnnotationPublishData setN(int annotationNo) {
      this.annotationNo = annotationNo;
      return this;
    }

    public int getN() {
      return annotationNo;
    }

    public AnnotationPublishData setAnnotatedText(String annotatedText) {
      this.annotatedText = XmlUtil.toPlainText(annotatedText);
      return this;
    }

    public String getAnnotatedText() {
      return annotatedText;
    }

    public AnnotationPublishData setText(String body) {
      this.body = XmlUtil.toSimpleHTML(body).trim();
      return this;
    }

    public String getText() {
      return body;
    }

    public AnnotationPublishData setType(AnnotationTypeData annotationTypeData) {
      this.annotationTypeData = annotationTypeData;
      return this;
    }

    public AnnotationTypeData getType() {
      return annotationTypeData;
    }
  }

  public static class AnnotationTypeData {
    private long id = 0;
    private String name = "";
    private String description = "";
    private Map<String, Object> metadata = Maps.newHashMap();

    public long getId() {
      return id;
    }

    public AnnotationTypeData setId(long l) {
      this.id = l;
      return this;
    }

    public AnnotationTypeData setName(String name) {
      this.name = name;
      return this;
    }

    public String getName() {
      return name;
    }

    public AnnotationTypeData setDescription(String description) {
      this.description = description;
      return this;
    }

    public String getDescription() {
      return description;
    }

    public AnnotationTypeData setMetadata(Map<String, Object> metadata) {
      this.metadata = metadata;
      return this;
    }

    public Map<String, Object> getMetadata() {
      return metadata;
    }
  }

  public static class Metadata {
    public String field = "";
    public String value = "";

    public Metadata(String _field, String _value) {
      field = _field;
      value = StringUtils.defaultIfBlank(_value, "");
    }
  }

  public static class MultivaluedMetadata {
    public String field = "";
    public List<String> value = Lists.newArrayList();

    public MultivaluedMetadata(String _field, String _value) {
      field = _field;
      value = Splitter.on(" | ").omitEmptyStrings().trimResults().splitToList(_value);
    }
  }

  public static class EntryData {
    @JsonIgnore public final Long entryId;
    @JsonIgnore public final Multimap<String, String> multivaluedFacetValues;
    public final String datafile;
    public final String name;
    public final String shortName;

    public EntryData(
        Long entryId,
        String _name,
        String _shortName,
        String _datafile,
        Multimap<String, String> _multivaluedFacetValues) {
      this.entryId = entryId;
      this.name = _name;
      this.shortName = _shortName;
      this.datafile = _datafile;
      multivaluedFacetValues = _multivaluedFacetValues;
    }
  }

  public static class TextlayerData {
    String text = "";
    List<AnnotationPublishData> annotations = Lists.newArrayList();

    public String getText() {
      return text;
    }

    public TextlayerData setText(String text) {
      this.text = text;
      return this;
    }

    public List<AnnotationPublishData> getAnnotationData() {
      return annotations;
    }

    public TextlayerData setAnnotations(List<AnnotationPublishData> annotations) {
      this.annotations = annotations;
      return this;
    }
  }

  static String cleanupAfterWord(String dirty) {
    return dirty.replaceAll("(?s)<!--.*?-->", "");
  }
}
