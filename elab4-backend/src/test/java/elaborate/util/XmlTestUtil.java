package elaborate.util;

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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlTestUtil {
  static Logger LOG = LoggerFactory.getLogger(XmlTestUtil.class);

  public static void assertXmlIsWellFormed(String xml) {
    DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    try {
      builder = dBF.newDocumentBuilder();
      InputSource is = new InputSource(new StringReader(xml));
      Document doc = builder.parse(is);
      assertThat(doc).isNotNull();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    } catch (SAXException e) {
      LOG.info("xml={}", xml);
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testFixXml() {
    String in = "<xml><i>blâ <b>&</i> co</b><ok/> &cie <.> ok</xml>";
    String expected = "<xml><i>blâ <b>&amp;</b></i><b> co</b><ok/> &amp;cie &lt;.&gt; ok</xml>";
    assertThat(XmlUtil.fixXhtml(in)).isEqualTo(expected);
  }

  //  @Test
  //  public void test() {
  //    String in = "<body>01 En<i>de</i> hine w@d sond<i>er</i> sage\n 02 Noit moede in dien dage\n 03 M@ sijn wesen gedurde tier vre\n 04 <ab id=\"81467\"/>Tot<i>er</i><ae id=\"81467\"/> vesp<i>er</i>tijt alde<i>n</i> dach dure\n 05 En<i>de</i> soude hebben geduert alsoe</body>";
  //    String expected = in;
  //    assertThat( "\n")).isEqualTo(expected, in.replaceAll("\\n\\s+[0-9][0-9]\\s+");
  //  }

}
