package elaborate.editor.model.orm.service;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.User;

@Singleton
public class AnnotationService extends AbstractStoredEntityService<Annotation> {

  @Override
  Class<Annotation> getEntityClass() {
    return Annotation.class;
  }

  @Override
  String getEntityName() {
    return "Annotation";
  }

  /* CRUD methods */
  public Annotation read(long id, User user) {
    openEntityManager();
    Annotation annotation = super.read(id);
    closeEntityManager();
    return annotation;
  }

  public void update(Annotation annotation, User user) {
    beginTransaction();
    super.update(annotation);
    commitTransaction();
  }

  public void delete(long id, User user) {
    beginTransaction();
    super.delete(id);
    commitTransaction();
  }

  /* */
  public Annotation getAnnotationByAnnotationNo(int annotationNo, EntityManager entityManager) {
    try {
      return entityManager.createQuery("from Annotation where annotationNo=:no", Annotation.class)//
          .setParameter("no", annotationNo)//
          .getSingleResult();

    } catch (NoResultException e) {
      return null;
    }
  }

  public Annotation getAnnotationByAnnotationNo(Integer annotationNo) {
    openEntityManager();
    Annotation annotation = getAnnotationByAnnotationNo(annotationNo, getEntityManager());
    closeEntityManager();
    return annotation;
  }
}
