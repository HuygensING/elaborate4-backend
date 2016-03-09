package elaborate.editor.export.mvn;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import nl.knaw.huygens.Log;

public class MVNClient {
  //  private static final String mvnserverBaseURL; //Configuration.instance().getSetting(Configuration.MVN_SERVER_URL);
  private static final Client client = Client.create();
  private static WebResource webresource;

  public MVNClient(String mvnServerBaseURL) {
    String path = mvnServerBaseURL;
    MVNClient.webresource = client.resource(path).path("editions");
    client.setFollowRedirects(true);
  }

  public ClientResponse putTEI(String sigle, String tei) {
    WebResource editionResource = webresource.path(sigle);
    Log.info("PUT tei to {}", editionResource.getURI());
    ClientResponse response = editionResource.type(MediaType.TEXT_XML).put(ClientResponse.class, tei);
    Log.info("response={}", response);
    return response;
  }

}
