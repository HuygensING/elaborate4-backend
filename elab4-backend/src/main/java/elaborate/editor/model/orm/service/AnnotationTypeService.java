package elaborate.editor.model.orm.service;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2021 Huygens ING
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

import java.util.List;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.google.common.collect.ImmutableList;

import nl.knaw.huygens.jaxrstools.exceptions.UnauthorizedException;

import elaborate.editor.model.orm.AnnotationType;
import elaborate.editor.model.orm.User;

@Singleton
public class AnnotationTypeService extends AbstractStoredEntityService<AnnotationType> {
  private static final String DEFAULT_ANNOTATIONTYPE_NAME = "Uncategorized";
  private static final AnnotationTypeService instance = new AnnotationTypeService();

  private AnnotationTypeService() {}

  public static AnnotationTypeService instance() {
    return instance;
  }

  @Override
  Class<AnnotationType> getEntityClass() {
    return AnnotationType.class;
  }

  @Override
  String getEntityName() {
    return "AnnotationType";
  }

  /* CRUD methods */

  public AnnotationType create(AnnotationType annotationType, User creator) {
    beginTransaction();
    if (creator.getPermissionFor(annotationType).canWrite()) {
      annotationType.setCreatedBy(creator);
      AnnotationType created = super.create(annotationType);
      commitTransaction();
      return created;
    }
    rollbackTransaction();
    throw new UnauthorizedException(exception(creator, "create new annotation types"));
  }

  private String exception(User creator, String string) {
    return "user " + creator.getUsername() + " is not authorized to " + string;
  }

  public AnnotationType read(long id, User reader) {
    openEntityManager();
    AnnotationType annotationType;
    try {
      annotationType = super.read(id);
    } finally {
      closeEntityManager();
    }
    return annotationType;
  }

  public void update(AnnotationType annotationType, User modifier) {
    beginTransaction();
    if (modifier.getPermissionFor(annotationType).canWrite()) {
      annotationType.setModifiedBy(modifier);
      super.update(annotationType);
      commitTransaction();
    } else {
      rollbackTransaction();
      throw new UnauthorizedException(exception(modifier, "update annotation types"));
    }
  }

  public void delete(long id, User modifier) {
    beginTransaction();
    AnnotationType annotationType = super.read(id);
    if (modifier.getPermissionFor(annotationType).canWrite()) {
      super.delete(id);
      commitTransaction();
    } else {
      rollbackTransaction();
      throw new UnauthorizedException(exception(modifier, "delete annotation types"));
    }
  }

  /**/
  @Override
  public ImmutableList<AnnotationType> getAll() {
    openEntityManager();
    ImmutableList<AnnotationType> all;
    try {
      all = super.getAll();
    } finally {
      closeEntityManager();
    }
    return all;
  }

  public AnnotationType getDefaultAnnotationType() {
    // ModelFactory.createAnnotationType("Uncategorized", "Any annotation", creator);
    beginTransaction();
    AnnotationType defaultAnnotationType;
    try {
      defaultAnnotationType =
          (AnnotationType)
              getEntityManager() //
                  .createQuery("from AnnotationType as at where at.name=?1") //
                  .setParameter(1, DEFAULT_ANNOTATIONTYPE_NAME) //
                  .getSingleResult();
      if (defaultAnnotationType == null) {
        defaultAnnotationType =
            new AnnotationType()
                .setName(DEFAULT_ANNOTATIONTYPE_NAME)
                .setDescription("Any annotation");
        User root = UserService.instance().getUser(1);
        create(defaultAnnotationType, root);
      }
    } finally {
      commitTransaction();
    }
    return defaultAnnotationType;
  }

  public AnnotationType getAnnotationTypeByName(String name, EntityManager entityManager) {
    try {
      List<AnnotationType> resultList =
          entityManager
              .createQuery("from AnnotationType where name=:name", AnnotationType.class) //
              .setParameter("name", name) //
              .getResultList();
      return resultList.isEmpty() ? null : resultList.get(0);

    } catch (NoResultException e) {
      return null;
    }
  }
}
