package elaborate.editor.config;

public class ConfigLocation {
  private static String CONFIG_XML;
  private static final String DEFAULT = "default";
  private static final String FILENAME = "config/config.xml";

  private ConfigLocation() {
    throw new AssertionError("Non-instantiable class");
  }

  public static String instance() {
    try {
      return createInstance(DEFAULT);
    } catch (IllegalStateException e) {
      return CONFIG_XML;
    }
  }

  public static String createInstance(String instance) throws IllegalStateException {
    if (CONFIG_XML == null) {
      CONFIG_XML = (instance.equals(DEFAULT) ? FILENAME : "instances/" + instance + "/" + FILENAME);
    } else {
      throw new IllegalStateException();
    }
    return CONFIG_XML;
  }

}
