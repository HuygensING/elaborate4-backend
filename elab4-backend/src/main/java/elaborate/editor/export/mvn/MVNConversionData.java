package elaborate.editor.export.mvn;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MVNConversionData {

  public static class EntryData {
    String id;
    String name;
    String body;
    String facs;
  }

  public static class AnnotationData {
    String type;
    String body;
  }

  private final Map<Integer, AnnotationData> annotationIndex = Maps.newHashMap();
  private final List<EntryData> entryDataList = Lists.newArrayList();
  private final Set<String> deepestTextNums = Sets.newHashSet();

  public List<EntryData> getEntryDataList() {
    return entryDataList;
  }

  public Map<Integer, AnnotationData> getAnnotationIndex() {
    return annotationIndex;
  }

  public Set<String> getDeepestTextNums() {
    return deepestTextNums;
  }

}
