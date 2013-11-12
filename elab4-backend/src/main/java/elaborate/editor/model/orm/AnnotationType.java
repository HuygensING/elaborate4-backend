package elaborate.editor.model.orm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import elaborate.editor.model.AbstractTrackedEntity;

@Entity
@Table(name = "annotation_types")
@XmlRootElement
public class AnnotationType extends AbstractTrackedEntity<AnnotationType> {
  private static final long serialVersionUID = 1L;

  private String name;
  private String description;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "annotationType")
  private Set<AnnotationTypeMetadataItem> annotationTypeMetadataItems;

  public String getName() {
    return name;
  }

  public AnnotationType setName(String name) {
    this.name = name;
    return this;
  };

  public String getDescription() {
    return description;
  };

  public AnnotationType setDescription(String description) {
    this.description = description;
    return this;
  }

  @JsonIgnore
  public Set<AnnotationTypeMetadataItem> getAnnotationTypeMetadataItems() {
    return annotationTypeMetadataItems;
  }

  public AnnotationType setAnnotationTypeMetadataItems(Set<AnnotationTypeMetadataItem> annotationTypeMetadataItems) {
    this.annotationTypeMetadataItems = annotationTypeMetadataItems;
    return this;
  };

  //  AnnotationTypeMetadataItem addMetadataItem(String name, String description) {
  //    return null;
  //  };

  @Transient
  Map<String, Object> getDataMap() {
    final Map<String, Object> annotationTypeMap = Maps.newHashMap();
    annotationTypeMap.put("name", getName());
    annotationTypeMap.put("description", getDescription());
    final List<Map<String, Object>> annotationTypeMetadataItemsList = Lists.newArrayList();
    annotationTypeMap.put("metadata_items", annotationTypeMetadataItemsList);
    for (final AnnotationTypeMetadataItem annotationTypeMetadataItem : getAnnotationTypeMetadataItems()) {
      annotationTypeMetadataItemsList.add(annotationTypeMetadataItem.getDataMap());
    }
    return annotationTypeMap;
  };

}
