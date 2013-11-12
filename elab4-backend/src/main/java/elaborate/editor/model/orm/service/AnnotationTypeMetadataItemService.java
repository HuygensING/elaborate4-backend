package elaborate.editor.model.orm.service;

import javax.inject.Singleton;

import nl.knaw.huygens.jaxrstools.exceptions.UnauthorizedException;

import com.google.common.collect.ImmutableList;

import elaborate.editor.model.orm.AnnotationType;
import elaborate.editor.model.orm.AnnotationTypeMetadataItem;
import elaborate.editor.model.orm.User;
import elaborate.editor.resources.orm.wrappers.AnnotationTypeMetadataItemInput;

@Singleton
public class AnnotationTypeMetadataItemService extends AbstractStoredEntityService<AnnotationTypeMetadataItem> {

  @Override
  Class<AnnotationTypeMetadataItem> getEntityClass() {
    return AnnotationTypeMetadataItem.class;
  }

  @Override
  String getEntityName() {
    return "AnnotationTypeMetadataItem";
  }

  /* CRUD methods */

  public AnnotationTypeMetadataItem create(AnnotationTypeMetadataItemInput input, User creator) {
    beginTransaction();
    AnnotationTypeMetadataItem annotationTypeMetadataItem = new AnnotationTypeMetadataItem();
    if (creator.getPermission(annotationTypeMetadataItem).canWrite()) {
      AnnotationTypeMetadataItem create = super.create(annotationTypeMetadataItem);
      commitTransaction();
      return create;
    }
    rollbackTransaction();
    throw new UnauthorizedException(exception(creator, "create new annotation types"));
  }

  private String exception(User creator, String string) {
    return "user " + creator.getUsername() + " is not authorized to " + string;
  }

  public AnnotationTypeMetadataItem read(long id, User reader) {
    openEntityManager();
    AnnotationTypeMetadataItem annotationType = super.read(id);
    closeEntityManager();
    return annotationType;
  }

  public void update(AnnotationTypeMetadataItem annotationType, User modifier) {
    beginTransaction();
    if (modifier.getPermission(annotationType).canWrite()) {
      super.update(annotationType);
      commitTransaction();
    } else {
      rollbackTransaction();
      throw new UnauthorizedException(exception(modifier, "update annotation types"));
    }
  }

  public void delete(long id, User modifier) {
    beginTransaction();
    AnnotationTypeMetadataItem annotationType = super.read(id);
    if (modifier.getPermission(annotationType).canWrite()) {
      super.delete(id);
      commitTransaction();
    } else {
      rollbackTransaction();
      throw new UnauthorizedException(exception(modifier, "delete annotation types"));
    }
  }

  /**/
  public ImmutableList<AnnotationTypeMetadataItem> getAll(long annotationTypeId) {
    openEntityManager();
    AnnotationTypeService annotationTypeService = new AnnotationTypeService();
    annotationTypeService.setEntityManager(getEntityManager());
    AnnotationType annotationType = annotationTypeService.read(annotationTypeId);
    ImmutableList<AnnotationTypeMetadataItem> list = ImmutableList.copyOf(annotationType.getMetadataItems());
    closeEntityManager();
    return list;
  }

  public void update(long id, AnnotationTypeMetadataItemInput input, User user) {
    // TODO Auto-generated method stub

  }

  public void update(AnnotationTypeMetadataItemInput input, User user) {
    // TODO Auto-generated method stub

  }

}
