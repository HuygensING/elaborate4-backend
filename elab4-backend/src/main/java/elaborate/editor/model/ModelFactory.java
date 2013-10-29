package elaborate.editor.model;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import com.google.common.collect.ImmutableList;

import elaborate.editor.model.orm.TranscriptionType;
import elaborate.editor.model.orm.User;

public class ModelFactory {
  //  private static final String PERSISTENCE_UNIT_NAME = "nl.knaw.huygens.elaborate.jpa";
  private static final String PERSISTENCE_UNIT_NAME = "nl.knaw.huygens.elaborate.old.jpa";
  public static final ModelFactory INSTANCE = new ModelFactory();
  private final static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
  private final static EntityManager entityManager = entityManagerFactory.createEntityManager();

  private ModelFactory() {}

  public static <T extends AbstractStoredEntity<T>> T create(Class<T> clazz) {
    try {
      return clazz.newInstance();
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T extends AbstractMetadataItem<T>> T createMetadataItem(Class<T> clazz, String field, String data, User creator) {
    try {
      return clazz.newInstance()//
          .setCreatedOn(new Date())//
          .setCreator(creator)//
          .setModifiedOn(new Date())//
          .setModifier(creator)//
          .setField(field)//
          .setData(data);
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T extends AbstractTrackedEntity<T>> T createTrackedEntity(Class<T> clazz, User creator) {
    try {
      return clazz.newInstance()//
          .setCreatedOn(new Date())//
          .setCreator(creator)//
          .setModifiedOn(new Date())//
          .setModifier(creator);
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public EntityManagerFactory getEntityManagerFactory() {
    return entityManagerFactory;
  }

  public static TranscriptionType getDefaultTranscriptionType() {
    ImmutableList<TranscriptionType> entities = getTranscriptionTypes();
    return entities.size() > 0 ? entities.get(0) : create(TranscriptionType.class).setName(TranscriptionType.DIPLOMATIC);
  }

  // TODO: more entitymanager dependency to service/doa
  public static ImmutableList<TranscriptionType> getTranscriptionTypes() {
    TypedQuery<TranscriptionType> createQuery = entityManager.createQuery("from TranscriptionType", TranscriptionType.class);
    ImmutableList<TranscriptionType> list = ImmutableList.copyOf(createQuery.getResultList());
    return list;
  }
}
