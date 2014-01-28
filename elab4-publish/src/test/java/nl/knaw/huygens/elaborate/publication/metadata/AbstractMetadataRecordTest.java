package nl.knaw.huygens.elaborate.publication.metadata;

import static org.assertj.core.api.Assertions.assertThat;
import nl.knaw.huygens.LoggableObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractMetadataRecordTest extends LoggableObject {
  Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  //  @Test
  public void testDCAsXML() throws Exception {
    DublinCoreRecord dc = new DublinCoreRecord()//
        .setType(DCMITypes.COLLECTION)//
        .setDate("now")//
        .setContributor("contributor");
    String xml = dc.asXML();
    LOG.info("xml={}", xml);
    //    assertXpathExists("/dc/date", xml);
    //    assertXpathExists("/oai:dc/dc:date", xml);
    //    assertXpathEvaluatesTo("now", "/oai:dc/dc:date", xml);
    //    assertXpathEvaluatesTo("Collection", "/oai:dc/dc:type", xml);
    //    assertXpathEvaluatesTo("contributor", "/oai:dc/dc:contributor", xml);
    //    assertXMLValid(xml);
    assertThat(xml).contains(">now<");
    assertThat(xml).contains(">Collection<");
    assertThat(xml).contains(">contributor<");
  }

  @Test
  public void testCMDIAsXML() throws Exception {
    CMDIRecord dc = new ElaborateCMDIRecordBuilder().setMdCreator("creator").setMdSelfLink("mdSelfLink").build();
    String xml = dc.asXML();
    LOG.info("xml={}", xml);
    //    assertXMLValid(xml);
    assertThat(xml).contains("cmdi");
  }
}
