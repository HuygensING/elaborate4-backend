package elaborate.editor.model.orm.service;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2015 Huygens ING
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

import nl.knaw.huygens.jaxrstools.exceptions.UnauthorizedException;

import com.google.common.collect.ImmutableList;

import elaborate.editor.model.orm.AnnotationType;
import elaborate.editor.model.orm.AnnotationTypeMetadataItem;
import elaborate.editor.model.orm.User;
import elaborate.editor.resources.orm.wrappers.AnnotationTypeMetadataItemInput;

@Singleton
public class AnnotationTypeMetadataItemService extends AbstractStoredEntityService<AnnotationTypeMetadataItem> {
	private static AnnotationTypeMetadataItemService instance = new AnnotationTypeMetadataItemService();

	private AnnotationTypeMetadataItemService() {}

	public static AnnotationTypeMetadataItemService instance() {
		return instance;
	}

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
		if (creator.getPermissionFor(annotationTypeMetadataItem).canWrite()) {
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
		AnnotationTypeMetadataItem annotationType;
		try {
			annotationType = super.read(id);
		} finally {
			closeEntityManager();
		}
		return annotationType;
	}

	public void update(AnnotationTypeMetadataItem annotationTypeMetadataItem, User modifier) {
		beginTransaction();
		if (modifier.getPermissionFor(annotationTypeMetadataItem).canWrite()) {
			super.update(annotationTypeMetadataItem);
			commitTransaction();
		} else {
			rollbackTransaction();
			throw new UnauthorizedException(exception(modifier, "update annotation types"));
		}
	}

	public void delete(long id, User modifier) {
		beginTransaction();
		AnnotationTypeMetadataItem annotationType = super.read(id);
		if (modifier.getPermissionFor(annotationType).canWrite()) {
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
		ImmutableList<AnnotationTypeMetadataItem> list;
		try {
			AnnotationTypeService annotationTypeService = AnnotationTypeService.instance();
			annotationTypeService.setEntityManager(getEntityManager());
			AnnotationType annotationType = annotationTypeService.read(annotationTypeId);
			list = ImmutableList.copyOf(annotationType.getMetadataItems());
		} finally {
			closeEntityManager();
		}
		return list;
	}
}
