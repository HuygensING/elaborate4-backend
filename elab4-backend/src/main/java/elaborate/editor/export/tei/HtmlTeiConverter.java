package elaborate.editor.export.tei;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2014 Huygens ING
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

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

import nl.knaw.huygens.LoggableObject;
import nl.knaw.huygens.tei.DelegatingVisitor;
import nl.knaw.huygens.tei.XmlContext;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.google.common.base.Charsets;

import elaborate.util.XmlUtil;

public class HtmlTeiConverter extends LoggableObject {
  private Document teiDocument;
  private final TeiConversionConfig config;
  private final EntityManager entityManager;

  public HtmlTeiConverter(String _html, TeiConversionConfig _config, String transcriptionType, EntityManager entityManager) {
    this.config = _config;
    this.entityManager = entityManager;
    String html = _html;
    if (_html == null) {
      html = "";
    }
    //    LOG.info("html in = ''{}''", html);
    String xml = toXml(html);
    String teiSource = convert2TEI(xml, transcriptionType);
    DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
    //    System.out.println(teiSource);
    try {
      DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
      teiDocument = docBuilder.parse(new ByteArrayInputStream(teiSource.getBytes(Charsets.UTF_8)));
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String asXML() {
    TransformerFactory transfac = TransformerFactory.newInstance();
    try {
      DOMSource source = new DOMSource(teiDocument);
      Transformer trans = transfac.newTransformer();
      trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      trans.setOutputProperty(OutputKeys.INDENT, "no");
      StringWriter sw = new StringWriter();
      StreamResult result = new StreamResult(sw);
      trans.transform(source, result);
      return sw.toString();
    } catch (TransformerConfigurationException e) {
      e.printStackTrace();
    } catch (TransformerException e) {
      e.printStackTrace();
    }
    return null;
  }

  public Document getDocument() {
    return teiDocument;
  }

  public Node getContent() {
    return teiDocument.getFirstChild();
  }

  Element setUpTeiDocument() {
    DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder;
    try {
      docBuilder = dbfac.newDocumentBuilder();
      teiDocument = docBuilder.newDocument();
      Element body = teiDocument.createElement("body");
      teiDocument.appendChild(body);
      return body;
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    }
    return null;
  }

  String convert2TEI(String xml, String transcriptionType) {
    DelegatingVisitor<XmlContext> visitor = new TranscriptionVisitor(config, transcriptionType, entityManager);

    final nl.knaw.huygens.tei.Document document = nl.knaw.huygens.tei.Document.createFromXml(xml.replaceAll("\n", " "), false);
    document.accept(visitor);
    final XmlContext c = visitor.getContext();
    String rawResult = c.getResult();

    return XmlUtil.fixXhtml(rawResult);
  }

  private String toXml(String html) {
    String fixedHtml = html//
        .replaceAll("\\&", "&amp;")//
        .replaceAll("<br>", "<br/>\n")//
        .replaceAll("<body>", "<div>")//
        .replaceAll("</body>", "</div>\n")//
        .replaceAll("\n", "<lb/>\n");
    return XmlUtil.wrapInXml(fixedHtml);
  }
}
