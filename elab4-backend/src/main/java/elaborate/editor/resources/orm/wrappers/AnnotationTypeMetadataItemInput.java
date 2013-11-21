package elaborate.editor.resources.orm.wrappers;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import elaborate.editor.model.orm.AnnotationType;
import elaborate.editor.model.orm.AnnotationTypeMetadataItem;

public class AnnotationTypeMetadataItemInput {

  //  "id": 177,
  //  "annotationTypeMetadataItems": [
  //    {
  //      "id": 13,
  //      "description": "index term",
  //      "name": "Index term"
  //    }
  //  ],
  //  "description": "Index",
  //  "name": "index"
  //},
  long id;
  List<MetadataInput> annotationTypeMetadataItems = Lists.newArrayList();
  String description;
  String name;

  public long getId() {
    return id;
  }

  public AnnotationTypeMetadataItemInput setId(long id) {
    this.id = id;
    return this;
  }

  public List<MetadataInput> getAnnotationTypeMetadataItems() {
    return annotationTypeMetadataItems;
  }

  public AnnotationTypeMetadataItemInput setAnnotationTypeMetadataItems(List<MetadataInput> annotationTypeMetadataItems) {
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
      AnnotationTypeMetadataItem item = new AnnotationTypeMetadataItem()//
          .setId(input.getId())//
          .setName(input.getName())//
          .setDescription(input.getDescription());
      items.add(item);
    }
    return new AnnotationType()//
        .setId(id)//
        .setName(name)//
        .setDescription(description)//
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
