package elaborate.editor.export.mvn;

import java.util.Collection;
import java.util.List;

public interface AnnotationHandler {
  public List<String> relevantTags();

  public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> collection, Context context);

  public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> collection, Context context);
}
