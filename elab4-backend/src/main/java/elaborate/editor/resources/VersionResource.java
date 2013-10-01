package elaborate.editor.resources;

import java.io.IOException;
import java.util.Map;
import java.util.PropertyResourceBundle;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nl.knaw.huygens.jaxrstools.resources.UTF8MediaType;

import com.google.common.collect.Maps;

import elaborate.editor.config.Configuration;
import elaborate.jaxrs.APIDesc;

@Path("version")
public class VersionResource extends AbstractElaborateResource {
  private static PropertyResourceBundle propertyResourceBundle;

  @GET
  @APIDesc("Get version info")
  @Produces(UTF8MediaType.APPLICATION_JSON)
  public Object getVersion() {
    Map<String, String> data = Maps.newHashMap();
    data.put("build", getProperty("build"));
    data.put("builddate", getProperty("builddate"));
    data.put("version", Configuration.instance().getStringSetting("version", "[undefined]"));
    return data;
  }

  private static synchronized String getProperty(String key) {
    if (propertyResourceBundle == null) {
      try {
        propertyResourceBundle = new PropertyResourceBundle(Thread.currentThread().getContextClassLoader().getResourceAsStream("version.properties"));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return propertyResourceBundle.getString(key);
  }

}
