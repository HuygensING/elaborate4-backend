package elaborate.backend.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import nl.knaw.huygens.LoggableObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Elab4RestClientTest extends LoggableObject {
	private static Elab4RestClient e4;

	@Before
	public void before() {
		e4 = new Elab4RestClient("http://rest.elaborate.huygens.knaw.nl");
	}

	@After
	public void after() {
		e4 = null;
	}

	@Test
	public void testLoginFaila() throws Exception {
		boolean success = e4.login("bla", "boe");
		assertThat(success).isFalse();
	}

	@Test
	public void testLoginSucceeds() throws Exception {
		loginAsRoot();
	}

	@Test
	public void testVersion() throws Exception {
		Map<String, String> versionMap = e4.getVersion();
		LOG.info("{}", versionMap);
		assertThat(versionMap).containsKey("version");
	}

	@Test
	public void testGetProjectEntries() throws Exception {
		loginAsRoot();
		List<Map<String, Object>> entries = e4.getProjectEntries(1);
	}

	private void loginAsRoot() {
		boolean success = e4.login("root", "toor");
		assertThat(success).isTrue();
	}
}
