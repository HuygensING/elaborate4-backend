package elaborate.editor.model;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractIndexableProjectElement<T extends AbstractIndexableProjectElement<?>> extends AbstractProjectEntryElement<AbstractIndexableProjectElement<?>> {
  private static final long serialVersionUID = 1L;

  private String body;

  public String getBody() {
    return body;
  }

  public T setBody(String body) {
    this.body = body;
    return (T) this;
  }

  @Transient
  public String getSolrId() {
    return "";
  };

  public void index(boolean commitNow) {};

  /**
   * Remove the entity representation from the index
   */
  public void deindex() {};

}
