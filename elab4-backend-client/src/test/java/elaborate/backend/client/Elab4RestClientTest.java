package elaborate.backend.client;

/*
 * #%L
 * elab4-backend-client
 * =======
 * Copyright (C) 2013 - 2022 Huygens ING
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

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;

import nl.knaw.huygens.Log;

import static org.assertj.core.api.Assertions.assertThat;

class Elab4RestClientTest {
  private static Elab4RestClient e4;

  @Before
  public void before() {
    e4 = new Elab4RestClient("http://localhost:2013");
  }

  @After
  public void after() {
    e4 = null;
  }

  //  @Test
  public void testAddProject() {
    Log.info("logging in...");
    loginAsRoot();
    Log.info("add project...");
    Integer projectId = e4.addProject("Hattem, MV : C5");
    Log.info("projectId={}", projectId);
    Log.info("delete project...");
    Boolean deleted = e4.deleteProject(projectId);
    assertThat(deleted).isTrue();
  }

  //	@Test
  public void testLoginFaila() {
    boolean success = e4.login("bla", "boe");
    assertThat(success).isFalse();
  }

  //	@Test
  public void testLoginSucceeds() {
    loginAsRoot();
  }

  //	@Test
  public void testVersion() {
    Map<String, String> versionMap = e4.getAbout();
    Log.info("{}", versionMap);
    assertThat(versionMap).containsKey("version");
  }

  //	@Test
  public void testGetProjectEntries() {
    loginAsRoot();
    e4.getProjectEntries(1);
  }

  //	@Test
  public void testCNWPagebreakFix() {
    loginAsRoot();
    int projectId = 44; // CNW
    List<Map<String, Object>> transcriptionMaps = e4.getProjectEntryTextLayers(projectId, 24857);
    for (Map<String, Object> transcriptionMap : transcriptionMaps) {
      int id = (Integer) transcriptionMap.get("id");
      String body = (String) transcriptionMap.get("body");
      Log.info("{}: {}", id, body);
    }
  }

  private void loginAsRoot() {
    boolean success = e4.login("root", System.getProperty("ROOT_PW"));
    assertThat(success).isTrue();
  }
}
