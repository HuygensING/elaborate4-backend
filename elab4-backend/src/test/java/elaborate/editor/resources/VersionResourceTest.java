package elaborate.editor.resources;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.Test;

public class VersionResourceTest {

	@Test
	public void testGetVersion() throws Exception {
		VersionResource vr = new VersionResource();
		Map<String, String> version = vr.getVersion();
		assertThat(version).containsKey("publication_backend_build");
	}

}
