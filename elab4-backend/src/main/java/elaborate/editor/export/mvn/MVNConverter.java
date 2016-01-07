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
import org.apache.commons.lang.StringUtils;

import elaborate.editor.export.mvn.MVNConversionData.AnnotationData;
import elaborate.editor.export.mvn.MVNConversionData.EntryData;
import elaborate.editor.model.orm.Facsimile;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.Transcription;
import elaborate.editor.publish.Publication.Status;
import elaborate.util.HibernateUtil;
import elaborate.util.XmlUtil;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.tei.Document;
import nl.knaw.huygens.tei.XmlContext;

public class MVNConverter {
  private static final boolean DEBUG = false; // for release
  //  private static final boolean DEBUG = true; // for testing
  private final Project project;
  private final MVNConversionData data;
  private final Status status;

  public MVNConverter(final Project project, final MVNConversionData data, Status status) {
    this.project = project;
    //    data = getConversionData(project.getId());
    this.data = data;
    this.status = status;
  }

  // fase 1: collect MVNFolium with raw transcription 
  // order by 'order' or entryname
  // fail when 1, but not all entries have order
  // fail when entrynames are not unique
  // select id, name from project_entries where project_id=$projectId
  // select 

  @SuppressWarnings("unchecked")
  public static MVNConversionData getConversionData(final long project_id, Status status) {
    final MVNConversionData conversionData = new MVNConversionData();
    final EntityManager entityManager = HibernateUtil.beginTransaction();

    final String transcriptionSQL = "select"//
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
    final Query transcriptionQuery = entityManager.createNativeQuery(transcriptionSQL);
    final List<Object[]> transcriptions = transcriptionQuery.getResultList();
    for (final Object[] transcription : transcriptions) {
      final EntryData entryData = new MVNConversionData.EntryData();
      final Integer id = (Integer) transcription[0];
      entryData.id = String.valueOf(id);
      entryData.name = (String) transcription[1];
      //      String order = (String) transcription[2];
      entryData.body = (String) transcription[3];
      entryData.facs = (String) transcription[4];
      conversionData.getEntryDataList().add(entryData);
    }

    final String annotationSQL = "select"//
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
    final Query annotationQuery = entityManager.createNativeQuery(annotationSQL);
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

    HibernateUtil.rollbackTransaction(entityManager);
    return conversionData;
  }

  public MVNConversionResult convert() {
    final MVNConversionResult result = new MVNConversionResult(project, status);
    final StringBuilder editionTextBuilder = new StringBuilder();
    for (final MVNConversionData.EntryData entryData : data.getEntryDataList()) {
      final String pageId = result.getSigle() + "-pb-" + entryData.name;
      editionTextBuilder//
          .append("\n<pb n=\"")//
          .append(entryData.name)//
          .append("\" xml:id=\"")//
          .append(pageId)//
          .append("\" facs=\"")//
          .append(entryData.facs)//
          .append("\" _entryId=\"")//
          .append(entryData.id)//
          .append("\"/>\n")//
          .append("<lb/>")//
          .append(transcriptionBody(entryData).replace("\n", "<le/>\n<lb/>"))//
          .append("<le/>");
    }

    final String xml = "<body>" + editionTextBuilder.toString().replace("<lb/><le/>", "").replace("\n\n", "\n") + "</body>";
    final String cooked = cook(xml);
    validateTextNums(cooked, result);
    if (DEBUG) {
      outputFiles(xml, cooked);
    }
    final String tei = toTei(xml, result);
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

  private void validateTextNums(final String cooked, final MVNConversionResult result) {
    final Stack<String> textNumStack = new Stack<String>();
    final Matcher matcher = Pattern.compile("mvn:tekst([be][^ >]+) body=\"([^\"]+)\"").matcher(cooked);
    boolean lastTagWasBegin = false;
    while (matcher.find()) {
      final String beginOrEinde = matcher.group(1);
      final String textnum = matcher.group(2).trim().replaceFirst(";.*$", "");
      if ("begin".equals(beginOrEinde)) {
        lastTagWasBegin = true;
        textNumStack.push(textnum);

      } else if ("einde".equals(beginOrEinde)) {
        final String peek = textNumStack.peek();
        if (textnum.equals(peek)) {
          if (lastTagWasBegin) {
            data.getDeepestTextNums().add(textnum);
          }
          textNumStack.pop();
        } else {
          result.addError("", "mvn:regeleinde : tekstNum '" + textnum + "' gevonden waar '" + peek + "' verwacht was.");
        }
        lastTagWasBegin = false;

      } else {
        throw new RuntimeException("unexpected type mvn:tekst" + beginOrEinde);
      }
    }

  }

  private void outputFiles(final String xml, final String cooked) {
    try {
      FileUtils.write(new File("out/rawbody.xml"), xml);
      FileUtils.write(new File("out/cookedbody.xml"), cooked);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private String cook(final String xml) {
    String cooked = xml;
    for (final String annotationNoString : XmlUtil.extractAnnotationNos(xml)) {
      final Integer annotationNo = Integer.valueOf(annotationNoString);
      if (data.getAnnotationIndex().containsKey(annotationNo)) {
        final AnnotationData annotationData = data.getAnnotationIndex().get(annotationNo);
        final String type = annotationData.type.replaceAll("[ \\(\\)]+", "_").replaceFirst("_$", "");
        String attributes = "";
        if (StringUtils.isNotBlank(annotationData.body) && !"nvt".equals(annotationData.body)) {
          attributes = " body=\"" + annotationData.body.replace("\"", "&quot;") + "\"";
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
    return entryData.body//
        .replace("<body>", "")//
        .replace("</body>", "")//
        .replace("&nbsp;", " ")//
        .trim();
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
    final MVNTranscriptionVisitor visitor = new MVNTranscriptionVisitor(result, data.getAnnotationIndex(), data.getDeepestTextNums());
    Log.info("xml={}", xml);

    final Document document = Document.createFromXml(xml, false);
    document.accept(visitor);

    final XmlContext c = visitor.getContext();
    final String rawResult = c.getResult();

    return rawResult;
  }

}
