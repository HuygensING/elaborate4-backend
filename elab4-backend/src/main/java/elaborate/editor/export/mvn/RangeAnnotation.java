package elaborate.editor.export.mvn;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2022 Huygens ING
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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import nl.knaw.huygens.tei.Element;

public class RangeAnnotation {
  private Element element;
  private int startOffset;
  private int endOffset;

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
    return "RangeAnnotation(" + element + ", " + startOffset + " - " + endOffset + ")";
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof RangeAnnotation)) {
      return false;
    }
    RangeAnnotation otherRangeAnnotation = (RangeAnnotation) other;
    return new EqualsBuilder()
        .append(element.getName(), otherRangeAnnotation.getElement().getName())
        .append(element.getAttributes(), otherRangeAnnotation.getElement().getAttributes())
        .append(startOffset, otherRangeAnnotation.getStartOffset())
        .append(endOffset, otherRangeAnnotation.getEndOffset())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(element.getName())
        .append(element.getAttributes())
        .append(startOffset)
        .append(endOffset)
        .hashCode();
  }
}
