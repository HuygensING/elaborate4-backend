package elaborate.editor.solr;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import elaborate.AbstractTest;

public class ElaborateSearchParametersTest extends AbstractTest {
  @Test
  public void testJson() throws JsonGenerationException, JsonMappingException, IOException {
    ElaborateSearchParameters e = new ElaborateSearchParameters();
    LOG.info(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(e));
  }
}
