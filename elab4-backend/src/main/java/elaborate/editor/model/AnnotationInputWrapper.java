package elaborate.editor.model;

import java.util.Map;

import com.google.common.collect.Maps;

public class AnnotationInputWrapper {
  public String body = "";
  public long typeId = 1l;
  public Map<String, String> metadata = Maps.newHashMap();
}
