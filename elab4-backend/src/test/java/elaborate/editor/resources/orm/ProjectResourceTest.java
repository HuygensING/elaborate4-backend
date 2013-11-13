package elaborate.editor.resources.orm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import elaborate.editor.model.orm.Project;
import elaborate.jaxrs.filters.AuthenticationResourceFilter;

public class ProjectResourceTest extends ResourceTest {
  private String authHeader;

  public ProjectResourceTest() {
    super();
  }

  @Before
  public void doLogin() {
    //    authHeader = login("root", "d3gelijk");
  }

  //  @Test
  public void test() {
    String string = resource()//
        .path("/projects")//
        .header(AuthenticationResourceFilter.HEADER, authHeader)//
        .get(String.class);
    assertThat(string).isEqualTo("[]");

    ClientResponse response = resource()//
        .path("/projects/1")//
        .header(AuthenticationResourceFilter.HEADER, authHeader)//
        .get(ClientResponse.class);
    assertThat(response.getStatus()).isEqualTo(404);
  }

  @Test
  public void testOptionsDoesntNeedAuthorization() {
    // options: no authorization required
    resource().path("/projects").method("OPTIONS");
    // get: authorization required
    try {
      resource().path("/projects").method("GET");
      fail("I was expecting an UnauthorizedException here.");
    } catch (UniformInterfaceException uie) {
      assertThat(uie.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
    }
  }

  //  @Test
  public void testGetAll() {
    ProjectResource projectResource = new ProjectResource();
    List<Project> all = projectResource.getAll();
    assertFalse(all.isEmpty());
  }

  /* */

  private String login(String username, String password) {
    MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
    formData.add("username", username);
    formData.add("password", password);
    ClientResponse loginResponse = resource()//
        .path("sessions/login")//
        .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)//
        .post(ClientResponse.class, formData);
    assertThat(loginResponse.getStatus()).isEqualTo(200);
    String json = loginResponse.getEntity(String.class);
    ObjectMapper mapper = new ObjectMapper();
    String token = null;
    try {
      Map<String, String> map = mapper.readValue(json, Map.class);
      token = map.get("token");
    } catch (JsonParseException e) {
      e.printStackTrace();
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return AuthenticationResourceFilter.SCHEME + " " + token;
  }

}
