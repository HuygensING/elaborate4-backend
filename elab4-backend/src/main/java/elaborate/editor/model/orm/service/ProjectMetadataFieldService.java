package elaborate.editor.model.orm.service;

import java.util.Date;
import java.util.List;

import nl.knaw.huygens.jaxrstools.exceptions.UnauthorizedException;

import com.google.common.collect.ImmutableList;

import elaborate.editor.model.orm.ProjectMetadataField;
import elaborate.editor.model.orm.User;

public class ProjectMetadataFieldService extends AbstractStoredEntityService<ProjectMetadataField> {
  private static ProjectMetadataFieldService instance;

  private ProjectMetadataFieldService() {}

  public static ProjectMetadataFieldService instance() {
    if (instance == null) {
      instance = new ProjectMetadataFieldService();
    }
    return instance;
  }

  @Override
  Class<ProjectMetadataField> getEntityClass() {
    return ProjectMetadataField.class;
  }

  @Override
  String getEntityName() {
    return "ProjectMetadataFields";
  }

  /* CRUD methods */
  @Override
  public ProjectMetadataField read(long entry_id) {
    openEntityManager();
    ProjectMetadataField projectMetadataField = super.read(entry_id);
    closeEntityManager();
    return projectMetadataField;
  }

  public void update(ProjectMetadataField ProjectMetadataField, User user) {
    if (rootOrAdmin(user)) {
      beginTransaction();
      super.update(ProjectMetadataField);
      commitTransaction();

    } else {
      throw new UnauthorizedException();
    }
  }

  public void delete(long entry_id, User user) {
    if (rootOrAdmin(user)) {
      beginTransaction();
      super.delete(entry_id);
      commitTransaction();

    } else {
      throw new UnauthorizedException();
    }
  }

  /* */
  public List<ProjectMetadataField> getAll(User user) {
    if (rootOrAdmin(user)) {
      openEntityManager();
      ImmutableList<ProjectMetadataField> all = super.getAll();
      closeEntityManager();
      return all;

    } else {
      throw new UnauthorizedException();
    }
  }

  public void create(ProjectMetadataField pmField, User user) {
    if (rootOrAdmin(user)) {
      beginTransaction();
      pmField.setCreator(user);
      pmField.setCreatedOn(new Date());
      pmField.setModifier(user);
      pmField.setModifiedOn(new Date());
      super.create(pmField);
      commitTransaction();

    } else {
      throw new UnauthorizedException();
    }
  }

}
