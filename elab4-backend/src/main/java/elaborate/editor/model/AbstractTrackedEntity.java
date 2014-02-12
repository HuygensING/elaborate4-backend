package elaborate.editor.model;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2014 Huygens ING
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

  public void setCreatedBy(User creator) {
    setCreator(creator);
    setCreatedOn(new Date());
    setModifiedBy(creator);
  };

}
