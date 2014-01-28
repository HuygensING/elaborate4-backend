package elaborate.util;

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
