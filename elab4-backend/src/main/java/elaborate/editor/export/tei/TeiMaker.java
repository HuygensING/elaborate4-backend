package elaborate.editor.export.tei;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2020 Huygens ING
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

import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import elaborate.editor.model.orm.Facsimile;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.ProjectEntryMetadataItem;
import elaborate.editor.model.orm.ProjectMetadataItem;
import elaborate.editor.model.orm.Transcription;
import elaborate.editor.model.orm.service.ProjectService;
import nl.knaw.huygens.facetedsearch.SolrUtils;

public class TeiMaker {
  public static final Map<String, String> HI_TAGS = ImmutableMap.<String, String> builder()//
      .put("strong", "bold")//
      .put("b", "bold")//
      .put("u", "underline")//
      .put("em", "italic")//
      .put("i", "italic")//
      .put("sub", "subscript")//
      .put("sup", "superscript")//
      .build();

  public static final String INTERP_GRP = "interpGrp";

  private final Document tei;
  private final Project project;
  private final TeiConversionConfig config;
  private final EntityManager entityManager;

  public TeiMaker(Project _project, TeiConversionConfig _config, EntityManager entityManager) {
    this.project = _project;
    this.config = _config;
    this.entityManager = entityManager;
    if (_project == null) {
      tei = null;
    } else {
      tei = createTeiDocument();

      Element root = tei.createElement("TEI");
      root.setAttribute("xmlns", "http://www.tei-c.org/ns/1.0");
      tei.appendChild(root);

      Element header = createHeader();
      root.appendChild(header);
      addNewLine(root);
      ProjectService projectService = ProjectService.instance();
      projectService.setEntityManager(entityManager);
      List<ProjectEntry> projectEntriesInOrder = projectService.getProjectEntriesInOrder(project.getId());
      Element facsimile = createFacsimile(projectEntriesInOrder);
      root.appendChild(facsimile);
      addNewLine(root);

      Element text = createText(projectEntriesInOrder);
      root.appendChild(text);
    }
  }

  private void addNewLine(Element root) {
    Text textNode = tei.createTextNode("\n");
    root.appendChild(textNode);
  }

  public String toXML() {
    if (tei == null) {
      return null;
    }
    TransformerFactory transfac = TransformerFactory.newInstance();
    try {
      DOMSource source = new DOMSource(tei);
      Transformer trans = transfac.newTransformer();
      trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
      trans.setOutputProperty(OutputKeys.INDENT, "no");
      StringWriter sw = new StringWriter();
      StreamResult result = new StreamResult(sw);
      trans.transform(source, result);
      return sw.toString().replace("interpgrp>", "interpGrp>").replaceAll(" +<lb/>", "<lb/>");
    } catch (TransformerConfigurationException e) {
      e.printStackTrace();
    } catch (TransformerException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Element createFacsimile(List<ProjectEntry> projectEntriesInOrder) {
    Element facsimileElement = tei.createElement("facsimile");
    for (ProjectEntry entry : projectEntriesInOrder) {
      int n = 1;
      for (Facsimile facsimile : entry.getFacsimiles()) {
        Element surfaceElement = tei.createElement("surface");
        surfaceElement.setAttribute("xml:id", "facs-" + entry.getShortName() + "-" + n++);
        surfaceElement.setAttribute("n", facsimile.getName());

        Element graphicElement = tei.createElement("graphic");
        graphicElement.setAttribute("url", facsimile.getZoomableUrl());

        surfaceElement.appendChild(graphicElement);
        facsimileElement.appendChild(surfaceElement);
      }
    }
    return facsimileElement;
  }

  private Element createText(List<ProjectEntry> entries) {
    int pageno = 1;
    Element text = tei.createElement("text");
    addNewLine(text);

    Element interpGrp = createProjectInterpGrp();
    if (interpGrp != null) {
      text.appendChild(interpGrp);
      addNewLine(text);
    }

    if (config.getGroupTextsByMetadata() != null) {
      Element group = tei.createElement("group");
      text.appendChild(group);
      // TODO implement grouping

    } else {
      Element body = tei.createElement("body");
      text.appendChild(body);

      String currentFolio = "";
      for (ProjectEntry entry : entries) {
        // if (entry.hasTranscriptions()) {
        pageno = processEntry(pageno, body, currentFolio, entry);
        // }
      }
    }
    return text;
  }

  private Element createProjectInterpGrp() {
    List<ProjectMetadataItem> projectMetadataItems = project.getProjectMetadataItems();
    Element interpGrp = tei.createElement(INTERP_GRP);
    interpGrp.appendChild(interp("title", project.getTitle()));
    for (ProjectMetadataItem projectMetadataItem : projectMetadataItems) {
      String type = projectMetadataItem.getField();
      String value = projectMetadataItem.getData();
      interpGrp.appendChild(interp(type, value));
    }
    return interpGrp;
  }

  private Element interp(String type, String value) {
    Element interp = tei.createElement("interp");
    interp.setAttribute("type", SolrUtils.normalize(type).replace(" ", ""));
    interp.appendChild(tei.createTextNode(StringEscapeUtils.escapeHtml(value)));
    return interp;
  }

  private static final Comparator<Transcription> ORDER_BY_TYPE = new Comparator<Transcription>() {
    @Override
    public int compare(Transcription t1, Transcription t2) {
      Long tt1 = t1.getTranscriptionType().getId();
      Long tt2 = t2.getTranscriptionType().getId();
      return tt1.compareTo(tt2);
    }
  };

  private int processEntry(int _pageno, Element body, String _currentFolio, ProjectEntry projectEntry) {
    int pageno = _pageno;
    String currentFolio = _currentFolio;
    String folio = StringUtils.defaultIfBlank(projectEntry.getMetadataValue("Folio number"), "") + StringUtils.defaultIfBlank(projectEntry.getMetadataValue("Folio side"), "");
    if (!currentFolio.equals(folio)) {
      pageno = addPb(body, pageno, projectEntry, folio);
    }
    // addCb(body, projectEntry);
    currentFolio = folio;

    Element entryDiv = tei.createElement("div");
    entryDiv.setAttribute("xml:id", "e" + projectEntry.getId());
    entryDiv.setAttribute("n", projectEntry.getName());

    addEntryInterpGrp(entryDiv, projectEntry);

    List<Transcription> orderedTranscriptions = Lists.newArrayList(projectEntry.getTranscriptions());
    Collections.sort(orderedTranscriptions, ORDER_BY_TYPE);
    for (Transcription transcription : orderedTranscriptions) {
      HtmlTeiConverter htmlTeiConverter = new HtmlTeiConverter(transcription.getBody(), config, transcription.getTranscriptionType().getName(), entityManager);
      Node transcriptionNode = htmlTeiConverter.getContent();
      Node importedTranscriptionNode = tei.importNode(transcriptionNode, true);
      Node child = importedTranscriptionNode.getFirstChild();
      while (child != null) {
        Node nextSibling = child.getNextSibling();
        if (child.getNodeName().equals("div") && child.hasChildNodes()) {
          entryDiv.appendChild(child);
        }
        child = nextSibling;
      }
    }
    body.appendChild(entryDiv);
    addNewLine(body);

    return pageno;
  }

  private void addEntryInterpGrp(Element entryDiv, ProjectEntry projectEntry) {
    Map<String, String> metaMap = Maps.newHashMap();
    List<String> metadataToInclude = ImmutableList.copyOf(projectEntry.getProject().getProjectEntryMetadataFieldnames());
    for (ProjectEntryMetadataItem meta : projectEntry.getProjectEntryMetadataItems()) {
      if (metadataToInclude.contains(meta.getField())) {
        metaMap.put(SolrUtils.normalize(meta.getField()), meta.getData());
      }
    }
    if (!metaMap.isEmpty()) {
      Element interpGrp = tei.createElement(INTERP_GRP);
      for (Entry<String, String> entry : metaMap.entrySet()) {
        Element interp = tei.createElement("interp");
        interp.setAttribute("type", entry.getKey());
        interp.setAttribute("value", StringEscapeUtils.escapeHtml(entry.getValue()));

        interpGrp.appendChild(interp);
      }
      entryDiv.appendChild(interpGrp);
    }
  }

  private int addPb(Element body, int _pageno, ProjectEntry projectEntry, String folio) {
    int pageno = _pageno;
    Element pb = tei.createElement("pb");
    if (StringUtils.isNotEmpty(folio)) {
      pb.setAttribute("f", folio);
    }
    pb.setAttribute("facs", "#facs-" + projectEntry.getShortName() + "-" + pageno);
    pb.setAttribute("n", String.valueOf(pageno++));
    body.appendChild(pb);
    return pageno;
  }

  private Element createHeader() {
    Element p1 = tei.createElement("p");
    Element header = tei.createElement("teiHeader");
    Element fileDesc = tei.createElement("fileDesc");
    Element publicationStmt = tei.createElement("publicationStmt");
    Element publisher = tei.createElement("publisher");
    publisher.appendChild(tei.createTextNode("Huygens ING"));
    publicationStmt.appendChild(publisher);
    Element sourceDesc = tei.createElement("sourceDesc");
    Element p = tei.createElement("p");
    sourceDesc.appendChild(p);
    Element titleStmt = tei.createElement("titleStmt");
    Element title = tei.createElement("title");
    Comment ordering = tei.createComment(MessageFormat.format("ordering: {0} / {1} / {2}", project.getLevel1(), project.getLevel2(), project.getLevel3()));

    Text textNode = tei.createTextNode(project.getTitle());
    title.appendChild(textNode);

    titleStmt.appendChild(title);

    fileDesc.appendChild(titleStmt);
    fileDesc.appendChild(publicationStmt);
    fileDesc.appendChild(sourceDesc);

    header.appendChild(fileDesc);
    header.appendChild(ordering);
    return header;
  }

  private static Document createTeiDocument() {
    DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
      return docBuilder.newDocument();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    }
    return null;
  }

}
