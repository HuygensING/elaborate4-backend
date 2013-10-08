package nl.knaw.huygens.elaborate.publication.metadata;

import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractMetadataRecordTest extends XMLTestCase {
  Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Override
  @Before
  public void setUp() throws Exception {}

  @Override
  @After
  public void tearDown() throws Exception {}

  //  @Test
  //  public void testDCAsXML() throws Exception {
  //    DublinCoreRecord dc = new DublinCoreRecord()//
  //    .setType(DCMITypes.COLLECTION)//
  //    .setDate("now")//
  //    .setContributor("contributor");
  //    String xml = dc.asXML();
  //    LOG.info("xml={}", xml);
  //    //    assertXpathExists("/dc/date", xml);
  //    //    assertXpathExists("/oai:dc/dc:date", xml);
  //    //    assertXpathEvaluatesTo("now", "/oai:dc/dc:date", xml);
  //    //    assertXpathEvaluatesTo("Collection", "/oai:dc/dc:type", xml);
  //    //    assertXpathEvaluatesTo("contributor", "/oai:dc/dc:contributor", xml);
  //    //    assertXMLValid(xml);
  //    assertTrue(xml.contains(">now<"));
  //    assertTrue(xml.contains(">Collection<"));
  //    assertTrue(xml.contains(">contributor<"));
  //  }

  @Test
  public void testCMDIAsXML() throws Exception {
    CmdiRecord dc = new CmdiRecord();
    String xml = dc.asXML();
    LOG.info("xml={}", xml);
    //    assertXMLValid(xml);
    assertTrue(xml.contains("cmdi"));
  }
}
