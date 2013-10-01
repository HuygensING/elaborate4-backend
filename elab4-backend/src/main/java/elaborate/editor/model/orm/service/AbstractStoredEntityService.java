package elaborate.editor.model.orm.service;

import java.text.MessageFormat;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.NotFoundException;

import elaborate.editor.config.Configuration;
import elaborate.editor.model.AbstractStoredEntity;
import elaborate.editor.model.AbstractTrackedEntity;
import elaborate.editor.model.ElaborateRoles;
import elaborate.editor.model.LoggableObject;
import elaborate.editor.model.ModelFactory;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.User;
import elaborate.editor.solr.RemoteSolrServer;
import elaborate.editor.solr.SolrServerWrapper;

@Singleton
public abstract class AbstractStoredEntityService<T extends AbstractStoredEntity<T>> extends LoggableObject {
  private SolrServerWrapper solrserver = null;
  private ProjectService projectService = null;

  //  private ProjectEntryService projectEntryService = null;

  abstract Class<? extends AbstractStoredEntity<?>> getEntityClass();

  abstract String getEntityName();

  /* CRUD methods */
  public T create(T entity) {
    persist(entity);
    return entity;
  }

  public T read(long id) {
    T entity = (T) getEntityManager().find(getEntityClass(), id);
    checkEntityFound(entity, id);
    return entity;
  }

  public T update(T entity) {
    return getEntityManager().merge(entity);
  }

  public T delete(long id) {
    T entity = (T) getEntityManager().find(getEntityClass(), id);
    checkEntityFound(entity, id);
    getEntityManager().remove(entity);
    return entity;
  }

  /* public */
  public ImmutableList<T> getAll() {
    TypedQuery<T> createQuery = (TypedQuery<T>) getEntityManager().createQuery("from " + getEntityName(), getEntityClass());
    ImmutableList<T> list = ImmutableList.copyOf(createQuery.getResultList());
    return list;
  }

  public SolrServerWrapper getSolrServer() {
    if (solrserver == null) {
      solrserver = new RemoteSolrServer(Configuration.instance().getSetting(Configuration.SOLR_URL_KEY));
    }
    return solrserver;
  }

  public void setEntityManager(EntityManager entityManager) {
    tlem.set(entityManager);
  }

  /* private */
  protected void checkEntityFound(T entity, long id) {
    if (entity == null) {
      throw new NotFoundException(MessageFormat.format("No {0} found with id {1,number,#}", getEntityName(), id));
    }
  }

  protected boolean rootOrAdmin(User user) {
    return user.isRoot() || user.hasRole(ElaborateRoles.ADMIN);
  }

  void setModifiedBy(AbstractTrackedEntity<?> trackedEntity, User modifier) {
    trackedEntity.setModifiedBy(modifier);
    merge(trackedEntity);
  }

  /* entitymanager methods */
  final static EntityManagerFactory ENTITY_MANAGER_FACTORY = ModelFactory.INSTANCE.getEntityManagerFactory();
  //  EntityManager entityManager;
  protected static final ThreadLocal<EntityManager> tlem = new ThreadLocal<EntityManager>() {};

  public EntityManager getEntityManager() {
    EntityManager em = tlem.get();
    if (em == null) {
      throw new RuntimeException("no entityManager set, did you call openEntityManager() or beginTransaction()?");
    }
    return em;
  }

  /** start read **/
  public void openEntityManager() {
    tlem.set(ENTITY_MANAGER_FACTORY.createEntityManager());
  }

  /** end read **/
  public void closeEntityManager() {
    EntityManager em = tlem.get();
    if (em != null) {
      em.close();
      tlem.set(null);
    }
  }

  /** start write **/
  public void beginTransaction() {
    openEntityManager();
    getEntityManager().getTransaction().begin();
  }

  /** commit and end write **/
  public void commitTransaction() {
    getEntityManager().getTransaction().commit();
    closeEntityManager();
  }

  /** discard changes and end write **/
  public void rollbackTransaction() {
    getEntityManager().getTransaction().rollback();
    closeEntityManager();
  }

  public void persist(Object entity) {
    getEntityManager().persist(entity);
  }

  public void merge(Object entity) {
    getEntityManager().merge(entity);
  }

  public void remove(Object entity) {
    getEntityManager().remove(entity);
  }

  public <X extends AbstractStoredEntity<X>> X find(Class<X> entityClass, Object primaryKey) {
    return getEntityManager().find(entityClass, primaryKey);
  }

  /* private methods */

  void initServices() {
    if (projectService == null) {
      projectService = new ProjectService();
      //      projectEntryService = new ProjectEntryService();
    }
  }

  Project checkProjectPermissions(long project_id, User user) {
    initServices();
    projectService.setEntityManager(getEntityManager());
    return projectService.getProjectIfUserIsAllowed(project_id, user);
  }

  void updateParents(ProjectEntry projectEntry, User user, String logLine) {
    setModifiedBy(projectEntry, user);
    merge(projectEntry);

    Project project = projectEntry.getProject();
    setModifiedBy(project, user);
    merge(project);

    persist(project.addLogEntry(logLine, user));
  }

}
