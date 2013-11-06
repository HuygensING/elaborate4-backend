package elaborate.editor.publish;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import nl.knaw.huygens.LoggableObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;

import elaborate.editor.config.Configuration;
import elaborate.editor.model.ProjectMetadataFields;
import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.AnnotationMetadataItem;
import elaborate.editor.model.orm.AnnotationType;
import elaborate.editor.model.orm.Facsimile;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.ProjectEntryMetadataItem;
import elaborate.editor.model.orm.ProjectMetadataItem;
import elaborate.editor.model.orm.Transcription;
import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.service.AnnotationService;
import elaborate.editor.model.orm.service.ProjectService;
import elaborate.editor.resources.orm.wrappers.TranscriptionWrapper;
import elaborate.editor.solr.IndexException;
import elaborate.editor.solr.LocalSolrServer;
import elaborate.editor.solr.SolrIndexer;
import elaborate.editor.solr.SolrServerWrapper;
import elaborate.freemarker.FreeMarker;
import elaborate.util.HibernateUtil;
import elaborate.util.XmlUtil;

public class PublishTask extends LoggableObject implements Runnable {
  private static final String ANNOTATION_INDEX_JSON = "annotation_index.json";
  private static final String TOMCAT_WEBAPPS_DIR = "C:/devel/tomcat6/webapps/";
  //  private static final String TOMCAT_WEBAPPS_DIR = "/usr/share/tomcat6/webapps/";
  private static final String PUBLICATION_TOMCAT_URL = "http://demo7.huygens.knaw.nl/";

  private final Publication.Status status;
  private final Publication.Settings settings;
  private File rootDir;
  private File distDir;
  private File jsonDir;
  private final Long projectId;
  private SolrServerWrapper solrServer;
  private final AnnotationService annotationService = new AnnotationService();

  public PublishTask(Publication.Settings settings) {
    this.settings = settings;
    this.projectId = settings.getProjectId();
    this.status = new Publication.Status(projectId);
  }

  @Override
  public void run() {
    status.addLogline("started");
    prepareDirectories();
    status.addLogline("setting up new solr index");
    prepareSolr();
    entityManager = HibernateUtil.getEntityManager();
    ProjectService ps = new ProjectService();
    List<String> projectEntryMetadataFields = getProjectEntryMetadataFields(ps);
    ps.setEntityManager(entityManager);
    //    annotationService.setEntityManager(entityManager);
    List<ProjectEntry> projectEntriesInOrder = ps.getProjectEntriesInOrder(projectId);
    int entryNum = 1;
    List<String> entryFilenames = Lists.newArrayList();
    Map<Long, List<String>> thumbnails = Maps.newHashMap();
    Multimap<String, AnnotationIndexData> annotationIndex = ArrayListMultimap.create();
    for (ProjectEntry projectEntry : projectEntriesInOrder) {
      if (projectEntry.isPublishable()) {
        status.addLogline(MessageFormat.format("exporting entry {0,number,#}: \"{1}\"", entryNum, projectEntry.getName()));
        ExportedEntryData eed = exportEntryData(projectEntry, entryNum++, projectEntryMetadataFields);
        long id = projectEntry.getId();
        entryFilenames.add(id + ".json");
        thumbnails.put(id, eed.thumbnailUrls);
        annotationIndex.putAll(eed.annotationDataMap);
        indexEntry(projectEntry);
      }
    }
    commitAndCloseSolr();
    exportPojectData(entryFilenames, thumbnails, annotationIndex);

    Project project = entityManager.find(Project.class, projectId);
    exportSearchConfig(project, projectEntryMetadataFields);
    String basename = getBasename(project);
    entityManager.close();

    status.addLogline("generating war file " + basename + ".war");
    File war = new WarMaker(basename, distDir, rootDir).make();
    String url = getBaseURL(basename);
    status.addLogline("deploying war to " + url);
    deploy(war);
    status.setUrl(url);
    status.addLogline("cleaning up temporary directories");
    clearDirectories();
    status.addLogline("finished");
    status.setDone();
  }

  private String getBaseURL(String basename) {
    return PUBLICATION_TOMCAT_URL + basename;
  }

  private String getBasename(Project project) {
    return "elab4-" + project.getName();
  }

  private void exportSearchConfig(Project project, List<String> projectEntryMetadataFields) {
    File json = new File(distDir, "WEB-INF/classes/config.json");
    exportJson(json, new SearchConfig(project, projectEntryMetadataFields));
  }

  private List<String> getProjectEntryMetadataFields(ProjectService ps) {
    List<String> projectEntryMetadataFields = settings.getProjectEntryMetadataFields();
    if (projectEntryMetadataFields.isEmpty()) {
      User rootUser = new User().setRoot(true);
      projectEntryMetadataFields = ImmutableList.copyOf(ps.getProjectEntryMetadataFields(projectId, rootUser));
    }
    return projectEntryMetadataFields;
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

  Map<String, Object> getProjectData(Project project, List<String> entryFilenames, Map<Long, List<String>> thumbnails) {
    Map<String, String> metadataMap = project.getMetadataMap();
    Map<String, Object> map = Maps.newHashMap();
    map.put("id", project.getId());
    map.put("title", StringUtils.defaultIfBlank(metadataMap.get(ProjectMetadataFields.PUBLICATION_TITLE), project.getTitle()));
    map.put("publicationDate", new DateTime().toString("yyyy-MM-dd HH:mm"));
    map.put("entryIds", entryFilenames);
    map.put("thumbnails", thumbnails);
    map.put("entryTermSingular", metadataMap.get(ProjectMetadataFields.ENTRYTERM_SINGULAR));
    map.put("entryTermPlural", metadataMap.get(ProjectMetadataFields.ENTRYTERM_PLURAL));
    map.put("baseURL", getBaseURL(getBasename(project)));
    map.put("annotationIndex", ANNOTATION_INDEX_JSON);
    map.put("metadata", getMetadata(project));
    return map;
  }

  private Map<String, Object> getMetadata(Project project) {
    Map<String, Object> metamap = Maps.newHashMap();
    for (ProjectMetadataItem projectMetadataItem : project.getProjectMetadataItems()) {
      metamap.put(projectMetadataItem.getField(), projectMetadataItem.getData());
    }
    return metamap;
  }

  private static final Comparator<Facsimile> SORT_ON_FILENAME = new Comparator<Facsimile>() {
    @Override
    public int compare(Facsimile f1, Facsimile f2) {
      return f1.getFilename().compareTo(f2.getFilename());
    }
  };
  private EntityManager entityManager;

  Map<String, Object> getProjectEntryData(ProjectEntry projectEntry, List<String> projectMetadataFields) {
    Map<String, Object> map = Maps.newHashMap();
    map.put("name", projectEntry.getName());
    map.put("id", projectEntry.getId());
    map.put("facsimiles", getFacsimileURLs(projectEntry));
    Map<String, TextlayerData> texts = getTexts(projectEntry);
    map.put("paralleltexts", texts);
    map.put("metadata", getMetadata(projectEntry, projectMetadataFields));
    Multimap<String, AnnotationIndexData> annotationDataMap = ArrayListMultimap.create();
    String[] textLayers = projectEntry.getProject().getTextLayers();
    for (String textLayer : textLayers) {
      int order = 1;
      TextlayerData textlayerData = texts.get(textLayer);
      if (textlayerData != null) {
        for (AnnotationData ad : textlayerData.getAnnotations()) {
          AnnotationIndexData annotationIndexData = new AnnotationIndexData()//
              .setEntryId(projectEntry.getId())//
              .setN(ad.getN())//
              .setAnnotatedText(ad.getAnnotatedText())//
              .setAnnotationText(ad.getText())//
              .setTextLayer(textLayer)//
              .setAnnotationOrder(order++);
          String atype = ad.getType().getName();
          annotationDataMap.put(atype, annotationIndexData);
        }
      }
    }
    map.put("annotationDataMap", annotationDataMap);
    return map;
  }

  private Map<String, TextlayerData> getTexts(ProjectEntry projectEntry) {
    Map<String, TextlayerData> map = Maps.newHashMap();
    for (Transcription transcription : projectEntry.getTranscriptions()) {
      map.put(transcription.getTranscriptionType().getName(), getTextlayerData(transcription));
    }
    return map;
  }

  private TextlayerData getTextlayerData(Transcription transcription) {
    TranscriptionWrapper tw = new TranscriptionWrapper(transcription);
    TextlayerData textlayerData = new TextlayerData().setText(tw.body).setAnnotations(getAnnotationData(tw.annotationNumbers));
    return textlayerData;
  }

  private List<AnnotationData> getAnnotationData(List<Integer> annotationNumbers) {
    List<AnnotationData> list = Lists.newArrayList();
    for (Integer integer : annotationNumbers) {
      Annotation annotation = annotationService.getAnnotationByAnnotationNo(integer, entityManager);
      if (annotation != null) {
        AnnotationType annotationType = annotation.getAnnotationType();
        if (settings.includeAnnotationType(annotationType)) {
          AnnotationData ad2 = new AnnotationData()//
              .setN(annotation.getAnnotationNo())//
              .setText(annotation.getBody())//
              .setAnnotatedText(annotation.getAnnotatedText())//
              .setType(getAnnotationTypeData(annotationType, annotation.getAnnotationMetadataItems()));
          list.add(ad2);
        }
      }
    }
    return list;
  }

  //  private List<Map<String, Object>> getAnnotationData(Transcription transcription) {
  //    List<Map<String, Object>> list = Lists.newArrayList();
  //    List<Annotation> annotations = transcription.getAnnotations();
  //    for (Annotation annotation : annotations) {
  //      AnnotationType annotationType = annotation.getAnnotationType();
  //      if (settings.includeAnnotationType(annotationType)) {
  //        Map<String, Object> map = Maps.newHashMap();
  //        map.put("n", annotation.getAnnotationNo());
  //        map.put("text", annotation.getBody());
  //        map.put("type", getAnnotationTypeData(annotationType, annotation.getAnnotationMetadataItems()));
  //        list.add(map);
  //      }
  //    }
  //    return list;
  //  }

  private AnnotationTypeData getAnnotationTypeData(AnnotationType annotationType, Set<AnnotationMetadataItem> meta) {
    Map<String, Object> metadata = getMetadata(meta);
    AnnotationTypeData annotationTypeData = new AnnotationTypeData()//
        .setName(annotationType.getName())//
        .setDescription(annotationType.getDescription())//
        .setMetadata(metadata);
    return annotationTypeData;
  }

  private Map<String, Object> getMetadata(Set<AnnotationMetadataItem> meta) {
    Map<String, Object> map = Maps.newHashMap();
    for (AnnotationMetadataItem annotationMetadataItem : meta) {
      map.put(annotationMetadataItem.getAnnotationTypeMetadataItem().getName(), annotationMetadataItem.getData());
    }
    return map;
  }

  private List<Metadata> getMetadata(ProjectEntry projectEntry, List<String> metadataFields) {
    Map<String, String> metamap = Maps.newHashMap();
    for (ProjectEntryMetadataItem projectEntryMetadataItem : projectEntry.getProjectEntryMetadataItems()) {
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
    List<Facsimile> facsimiles = projectEntry.getFacsimiles();
    Collections.sort(facsimiles, SORT_ON_FILENAME);
    List<Map<String, String>> facsimileURLs = Lists.newArrayList();
    for (Facsimile facsimile : facsimiles) {
      facsimileURLs.add(getFacsimileData(facsimile.getZoomableUrl()));
    }
    return facsimileURLs;
  }

  private Map<String, String> getFacsimileData(String zoomableUrl) {
    Map<String, String> map = Maps.newHashMap();
    map.put("zoom", "https://tomcat.tiler01.huygens.knaw.nl/adore-huygens-viewer-2.0/viewer.html?rft_id=" + zoomableUrl);
    map.put("thumbnail", "https://tomcat.tiler01.huygens.knaw.nl/adore-djatoka/resolver?url_ver=Z39.88-2004&svc_id=info:lanl-repo/svc/getRegion&svc_val_fmt=info:ofi/fmt:kev:mtx:jpeg2000&svc.format=image/jpeg&svc.level=1&rft_id=" + zoomableUrl);
    return map;
  }

  private void prepareDirectories() {
    rootDir = Files.createTempDir();
    LOG.info("directory={}", rootDir);
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

  private void exportPojectData(List<String> entryFilenames, Map<Long, List<String>> thumbnails, Multimap<String, AnnotationIndexData> annotationIndex) {
    File json = new File(jsonDir, "config.json");
    EntityManager entityManager = HibernateUtil.getEntityManager();
    Project project = entityManager.find(Project.class, projectId);
    Map<String, Object> projectData = getProjectData(project, entryFilenames, thumbnails);
    entityManager.close();
    exportJson(json, projectData);

    json = new File(jsonDir, ANNOTATION_INDEX_JSON);
    exportJson(json, annotationIndex.asMap());

    //    String indexfilename = "index-" + settings.getProjectType() + ".html.ftl";
    String indexfilename = "index.html.ftl";
    File destIndex = new File(distDir, "index.html");
    String projectType = settings.getProjectType();
    String version = Configuration.instance().getSetting("publication.version." + projectType);
    String cdnBaseURL = Configuration.instance().getSetting("publication.cdn");
    Map<String, Object> fmRootMap = ImmutableMap.of(//
        "BASE_URL", projectData.get("baseURL"),//
        "TYPE", projectType,//
        "ELABORATE_CDN", cdnBaseURL,//
        "VERSION", version//
        );
    FreeMarker.templateToFile(indexfilename, destIndex, fmRootMap, getClass());
  }

  private ExportedEntryData exportEntryData(ProjectEntry projectEntry, int entryNum, List<String> projectEntryMetadataFields) {
    //    String entryFilename = entryFilename(entryNum);
    String entryFilename = projectEntry.getId() + ".json";
    File json = new File(jsonDir, entryFilename);
    EntityManager entityManager = HibernateUtil.getEntityManager();
    entityManager.merge(projectEntry);
    Map<String, Object> entryData = getProjectEntryData(projectEntry, projectEntryMetadataFields);
    Multimap<String, AnnotationIndexData> annotationDataMap = (Multimap<String, AnnotationIndexData>) entryData.remove("annotationDataMap");
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
    File destDir = new File(TOMCAT_WEBAPPS_DIR);
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
    solrServer = new LocalSolrServer(solrDir, "entries");
    try {
      solrServer.initialize();
    } catch (IndexException e) {
      e.printStackTrace();
    }
  }

  private void indexEntry(ProjectEntry projectEntry) {
    SolrInputDocument doc = SolrIndexer.getSolrInputDocument(projectEntry, true);
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
    private long entryId = 0l;
    private String textLayer = "";
    private String annotatedText = "";
    private String annotationText = "";
    private int annotationOrder = 0;
    private int n;

    public String getAnnotationText() {
      return annotationText;
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

  }

  public static class AnnotationData {
    private int annotationNo = 0;
    private String body = "";
    private AnnotationTypeData annotationTypeData = null;
    private String annotatedText = "";

    public AnnotationData setN(int annotationNo) {
      this.annotationNo = annotationNo;
      return this;
    }

    public int getN() {
      return annotationNo;
    }

    public String getAnnotatedText() {
      return annotatedText;
    }

    public AnnotationData setAnnotatedText(String annotatedText) {
      this.annotatedText = XmlUtil.removeXMLtags(annotatedText).trim();
      return this;
    }

    public AnnotationData setText(String body) {
      this.body = XmlUtil.removeXMLtags(body.replaceAll("<span class=\"annotationStub\">.*?</span>", "")).trim();
      return this;
    }

    public String getText() {
      return body;
    }

    public AnnotationData setType(AnnotationTypeData annotationTypeData) {
      this.annotationTypeData = annotationTypeData;
      return this;
    }

    public AnnotationTypeData getType() {
      return annotationTypeData;
    }

  }

  public static class AnnotationTypeData {
    private String name = "";
    private String description = "";
    private Map<String, Object> metadata = Maps.newHashMap();

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

  public static class TextlayerData {
    String text = "";
    List<AnnotationData> annotations = Lists.newArrayList();

    public String getText() {
      return text;
    }

    public TextlayerData setText(String text) {
      this.text = text;
      return this;
    }

    public List<AnnotationData> getAnnotations() {
      return annotations;
    }

    public TextlayerData setAnnotations(List<AnnotationData> annotations) {
      this.annotations = annotations;
      return this;
    }

  }
}
