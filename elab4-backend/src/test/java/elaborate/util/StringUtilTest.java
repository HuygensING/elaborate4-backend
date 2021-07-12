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

import java.net.URI;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.junit.Test;

import nl.knaw.huygens.Log;

import elaborate.editor.resources.SessionResource;

import static org.assertj.core.api.Assertions.assertThat;

public class StringUtilTest {

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

  private void testReplacement(
      String body,
      String expected,
      String originalTerm,
      String replacementTerm,
      List<Integer> occurencesToReplace) {
    String replaced =
        StringUtil.replace(originalTerm, replacementTerm, body, occurencesToReplace, true);
    assertThat(replaced).isEqualTo(expected);
  }

  @Test
  public void testSerializeMap() {
    Map<String, String> map = Maps.newHashMap();
    map.put("key", "value");
    map.put("difficult", "áàçë");
    JSONSerializer serializer = new JSONSerializer();
    String mapJson = serializer.serialize(map);
    Map<String, String> map2 = new JSONDeserializer<Map<String, String>>().deserialize(mapJson);
    assertThat(map2.toString()).isEqualTo(map.toString());
  }

  // @Test
  public void testStringSize() {
    String string = "bláàt sçhapën";
    assertThat(string.length()).isEqualTo(13); // 9 normal + 4 utf-8 chars
    assertThat(string.getBytes().length).isEqualTo(17); // (9 + 2x4 bytes)
    assertThat(string.codePointCount(0, 13)).isEqualTo(13);
  }

  @Test
  public void testActivateURLs() {
    String string =
        "kijk ook eens op http://hier.daar.nl/ok en mail mailto:bram.buitendijk@huygens.nl .";
    String expected =
        "kijk ook eens op <a target=\"_blank\" href=\"http://hier.daar.nl/ok\">hier.daar.nl/ok</a> en mail <a target=\"_blank\" href=\"mailto:bram.buitendijk@huygens.nl\">bram.buitendijk@huygens.nl</a> .";
    assertThat(StringUtil.activateURLs(string)).isEqualTo(expected);
    assertThat(StringUtil.activateURLs(expected)).isEqualTo(expected);
  }

  @Test
  public void testActivateURLs2() {
    String string = "<span>http://daysmagazine.com/index.php/days-at-home/de-eenhoorn.html</span>";
    String expected =
        "<span><a target=\"_blank\" href=\"http://daysmagazine.com/index.php/days-at-home/de-eenhoorn.html\">daysmagazine.com/index.php/days-at-home/de-eenhoorn.html</a></span>";
    assertThat(StringUtil.activateURLs(string)).isEqualTo(expected);
    assertThat(StringUtil.activateURLs(expected)).isEqualTo(expected);
  }

  @Test
  public void testPageTitle() {
    assertThat(StringUtil.pageTitle()).isEqualTo("eLaborate3");
    assertThat(StringUtil.pageTitle("edit")).isEqualTo("eLaborate3 :: edit");
    assertThat(StringUtil.pageTitle("Torec", "Publish"))
        .isEqualTo("eLaborate3 :: Torec :: Publish");
  }

  @Test
  public void testEscapeQuotes() {
    assertThat(StringUtil.escapeQuotes("bla \"bla\" bla")).isEqualTo("bla \\\"bla\\\" bla");
    assertThat(StringUtil.escapeQuotes("single 'quotes'")).isEqualTo("single \\'quotes\\'");
  }

  @Test
  public void testFixXML() {
    String brokenxml = "<xml>a&b, bla &amp; co; 4 > 2 < 3; &gt;</xml>";
    String xml = StringUtil.fixXML(brokenxml);
    // Log.info(xml);
    XmlTestUtil.assertXmlIsWellFormed(xml);
    String expected = "<xml>a&amp;b, bla &amp; co; 4 &gt; 2 &lt; 3; &gt;</xml>";
    assertThat(xml).isEqualTo(expected);
  }

  @Test
  public void testFixXML2() {
    String brokenxml = "<body>a&b, bla &amp; co; 4 > 2 < 3; &gt;</body>";
    String expected = "a&amp;b, bla &amp; co; 4 &gt; 2 &lt; 3; &gt;";
    testHTML2XML(brokenxml, expected);
  }

  @Test
  public void testHtml2Xml1() {
    String html =
        "<body>&nbsp;bla <span id=9000316 class=annotationstart></span>x<span id=9000316 class=annotationend></span><br>whatever</body>";
    String expected =
        " bla <span id=\"9000316\" class=\"annotationstart\"></span>x<span id=\"9000316\" class=\"annotationend\"></span><br />whatever";
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
    assertThat(Boolean.valueOf("true")).isTrue();
    assertThat(Boolean.valueOf("false")).isFalse();
  }

  private void testHTML2XML(String html, String expected) {
    String xmlBody = StringUtil.html2xml(html);
    Log.info(xmlBody);
    XmlTestUtil.assertXmlIsWellFormed(wrap(xmlBody));
    assertThat(xmlBody).isEqualTo(expected);
  }

  private String wrap(String xml) {
    return "<xml>" + xml + "</xml>";
  }

  @Test
  public void test2() {
    String cutoffDate = new DateTime().minusDays(1).toString("YYYY-MM-dd HH:mm:ss");
    Log.info(cutoffDate);
    Log.info("{} ms", Hours.ONE.toStandardSeconds().getSeconds() * 1000);
  }

  @Test
  public void testURIBuilder()
      throws IllegalArgumentException, UriBuilderException, SecurityException,
          NoSuchMethodException {
    URI build = null;
    build =
        UriBuilder.fromResource(SessionResource.class)
            .scheme("http")
            .host("rest.elaborate.huygens.knaw.nl")
            .path(SessionResource.class.getMethod("logout", String.class))
            .build("TOKEN");
    Log.info("{}", build);
  }
}
