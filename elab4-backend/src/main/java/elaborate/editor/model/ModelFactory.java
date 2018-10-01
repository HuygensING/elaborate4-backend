package elaborate.editor.model;

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

import java.util.Date;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.common.collect.ImmutableList;

import elaborate.editor.model.orm.TranscriptionType;
import elaborate.editor.model.orm.User;
import elaborate.editor.model.orm.service.TranscriptionService;

public class ModelFactory {
	// private static final String PERSISTENCE_UNIT_NAME = "nl.knaw.huygens.elaborate.jpa";
	private static final String PERSISTENCE_UNIT_NAME = "nl.knaw.huygens.elaborate.old.jpa";
	public static final ModelFactory INSTANCE = new ModelFactory();
	private static TranscriptionService transcriptionService = TranscriptionService.instance();

	private final static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

	// private final static EntityManager entityManager = entityManagerFactory.createEntityManager();

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
		ImmutableList<TranscriptionType> entities = transcriptionService.getTranscriptionTypes();
		return entities.size() > 0 ? entities.get(0) : create(TranscriptionType.class).setName(TranscriptionType.DIPLOMATIC);
	}

}
