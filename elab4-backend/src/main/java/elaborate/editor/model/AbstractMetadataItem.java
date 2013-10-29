package elaborate.editor.model;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractMetadataItem<T extends AbstractMetadataItem<T>> extends AbstractTrackedEntity<AbstractMetadataItem<T>> {
  private static final long serialVersionUID = 1L;

  String field;
  String data;

  public String getField() {
    return field;
  }

  public T setField(String field) {
    this.field = field;
    return (T) this;
  }

  public String getData() {
    return data;
  }

  public T setData(String data) {
    this.data = data;
    return (T) this;
  }

}
