package elaborate.editor.export.mvn;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2019 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


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
    MVNClient.webresource = client.resource(mvnServerBaseURL).path("editions");
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
