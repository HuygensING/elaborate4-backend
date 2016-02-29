package elaborate.backend.client;

/*
 * #%L
 * elab4-backend-client
 * =======
 * Copyright (C) 2013 - 2016 Huygens ING
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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.jackson.JacksonFeature;

import jersey.repackaged.com.google.common.collect.Maps;
import nl.knaw.huygens.Log;

public class Elab4RestClient {
  private final WebTarget sessionsTarget;
  private final WebTarget projectsTarget;
  private final WebTarget elab4;
  private String token;

  public Elab4RestClient(String baseurl) {
    Client client = ClientBuilder.newClient().register(JacksonFeature.class);
    elab4 = client.target(baseurl);
    sessionsTarget = elab4.path("sessions");
    projectsTarget = elab4.path("projects");
  }

  public boolean login(String username, String password) {
    token = null;
    Form form = new Form().param("username", username).param("password", password);
    Response response = sessionsTarget.path("login")//
        .request(MediaType.APPLICATION_JSON)//
        .post(Entity.form(form));

    boolean success = (response.getStatus() == Status.OK.getStatusCode() || response.getStatus() == Status.FOUND.getStatusCode());
    if (success) {
      Map<String, Object> map = response.readEntity(Map.class);
      token = (String) map.get("token");
    }

    return success;
  }

  public Map<String, String> getAbout() {
    Map<String, String> about = elab4.path("about").request().get(Map.class);
    return about;
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getProjectEntries(int i) {
    List<Map<String, Object>> list = projectsTarget.path(String.valueOf(i)).path("entries")//
        .request()//
        .header("Authorization", "SimpleAuth " + token)//
        .get(List.class);
    return list;
  }

  @SuppressWarnings("unchecked")
  public Map<String, String> getProjectEntryMetadata(long projectId, long entryId) {
    Map<String, String> map = projectsTarget.path(String.valueOf(projectId)).path("entries").path(String.valueOf(entryId)).path("settings")//
        .request()//
        .header("Authorization", "SimpleAuth " + token)//
        .get(Map.class);
    Map<String, String> map2 = projectsTarget.path(String.valueOf(projectId)).path("entries").path(String.valueOf(entryId))//
        .request()//
        .header("Authorization", "SimpleAuth " + token)//
        .get(Map.class);
    map.put("entryname", map2.get("name"));
    return map;
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, String>> getProjectEntryTextLayers(long projectId, long entryId) {
    List<Map<String, String>> map = projectsTarget.path(String.valueOf(projectId)).path("entries").path(String.valueOf(entryId)).path("transcriptions")//
        .request()//
        .header("Authorization", "SimpleAuth " + token)//
        .get(List.class);
    return map;
  }

  public void setProjectEntryTextLayerBody(int projectId, int entryId, int transcriptionId, String newBody) {
    Map<String, Object> transcription = Maps.newHashMap();
    transcription.put("body", newBody);
    Entity<?> entity = Entity.entity(transcription, MediaType.APPLICATION_JSON);
    Response response = projectsTarget.path(String.valueOf(projectId))//
        .path("entries").path(String.valueOf(entryId))//
        .path("transcriptions").path(String.valueOf(transcriptionId))//
        .request(MediaType.APPLICATION_JSON)//
        .header("Authorization", "SimpleAuth " + token)//
        .put(entity);
    Log.info("response.status={}", response.getStatus());
  }

  public Integer addProject(String projectTitle) {
    Map<String, Object> payload = Maps.newHashMap();
    payload.put("title", projectTitle);
    Entity<?> entity = Entity.entity(payload, MediaType.APPLICATION_JSON);
    Response response = projectsTarget//
        .request(MediaType.APPLICATION_JSON)//
        .header("Authorization", "SimpleAuth " + token)//
        .post(entity);
    Log.info("response = {}", response);
    String location = response.getHeaderString("Location");
    Integer projectId = Integer.valueOf(location.replaceFirst("^.*/", ""));
    return projectId;
  }

  public Boolean deleteProject(int projectId) {
    Response response = projectsTarget//
        .path(String.valueOf(projectId))//
        .request()//
        .header("Authorization", "SimpleAuth " + token)//
        .delete();
    return response.getStatus() == 204;
  }

}
