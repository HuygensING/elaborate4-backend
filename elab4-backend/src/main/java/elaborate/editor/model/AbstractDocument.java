package elaborate.editor.model;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractDocument<T extends AbstractDocument<T>> extends AbstractTrackedEntity<T> {
  private static final long serialVersionUID = 1L;

  private String name;
  private String title;

  public String getName() {
    return name;
  }

  public T setName(String name) {
    this.name = name;
    return ((T) this);
  };

  public String getTitle() {
    return title;
  };

  public T setTitle(String title) {
    this.title = title;
    return ((T) this);
  };

}
