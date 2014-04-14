package elaborate.editor.backend;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2014 Huygens ING
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
