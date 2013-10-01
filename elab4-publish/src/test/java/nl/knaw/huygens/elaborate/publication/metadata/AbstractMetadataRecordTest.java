package nl.knaw.huygens.elaborate.publication.metadata;

import static org.junit.Assert.*;
import nl.knaw.huygens.LoggableObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AbstractMetadataRecordTest extends LoggableObject {

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testDCAsXML() throws Exception {
    DublinCoreRecord dc = new DublinCoreRecord()//
    .setType(DCMITypes.COLLECTION)//
    .setDate("now")//
    .setContributor("contributor");
    String xml = dc.asXML();
    LOG.info("xml={}", xml);
    assertTrue(xml.contains(">now<"));
    assertTrue(xml.contains(">Collection<"));
    assertTrue(xml.contains(">contributor<"));
  }

  @Test
  public void testCMDIAsXML() throws Exception {
    CmdiRecord dc = new CmdiRecord();
    String xml = dc.asXML();
    LOG.info("xml={}", xml);
    assertTrue(xml.contains("cmdi"));
  }
}
