package elaborate.editor.model.orm.service;

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


import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.User;

@Singleton
public class AnnotationService extends AbstractStoredEntityService<Annotation> {
	private static AnnotationService instance = new AnnotationService();

	private AnnotationService() {}

	public static AnnotationService instance() {
		return instance;
	}

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
		//		LOG.info("annotationNo={}", annotationNo);
		try {
			return entityManager.createQuery("from Annotation where annotationNo=:no", Annotation.class)//
					.setParameter("no", annotationNo)//
					.getResultList().get(0);

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
