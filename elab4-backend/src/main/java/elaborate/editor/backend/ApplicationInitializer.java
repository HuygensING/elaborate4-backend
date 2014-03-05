package elaborate.editor.backend;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import nl.knaw.huygens.LoggableObject;

public class ApplicationInitializer extends LoggableObject implements ServletContextListener {

	public ApplicationInitializer() {
		System.setProperty("application.starttime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		LOG.info("initializing context");
		LOG.info("serverinfo={}", sce.getServletContext().getServerInfo());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		LOG.info("destroying context");
	}

}
