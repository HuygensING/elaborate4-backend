package nl.knaw.huygens.elaborate.publication.metadata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ElaborateCMDIRecordBuilderTest {

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void test() {
    CMDIRecord cr = new ElaborateCMDIRecordBuilder().setMdCreator("me").setMdSelfLink("mdSelfLink").build();
    assertThat(cr.getMdCollectionDisplayName()).isEqualTo("Elaborate Editions");
  }

}
