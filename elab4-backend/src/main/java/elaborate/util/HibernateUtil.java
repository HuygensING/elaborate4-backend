package elaborate.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import elaborate.editor.model.ModelFactory;

public class HibernateUtil {
  final static EntityManagerFactory ENTITY_MANAGER_FACTORY = ModelFactory.INSTANCE.getEntityManagerFactory();
  static Logger LOG = LoggerFactory.getLogger(HibernateUtil.class);

  public static EntityManager beginTransaction() {
    EntityManager entityManager = getEntityManager();
    entityManager.getTransaction().begin();
    return entityManager;
  }

  public static void commitTransaction(EntityManager entityManager) {
    entityManager.getTransaction().commit();
    endTransaction(entityManager);
  }

  public static void endTransaction(EntityManager entityManager) {
    entityManager.close();
  }

  public static void rollbackTransaction(EntityManager entityManager) {
    entityManager.getTransaction().rollback();
    endTransaction(entityManager);
  }

  public static EntityManager getEntityManager() {
    return ENTITY_MANAGER_FACTORY.createEntityManager();
  }

}
