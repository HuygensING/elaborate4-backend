package elaborate.editor.model.orm;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;

import elaborate.editor.model.AbstractStoredEntity;

@Entity
@Table(name = "annotation_type_metadata_items")
@XmlRootElement
public class AnnotationTypeMetadataItem extends AbstractStoredEntity<AnnotationTypeMetadataItem> {
  private static final long serialVersionUID = 1L;

  private String name;
  private String description;

  @ManyToOne
  @JoinColumn(name = "annotation_type_id", columnDefinition = "int4")
  private AnnotationType annotationType;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @JsonIgnore
  public AnnotationType getAnnotationType() {
    return annotationType;
  }

  public void setAnnotationType(AnnotationType annotationType) {
    this.annotationType = annotationType;
  }

  @Transient
  @JsonIgnore
  public Map<String, Object> getDataMap() {
    Map<String, Object> map = Maps.newHashMap();
    map.put("name", getName());
    map.put("description", getDescription());
    return map;
  }

}
