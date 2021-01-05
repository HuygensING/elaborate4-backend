package elaborate.editor.export.mvn;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2021 Huygens ING
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


import static org.assertj.core.api.Assertions.assertThat;

import com.sun.jersey.api.client.ClientResponse;

class MVNClientTest {

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
