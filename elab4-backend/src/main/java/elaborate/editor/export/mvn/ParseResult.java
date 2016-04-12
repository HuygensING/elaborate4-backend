package elaborate.editor.export.mvn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ParseResult {
  private final List<String> textSegments = new ArrayList<String>();
  private final Set<XmlAnnotation> xmlAnnotations = new TreeSet<XmlAnnotation>();

  public List<String> getTextSegments() {
    return textSegments;
  }

  public Set<XmlAnnotation> getXmlAnnotations() {
    return xmlAnnotations;
  }

}
