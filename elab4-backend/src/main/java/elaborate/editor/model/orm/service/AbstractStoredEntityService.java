package elaborate.editor.model.orm.service;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2018 Huygens ING
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

import java.text.MessageFormat;
import java.util.Date;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.NotFoundException;

import elaborate.editor.config.Configuration;
import elaborate.editor.model.AbstractStoredEntity;
import elaborate.editor.model.AbstractTrackedEntity;
import elaborate.editor.model.ElaborateRoles;
import elaborate.editor.model.ModelFactory;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.User;
import elaborate.editor.solr.ElaborateEditorQueryComposer;
import elaborate.editor.solr.ElaborateSolrIndexer;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.facetedsearch.RemoteSolrServer;
import nl.knaw.huygens.facetedsearch.SolrServerWrapper;
import nl.knaw.huygens.jaxrstools.exceptions.UnauthorizedException;

@Singleton
public abstract class AbstractStoredEntityService<T extends AbstractStoredEntity<T>> {
	private SolrServerWrapper solrserver = null;
	private ElaborateSolrIndexer solrindexer = null;
	private ProjectService projectService = null;

	// private ProjectEntryService projectEntryService = null;

	abstract Class<? extends AbstractStoredEntity<?>> getEntityClass();

	abstract String getEntityName();

	/* CRUD methods */
	public T create(T entity) {
		persist(entity);
		return entity;
	}

	T read(long id) {
		T entity = (T) getEntityManager().find(getEntityClass(), id);
		checkEntityFound(entity, id);
		return entity;
	}

	T update(T entity) {
		T original = (T) getEntityManager().find(getEntityClass(), entity.getId());
		if (entity instanceof AbstractTrackedEntity) {
			AbstractTrackedEntity trackedOriginal = (AbstractTrackedEntity) original;
			((AbstractTrackedEntity) entity).setCreator(trackedOriginal.getCreator());
			((AbstractTrackedEntity) entity).setCreatedOn(trackedOriginal.getCreatedOn());
		}
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
    return ImmutableList.copyOf(createQuery.getResultList());
	}

	public SolrServerWrapper getSolrServer() {
		if (solrserver == null) {
			solrserver = new RemoteSolrServer(Configuration.instance().getSetting(Configuration.SOLR_URL_KEY), new ElaborateEditorQueryComposer());
		}
		return solrserver;
	}

	public void setEntityManager(EntityManager entityManager) {
		tlem.set(entityManager);
	}

	/* private */
	/**
	 * @param entity
	 *          the StoredEntity to check for null
	 * @param id
	 *          the id of the StoredEntity that was searched for
	 * 
	 * @throws NotFoundException
	 *           when there was no entity found with the given id
	 */
	void checkEntityFound(T entity, long id) {
		if (entity == null) {
			closeEntityManager();
			throw new NotFoundException(MessageFormat.format("No {0} found with id {1,number,#}", getEntityName(), id));
		}
	}

	boolean rootOrAdmin(User user) {
		return user.isRoot() || user.hasRole(ElaborateRoles.ADMIN);
	}

	/*
	 * Will index projectEntries
	 */
	void setModifiedBy(AbstractTrackedEntity<?> trackedEntity, User modifier) {
		trackedEntity.setModifiedBy(modifier);
		trackedEntity.setModifiedOn(new Date());
		persist(trackedEntity);
		if (trackedEntity instanceof ProjectEntry) {
			ProjectEntry projectEntry = (ProjectEntry) trackedEntity;
			getSolrIndexer().index(projectEntry, true);
		}
	}

	void setCreatedBy(AbstractTrackedEntity<?> trackedEntity, User creator) {
		trackedEntity.setCreator(creator);
		trackedEntity.setCreatedOn(new Date());
		merge(trackedEntity);
		setModifiedBy(trackedEntity, creator);
	}

	ElaborateSolrIndexer getSolrIndexer() {
		if (solrindexer == null) {
			solrindexer = new ElaborateSolrIndexer();
		}
		return solrindexer;
	}

	/* entitymanager methods */
	private final static EntityManagerFactory ENTITY_MANAGER_FACTORY = ModelFactory.INSTANCE.getEntityManagerFactory();
	// EntityManager entityManager;
	private static final ThreadLocal<EntityManager> tlem = new ThreadLocal<EntityManager>() {};

	EntityManager getEntityManager() {
		EntityManager em = tlem.get();
		if (em == null) {
			throw new RuntimeException("no entityManager set, did you call openEntityManager() or beginTransaction()?");
		}
		return em;
	}

	/** start read **/
	void openEntityManager() {
		if (tlem.get() == null) {
			tlem.set(ENTITY_MANAGER_FACTORY.createEntityManager());
		}
		EntityManager em = tlem.get();
		if (em.isJoinedToTransaction()) {
			em.flush();
			em.clear();
		}
	}

	/** end read **/
	void closeEntityManager() {
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
		EntityManager entityManager = getEntityManager();
		if (entityManager != null) {
			EntityTransaction transaction = entityManager.getTransaction();
			try {
				transaction.commit();
			} catch (Exception e) {
				Log.error(e.getMessage());
				e.printStackTrace();
				transaction.rollback();
			}
			closeEntityManager();
		}
	}

	/** discard changes and end write **/
	public void rollbackTransaction() {
		getEntityManager().getTransaction().rollback();
		closeEntityManager();
	}

	public void persist(Object entity) {
		getEntityManager().persist(entity);
	}

	private void merge(Object entity) {
		getEntityManager().merge(entity);
	}

	void remove(Object entity) {
		getEntityManager().remove(entity);
	}

	<X extends AbstractStoredEntity<X>> X find(Class<X> entityClass, Object primaryKey) {
		return getEntityManager().find(entityClass, primaryKey);
	}

	/* private methods */

	private void initServices() {
		if (projectService == null) {
			projectService = ProjectService.instance();
			// projectEntryService = ProjectEntryService.instance();
		}
	}

	private Project checkProjectReadPermissions(long project_id, User user) {
		initServices();
		projectService.setEntityManager(getEntityManager());
		return projectService.getProjectIfUserCanRead(project_id, user);
	}

	/**
	 * Throws UnAuthorizedException if the user has no read permissions for the project
	 */
	void abortUnlessUserHasReadPermissionsForProject(User user, long project_id) {
		checkProjectReadPermissions(project_id, user);
	}

	private void checkProjectWritePermissions(long project_id, User user) {
		Project project = checkProjectReadPermissions(project_id, user);
		if (user.getPermissionFor(project).canWrite()) {
		} else {
			// closeEntityManager();
			throw new UnauthorizedException("user " + user.getUsername() + " has no write permission for project " + project.getName());
		}
	}

	/**
	 * Throws UnAuthorizedException if the user has no write permissions for the project
	 */
	void abortUnlessUserHasWritePermissionsForProject(User user, long project_id) {
		checkProjectWritePermissions(project_id, user);
	}

	void updateParents(ProjectEntry projectEntry, User user, String logLine) {
		setModifiedBy(projectEntry, user);
		merge(projectEntry);

		Project project = projectEntry.getProject();
		getEntityManager().lock(project, LockModeType.PESSIMISTIC_WRITE);
		setModifiedBy(project, user);
		merge(project);

		persist(project.addLogEntry(logLine, user));
	}

}
