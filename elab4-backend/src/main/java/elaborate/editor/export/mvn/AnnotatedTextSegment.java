package elaborate.editor.export.mvn;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2020 Huygens ING
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
