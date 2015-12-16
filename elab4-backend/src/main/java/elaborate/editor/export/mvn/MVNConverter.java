package elaborate.editor.export.mvn;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.io.FileUtils;

import elaborate.editor.export.mvn.MVNConversionData.AnnotationData;
import elaborate.editor.export.mvn.MVNConversionData.EntryData;
import elaborate.editor.model.orm.Facsimile;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.Transcription;
import elaborate.util.HibernateUtil;
import elaborate.util.XmlUtil;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.tei.Document;
import nl.knaw.huygens.tei.XmlContext;

public class MVNConverter {
  private final Project project;
  private final MVNConversionData data;

  public MVNConverter(Project project, MVNConversionData data) {
    this.project = project;
    //    data = getConversionData(project.getId());
    this.data = data;
  }

  // fase 1: collect MVNFolium with raw transcription 
  // order by 'order' or entryname
  // fail when 1, but not all entries have order
  // fail when entrynames are not unique
  // select id, name from project_entries where project_id=$projectId
  // select 

  public MVNConversionResult convert() {
    MVNConversionResult result = new MVNConversionResult(project);
    StringBuilder editionTextBuilder = new StringBuilder();
    for (MVNConversionData.EntryData entryData : data.getEntryDataList()) {
      String pageId = result.getSigle() + "-pb-" + entryData.name;
      editionTextBuilder//
          .append("<pb n=\"")//
          .append(entryData.name)//
          .append("\" xml:id=\"")//
          .append(pageId)//
          .append("\" facs=\"")//
          .append(entryData.facs)//
          .append("\" _entryId=\"")//
          .append(entryData.id)//
          .append("\"/>")//
          .append(transcriptionBody(entryData));
    }

    String xml = "<body>" + editionTextBuilder.toString() + "</body>";
    try {
      FileUtils.write(new File("out/rawbody.xml"), xml);
      FileUtils.write(new File("out/smokedbody.xml"), smoke(xml));
    } catch (IOException e) {
      e.printStackTrace();
    }
    String tei = toTei(xml, result);
    result.setBody(tei);
    Log.info("tei={}", tei);
    //    for (ProjectEntry entry : project.getProjectEntries()) {
    //      MVNFolium page = new MVNFolium();
    //      String n = entry.getName();
    //      page.setN(n);
    //      page.setId(result.getSigle() + "-pb-" + n);
    //      setFacs(entry, page, result);
    //      setOrder(page, entry);
    //      setBody(page, entry, result);
    //      result.addPages(page);
    //    }
    return result;
  }

  private String smoke(String xml) {
    String smoked = xml;
    for (String annotationNoString : XmlUtil.extractAnnotationNos(xml)) {
      Integer annotationNo = Integer.valueOf(annotationNoString);
      if (data.getAnnotationIndex().containsKey(annotationNo)) {
        String type = data.getAnnotationIndex().get(annotationNo).type.replaceAll("[ \\(\\)]+", "_");
        smoked = smoked//
            .replace(originalAnnotationBegin(annotationNoString), "<" + type + ">")//
            .replace(originalAnnotationEnd(annotationNoString), "</" + type + ">");
      }
    }
    return smoked;
  }

  private static String originalAnnotationEnd(String annotationNo) {
    return "<ae id=\"" + annotationNo + "\"/>";
  }

  private static String originalAnnotationBegin(String annotationNo) {
    return "<ab id=\"" + annotationNo + "\"/>";
  }

  private String transcriptionBody(MVNConversionData.EntryData entryData) {
    return entryData.body//
        .replace("<body>", "")//
        .replace("</body>", "")//
        .replace("&nbsp;", " ")//
        .trim();
  }

  @SuppressWarnings("unchecked")
  public static MVNConversionData getConversionData(long project_id) {
    MVNConversionData conversionData = new MVNConversionData();
    EntityManager entityManager = HibernateUtil.beginTransaction();

    String transcriptionSQL = "select"//
        + "  e.id as id,"//
        + "  e.name as name,"//
        + "  m.data as entry_order,"//
        + "  t.body as transcription,"//
        + "  f.zoomable_url"//
        + " from project_entries e"//
        + "   left outer join project_entry_metadata_items m on (e.id = m.project_entry_id and m.field='order')"//
        + "   left outer join transcriptions t on (e.id = t.project_entry_id and t.text_layer='Diplomatic')"//
        + "   left outer join facsimiles f on (e.id = f.project_entry_id)"//
        + " where project_id=" + project_id//
        + " order by entry_order, name".replaceAll(" +", " ");
    Query transcriptionQuery = entityManager.createNativeQuery(transcriptionSQL);
    List<Object[]> transcriptions = transcriptionQuery.getResultList();
    for (Object[] transcription : transcriptions) {
      EntryData entryData = new MVNConversionData.EntryData();
      Integer id = (Integer) transcription[0];
      entryData.id = String.valueOf(id);
      entryData.name = (String) transcription[1];
      //      String order = (String) transcription[2];
      entryData.body = (String) transcription[3];
      entryData.facs = (String) transcription[4];
      conversionData.getEntryDataList().add(entryData);
    }

    String annotationSQL = "select"//
        + "   a.annotation_no as annotation_num,"//
        + "   at.name as annotation_type,"//
        + "   a.body as annotation_body"//
        + " from project_entries e"//
        + "   left outer join transcriptions t"//
        + "     left outer join annotations a"//
        + "       left outer join annotation_types at"//
        + "       on (at.id=a.annotation_type_id)"//
        + "     on (t.id = a.transcription_id)"//
        + "   on (e.id = t.project_entry_id and t.text_layer='Diplomatic')"//
        + " where project_id=" + project_id//
        + " order by annotation_num;".replaceAll(" +", " ");
    Query annotationQuery = entityManager.createNativeQuery(annotationSQL);
    List<Object[]> annotations = annotationQuery.getResultList();
    //    Log.info("SQL: {}", annotationSQL);
    //    Log.info("{} results:", annotations.size());
    for (Object[] annotation : annotations) {
      AnnotationData annotationData = new MVNConversionData.AnnotationData();
      Integer annotationNum = (Integer) annotation[0];
      annotationData.type = (String) annotation[1];
      annotationData.body = (String) annotation[2];
      conversionData.getAnnotationIndex().put(annotationNum, annotationData);
    }

    HibernateUtil.rollbackTransaction(entityManager);
    return conversionData;
  }

  private void setFacs(ProjectEntry entry, MVNFolium page, MVNConversionResult result) {
    List<Facsimile> facsimiles = entry.getFacsimiles();
    if (facsimiles.isEmpty()) {
      result.addError("" + entry.getId(), "no facsimile");
    } else {
      if (facsimiles.size() > 1) {
        result.addError("" + entry.getId(), "multiple facsimiles, using first");
      }
      page.setFacs(facsimiles.get(0).getName());
    }
  }

  private void setBody(MVNFolium page, ProjectEntry entry, MVNConversionResult result, Map<Integer, AnnotationData> annotationIndex) {
    String body = null;
    for (Transcription transcription : entry.getTranscriptions()) {
      if ("Diplomatic".equals(transcription.getTextLayer())) {
        body = transcription.getBody().replace("&nbsp;", " ");
      } else {
        result.addError("" + entry.getId(), "incorrect textlayer found: " + transcription.getTextLayer());
      }
    }
    Log.info("body=[\n{}\n]", body);
    if (body == null) {
      result.addError("" + entry.getId(), "no Diplomatic textlayer");
    } else {
      page.setBody(toTei(body, result));
    }
    Log.info("body=[\n{}\n]", page.getBody());
  }

  String toTei(String xml, MVNConversionResult result) {
    MVNTranscriptionVisitor visitor = new MVNTranscriptionVisitor(result, data.getAnnotationIndex());
    Log.info("xml={}", xml);

    final Document document = Document.createFromXml(xml, false);
    document.accept(visitor);

    final XmlContext c = visitor.getContext();
    String rawResult = c.getResult();

    return rawResult;
  }

}
