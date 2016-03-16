package elaborate.editor.export.mvn;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import nl.knaw.huygens.tei.Element;

public class RangeAnnotation {
  private Element element;
  private int startOffset;
  private int endOffset;
  private final int order;

  public RangeAnnotation(int order) {
    this.order = order;
  }

  public Element getElement() {
    return element;
  }

  public RangeAnnotation setElement(Element element) {
    this.element = element;
    return this;
  }

  public int getStartOffset() {
    return startOffset;
  }

  public RangeAnnotation setStartOffset(int startOffset) {
    this.startOffset = startOffset;
    return this;
  }

  public int getEndOffset() {
    return endOffset;
  }

  public RangeAnnotation setEndOffset(int length) {
    this.endOffset = length;
    return this;
  }

  @Override
  public String toString() {
    return "RangeAnnotation(" + order + ", " + element + ", " + startOffset + " - " + endOffset + ")";
  }

  public int getOrder() {
    return order;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof RangeAnnotation)) {
      return false;
    }
    RangeAnnotation otherRangeAnnotation = (RangeAnnotation) other;
    return new EqualsBuilder()//
        .append(element.getName(), otherRangeAnnotation.getElement().getName())//
        .append(element.getAttributes(), otherRangeAnnotation.getElement().getAttributes())//
        .append(startOffset, otherRangeAnnotation.getStartOffset())//
        .append(endOffset, otherRangeAnnotation.getEndOffset())//
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()//
        .append(element.getName())//
        .append(element.getAttributes())//
        .append(startOffset)//
        .append(endOffset)//
        .hashCode();
  }

}
