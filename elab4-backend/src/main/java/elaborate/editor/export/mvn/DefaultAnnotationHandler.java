package elaborate.editor.export.mvn;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

public class DefaultAnnotationHandler implements AnnotationHandler {
  private final List<String> tags = Lists.newArrayList();

  public DefaultAnnotationHandler(Object... tagObjects) {
    for (Object tag : tagObjects) {
      if (tag instanceof String) {
        tags.add((String) tag);
      } else if (tag instanceof MVNAnnotationType) {
        tags.add(((MVNAnnotationType) tag).getName());
      } else {
        throw new RuntimeException("bad tag: " + tag);
      }
    }
  }

  @Override
  public List<String> relevantTags() {
    return tags;
  }

  @Override
  public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {}

  @Override
  public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {}

}
