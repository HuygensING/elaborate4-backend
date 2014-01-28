package elaborate.editor.export.tei;

import java.util.Map;

import com.google.common.collect.Maps;

public class TagInfo {
  private String name = "";
  private Map<String, String> attributes = Maps.newHashMap();
  private boolean skipNewlineAfter = false;

  public String getName() {
    return name;
  }

  public TagInfo setName(String name1) {
    this.name = name1;
    return this;
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  public TagInfo setAttributes(Map<String, String> attributes1) {
    this.attributes = attributes1;
    return this;
  }

  public void addAttribute(String key, String value) {
    attributes.put(key, value);
  }

  public boolean skipNewlineAfter() {
    return skipNewlineAfter;
  }

  public TagInfo setSkipNewlineAfter(boolean skipNewlineAfter1) {
    this.skipNewlineAfter = skipNewlineAfter1;
    return this;
  }
}
