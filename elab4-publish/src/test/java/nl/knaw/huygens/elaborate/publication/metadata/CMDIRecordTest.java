package nl.knaw.huygens.elaborate.publication.metadata;

import static org.junit.Assert.*;
import nl.knaw.huygens.LoggableObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CMDIRecordTest extends LoggableObject {

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testBuild1() throws Exception {
    try {
      CMDIRecord cr = new CMDIRecord.Builder().build();
    } catch (Exception e) {
      assertEquals("invalid CMDIRecord: set MdCreator; set MdProfile; set MdSelfLink", e.getMessage());
    }
  }

  @Test
  public void testBuild2() throws Exception {
    try {
      CMDIRecord cr = new CMDIRecord.Builder().setMdCreator("mdCreator").build();
    } catch (Exception e) {
      assertEquals("invalid CMDIRecord: set MdProfile; set MdSelfLink", e.getMessage());
    }
  }

  @Test
  public void testBuild3() throws Exception {
    CMDIRecord cr = new CMDIRecord.Builder()//
    .setMdCreator("mdCreator")//
    .setMdProfile("profile")//
    .setMdSelfLink("uri")//
    .setMdCollectionDisplayName("displayname")//
    .build();
    LOG.info("CMDI=", cr);
  }

}
