package elaborate.editor.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractStoredEntity<T extends AbstractStoredEntity<T>> extends LoggableObject implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(columnDefinition = "serial")
  long id;

  //  @Version
  long rev = 0;

  @JsonView({ Views.IdOnly.class })
  public long getId() {
    return id;
  }

  public T setId(long id) {
    this.id = id;
    return ((T) this);
  }

  @JsonIgnore
  public long getRev() {
    return rev;
  }

  public T setRev(long rev) {
    this.rev = rev;
    return ((T) this);
  }

}
