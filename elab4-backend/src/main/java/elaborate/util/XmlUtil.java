package elaborate.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlUtil {
  static Logger LOG = LoggerFactory.getLogger(XmlUtil.class);
  private static final String XML_CLOSE_TAG = "</xml>";
  private static final String XML_OPEN_TAG = "<xml>";

  public static String unwrapFromXml(String xml) {
    return xml.replaceFirst(XML_OPEN_TAG, "").replaceFirst(XML_CLOSE_TAG, "").replaceAll("&apos;", "'");
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
    } catch (ParserConfigurationException e1) {
      e1.printStackTrace();
      return false;
    } catch (SAXException e1) {
      e1.printStackTrace();
      LOG.error("body={}", body);
      return false;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public static String fixXhtml(String badxml) {
    Document doc = Jsoup.parse(badxml);
    doc.outputSettings().indentAmount(0).prettyPrint(false).escapeMode(Entities.EscapeMode.xhtml).charset("UTF-8");
    return doc.body().html().replaceAll(" />", "/>");
    //    return Jsoup.clean(badxml, Whitelist.relaxed());
  }

  static final Pattern ENDTAG_AFTER_NEWLINE_PATTERN = Pattern.compile("\n(</.*?>)"); // endtag at the beginning of line, should be at end of previouse line

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
    return xml.replaceAll("<.*?>", "");
  }

  private XmlUtil() {
    throw new AssertionError("Non-instantiable class");
  }

}
