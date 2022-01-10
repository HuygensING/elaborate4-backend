package elaborate.editor.resources.orm.wrappers;

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

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import elaborate.editor.model.orm.AnnotationType;
import elaborate.editor.model.orm.AnnotationTypeMetadataItem;

public class AnnotationTypeMetadataItemInput {

  // "id": 177,
  // "annotationTypeMetadataItems": [
  // {
  // "id": 13,
  // "description": "index term",
  // "name": "Index term"
  // }
  // ],
  // "description": "Index",
  // "name": "index"
  // },
  private long id;
  private List<MetadataInput> annotationTypeMetadataItems = Lists.newArrayList();
  private String description;
  private String name;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public List<MetadataInput> getAnnotationTypeMetadataItems() {
    return annotationTypeMetadataItems;
  }

  public AnnotationTypeMetadataItemInput setAnnotationTypeMetadataItems(
      List<MetadataInput> annotationTypeMetadataItems) {
    this.annotationTypeMetadataItems = annotationTypeMetadataItems;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public AnnotationTypeMetadataItemInput setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getName() {
    return name;
  }

  public AnnotationTypeMetadataItemInput setName(String name) {
    this.name = name;
    return this;
  }

  public AnnotationType getAnnotationType() {
    Set<AnnotationTypeMetadataItem> items = Sets.newHashSet();
    for (MetadataInput input : annotationTypeMetadataItems) {
      AnnotationTypeMetadataItem item =
          new AnnotationTypeMetadataItem()
              .setId(input.getId())
              .setName(input.getName())
              .setDescription(input.getDescription());
      items.add(item);
    }
    return new AnnotationType()
        .setId(id)
        .setName(name)
        .setDescription(description)
        .setMetadataItems(items);
  }

  public static class MetadataInput {
    long id;
    String description;
    String name;

    public long getId() {
      return id;
    }

    public MetadataInput setId(long id) {
      this.id = id;
      return this;
    }

    public String getDescription() {
      return description;
    }

    public MetadataInput setDescription(String description) {
      this.description = description;
      return this;
    }

    public String getName() {
      return name;
    }

    public MetadataInput setName(String name) {
      this.name = name;
      return this;
    }
  }
}
