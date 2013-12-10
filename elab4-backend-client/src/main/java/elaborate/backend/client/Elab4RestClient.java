package elaborate.backend.client;

import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.jackson.JacksonFeature;

public class Elab4RestClient {
	private final WebTarget sessionsTarget;
	private final WebTarget projectsTarget;
	private final WebTarget elab4;
	private String token;

	public Elab4RestClient(String baseurl) {
		Client client = ClientBuilder.newClient().register(JacksonFeature.class);
		elab4 = client.target(baseurl);
		sessionsTarget = elab4.path("sessions");
		projectsTarget = elab4.path("projects");
	}

	public boolean login(String username, String password) {
		token = null;
		Form form = new Form().param("username", username).param("password", password);
		Response response = sessionsTarget.path("login")//
				.request(MediaType.APPLICATION_JSON)//
				.post(Entity.form(form));

		boolean success = (response.getStatus() == Status.OK.getStatusCode());
		if (success) {
			Map<String, Object> map = response.readEntity(Map.class);
			token = (String) map.get("token");
		}

		return success;
	}

	public Map<String, String> getVersion() {
		Map<String, String> version = elab4.path("version").request().get(Map.class);
		return version;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getProjectEntries(int i) {
		List<Map<String, Object>> list = projectsTarget.path(String.valueOf(i)).path("entries")//
				.request()//
				.header("Authorization", "SimpleAuth " + token)//
				.get(List.class);
		return list;
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getProjectEntryMetadata(int projectId, int entryId) {
		Map<String, String> map = projectsTarget.path(String.valueOf(projectId)).path("entries").path(String.valueOf(entryId)).path("settings")//
				.request()//
				.header("Authorization", "SimpleAuth " + token)//
				.get(Map.class);
		return map;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getProjectEntryTextLayers(int projectId, int entryId) {
		List<Map<String, String>> map = projectsTarget.path(String.valueOf(projectId)).path("entries").path(String.valueOf(entryId)).path("transcriptions")//
				.request()//
				.header("Authorization", "SimpleAuth " + token)//
				.get(List.class);
		return map;
	}
}
