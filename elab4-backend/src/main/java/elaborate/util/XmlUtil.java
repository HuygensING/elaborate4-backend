package elaborate.util;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2022 Huygens ING
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

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import nl.knaw.huygens.Log;
import nl.knaw.huygens.tei.Element;

public class XmlUtil {
  private static final String XML_CLOSE_TAG = "</xml>";
  private static final String XML_OPEN_TAG = "<xml>";

  public static String unwrapFromXml(String xml) {
    return xml.replaceFirst(XML_OPEN_TAG, "")
        .replaceFirst(XML_CLOSE_TAG, "")
        .replaceAll("&apos;", "'");
  }

  public static String wrapInXml(String xmlContent) {
    return XML_OPEN_TAG + xmlContent + XML_CLOSE_TAG;
  }

  public static boolean isWellFormed(String body) {
    try {
      SAXParser parser;
      parser = SAXParserFactory.newInstance().newSAXParser();
      DefaultHandler dh = new DefaultHandler();
      parser.parse(new InputSource(new StringReader(body)), dh);
    } catch (ParserConfigurationException | IOException e1) {
      e1.printStackTrace();
      return false;
    } catch (SAXException e1) {
      e1.printStackTrace();
      Log.error("body={}", body);
      return false;
    }
      return true;
  }

  public static String fixXhtml(String badxml) {
    Document doc = Jsoup.parse(badxml);
    doc.outputSettings()
        .indentAmount(0)
        .prettyPrint(false)
        .escapeMode(Entities.EscapeMode.xhtml)
        .charset("UTF-8");
    return doc.body().html().replaceAll(" />", "/>").replace("\u00A0", "&#160;");
    // return Jsoup.clean(badxml, Whitelist.relaxed());
  }

  private static final Pattern ENDTAG_AFTER_NEWLINE_PATTERN =
      Pattern.compile(
          "\n(</.*?>)"); // endtag at the beginning of line, should be at end of previouse line

  public static String fixTagEndings(String body) {
    String newBody = body;
    Matcher matcher = ENDTAG_AFTER_NEWLINE_PATTERN.matcher(body);
    while (matcher.find()) {
      String endTag = matcher.group(1);
      newBody = newBody.replaceFirst("\n" + endTag, endTag + "\n");
      matcher = ENDTAG_AFTER_NEWLINE_PATTERN.matcher(newBody);
    }
    return newBody;
  }

  public static String removeXMLtags(String xml) {
    return xml.replaceAll("(?s)<.*?>", "");
  }

  public static String toPlainText(String body) {
    String breaksToNewlines = body.replaceAll("<br/?>", "\n");
    String noTags = removeXMLtags(breaksToNewlines);
    return StringEscapeUtils.unescapeXml(noTags).replace("&nbsp;", " ").trim();
  }

  public static String toSimpleHTML(String body) {
    String escapeAllowedHtml =
        body.replaceAll("<br/?>", "[[#br/#]]")
            .replaceAll("<em>(.*?)</em>", "[[#em#]]$1[[#/em#]]")
            .replaceAll("<i>(.*?)</i>", "[[#em#]]$1[[#/em#]]")
            .replaceAll("<span style=\"font-style: italic;\">(.*?)</span>", "[[#em#]]$1[[#/em#]]")
            .replaceAll("<strong>(.*?)</strong>", "[[#strong#]]$1[[#/strong#]]")
            .replaceAll("<b>(.*?)</b>", "[[#strong#]]$1[[#/strong#]]")
            .replaceAll(
                "<span style=\"font-weight: bold;\">(.*?)</span>", "[[#strong#]]$1[[#/strong#]]")
            .replaceAll("<u>(.*?)</u>", "[[#u#]]$1[[#/u#]]")
            .replaceAll(
                "<span style=\"text-decoration: underline;\">(.*?)</span>", "[[#u#]]$1[[#/u#]]")
            .replaceAll("<sup>(.*?)</sup>", "[[#sup#]]$1[[#/sup#]]")
            .replaceAll("<sub>(.*?)</sub>", "[[#sub#]]$1[[#/sub#]]");
    return removeXMLtags(escapeAllowedHtml).replace("[[#", "<").replace("#]]", ">");
  }

  //  public static String fixTagHierarchy(String body) {
  //    Collection<String> annotationNos = extractAnnotationNos(body);
  //    String bodyWithConvertedAnnotationTags = body;
  //    bodyWithConvertedAnnotationTags = convertAnnotationTagsToCustom(annotationNos,
  // bodyWithConvertedAnnotationTags);
  //    String fixed = fixXhtml(bodyWithConvertedAnnotationTags);
  //    fixed = convertCustomAnnotationTagsToOriginal(annotationNos, fixed);
  //    return fixed;
  //  }

  //  private static String convertCustomAnnotationTagsToOriginal(Collection<String> annotationNos,
  // String fixed) {
  //    for (String annotationNo : annotationNos) {
  //      fixed = fixed//
  //          .replace(customAnnotationBegin(annotationNo), originalAnnotationBegin(annotationNo))//
  //          .replace(customAnnotationEnd(annotationNo), originalAnnotationEnd(annotationNo));
  //    }
  //    return fixed;
  //  }

  //  private static String convertAnnotationTagsToCustom(Collection<String> annotationNos, String
  // bodyWithConvertedAnnotationTags) {
  //    for (String annotationNo : annotationNos) {
  //      bodyWithConvertedAnnotationTags = bodyWithConvertedAnnotationTags//
  //          .replace(originalAnnotationBegin(annotationNo), customAnnotationBegin(annotationNo))//
  //          .replace(originalAnnotationEnd(annotationNo), customAnnotationEnd(annotationNo));
  //    }
  //    return bodyWithConvertedAnnotationTags;
  //  }

  //  private static String customAnnotationEnd(String annotationNo) {
  //    return "</a" + annotationNo + ">";
  //  }

  //  private static String originalAnnotationEnd(String annotationNo) {
  //    return "<ae id=\"" + annotationNo + "\"/>";
  //  }

  //  private static String customAnnotationBegin(String annotationNo) {
  //    return "<a" + annotationNo + ">";
  //  }

  //  private static String originalAnnotationBegin(String annotationNo) {
  //    return "<ab id=\"" + annotationNo + "\"/>";
  //  }

  public static Collection<String> extractAnnotationNos(String body) {
    Set<String> annotationNos = Sets.newHashSet();
    Matcher matcher = Pattern.compile(" id=\"([0-9]+?)\"").matcher(body);
    while (matcher.find()) {
      annotationNos.add(matcher.group(1));
    }
    return annotationNos;
  }

  public static String closingTag(Element element) {
    return closingTag(element.getName());
  }

  public static String openingTag(Element element) {
    return openTagBuilder(element).append(">").toString();
  }

  private static StringBuilder openTagBuilder(Element element) {
    StringBuilder b = new StringBuilder("<").append(element.getName());
    Set<Entry<String, String>> entrySet = element.getAttributes().entrySet();
    for (Entry<String, String> entry : entrySet) {
      b.append(" ").append(entry.getKey()).append("=\"");
      appendAttributeValue(b, entry.getValue());
      b.append("\"");
    }
    return b;
  }

  public static String milestoneTag(Element element) {
    return openTagBuilder(element).append("/>").toString();
  }

  public static String milestoneTag(String name) {
    return milestoneTag(new Element(name));
  }

  private static void appendAttributeValue(StringBuilder builder, String value) {
    int n = value.length();
    for (int i = 0; i < n; i++) {
      char c = value.charAt(i);
      switch (c) {
        case '<':
          builder.append("&lt;");
          break;
        case '>':
          builder.append("&gt;");
          break;
        case '&':
          builder.append("&amp;");
          break;
        default:
          builder.append(c);
          break;
      }
    }
  }

  public static String closingTag(String name) {
    return "</" + name + ">";
  }

  public static String openingTag(String name) {
    return "<" + name + ">";
  }

  // -- private methods --//

  private XmlUtil() {
    throw new AssertionError("Non-instantiable class");
  }
}
