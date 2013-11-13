package elaborate.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class XmlUtilTest {

  @Test
  public void testFixXhtml() {
    String badxml = "<interpGrp>not really bad</interpGrp>";
    String fixedXml = XmlUtil.fixXhtml(badxml);
    assertThat(fixedXml).isEqualTo(badxml.toLowerCase());
  }

  @Test
  public void testFixTagEndings() throws Exception {
    String in = "<tag>bla<bla>babal<tag>\n</tag></bla></tag>whatever";
    String fixed = "<tag>bla<bla>babal<tag></tag></bla></tag>\nwhatever";
    assertThat(XmlUtil.fixTagEndings(in)).isEqualTo(fixed);
  }

  @Test
  public void test() {
    assertThat(0).isEqualTo((40 % 5));
    assertTrue((41 % 5) != 0);
  }

  @Test
  public void testRemoveXMLtags() throws Exception {
    assertThat(XmlUtil.removeXMLtags("<body>kaal</body>")).isEqualTo("kaal");
    assertThat(XmlUtil.removeXMLtags("<1>aap\n <2>noot\n mies</2></1>")).isEqualTo("aap\n noot\n mies");
  }
}
