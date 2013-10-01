package elaborate.editor.model;

import java.util.Date;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

import elaborate.editor.model.orm.User;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractTrackedEntity<T extends AbstractTrackedEntity<T>> extends AbstractStoredEntity<T> {
  private static final long serialVersionUID = 1L;

  @ManyToOne
  @JoinColumn(name = "creator_id", columnDefinition = "int4")
  private User creator;

  @Temporal(TemporalType.TIMESTAMP)
  private Date created_on;

  @ManyToOne
  @JoinColumn(name = "modifier_id", columnDefinition = "int4")
  private User modifier;

  @Temporal(TemporalType.TIMESTAMP)
  private Date modified_on;

  @JsonView({ Views.Extended.class })
  public User getCreator() {
    return creator;
  };

  public T setCreator(User user) {
    this.creator = user;
    return ((T) this);
  };

  @JsonView({ Views.Extended.class })
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "CET")
  public Date getCreatedOn() {
    return created_on;
  };

  public T setCreatedOn(Date date) {
    this.created_on = date;
    return ((T) this);
  };

  @JsonView({ Views.Extended.class })
  public User getModifier() {
    return modifier;
  };

  public T setModifier(User user) {
    this.modifier = user;
    return ((T) this);
  };

  @JsonView({ Views.Extended.class })
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "CET")
  public Date getModifiedOn() {
    return modified_on;
  };

  public T setModifiedOn(Date date) {
    this.modified_on = date;
    return ((T) this);
  };

  public void setModifiedBy(User _modifier) {
    setModifier(_modifier);
    setModifiedOn(new Date());
  }

}
