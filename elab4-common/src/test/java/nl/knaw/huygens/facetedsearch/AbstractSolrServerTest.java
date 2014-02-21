package nl.knaw.huygens.facetedsearch;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import nl.knaw.huygens.LoggableObject;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class AbstractSolrServerTest extends LoggableObject {

	@Test
	public void testExtractTerms() throws Exception {
		String snippet1 = "bladie <em>bla</em> <em>bla1</em>.";
		String snippet2 = "bladie <em>bla</em> <em>bla2</em>.";
		Collection<String> extractTerms = AbstractSolrServer.extractTerms(ImmutableList.of(snippet1, snippet2));
		LOG.info("terms={}", extractTerms);
		assertThat(extractTerms).containsExactly("bla", "bla1", "bla", "bla2");
	}

}
