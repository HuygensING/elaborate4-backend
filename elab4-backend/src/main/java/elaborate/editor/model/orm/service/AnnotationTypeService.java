package elaborate.editor.model.orm.service;

import javax.inject.Singleton;

import nl.knaw.huygens.jaxrstools.exceptions.UnauthorizedException;

import com.google.common.collect.ImmutableList;

import elaborate.editor.model.orm.AnnotationType;
import elaborate.editor.model.orm.User;

@Singleton
public class AnnotationTypeService extends AbstractStoredEntityService<AnnotationType> {
	private static AnnotationTypeService instance = new AnnotationTypeService();

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
		AnnotationType annotationType = super.read(id);
		closeEntityManager();
		return annotationType;
	}

  public void update(AnnotationType annotationType, User modifier) {
    beginTransaction();
    if (modifier.getPermissionFor(annotationType).canWrite()) {
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
		ImmutableList<AnnotationType> all = super.getAll();
		closeEntityManager();
		return all;
	}

}
