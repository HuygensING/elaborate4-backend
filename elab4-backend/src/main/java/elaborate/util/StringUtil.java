package elaborate.util;

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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.io.output.NullWriter;
import org.apache.commons.lang.StringUtils;
import org.w3c.tidy.Tidy;

public class StringUtil {
  private static final String UTF8 = "UTF8";
  static final Tidy TIDY = new Tidy();

  static {
    TIDY.setAltText("");
    TIDY.setAsciiChars(true);
    TIDY.setBreakBeforeBR(false);
    TIDY.setBurstSlides(false);
    TIDY.setDropEmptyParas(true);
    TIDY.setDropFontTags(true);
    TIDY.setDropProprietaryAttributes(false);
    TIDY.setEncloseBlockText(false);
    TIDY.setEncloseText(false);
    TIDY.setErrout(new PrintWriter(new NullWriter()));
    TIDY.setEscapeCdata(true);
    TIDY.setFixBackslash(true);
    TIDY.setFixComments(true);
    TIDY.setFixUri(true);
    TIDY.setForceOutput(true);
    TIDY.setHideComments(true);
    TIDY.setHideEndTags(false);
    TIDY.setIndentAttributes(false);
    TIDY.setIndentCdata(false);
    TIDY.setIndentContent(false);
    TIDY.setInputEncoding(UTF8);
    TIDY.setJoinClasses(true);
    TIDY.setJoinStyles(true);
    TIDY.setKeepFileTimes(false);
    TIDY.setLiteralAttribs(false);
    TIDY.setLogicalEmphasis(true);
    TIDY.setLowerLiterals(true);
    TIDY.setMakeBare(true);
    TIDY.setMakeClean(true);
    TIDY.setNumEntities(true);
    TIDY.setOutputEncoding(UTF8);
    TIDY.setPrintBodyOnly(true);
    TIDY.setQuiet(false);
    TIDY.setQuoteAmpersand(false);
    TIDY.setQuoteMarks(false);
    TIDY.setQuoteNbsp(false);
    TIDY.setReplaceColor(false);
    TIDY.setSmartIndent(false);
    TIDY.setSpaces(1);
    TIDY.setTidyMark(false);
    TIDY.setTrimEmptyElements(false);
    TIDY.setUpperCaseAttrs(false);
    TIDY.setUpperCaseTags(false);
    TIDY.setWord2000(true);
    TIDY.setWrapAttVals(false);
    TIDY.setWraplen(2000);
    TIDY.setXmlOut(true);
    TIDY.setXmlPi(false);
    TIDY.setXmlSpace(false);
    TIDY.setXmlTags(false);
  }

  private static final String PAGETITLESEPARATOR = " :: ";
  private static final String APPNAME = "eLaborate3";
  static final String DELIM = "+-*/(),.;'`\"<> ";

  @SuppressWarnings("boxing")
  public static String replace(
      String originalTerm,
      String replacementTerm,
      String body,
      List<Integer> occurrencesToReplace,
      boolean preserveCase) {
    StringTokenizer tokenizer = new StringTokenizer(body, DELIM, true);

    // Log.info("body:[{}]", body);
    StringBuilder replaced = new StringBuilder(body.length());
    int occurrence = 1;
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      DelimiterDetector delimiterDetector = new DelimiterDetector(token);
      String strippedToken = delimiterDetector.getStripped();
      if (strippedToken.equalsIgnoreCase(originalTerm)) {
        String replacement =
            preserveCase
                ? TextCase.detectCase(strippedToken).applyTo(replacementTerm)
                : replacementTerm;
        replaced.append(
            occurrencesToReplace.contains(occurrence)
                ? delimiterDetector.getPreDelimiters()
                    + replacement
                    + delimiterDetector.getPostDelimiters()
                : token);
        occurrence++;
      } else {
        replaced.append(token);
      }
    }
    // Log.info("replaced:[{}]", replaced);
    return replaced.toString();
  }

  static final List<String> hostProtocols = Lists.newArrayList("http", "https");

  /**
   * change ULRs in <code>textWithURLs</code> to links
   *
   * @param textWithURLs text with URLs
   * @return text with links
   */
  public static String activateURLs(String textWithURLs) {
    StringTokenizer tokenizer = new StringTokenizer(textWithURLs, "<> ", true);
    StringBuilder replaced = new StringBuilder(textWithURLs.length());
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      // Log.info("token={}", token);
      try {
        URL url = new URL(token);
        // If possible then replace with anchor...
        String linktext = token;
        String file = url.getFile();
        if (StringUtils.isNotBlank(file)) {
          linktext = file;
        }
        String protocol = url.getProtocol();
        if (hostProtocols.contains(protocol)) {
          linktext = url.getHost() + linktext;
        }
        replaced
            .append("<a target=\"_blank\" href=\"")
            .append(url)
            .append("\">")
            .append(linktext)
            .append("</a>");
      } catch (MalformedURLException e) {
        replaced.append(token);
      }
    }

    return replaced.toString();
  }

  // public static String activateURLs(String textWithURLs) {
  // StringTokenizer tokenizer = new StringTokenizer(textWithURLs, DELIM, true);

  // List<String> htmlParts = Lists.newArrayList();
  // for (String part : textWithURLs.split("\\s")) {
  // try {
  // URL url = new URL(part);
  // // If possible then replace with anchor...
  // String linktext = part;
  // String file = url.getFile();
  // if (StringUtils.isNotBlank(file)) {
  // linktext = file;
  // }
  // String protocol = url.getProtocol();
  // if (hostProtocols.contains(protocol)) {
  // linktext = url.getHost() + linktext;
  // }
  // htmlParts.add("<a target=\"_blank\" href=\"" + url + "\">" + linktext + "</a>");
  // } catch (MalformedURLException e) {
  // htmlParts.add(part);
  // }
  // }
  // return Joiner.on(" ").join(htmlParts);
  // }

  public static String pageTitle(String... subtitles) {
    List<String> parts = Lists.newArrayList(APPNAME);
    parts.addAll(Lists.newArrayList(subtitles));
    return Joiner.on(PAGETITLESEPARATOR).join(parts);
  }

  public static String escapeQuotes(String string) {
    return string.replaceAll("\"", "\\\\\"").replaceAll("'", "\\\\'");
  }

  static final List<String> XML_ENTITIES = Lists.newArrayList("quot", "amp", "apos", "lt", "gt");

  public static String fixXML(String brokenxml) {
    String fixedXml = brokenxml.replaceAll("&", "&amp;");
    for (String entity : XML_ENTITIES) {
      fixedXml = fixedXml.replaceAll("&amp;" + entity + ";", "&" + entity + ";");
    }
    Pattern p = Pattern.compile("<([^<]*?)>");
    Matcher matcher = p.matcher(fixedXml);
    while (matcher.find()) {
      String group = matcher.group();
      String replacement = "&0&" + matcher.group(1) + "&1&";
      fixedXml = fixedXml.replace(group, replacement);
    }
    fixedXml =
        fixedXml
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll("&0&", "<")
            .replaceAll("&1&", ">");
    return fixedXml;
  }

  public static String html2xml(String editBody) {
    StringReader in = new StringReader(editBody);
    StringWriter out = new StringWriter();
    TIDY.parse(in, out);
    try {
      in.close();
      out.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return out.toString().replaceAll("\r\n", "").replaceAll("\n", "");
  }

  private StringUtil() {
    throw new AssertionError("Non-instantiable class");
  }

  private static final String MULTIVALUED_DIVIDER = " | ";

  public static Iterable<String> getValues(String multiValue) {
    return Splitter.on(MULTIVALUED_DIVIDER).trimResults().split(multiValue);
  }
}
