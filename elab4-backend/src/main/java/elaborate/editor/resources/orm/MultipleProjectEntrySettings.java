package elaborate.editor.resources.orm;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MultipleProjectEntrySettings {
  private static final String PUBLISHABLE = "Publishable";
  private List<Long> projectEntryIds;
  private Map<String, Object> settings;
  private boolean publishable;
  private boolean changePublishable = false;

  private MultipleProjectEntrySettings() {}

  public void setProjectEntryIds(List<Long> _projectEntryIds) {
    this.projectEntryIds = _projectEntryIds;
  }

  public List<Long> getProjectEntryIds() {
    return projectEntryIds;
  }

  public Map<String, Object> getSettings() {
    return settings;
  }

  public void setSettings(Map<String, Object> _settings) {
    if (_settings.containsKey(PUBLISHABLE)) {
      publishable = (Boolean) _settings.remove(PUBLISHABLE);
      changePublishable = true;
    }
    this.settings = _settings;
  }

  public boolean changePublishable() {
    return changePublishable;
  }

  public boolean getPublishableSetting() {
    return publishable;
  }

}
