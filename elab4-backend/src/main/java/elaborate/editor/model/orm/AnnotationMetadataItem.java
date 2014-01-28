package elaborate.editor.model.orm;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

import elaborate.editor.model.AbstractStoredEntity;

@Entity
@Table(name = "annotation_metadata_items")
@XmlRootElement
public class AnnotationMetadataItem extends AbstractStoredEntity<AnnotationMetadataItem> {
  private static final long serialVersionUID = 1L;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "annotation_id", columnDefinition = "int4")
  private Annotation annotation;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "annotation_type_metadata_item_id", columnDefinition = "int4")
  private AnnotationTypeMetadataItem annotationTypeMetadataItem;

  private String data;

  @JsonIgnore
  public Annotation getAnnotation() {
    return annotation;
  }

  public AnnotationMetadataItem setAnnotation(Annotation annotation) {
    this.annotation = annotation;
    return this;
  }

  public AnnotationTypeMetadataItem getAnnotationTypeMetadataItem() {
    return annotationTypeMetadataItem;
  }

  public AnnotationMetadataItem setAnnotationTypeMetadataItem(AnnotationTypeMetadataItem annotationTypeMetadataItem) {
    this.annotationTypeMetadataItem = annotationTypeMetadataItem;
    return this;
  }

  public String getData() {
    return data;
  }

  public AnnotationMetadataItem setData(String data) {
    this.data = data;
    return this;
  }

}
