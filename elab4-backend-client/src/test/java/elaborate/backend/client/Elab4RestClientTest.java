package elaborate.backend.client;

/*
 * #%L
 * elab4-backend-client
 * =======
 * Copyright (C) 2013 - 2014 Huygens ING
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
