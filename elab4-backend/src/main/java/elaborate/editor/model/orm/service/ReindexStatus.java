package elaborate.editor.model.orm.service;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

public class ReindexStatus {

	public URI getURI() {
		try {
			return new URIBuilder().build();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
