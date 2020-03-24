package elaborate.editor.backend;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2020 Huygens ING
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

import com.google.common.collect.ImmutableList;

import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.service.SearchService;
import elaborate.editor.model.orm.service.UserService;
import nl.knaw.huygens.Log;

public class ApplicationInitializer implements ServletContextListener {

	public ApplicationInitializer() {
		System.setProperty("application.starttime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

		Log.info("clearing expired searches");
		SearchService searchService = SearchService.instance();
		searchService.removeExpiredSearches();

		Log.info("logging out all users");
		UserService userService = UserService.instance();
		ImmutableList<User> all = userService.getAll();
		for (User user : all) {
			userService.setUserIsLoggedOut(user);
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		Log.info("initializing context");
		Log.info("serverinfo={}", sce.getServletContext().getServerInfo());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Log.info("destroying context");
	}

}
