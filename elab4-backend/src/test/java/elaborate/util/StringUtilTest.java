package elaborate.util;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import nl.knaw.huygens.LoggableObject;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import elaborate.editor.resources.SessionResource;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class StringUtilTest extends LoggableObject {

  @Test
  public void testNormalize() {
    assertEquals("abc_def", StringUtil.normalize("Abc Def"));
  }

  @Test
  public void testReplace1() {
    String body = "<content>1 Kat 2 kat 3 kat 4 KAT 5 kat 6 kat 7 kat</content>";
    String expected = "<content>1 Dog 2 kat 3 dog 4 KAT 5 dog 6 kat 7 kat</content>";
    String originalTerm = "kat";
    String replacementTerm = "dog";
    List<Integer> occurrencesToReplace = Lists.newArrayList();
    occurrencesToReplace.add(1);
    occurrencesToReplace.add(3);
    occurrencesToReplace.add(5);
    testReplacement(body, expected, originalTerm, replacementTerm, occurrencesToReplace);
  }

  @Test
  public void testReplace2() {
    String body = "<content>kat; 2 kat 3 kat 4 kat 5 kat 6 kat 7 kat</content>";
    String expected = "<content>dog; 2 kat 3 kat 4 kat 5 kat 6 dog 7 dog</content>";
    String originalTerm = "kat";
    String replacementTerm = "dog";
    List<Integer> occurencesToReplace = Lists.newArrayList();
    occurencesToReplace.add(1);
    occurencesToReplace.add(6);
    occurencesToReplace.add(7);
    testReplacement(body, expected, originalTerm, replacementTerm, occurencesToReplace);
  }

  @Test
  public void testReplace3() {
    String body = "1 kat kater 2 'kat'";
    String expected = "1 kat kater 2 'dog'";
    String originalTerm = "kat";
    String replacementTerm = "dog";
    List<Integer> occurencesToReplace = Lists.newArrayList();
    occurencesToReplace.add(2);
    testReplacement(body, expected, originalTerm, replacementTerm, occurencesToReplace);
  }

  private void testReplacement(String body, String expected, String originalTerm, String replacementTerm, List<Integer> occurencesToReplace) {
    String replaced = StringUtil.replace(originalTerm, replacementTerm, body, occurencesToReplace, true);
    assertEquals(expected, replaced);
  }

  @Test
  public void testSerializeMap() {
    Map<String, String> map = Maps.newHashMap();
    map.put("key", "value");
    map.put("difficult", "áàçë");
    JSONSerializer serializer = new JSONSerializer();
    String mapJson = serializer.serialize(map);
    Map<String, String> map2 = new JSONDeserializer<Map<String, String>>().deserialize(mapJson);
    assertEquals(map.toString(), map2.toString());
  }

  //  @Test
  public void testStringSize() {
    String string = "bláàt sçhapën";
    assertEquals(13, string.length()); // 9 normal + 4 utf-8 chars
    assertEquals(17, string.getBytes().length); //(9 + 2x4 bytes)
    assertEquals(13, string.codePointCount(0, 13));
  }

  @Test
  public void testActivateURLs() {
    String string = "kijk ook eens op http://hier.daar.nl/ok en mail mailto:bram.buitendijk@huygens.nl .";
    String expected = "kijk ook eens op <a target=\"_blank\" href=\"http://hier.daar.nl/ok\">hier.daar.nl/ok</a> en mail <a target=\"_blank\" href=\"mailto:bram.buitendijk@huygens.nl\">bram.buitendijk@huygens.nl</a> .";
    assertEquals(expected, StringUtil.activateURLs(string));
    assertEquals(expected, StringUtil.activateURLs(expected));
  }

  @Test
  public void testActivateURLs2() {
    String string = "<span>http://daysmagazine.com/index.php/days-at-home/de-eenhoorn.html</span>";
    String expected = "<span><a target=\"_blank\" href=\"http://daysmagazine.com/index.php/days-at-home/de-eenhoorn.html\">daysmagazine.com/index.php/days-at-home/de-eenhoorn.html</a></span>";
    assertEquals(expected, StringUtil.activateURLs(string));
    assertEquals(expected, StringUtil.activateURLs(expected));
  }

  @Test
  public void testPageTitle() {
    assertEquals("eLaborate3", StringUtil.pageTitle());
    assertEquals("eLaborate3 :: edit", StringUtil.pageTitle("edit"));
    assertEquals("eLaborate3 :: Torec :: Publish", StringUtil.pageTitle("Torec", "Publish"));
  }

  @Test
  public void testEscapeQuotes() {
    assertEquals("bla \\\"bla\\\" bla", StringUtil.escapeQuotes("bla \"bla\" bla"));
    assertEquals("single \\'quotes\\'", StringUtil.escapeQuotes("single 'quotes'"));
  }

  @Test
  public void testFixXML() {
    String brokenxml = "<xml>a&b, bla &amp; co; 4 > 2 < 3; &gt;</xml>";
    String xml = StringUtil.fixXML(brokenxml);
    //    LOG.info(xml);
    XmlTestUtil.assertXmlIsWellFormed(xml);
    String expected = "<xml>a&amp;b, bla &amp; co; 4 &gt; 2 &lt; 3; &gt;</xml>";
    assertEquals(expected, xml);
  }

  @Test
  public void testFixXML2() {
    String brokenxml = "<body>a&b, bla &amp; co; 4 > 2 < 3; &gt;</body>";
    String expected = "a&amp;b, bla &amp; co; 4 &gt; 2 &lt; 3; &gt;";
    testHTML2XML(brokenxml, expected);
  }

  @Test
  public void testHtml2Xml1() {
    String html = "<body>&nbsp;bla <span id=9000316 class=annotationstart></span>x<span id=9000316 class=annotationend></span><br>whatever</body>";
    String expected = " bla <span id=\"9000316\" class=\"annotationstart\"></span>x<span id=\"9000316\" class=\"annotationend\"></span><br />whatever";
    testHTML2XML(html, expected);
  }

  @Test
  public void testHtml2Xml2() {
    String html = "<html>bla & bli &amp; blo</html>";
    String expected = "bla &amp; bli &amp; blo";
    testHTML2XML(html, expected);
  }

  @Test
  public void test() {
    assertTrue(true == Boolean.valueOf("true"));
    assertTrue(false == Boolean.valueOf("false"));
  }

  private void testHTML2XML(String html, String expected) {
    String xmlBody = StringUtil.html2xml(html);
    LOG.info(xmlBody);
    XmlTestUtil.assertXmlIsWellFormed(wrap(xmlBody));
    assertEquals(expected, xmlBody);
  }

  private String wrap(String xml) {
    return "<xml>" + xml + "</xml>";
  }

  @Test
  public void test2() {
    String cutoffDate = new DateTime().minusDays(1).toString("YYYY-MM-dd HH:mm:ss");
    LOG.info(cutoffDate);
    LOG.info("{} ms", Hours.ONE.toStandardSeconds().getSeconds() * 1000);
  }

  @Test
  public void testURIBuilder() throws IllegalArgumentException, UriBuilderException, SecurityException, NoSuchMethodException {
    URI build = null;
    build = UriBuilder.fromResource(SessionResource.class).scheme("http").host("rest.elaborate.huygens.knaw.nl").path(SessionResource.class.getMethod("logout", String.class)).build("TOKEN");
    LOG.info("{}", build);
  }
}
