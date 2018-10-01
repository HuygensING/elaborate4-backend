package elaborate.util;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2018 Huygens ING
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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class XmlUtilTest {

  @Test
  public void testFixXhtml() {
    String badxml = "<interpGrp>not really bad</interpGrp>";
    String fixedXml = XmlUtil.fixXhtml(badxml);
    assertThat(fixedXml).isEqualTo(badxml.toLowerCase());
  }

  @Test
  public void testFixXhtmlPreserved160() {
    String xml = "<i>&nbsp;&#160;&nbsp;</i>";
    String fixedXml = XmlUtil.fixXhtml(xml);
    assertThat(fixedXml).isEqualTo("<i>&#160;&#160;&#160;</i>");
  }

  @Test
  public void testFixSupSub() {
    String supsubxml = "<sup>SUPER</sup> normaal <sub>sub</sub> <sup>SUPER</sup> normaal <sub>sub</sub>";
    String fixedXml = XmlUtil.fixXhtml(supsubxml);
    assertThat(fixedXml).isEqualTo(supsubxml);
  }

  @Test
  public void testFixTagEndings() throws Exception {
    String in = "<tag>bla<bla>babal<tag>\n</tag></bla></tag>whatever";
    String fixed = "<tag>bla<bla>babal<tag></tag></bla></tag>\nwhatever";
    assertThat(XmlUtil.fixTagEndings(in)).isEqualTo(fixed);
  }

  @Test
  public void test() {
    assertThat(40 % 5).isEqualTo(0);
    assertThat(41 % 5).isNotEqualTo(0);
  }

  @Test
  public void testRemoveXMLtags() throws Exception {
    assertThat(XmlUtil.removeXMLtags("<body>kaal</body>")).isEqualTo("kaal");
    assertThat(XmlUtil.removeXMLtags("<1>aap\n <2>noot\n mies</2></1>")).isEqualTo("aap\n noot\n mies");
  }

  @Test
  public void testToPlainTextReplacesXmlStuff() {
    assertThat(XmlUtil.toPlainText("<b>bold</b> &apos;t <i>kofschip</i><br>&nbsp;&quot;blabla&quot;")).isEqualTo("bold 't kofschip\n \"blabla\"");
  }

  @Test
  public void testToSimpleHTMLPreservesSimpleTagsRemovesOthers() {
    String body = "<span><b>bold</b> &apos;t <i>kofschip</i><br>&nbsp;&quot;blabla&quot;</span>";
    String expected = "<strong>bold</strong> &apos;t <em>kofschip</em><br/>&nbsp;&quot;blabla&quot;";
    assertThat(XmlUtil.toSimpleHTML(body)).isEqualTo(expected);
  }

  @Test
  public void testToSimpleHTMLPreservesUnderline() {
    String body = "<span style=\"text-decoration: underline;\">I</span>b<span style=\"text-decoration: underline;\">i</span> or <u>underline</u>";
    String expected = "<u>I</u>b<u>i</u> or <u>underline</u>";
    assertThat(XmlUtil.toSimpleHTML(body)).isEqualTo(expected);
  }

  @Test
  public void testToSimpleHTMLPreservesSupAndSub() {
    String body = "<sub>subscript</sub> en <sup>superscript</sup>";
    assertThat(XmlUtil.toSimpleHTML(body)).isEqualTo(body);
  }

  @Test
  public void testToSimpleHTMLNormalizesBoldToStrong() {
    String body = "<b>bold</b>, <span style=\"font-weight: bold;\">bold span</span> and <strong>strong</strong>";
    String expected = "<strong>bold</strong>, <strong>bold span</strong> and <strong>strong</strong>";
    assertThat(XmlUtil.toSimpleHTML(body)).isEqualTo(expected);
  }

  @Test
  public void testToSimpleHTMLNormalizesItalicsToEm() {
    String body = "<i>italic</i>, <span style=\"font-style: italic;\">italic span</span> and <em>emphasized</em>";
    String expected = "<em>italic</em>, <em>italic span</em> and <em>emphasized</em>";
    assertThat(XmlUtil.toSimpleHTML(body)).isEqualTo(expected);
  }

  @Test
  public void testToSimpleHTMLNormalizesBreaks() {
    String body = "line 1<br>line 2<br/>line 3";
    String expected = "line 1<br/>line 2<br/>line 3";
    assertThat(XmlUtil.toSimpleHTML(body)).isEqualTo(expected);
  }

  //  @Ignore
  //  @Test
  //  public void testFixTagHierarchy() {
  //    String body = "<ab id=\"9096396\"/><ab id=\"9096397\"/>W<ae id=\"9096396\"/>iē<ae id=\"9096397\"/>";
  //    String expected = "<ab id=\"9096397\"/><ab id=\"9096396\"/>W<ae id=\"9096396\"/>iē<ae id=\"9096397\"/>";
  //    String fixed = XmlUtil.fixTagHierarchy(body);
  //    assertThat(fixed).isEqualTo(expected);
  //  }

  //  @Test
  //  public void testFixTagHierarchy2() {
  //    String body = "0<hi>1<em>2</hi>3</em>4";
  //    String expected = "0<hi>1<em>2</em></hi><em>3</em>4";
  //    String fixed = XmlUtil.fixTagHierarchy(body);
  //    assertThat(fixed).isEqualTo(expected);
  //  }
  // @Test
  // public void testSelect() {
  // ProjectService ps = ProjectService.instance();
  // ps.openEntityManager();try{
  // EntityManager em = ps.getEntityManager();
  // String textlayer = "Translation";
  // Project project = new Project().setId(1l);
  // List resultList = em//
  // .createQuery("select e.id, t.id from ProjectEntry e join e.transcriptions as t where e.project=:project and t.text_layer=:textlayer")//
  // .setParameter("project", project)//
  // .setParameter("textlayer", textlayer)//
  // .getResultList();
  // Log.info("result={}", resultList);
  // for (Object object : resultList) {
  // Object[] objects = (Object[]) object;
  // Log.info("id[0]={}", objects[0]);
  // Log.info("id[1]={}", objects[1]);
  // }
  // ps.}finally{closeEntityManager();}
  // }
}
