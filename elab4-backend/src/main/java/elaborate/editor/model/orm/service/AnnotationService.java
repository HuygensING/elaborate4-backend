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

import java.util.Collection;
import java.util.List;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.google.common.collect.Lists;

import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.AnnotationType;
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
		Annotation annotation;
		try {
			annotation = super.read(id);
		} finally {
			closeEntityManager();
		}
		return annotation;
	}

	public void update(Annotation annotation, User user) {
		beginTransaction();
		try {
			annotation.setModifiedBy(user);
			super.update(annotation);
		} finally {
			commitTransaction();
		}
	}

	public void delete(long id, User user) {
		beginTransaction();
		try {
			super.delete(id);
		} finally {
			commitTransaction();
		}
	}

	/* */
	public Annotation getAnnotationByAnnotationNo(int annotationNo, EntityManager entityManager) {
		//		LOG.info("annotationNo={}", annotationNo);
		try {
			List<Annotation> resultList = entityManager.createQuery("from Annotation where annotationNo=:no", Annotation.class)//
					.setParameter("no", annotationNo)//
					.getResultList();
			return resultList.isEmpty() ? null : resultList.get(0);

		} catch (NoResultException e) {
			return null;
		}
	}

	public Annotation getAnnotationByAnnotationNo(Integer annotationNo) {
		openEntityManager();
		Annotation annotation;
		try {
			annotation = getAnnotationByAnnotationNo(annotationNo, getEntityManager());
		} finally {
			closeEntityManager();
		}
		return annotation;
	}

	public Collection<Annotation> getAnnotationsByAnnotationType(AnnotationType annotationType, EntityManager entityManager) {
		List<Annotation> list = Lists.newArrayList();
		try {
			List<Annotation> resultList = entityManager.createQuery("from Annotation where annotationType=:type", Annotation.class)//
					.setParameter("type", annotationType)//
					.getResultList();
			return resultList.isEmpty() ? list : resultList;

		} catch (NoResultException e) {
			return list;
		}

	}
}
