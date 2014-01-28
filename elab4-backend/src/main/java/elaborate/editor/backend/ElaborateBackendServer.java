package elaborate.editor.backend;

import java.io.IOException;
import java.net.URI;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.core.UriBuilder;

import nl.knaw.huygens.jaxrstools.filters.CORSFilter;

import org.glassfish.grizzly.http.server.HttpServer;
import org.joda.time.Hours;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.container.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import elaborate.editor.config.Configuration;
import elaborate.editor.model.ElaborateRoles;
import elaborate.editor.model.SessionService;
import elaborate.editor.model.orm.service.SearchService;
import elaborate.jaxrs.filters.ElaborateResourceFilterFactory;

@DeclareRoles({ ElaborateRoles.READER, ElaborateRoles.USER, ElaborateRoles.PROJECTLEADER, ElaborateRoles.ADMIN })
public class ElaborateBackendServer {
	private static final int ONE_HOUR = Hours.ONE.toStandardSeconds().getSeconds() * 1000;
	private static final Logger LOG = LoggerFactory.getLogger(ElaborateBackendServer.class);

	public static void main(String[] args) throws IOException {
		//    fill();
		//    System.out.println(ElaborateBackendServer.class.getClassLoader().getResource("logging.properties"));
		final HttpServer httpServer = startServer();
		LOG.info(String.format("Jersey app started with WADL available at %s/application.wadl\n", getBaseURI()));

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				LOG.info("Stopping HTTP server");
				httpServer.stop();
			}
		}));

		SessionService sessionService = SessionService.instance();
		SearchService searchService = SearchService.instance();
		while (true) {
			try {
				LOG.info("removing expired Sessions");
				sessionService.removeExpiredSessions();
				LOG.info("removing expired Searches");
				searchService.removeExpiredSearches();
				Thread.sleep(ONE_HOUR);
			} catch (InterruptedException e) {
				LOG.info("Stopping HTTP server");
				httpServer.stop();
			}
		}
	}

	private static URI getBaseURI() {
		Configuration config = Configuration.instance();
		return UriBuilder//
				.fromUri("")//
				.scheme(config.getStringSetting("server.scheme", "http"))//
				.host(config.getStringSetting("server.name", "127.0.0.1"))//
				.port(config.getIntegerSetting("server.port", 9998))//
				.build();
	}

	@SuppressWarnings("unchecked")
	protected static HttpServer startServer() throws IOException {
		LOG.info("Starting grizzly...");
		ResourceConfig rc = new PackagesResourceConfig("nl.knaw.huygens.jaxrstools.resources", "nl.knaw.huygens.jaxrstools.providers", "elaborate.editor.resources", "elaborate.editor.testresources", "elaborate.editor.providers"/*, "nl.knaw.huygens.security.client.filters"*/);

		rc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

		//    rc.getProperties().put(ResourceConfig.PROPERTY_WADL_GENERATOR_CONFIG, "elaborate.jaxrs.CustomWadlGeneratorConfig");

		rc.getContainerRequestFilters().add(new GZIPContentEncodingFilter());

		rc.getContainerResponseFilters().add(new GZIPContentEncodingFilter());
		rc.getContainerResponseFilters().add(new CORSFilter());

		//    rc.getResourceFilterFactories().add(new RolesAllowedResourceFilterFactory());
		rc.getResourceFilterFactories().add(new ElaborateResourceFilterFactory());

		return GrizzlyServerFactory.createHttpServer(getBaseURI(), rc);
	}
}
