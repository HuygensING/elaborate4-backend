package elaborate.publication.resources;

/*
 * #%L
 * elab4-publication-backend
 * =======
 * Copyright (C) 2013 - 2019 Huygens ING
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

@Path("about")
public class AboutResource {
  private static PropertyResourceBundle propertyResourceBundle;

  @GET
  //  @APIDesc("Get version info")
  @Produces(UTF8MediaType.APPLICATION_JSON)
  public Object getVersion() {
    Map<String, String> data = Maps.newHashMap();
    for (String field : new String[] { "commitId", "buildDate", "version", "scmBranch", "publishdate" }) {
      data.put("commitId", getProperty(field));
    }
    return data;
  }

  private static synchronized String getProperty(String key) {
    if (propertyResourceBundle == null) {
      try {
        propertyResourceBundle = new PropertyResourceBundle(Thread.currentThread().getContextClassLoader().getResourceAsStream("about.properties"));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return propertyResourceBundle.getString(key);
  }

}
