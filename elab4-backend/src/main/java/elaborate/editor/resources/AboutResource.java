package elaborate.editor.resources;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2021 Huygens ING
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

import java.io.IOException;
import java.util.Map;
import java.util.PropertyResourceBundle;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.google.common.collect.Maps;

import nl.knaw.huygens.jaxrstools.resources.UTF8MediaType;

import elaborate.jaxrs.APIDesc;

@Path("about")
public class AboutResource extends AbstractElaborateResource {
  private static PropertyResourceBundle propertyResourceBundle;
  private static PropertyResourceBundle publicationPropertyResourceBundle;

  @GET
  @APIDesc("Get version info")
  @Produces(UTF8MediaType.APPLICATION_JSON)
  public Map<String, String> getAbout() {
    Map<String, String> data = Maps.newLinkedHashMap();
    data.put("version", getProperty("version"));
    data.put("commitId", getProperty("commitId"));
    data.put("scmBranch", getProperty("scmBranch"));
    data.put("buildDate", getProperty("buildDate"));
    data.put("publicationBackendBuild", getPublicationProperty("commitId"));
    data.put("publicationBackendBuildDate", getPublicationProperty("buildDate"));
    data.put("startTime", System.getProperty("application.starttime"));
    return data;
  }

  private static synchronized String getProperty(String key) {
    if (propertyResourceBundle == null) {
      try {
        propertyResourceBundle =
            new PropertyResourceBundle( //
                Thread.currentThread()
                    .getContextClassLoader() //
                    .getResourceAsStream("about.properties") //
                );
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return propertyResourceBundle.getString(key);
  }

  private static synchronized String getPublicationProperty(String key) {
    if (publicationPropertyResourceBundle == null) {
      try {
        publicationPropertyResourceBundle =
            new PropertyResourceBundle( //
                Thread.currentThread()
                    .getContextClassLoader() //
                    .getResourceAsStream("publication/WEB-INF/classes/about.properties") //
                );
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return publicationPropertyResourceBundle.getString(key);
  }
}
