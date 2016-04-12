package elaborate.editor.export.mvn;

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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import elaborate.editor.export.mvn.MVNConversionData.AnnotationData;
import elaborate.editor.export.mvn.MVNConversionData.EntryData;
import elaborate.editor.export.mvn.MVNValidator.ValidationResult;
import elaborate.editor.model.orm.Facsimile;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.Transcription;
import elaborate.editor.model.orm.TranscriptionType;
import elaborate.editor.publish.Publication.Status;
import elaborate.util.HibernateUtil;
import elaborate.util.XmlUtil;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.tei.Document;
import nl.knaw.huygens.tei.XmlContext;

public class MVNConverter {
  //  private static final boolean DEBUG = false; // for release
  private static final boolean DEBUG = true; // for testing, don't release with DEBUG=true!!!
  private final Project project;
  private final MVNConversionData data;
  private final Status status;
  private final TranscriptionHierarchyFixer transcriptionHiearchyFixer = new TranscriptionHierarchyFixer();

  public MVNConverter(final Project project, final MVNConversionData data, Status status) {
    this.project = project;
    this.data = data;
    this.status = status;
  }

  // phase 1: collect MVNFolium with raw transcription 
  // order by 'order' or entryname
  // fail when 1, but not all entries have order
  // fail when entrynames are not unique
  // select id, name from project_entries where project_id=$projectId
  // select 

  @SuppressWarnings("unchecked")
  public static MVNConversionData getConversionData(final long project_id, Status status) {
    final MVNConversionData conversionData = new MVNConversionData();
    final EntityManager entityManager = HibernateUtil.beginTransaction();

    final String transcriptionSQL = ("select"//
        + "  e.id as id,"//
        + "  e.name as name,"//
        + "  m.data as entry_order,"//
        + "  t.body as transcription,"//
        + "  f.filename"//
        + " from project_entries e"//
        + "   left outer join project_entry_metadata_items m on (e.id = m.project_entry_id and m.field='order')"//
        + "   left outer join transcriptions t on (e.id = t.project_entry_id and t.text_layer='Diplomatic')"//
        + "   left outer join facsimiles f on (e.id = f.project_entry_id)"//
        + " where project_id=" + project_id//
        + " order by entry_order, name").replaceAll(" +", " ");
    final Query transcriptionQuery = entityManager.createNativeQuery(transcriptionSQL);
    status.addLogline("collecting transcription data");
    final List<Object[]> transcriptions = transcriptionQuery.getResultList();
    if (transcriptions.isEmpty()) {
      status.addError("no transcriptions found");

    } else {
      for (final Object[] transcriptionData : transcriptions) {
        final EntryData entryData = new MVNConversionData.EntryData();
        final Integer id = (Integer) transcriptionData[0];
        entryData.id = String.valueOf(id);
        entryData.name = (String) transcriptionData[1];
        entryData.body = (String) transcriptionData[3];
        entryData.facs = (String) transcriptionData[4];
        conversionData.getEntryDataList().add(entryData);
      }

      final String annotationSQL = ("select"//
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
          + " order by annotation_num;").replaceAll(" +", " ");
      final Query annotationQuery = entityManager.createNativeQuery(annotationSQL);
      status.addLogline("collecting annotation data");
      final List<Object[]> annotations = annotationQuery.getResultList();
      //    Log.info("SQL: {}", annotationSQL);
      //    Log.info("{} results:", annotations.size());
      for (final Object[] annotation : annotations) {
        final AnnotationData annotationData = new MVNConversionData.AnnotationData();
        final Integer annotationNum = (Integer) annotation[0];
        annotationData.type = (String) annotation[1];
        annotationData.body = (String) annotation[2];
        conversionData.getAnnotationIndex().put(annotationNum, annotationData);
      }
    }

    HibernateUtil.rollbackTransaction(entityManager);
    return conversionData;
  }

  public MVNConversionResult convert() {
    final MVNConversionResult result = new MVNConversionResult(project, status);
    if (!onlyTextLayerIsDiplomatic()) {
      result.addError("", "MVN projecten mogen alleen een Diplomatic textlayer hebben. Dit project heeft textlayer(s): " + Joiner.on(", ").join(project.getTextLayers()));
      return result;
    }
    final StringBuilder editionTextBuilder = new StringBuilder();
    status.addLogline("joining transcriptions");
    for (final MVNConversionData.EntryData entryData : data.getEntryDataList()) {
      final String pageId = result.getSigle() + "-pb-" + entryData.name;
      String transcriptionBody = transcriptionBody(entryData);
      validateTranscriptionContainsNoEmptyLines(transcriptionBody, result, entryData.id);
      editionTextBuilder//
          .append("\n<entry n=\"").append(entryData.name).append("\" xml:id=\"").append(pageId).append("\" facs=\"").append(entryData.facs).append("\" _entryId=\"").append(entryData.id).append("\">\n")//
          .append(transcriptionBody)//
          .append("</entry>");
    }

    final String xml = editionTextBuilder.append("</body>").toString();
    //    final String repairedXml = repairAnnotationHierarchy(xml);
    //    final String cooked = replaceAnnotationMilestones(repairedXml);
    //    validateTextNums(cooked, result);
    //    if (DEBUG) {
    //      outputFiles(xml, cooked);
    //    }
    final String tei = toTei(xml, result);
    result.setBody(tei);
    Log.info("tei={}", tei);
    String fullTEI = result.getTEI();
    ValidationResult validateTEI = MVNValidator.validateTEI(fullTEI);
    if (!validateTEI.isValid()) {
      result.addError("",
          "Gegenereerde TEI is niet valide:\n"//
              + "<blockquote>" + validateTEI.getMessage() + "</blockquote>\n"//
              + " TEI:\n<pre>" + StringEscapeUtils.escapeHtml(fullTEI) + "</pre>"//
      );
    }
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

  static void validateTranscriptionContainsNoEmptyLines(String transcriptionBody, MVNConversionResult result, String entryId) {
    if (transcriptionBody.matches(".*\n\\s*\n.*")) {
      result.addError(entryId, "Lege regels mogen niet voorkomen.");
    }
  }

  boolean onlyTextLayerIsDiplomatic() {
    String[] textLayers = project.getTextLayers();
    return textLayers.length == 1 && TranscriptionType.DIPLOMATIC.equals(textLayers[0]);
  }

  private void validateTextNums(final String cooked, final MVNConversionResult result) {
    final Stack<String> textNumStack = new Stack<String>();
    final List<String> openTextNums = Lists.newArrayList();
    final List<String> closeTextNums = Lists.newArrayList();
    final Matcher matcher = Pattern.compile("mvn:tekst([be][^ >]+) body=\"([^\"]+)\"").matcher(cooked);
    boolean lastTagWasBegin = false;
    while (matcher.find()) {
      final String beginOrEinde = matcher.group(1);
      final String textnum = matcher.group(2).trim().replaceFirst(";.*$", "");
      if ("begin".equals(beginOrEinde)) {
        lastTagWasBegin = true;
        textNumStack.push(textnum);
        openTextNums.add(textnum);

      } else if ("einde".equals(beginOrEinde)) {
        final String peek = textNumStack.peek();
        if (textnum.equals(peek)) {
          if (lastTagWasBegin) {
            data.getDeepestTextNums().add(textnum);
          }
          textNumStack.pop();
        } else {
          //          result.addError("", "mvn:teksteinde : tekstNum '" + textnum + "' gevonden waar '" + peek + "' verwacht was.");
        }
        lastTagWasBegin = false;
        closeTextNums.add(textnum);

      } else {
        throw new RuntimeException("unexpected type mvn:tekst" + beginOrEinde);
      }
    }

    List<String> openedButNotClosed = Lists.newArrayList(openTextNums);
    openedButNotClosed.removeAll(closeTextNums);
    for (String tekstNum : openedButNotClosed) {
      result.addError("", "mvn:teksteinde met tekstNum '" + tekstNum + "' ontbreekt. ");
    }
    //    List<String> closedButNotOpened = Lists.newArrayList(closeTextNums);
    //    closedButNotOpened.removeAll(openTextNums);
    //    for (String tekstNum : closedButNotOpened) {
    //      result.addError("", "mvn:teksbegin met tekstNum '" + tekstNum + "' ontbreekt. ");
    //    }

  }

  private void outputFiles(final String xml, final String cooked) {
    try {
      FileUtils.write(new File("out/rawbody.xml"), xml);
      FileUtils.write(new File("out/cookedbody.xml"), cooked);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  String repairAnnotationHierarchy(String xml) {
    AnnotationHierarchyRepairingVisitor visitor = new AnnotationHierarchyRepairingVisitor();
    final Document document = Document.createFromXml(xml, false);
    document.accept(visitor);
    return visitor.getRepairedXml();
  }

  private String replaceAnnotationMilestones(final String xml) {
    String cooked = xml;
    for (final String annotationNoString : XmlUtil.extractAnnotationNos(xml)) {
      final Integer annotationNo = Integer.valueOf(annotationNoString);
      if (data.getAnnotationIndex().containsKey(annotationNo)) {
        final AnnotationData annotationData = data.getAnnotationIndex().get(annotationNo);
        final String type = annotationData.type.replaceAll("[ \\(\\)]+", "_").replaceFirst("_$", "");
        String attributes = "";
        if (StringUtils.isNotBlank(annotationData.body) && !"nvt".equals(annotationData.body)) {
          attributes = " body=\"" + StringEscapeUtils.escapeXml(annotationData.body) + "\"";
        }
        cooked = cooked//
            .replace(originalAnnotationBegin(annotationNoString), "<" + type + attributes + ">")//
            .replace(originalAnnotationEnd(annotationNoString), "</" + type + ">");
      }
    }
    return cooked;
  }

  private static String originalAnnotationEnd(final String annotationNo) {
    return "<ae id=\"" + annotationNo + "\"/>";
  }

  private static String originalAnnotationBegin(final String annotationNo) {
    return "<ab id=\"" + annotationNo + "\"/>";
  }

  private String transcriptionBody(final MVNConversionData.EntryData entryData) {
    String rawBody = entryData.body//
        .replace("&nbsp;", " ")//
        .trim();
    if (rawBody.contains("tali conuiuio")) {
      Log.info(rawBody);
    }
    return transcriptionHiearchyFixer.fix(rawBody)//
        //    return rawBody//
        .replace("</i><i>", "")//
        .replace("<body>", "")//
        .replace("</body>", "");
  }

  private void setFacs(final ProjectEntry entry, final MVNFolium page, final MVNConversionResult result) {
    final List<Facsimile> facsimiles = entry.getFacsimiles();
    if (facsimiles.isEmpty()) {
      result.addError("" + entry.getId(), "no facsimile");
    } else {
      if (facsimiles.size() > 1) {
        result.addError("" + entry.getId(), "multiple facsimiles, using first");
      }
      page.setFacs(facsimiles.get(0).getName());
    }
  }

  private void setBody(final MVNFolium page, final ProjectEntry entry, final MVNConversionResult result, final Map<Integer, AnnotationData> annotationIndex) {
    String body = null;
    for (final Transcription transcription : entry.getTranscriptions()) {
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

  String toTei(final String xml, final MVNConversionResult result) {
    Log.info("xml={}", xml);
    final Document document = Document.createFromXml(xml, false);
    ParseResult parseresult = new ParseResult();
    final AnnotatedTranscriptionVisitor visitor = new AnnotatedTranscriptionVisitor(parseresult);
    document.accept(visitor);

    return visitor.getContext().getResult();
  }

  String toTei0(final String xml, final MVNConversionResult result) {
    final MVNTranscriptionVisitor visitor = new MVNTranscriptionVisitor(result, data.getAnnotationIndex(), data.getDeepestTextNums());
    Log.info("xml={}", xml);

    final Document document = Document.createFromXml(xml, false);
    document.accept(visitor);

    final XmlContext c = visitor.getContext();
    final String rawResult = c.getResult();

    return rawResult//
        .replace("<b>", "")//
        .replace("</b>", "")//
        .replace("<u>", "")//
        .replace("</u>", "")//
        //        .replaceAll("<gap>.*?</gap>", "<gap/>")// according to the rng, gaps should be empty
        .replace("<l><head", "<head")//
        //        .replace("<l><hi rend=\"rubric\"><head", "<head")//
        .replace("</head></l>", "</head>")//
        //        .replace("</head></hi></l>", "</head>")//
        .replace("<l><closer", "<closer")//
        .replace("</closer></l>", "</closer>")//
        .replace("<hi rend=\"rubric\"><hi rend=\"rubric\">¶</hi></hi>", "<hi rend=\"rubric\">¶</hi>");
  }

}
