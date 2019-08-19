package elaborate.editor.config;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2019 Huygens ING
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.io.FileUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

public class Configuration {
  private static final String SOLR_SORT_FIELDS = "solr.sort_fields";
  public static final String MVN_SERVER_URL = "mvn.server_url";

  private static final String CLASS_NAME = Configuration.class.getName();

  private static final String CONFIG_XML = ConfigLocation.instance();
  private static final String SETTINGS_PREFIX = "settings.";

  private static Map<String, String> messages;
  private static Map<String, String> renditions;

  private static final Configuration instance = new Configuration();
  private static XMLConfiguration xmlConfig = instance.load(createConfigReader());

  private Configuration() {}

  public static synchronized Configuration instance() {
    return instance;
  }

  private static void fatalError(String message) {
    System.err.printf("## %s\n", message);
    // System.exit(-1);
    throw new RuntimeException(message);
  }

  public void reload() {
    xmlConfig = load(createConfigReader());
  }

  private XMLConfiguration load(Reader reader) {
    try {
      messages = Maps.newTreeMap();
      AbstractConfiguration.setDefaultListDelimiter(',');
      XMLConfiguration config = new XMLConfiguration();
      config.clear();
      config.load(reader);
      processConfiguration(config);
      xmlConfig = config;
      return config;
    } catch (ConfigurationException e) {
      throw new RuntimeException("Failed to load configuration", e);
    }
  }

  private static Reader createConfigReader() {
    try {
      File file = new File(CONFIG_XML);
      System.out.println(CLASS_NAME + " - Loading configuration from " + file.getCanonicalPath());
      if (!file.isFile()) {
        // try resourceAsStream
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.xml");
        if (resourceAsStream == null) {
          fatalError("Cannot access configuration file '" + file.getCanonicalPath() + "'");
        }
        return new InputStreamReader(resourceAsStream);
      }
      String content = FileUtils.readFileToString(file, "UTF-8");
      return new StringReader(content);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load configuration", e);
    }
  }

  private static void processConfiguration(HierarchicalConfiguration h_config) {
    renditions = Maps.newTreeMap();
    HierarchicalConfiguration config = configurationAt(h_config, "renditions");
    if (config != null) {
      extractRenditions(config);
    }
    messages = Maps.newTreeMap();
    config = configurationAt(h_config, "messages");
    if (config != null) {
      int n = config.getMaxIndex("group");
      for (int i = 0; i <= n; i++) {
        String groupId = config.getString("group(" + i + ")[@id]");
        if (groupId != null) {
          HierarchicalConfiguration group = config.configurationAt("group(" + i + ")");
          extractMessages(groupId, group);
        }
      }
    }
  }

  private static void extractMessages(String parentId, HierarchicalConfiguration config) {
    int n = config.getMaxIndex("message");
    for (int i = 0; i <= n; i++) {
      String messageId = config.getString("message(" + i + ")[@id]");
      if (messageId != null) {
        String message = config.getString("message(" + i + ")");
        if (message != null) {
          message = message.replaceAll("\\{", "<").replaceAll("}", ">");
        }
        messages.put(parentId + "." + messageId, message);
      }
    }
  }

  private static void extractRenditions(HierarchicalConfiguration config) {
    int n = config.getMaxIndex("rendition");
    for (int i = 0; i <= n; i++) {
      String messageId = config.getString("rendition(" + i + ")[@id]");
      if (messageId != null) {
        String message = config.getString("rendition(" + i + ")");
        renditions.put(messageId, message);
      }
    }
  }

  private static HierarchicalConfiguration configurationAt(HierarchicalConfiguration config, String key) {
    List<HierarchicalConfiguration> list = config.configurationsAt(key);
    if (list.size() == 1) {
      return list.get(0);
    }

    System.out.printf("Configurations with key '%s': %d\n", key, list.size());
    return null;
  }

  public HierarchicalConfiguration configurationAt(String key) {
    return configurationAt(xmlConfig, key);
  }

  public String getSetting(String key) {
    return Joiner.on(xmlConfig.getListDelimiter()).join(xmlConfig.getStringArray(SETTINGS_PREFIX + key));
  }

  private String[] getSettings(String key) {
    // Log.info("'{}'", xmlConfig.getListDelimiter());
    // Log.info("'{}'", AbstractConfiguration.getDefaultListDelimiter());
    return xmlConfig.getStringArray(SETTINGS_PREFIX + key);
  }

  public String getStringSetting(String key, String defaultValue) {
    return xmlConfig.getString(SETTINGS_PREFIX + key, defaultValue);
  }

  public boolean getBooleanSetting(String key, boolean defaultValue) {
    return xmlConfig.getBoolean(SETTINGS_PREFIX + key, defaultValue);
  }

  public int getIntSetting(String key, int defaultValue) {
    return xmlConfig.getInt(SETTINGS_PREFIX + key, defaultValue);
  }

  public String getMessage(String id) {
    String message = messages.get(id);
    if (message != null) {
      return message;
    }

    return String.format("!! No message with id '%s' in %s !!", id, CONFIG_XML);
  }

  public String getRendition(String id) {
    String rendition = renditions.get(id);
    if (rendition != null) {
      return rendition;
    }

    return String.format("!! No rendition with id '%s' in %s !!", id, CONFIG_XML);
  }

  public String[] getExtraSortFields() {
    return getSettings(SOLR_SORT_FIELDS);
  }

  public void overrideProperty(String key, Object value) {
    xmlConfig.setProperty(SETTINGS_PREFIX + key, value);
    System.out.println(CLASS_NAME + " - Overriding setting: " + key + " => " + getSetting(key));
  }

  // ---------------------------------------------------------------------------

  private static final String APPLICATION_MODE_KEY = "application.mode";
  public static final String SOLR_URL_KEY = "solr.url";
  public static final String ROOT_PATH = "rootpath";
  public static final String PROJECT_FILES_BASEDIR = "";
  public static final String MAILHOST = "email.mailhost";
  public static final String FROM_EMAIL = "email.from_address";
  public static final String FROM_NAME = "email.from_name";
  public static final String WORK_URL = "work.url";

  // public static final String CONCORDANCEKEY = "GLP";

  public boolean useInMemoryDatabase() {
    return "test".equals(System.getProperty(APPLICATION_MODE_KEY));
  }

  public int getIntegerSetting(String key, int defaultValue) {
    return xmlConfig.getInt(SETTINGS_PREFIX + key, defaultValue);
  }

}
