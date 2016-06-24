package elaborate.editor.export.mvn;

import java.util.Collection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AnnotatedTextSegment {
  private Collection<XmlAnnotation> openingAnnotations;
  private final String text;
  private Collection<XmlAnnotation> closingAnnotations;

  public AnnotatedTextSegment(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public AnnotatedTextSegment withOpeningAnnotations(Collection<XmlAnnotation> openingAnnotations) {
    this.openingAnnotations = openingAnnotations;
    return this;
  }

  public Collection<XmlAnnotation> getOpeningAnnotations() {
    return openingAnnotations;
  }

  public AnnotatedTextSegment withClosingAnnotations(Collection<XmlAnnotation> closingAnnotations) {
    this.closingAnnotations = closingAnnotations;
    return this;
  }

  public Collection<XmlAnnotation> getClosingAnnotations() {
    return closingAnnotations;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
