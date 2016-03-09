package elaborate.editor.export.mvn;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.jersey.api.client.ClientResponse;

public class MVNClientTest {

  //  @Test
  public void testMVNClient() {
    MVNClient c = new MVNClient("http://localhost:2222");
    ClientResponse response = c.putTEI("TEST",
        "<MVN><text xml:id=\"TEST\"><body>"//
            + "<group><text n=\"1\" xml:id=\"BS1\"><body>"//
            + "<pb facs=\"1r.jpg\" n=\"1r\" xml:id=\"BSf1r\"/>"//
            + "<lb n=\"1\" xml:id=\"TEST-lb-1\"/>this is just a test"//
            + "</body></text></group>"//
            + "</body></text></MVN>");
    assertThat(response.getClientResponseStatus()).isEqualTo(ClientResponse.Status.CREATED);
  }

}
