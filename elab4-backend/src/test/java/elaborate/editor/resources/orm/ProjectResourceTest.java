package elaborate.editor.resources.orm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

import elaborate.editor.model.orm.Project;
import elaborate.jaxrs.filters.AuthenticationResourceFilter;

@Ignore
public class ProjectResourceTest extends ResourceTest {
	private String authHeader;

	public ProjectResourceTest() {
		super();
	}

	@Before
	public void doLogin() {
		//		authHeader = login("root", "ccccc");
	}

	@Test
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
		assertThat(all).isNotEmpty();
	}

	/* */

}
