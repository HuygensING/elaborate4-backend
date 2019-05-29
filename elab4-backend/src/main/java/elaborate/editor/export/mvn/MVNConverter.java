package elaborate.editor.export.mvn;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2019 Huygens ING
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

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elaborate.editor.export.mvn.MVNConversionData.AnnotationData;
import elaborate.editor.export.mvn.MVNConversionData.EntryData;
import elaborate.editor.export.mvn.MVNValidator.ValidationResult;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.TranscriptionType;
import elaborate.editor.publish.Publication.Status;
import elaborate.util.HibernateUtil;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.tei.Document;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static elaborate.util.XmlUtil.extractAnnotationNos;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import static org.apache.commons.text.StringEscapeUtils.escapeXml11;

public class MVNConverter {
  private static final boolean DEBUG = false; // for release
  //  private static final boolean DEBUG = true; // for testing, don't release with DEBUG=true!!!
  private final Project project;
  private final MVNConversionData data;
  private final Status status;
  private final TranscriptionHierarchyFixer transcriptionHiearchyFixer = new TranscriptionHierarchyFixer();
  private final String baseURL;

  public MVNConverter(final Project project, final MVNConversionData data, Status status, String baseURL) {
    this.project = project;
    this.data = data;
    this.status = status;
    this.baseURL = baseURL;
  }

  // phase 1: collect MVNFolium with raw transcription 
  // order by 'order' or entryname
  // fail when 1, but not all entries have order
  // fail when entrynames are not unique
  // fail when entrynames contain characters illegal in xml:id
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
        entryData.order = (String) transcriptionData[2];
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
    final MVNConversionResult result = new MVNConversionResult(project, status, baseURL);
    if (!onlyTextLayerIsDiplomatic()) {
      result.addError("", "MVN projecten mogen alleen een Diplomatic textlayer hebben. Dit project heeft textlayer(s): " + Joiner.on(", ").join(project.getTextLayers()));
      return result;
    }
    validateEntryOrderAndName(result);

    status.addLogline("joining transcriptions");
    final String xml = joinTranscriptions(result);

    final String repairedXml = xml.replaceAll("<ab id=\"([0-9]+)\"/><ae id=\"\\1\"/>", "");
    final String cooked = replaceAnnotationMilestones(repairedXml);
    validateTextNums(cooked, result);

    if (DEBUG) {
      outputFiles(xml, cooked);
    }

    final String tei = toTei(xml, result);
    Log.info("tei={}", tei);
    result.setBody(tei);

    String fullTEI = result.getTEI();
    result.getStatus().setTei(fullTEI);
    ValidationResult validateTEI = MVNValidator.validateTEI(fullTEI);
    if (!validateTEI.isValid()) {
      result.addError("",
          "Gegenereerde TEI voldoet niet aan TEI_MVN.rng:\n"//
              + "<blockquote>" + validateTEI.getMessage() + "</blockquote>\n"//
              + " TEI:\n<pre>" + escapeHtml4(fullTEI) + "</pre>"//
              + " DEBUG:\n<pre>" + escapeHtml4(cooked) + "</pre>"//
      );
    }
    return result;
  }

  static final String VALID_XML_ID_SUBSTRING_REGEXP = "[A-Za-z0-9\\-_:.]*";

  private void validateEntryOrderAndName(MVNConversionResult result) {
    boolean orderInUse = false;
    Map<String, String> entryOrderMap = Maps.newTreeMap();
    for (final MVNConversionData.EntryData entryData : data.getEntryDataList()) {
      if (!entryData.name.matches(VALID_XML_ID_SUBSTRING_REGEXP)) {
        result.addError(entryData.id, "Ongeldige entrynaam: " + entryData.name + ", voldoet niet aan de regexp " + VALID_XML_ID_SUBSTRING_REGEXP);
      }
      entryOrderMap.put(entryData.id, entryData.order);
      if (entryData.order != null) {
        orderInUse = true;
      }
    }
    if (orderInUse) {
      for (Entry<String, String> entry : entryOrderMap.entrySet()) {
        String entryId = entry.getKey();
        String order = entry.getValue();
        if (StringUtils.isEmpty(order)) {
          result.addError(entryId, "Ontbrekend metadataveld 'order'");
        } else if (!StringUtils.isNumeric(order)) {
          result.addError(entryId, "Ongeldige waarde voor metadataveld 'order': " + order + ", mag alleen cijfers bevatten");
        }
      }
    }
  }

  boolean onlyTextLayerIsDiplomatic() {
    String[] textLayers = project.getTextLayers();
    return textLayers.length == 1 && TranscriptionType.DIPLOMATIC.equals(textLayers[0]);
  }

  private String joinTranscriptions(final MVNConversionResult result) {
    final StringBuilder editionTextBuilder = new StringBuilder("<body>");
    List<EntryData> entryDataList = data.getEntryDataList();
    int total = entryDataList.size();
    int i = 1;
    for (final MVNConversionData.EntryData entryData : entryDataList) {
      final String pageId = result.getSigle() + "-pb-" + entryData.name;
      result.getStatus().addLogline("adding entry '" + entryData.name + "' (" + i++ + "/" + total + ")");
      String transcriptionBody = transcriptionBody(entryData);
      validateTranscriptionContainsNoEmptyLines(transcriptionBody, result, entryData.id);
      editionTextBuilder//
          .append("<entry n=\"").append(entryData.name).append("\" xml:id=\"").append(pageId).append("\" facs=\"").append(entryData.facs).append("\" _entryId=\"").append(entryData.id).append("\">")//
          .append("<lb/>")//
          .append(transcriptionBody.replaceAll("\\s*\n", "<le/><lb/>"))//
          .append("<le/>")//
          .append("</entry>");
    }

    return editionTextBuilder.append("</body>").toString().replace("<lb/><le/>", "");
  }

  private String transcriptionBody(final MVNConversionData.EntryData entryData) {
    String rawBody = entryData.body//
        .replace("&nbsp;", " ")//
        .trim();
    if (rawBody.contains("tali conuiuio")) {
      Log.info(rawBody);
    }
    return transcriptionHiearchyFixer.fix(rawBody)//
        .replace("</i><i>", "")//
        .replace("<body>", "")//
        .replace("<body/>", "")//
        .replace("</body>", "");
  }

  static void validateTranscriptionContainsNoEmptyLines(String transcriptionBody, MVNConversionResult result, String entryId) {
    if (Pattern.compile("\n\\s*\n").matcher(transcriptionBody).find()) {
      result.addError(entryId, "Lege regels mogen niet voorkomen.");
    }
  }

  private void validateTextNums(final String cooked, final MVNConversionResult result) {
    final Deque<String> textNumStack = new ArrayDeque<String>();
    final List<String> openTextNums = Lists.newArrayList();
    final List<String> closeTextNums = Lists.newArrayList();
    final Matcher matcher = Pattern.compile("<mvn:tekst([be][^ >]+) body=\"([^\"]+)\"").matcher(cooked);
    boolean lastTagWasBegin = false;
    while (matcher.find()) {
      final String beginOrEinde = matcher.group(1);
      final String textnum = matcher.group(2).trim().replaceFirst(";.*$", "");
      Log.info("{}: textNumStack: {}, textNum: {}", beginOrEinde, textNumStack, textnum);
      if ("begin".equals(beginOrEinde)) {
        lastTagWasBegin = true;
        validateTextNum(result, textnum, textNumStack, "");
        textNumStack.push(textnum);
        openTextNums.add(textnum);

      } else if ("einde".equals(beginOrEinde)) {
        if (!textNumStack.isEmpty()) {
          final String peek = textNumStack.peek();
          if (textnum.equals(peek)) {
            if (lastTagWasBegin) {
              data.getDeepestTextNums().add(textnum);
            }
            textNumStack.pop();
          } else {
            result.addError("", "mvn:teksteinde : tekstnummer '" + textnum + "' gevonden waar '" + peek + "' verwacht was.");
          }
        } else {
          result.addError("", "mvn:teksteinde : tekstnummer '" + textnum + "' heeft geen overeenkomstig mvn:tekstbegin");
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
      result.addError("", "mvn:teksteinde met tekstnummer '" + tekstNum + "' ontbreekt. ");
    }
    //    List<String> closedButNotOpened = Lists.newArrayList(closeTextNums);
    //    closedButNotOpened.removeAll(openTextNums);
    //    for (String tekstNum : closedButNotOpened) {
    //      result.addError("", "mvn:teksbegin met tekstNum '" + tekstNum + "' ontbreekt. ");
    //    }

  }

  private void validateTextNum(MVNConversionResult result, final String textNum, Deque<String> textNumStack, String entryId) {
    if (!textNum.matches("^[a-zA-Z0-9.]+$")) {
      addError(MVNAnnotationType.TEKSTBEGIN, "Ongeldig tekstnummer: '" + textNum + "' mag alleen letters, cijfers en (maximaal 3) punten bevatten.", result, entryId);

    } else if (textNum.split("\\.").length > 4) {
      addError(MVNAnnotationType.TEKSTBEGIN, "Ongeldig tekstnummer: '" + textNum + "' mag maximaal 3 punten bevatten.", result, entryId);

    } else if (!textNumStack.isEmpty() && !textNum.matches(textNumStack.peek() + "\\.[A-Za-z0-9]+$")) {
      addError(MVNAnnotationType.TEKSTBEGIN, "tekstnummer: '" + textNum + "' volgt niet op " + textNumStack.peek(), result, entryId);

    } else if (textNumStack.isEmpty() && textNum.contains(".")) {
      addError(MVNAnnotationType.TEKSTBEGIN, "tekstnummer '" + textNum + "' niet omvat in " + textNum.replaceFirst("\\..+", "") + " (en dieper)", result, entryId);
    }
  }

  private void outputFiles(final String xml, final String cooked) {
    try {
      String formatted = xml//
          .replace("<entry", "\n  <entry")//
          .replace("</entry>", "\n  </entry>\n")//
          .replace("<lb/>", "\n    <lb/>");
      FileUtils.write(new File("out/raw-formatted-body.xml"), formatted, Charsets.UTF_8);
      FileUtils.write(new File("out/cooked-body.xml"), cooked, Charsets.UTF_8);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private static void addError(MVNAnnotationType type, String error, MVNConversionResult result, String currentEntryId) {
    result.addError(currentEntryId, type.getName() + " : " + error);
  }

  String toTei(final String xml, final MVNConversionResult conversionResult) {
    Log.info("xml={}", xml);
    ParseResult parseresult = new ParseResult();
    final Document document = Document.createFromXml(xml, true);
    final AnnotatedTranscriptionVisitor visitor = new AnnotatedTranscriptionVisitor(data.getAnnotationIndex(), parseresult, conversionResult.getSigle());
    document.accept(visitor);
    parseresult.index();
    MVNTeiExporter teiExporter = new MVNTeiExporter(parseresult, conversionResult);
    return teiExporter.export();
  }

  private String replaceAnnotationMilestones(final String xml) {
    String cooked = xml;
    for (final String annotationNoString : extractAnnotationNos(xml)) {
      final Integer annotationNo = Integer.valueOf(annotationNoString);
      if (data.getAnnotationIndex().containsKey(annotationNo)) {
        final AnnotationData annotationData = data.getAnnotationIndex().get(annotationNo);
        final String type = annotationData.type.replaceAll("[ ()]+", "_").replaceFirst("_$", "");
        String attributes = "";
        if (isNotBlank(annotationData.body) && !"nvt".equals(annotationData.body)) {
          attributes = " body=\"" + escapeXml11(annotationData.body) + "\"";
        }
        cooked = cooked//
            .replace(originalAnnotationBegin(annotationNoString), "<" + type + attributes + ">")//
            .replace(originalAnnotationEnd(annotationNoString), "</" + type + ">");
      }
    }
    return cooked.replace("<entry", "\n  <entry")//
        .replace("</entry>", "\n  </entry>\n")//
        .replace("<lb/>", "\n    <lb/>");
  }

  private static String originalAnnotationBegin(final String annotationNo) {
    return "<ab id=\"" + annotationNo + "\"/>";
  }

  private static String originalAnnotationEnd(final String annotationNo) {
    return "<ae id=\"" + annotationNo + "\"/>";
  }

  String repairAnnotationHierarchy(String xml) {
    AnnotationHierarchyRepairingVisitor visitor = new AnnotationHierarchyRepairingVisitor();
    final Document document = Document.createFromXml(xml, false);
    document.accept(visitor);
    return visitor.getRepairedXml();
  }
  //  public MVNConversionResult convert0() {
  //    final MVNConversionResult result = new MVNConversionResult(project, status);
  //    if (!onlyTextLayerIsDiplomatic()) {
  //      result.addError("", "MVN projecten mogen alleen een Diplomatic textlayer hebben. Dit project heeft textlayer(s): " + Joiner.on(", ").join(project.getTextLayers()));
  //      return result;
  //    }
  //    final StringBuilder editionTextBuilder = new StringBuilder();
  //    status.addLogline("joining transcriptions");
  //    for (final MVNConversionData.EntryData entryData : data.getEntryDataList()) {
  //      final String pageId = result.getSigle() + "-pb-" + entryData.name;
  //      String transcriptionBody = transcriptionBody(entryData);
  //      validateTranscriptionContainsNoEmptyLines(transcriptionBody, result, entryData.id);
  //      editionTextBuilder//
  //          .append("\n<pb n=\"")//
  //          .append(entryData.name)//
  //          .append("\" xml:id=\"")//
  //          .append(pageId)//
  //          .append("\" facs=\"")//
  //          .append(entryData.facs)//
  //          .append("\" _entryId=\"")//
  //          .append(entryData.id)//
  //          .append("\"/>\n")//
  //          .append("<lb/>")//
  //          .append(transcriptionBody(entryData).replace("\n", "<le/>\n<lb/>"))//
  //          .append("<le/>");
  //    }
  //
  //    final String xml = "<body>" + editionTextBuilder.toString().replace("<lb/><le/>", "").replace("\n\n", "\n") + "</body>";
  //    final String repairedXml = repairAnnotationHierarchy(xml);
  //    final String cooked = replaceAnnotationMilestones(repairedXml);
  //    validateTextNums(cooked, result);
  //    if (DEBUG) {
  //      outputFiles(xml, cooked);
  //    }
  //    final String tei = toTei(repairedXml, result);
  //    result.setBody(tei);
  //    Log.info("tei={}", tei);
  //    String fullTEI = result.getTEI();
  //    ValidationResult validateTEI = MVNValidator.validateTEI(fullTEI);
  //    if (!validateTEI.isValid()) {
  //      result.addError("",
  //          "Gegenereerde TEI is niet valide:\n"//
  //              + "<blockquote>" + validateTEI.getMessage() + "</blockquote>\n"//
  //              + " TEI:\n<pre>" + escapeHtml(fullTEI) + "</pre>"//
  //      );
  //    }
  //    //    for (ProjectEntry entry : project.getProjectEntries()) {
  //    //      MVNFolium page = new MVNFolium();
  //    //      String n = entry.getName();
  //    //      page.setN(n);
  //    //      page.setId(result.getSigle() + "-pb-" + n);
  //    //      setFacs(entry, page, result);
  //    //      setOrder(page, entry);
  //    //      setBody(page, entry, result);
  //    //      result.addPages(page);
  //    //    }
  //    return result;
  //  }
  //
  //  private void setFacs(final ProjectEntry entry, final MVNFolium page, final MVNConversionResult result) {
  //    final List<Facsimile> facsimiles = entry.getFacsimiles();
  //    if (facsimiles.isEmpty()) {
  //      result.addError("" + entry.getId(), "no facsimile");
  //    } else {
  //      if (facsimiles.size() > 1) {
  //        result.addError("" + entry.getId(), "multiple facsimiles, using first");
  //      }
  //      page.setFacs(facsimiles.get(0).getName());
  //    }
  //  }
  //
  //  private void setBody(final MVNFolium page, final ProjectEntry entry, final MVNConversionResult result, final Map<Integer, AnnotationData> annotationIndex) {
  //    String body = null;
  //    for (final Transcription transcription : entry.getTranscriptions()) {
  //      if ("Diplomatic".equals(transcription.getTextLayer())) {
  //        body = transcription.getBody().replace("&nbsp;", " ");
  //      } else {
  //        result.addError("" + entry.getId(), "incorrect textlayer found: " + transcription.getTextLayer());
  //      }
  //    }
  //    Log.info("body=[\n{}\n]", body);
  //    if (body == null) {
  //      result.addError("" + entry.getId(), "no Diplomatic textlayer");
  //    } else {
  //      page.setBody(toTei(body, result));
  //    }
  //    Log.info("body=[\n{}\n]", page.getBody());
  //  }
  //  String toTei0(final String xml, final MVNConversionResult result) {
  //    final MVNTranscriptionVisitor visitor = new MVNTranscriptionVisitor(result, data.getAnnotationIndex(), data.getDeepestTextNums());
  //    Log.info("xml={}", xml);
  //
  //    final Document document = Document.createFromXml(xml, false);
  //    document.accept(visitor);
  //
  //    final XmlContext c = visitor.getContext();
  //    final String rawResult = c.getResult();
  //
  //    return rawResult//
  //        .replace("<b>", "")//
  //        .replace("</b>", "")//
  //        .replace("<u>", "")//
  //        .replace("</u>", "")//
  //        //        .replaceAll("<gap>.*?</gap>", "<gap/>")// according to the rng, gaps should be empty
  //        .replace("<l><head", "<head")//
  //        //        .replace("<l><hi rend=\"rubric\"><head", "<head")//
  //        .replace("</head></l>", "</head>")//
  //        //        .replace("</head></hi></l>", "</head>")//
  //        .replace("<l><closer", "<closer")//
  //        .replace("</closer></l>", "</closer>")//
  //        .replace("<hi rend=\"rubric\"><hi rend=\"rubric\">¶</hi></hi>", "<hi rend=\"rubric\">¶</hi>");
  //  }

}
