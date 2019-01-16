package elaborate.editor.export.mvn;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2019 Huygens ING
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
