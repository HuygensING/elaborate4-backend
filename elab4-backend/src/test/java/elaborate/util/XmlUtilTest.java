package elaborate.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class XmlUtilTest {

  @Test
  public void testFixXhtml() {
    String badxml = "<interpGrp>not really bad</interpGrp>";
    String fixedXml = XmlUtil.fixXhtml(badxml);
    assertEquals(badxml.toLowerCase(), fixedXml);
  }

  @Test
  public void testFixTagEndings() throws Exception {
    String in = "<tag>bla<bla>babal<tag>\n</tag></bla></tag>whatever";
    String fixed = "<tag>bla<bla>babal<tag></tag></bla></tag>\nwhatever";
    assertEquals(fixed, XmlUtil.fixTagEndings(in));
  }

  @Test
  public void test() {
    assertEquals((40 % 5), 0);
    assertTrue((41 % 5) != 0);
  }

  @Test
  public void testRemoveXMLtags() throws Exception {
    assertEquals("kaal", XmlUtil.removeXMLtags("<body>kaal</body>"));
    assertEquals("aap\n noot\n mies", XmlUtil.removeXMLtags("<1>aap\n <2>noot\n mies</2></1>"));
  }
}
