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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;
import javax.persistence.TypedQuery;

import nl.knaw.huygens.jaxrstools.exceptions.BadRequestException;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import elaborate.editor.model.AbstractStoredEntity;
import elaborate.editor.model.AnnotationInputWrapper;
import elaborate.editor.model.ModelFactory;
import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.AnnotationMetadataItem;
import elaborate.editor.model.orm.AnnotationType;
import elaborate.editor.model.orm.AnnotationTypeMetadataItem;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.Transcription;
import elaborate.editor.model.orm.TranscriptionType;
import elaborate.editor.model.orm.User;
import elaborate.editor.resources.orm.wrappers.TranscriptionWrapper;

@Singleton
public class TranscriptionService extends AbstractStoredEntityService<Transcription> {
	private static TranscriptionService instance = new TranscriptionService();

	private TranscriptionService() {}

	public static TranscriptionService instance() {
		return instance;
	}

	@Override
	Class<? extends AbstractStoredEntity<?>> getEntityClass() {
		return Transcription.class;
	}

	@Override
	String getEntityName() {
		return "Transcription";
	}

	//  AnnotationService annotationService = AnnotationService.instance();

	public Transcription read(long project_id, long transcription_id, User user) {
		openEntityManager();
		Transcription transcription;
		try {
			abortUnlessUserHasReadPermissionsForProject(user, project_id);

			transcription = read(transcription_id);
		} finally {
			closeEntityManager();
		}
		return transcription;
	}

	public void update(long project_id, long transcription_id, TranscriptionWrapper wrapper, User user) {
		beginTransaction();
		try {
			abortUnlessUserHasWritePermissionsForProject(user, project_id);

			Transcription transcription = read(transcription_id);
			if (wrapper.getBody() != null) {
				transcription.setBody(wrapper.getBodyForDb());
			}
			persist(transcription);
			cleanupAnnotations(transcription);
			ProjectEntry projectEntry = transcription.getProjectEntry();
			String logLine = MessageFormat.format("updated transcription ''{0}'' for entry ''{1}''", transcription.getTextLayer(), projectEntry.getName());
			updateParents(projectEntry, user, logLine);

		} finally {
			commitTransaction();
		}
	}

	public void delete(long project_id, long transcription_id, User user) {
		beginTransaction();
		try {
			abortUnlessUserHasWritePermissionsForProject(user, project_id);

			Transcription transcription = find(Transcription.class, transcription_id);
			checkEntityFound(transcription, transcription_id);
			for (Annotation annotation : transcription.getAnnotations()) {
				remove(annotation);
			}
			remove(transcription);

			ProjectEntry projectEntry = transcription.getProjectEntry();

			String logLine = MessageFormat.format("deleted transcription ''{0}'' and its annotations for entry ''{1}''", transcription.getTextLayer(), projectEntry.getName());

			updateParents(projectEntry, user, logLine);
		} finally {
			commitTransaction();
		}
	}

	/* annotations */
	public Collection<Annotation> getAnnotations(long project_id, long transcription_id, User user) {
		openEntityManager();
		List<Annotation> annotations;
		try {
			abortUnlessUserHasReadPermissionsForProject(user, project_id);

			Transcription transcription = read(transcription_id);
			annotations = ImmutableList.copyOf(transcription.getAnnotations());

		} finally {
			closeEntityManager();
		}
		return annotations;
	}

	private static final int ANNOTATION_NO_START = 9000000;

	public Annotation addAnnotation(long project_id, long transcription_id, AnnotationInputWrapper annotationInput, User user) {
		beginTransaction();
		Annotation annotation;
		try {
			abortUnlessUserHasWritePermissionsForProject(user, project_id);

			Transcription transcription = read(transcription_id);
			annotation = ModelFactory.createTrackedEntity(Annotation.class, user);
			annotation.setTranscription(transcription);
			annotation.setBody(annotationInput.body);

			AnnotationType annotationType = getAnnotationType(annotationInput);
			annotation.setAnnotationType(annotationType);

			persist(annotation);
			if (!annotationInput.metadata.isEmpty()) {
				createAnnotationMetadataItems(annotation, annotationInput, annotationType);
			}

			ProjectEntry projectEntry = transcription.getProjectEntry();

			String logLine = MessageFormat.format("added annotation ''{0}'' for transcription ''{1}'' in entry ''{2}''", annotationType.getName(), transcription.getTextLayer(), projectEntry.getName());
			updateParents(projectEntry, user, logLine);
		} finally {
			commitTransaction();
		}

		long id = annotation.getId();

		beginTransaction();
		try {
			annotation = find(Annotation.class, id);
			annotation.setAnnotationNo((int) (ANNOTATION_NO_START + id));
			persist(annotation);
		} finally {
			commitTransaction();
		}

		return annotation;
	}

	private AnnotationType getAnnotationType(AnnotationInputWrapper annotationInput) {
		AnnotationType annotationType = find(AnnotationType.class, annotationInput.typeId);
		if (annotationType == null) {
			rollbackTransaction();
			throw new BadRequestException("bad typeId: no AnnotationType found with id " + annotationInput.typeId);
		}
		return annotationType;
	}

	private List<AnnotationMetadataItem> createAnnotationMetadataItems(Annotation annotation, AnnotationInputWrapper annotationInput, AnnotationType annotationType) {
		Map<String, Long> atmiMap = Maps.newHashMap();
		Iterator<AnnotationTypeMetadataItem> iterator = annotationType.getMetadataItems().iterator();
		while (iterator.hasNext()) {
			AnnotationTypeMetadataItem annotationTypeMetadataItem = iterator.next();
			atmiMap.put(annotationTypeMetadataItem.getName(), annotationTypeMetadataItem.getId());
		}

		List<AnnotationMetadataItem> annotationMetadataItems = Lists.newArrayListWithCapacity(annotationInput.metadata.size());
		for (Entry<String, String> entry : annotationInput.metadata.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			AnnotationMetadataItem ami = ModelFactory.create(AnnotationMetadataItem.class);
			if (!atmiMap.containsKey(key)) {
				rollbackTransaction();
				throw new BadRequestException("bad metadata key: no AnnotationTypeMetadataItem found with name " + key);
			}
			long atmiId = atmiMap.get(key);
			AnnotationTypeMetadataItem annotationTypeMetadataItem = find(AnnotationTypeMetadataItem.class, atmiId);
			ami.setAnnotationTypeMetadataItem(annotationTypeMetadataItem);
			ami.setData(value);
			ami.setAnnotation(annotation);
			persist(ami);
			annotationMetadataItems.add(ami);
		}
		return annotationMetadataItems;
	}

	public Annotation readAnnotation(long project_id, long annotation_id, User user) {
		openEntityManager();
		Annotation annotation;
		try {
			abortUnlessUserHasReadPermissionsForProject(user, project_id);

			annotation = find(Annotation.class, annotation_id);
		} finally {
			closeEntityManager();
		}
		return annotation;
	}

	public void updateAnnotation(long project_id, long annotation_id, AnnotationInputWrapper update, User user) {
		beginTransaction();
		try {
			abortUnlessUserHasWritePermissionsForProject(user, project_id);

			Annotation annotation = find(Annotation.class, annotation_id);
			AnnotationType annotationType = getAnnotationType(update);
			annotation.setBody(update.body).setAnnotationType(annotationType);
			persist(annotation);

			// annotationmetadata: remove existing
			for (AnnotationMetadataItem annotationMetadataItem : annotation.getAnnotationMetadataItems()) {
				remove(annotationMetadataItem);
			}

			Set<AnnotationMetadataItem> annotationMetadataItems = Sets.newHashSet();
			for (String key : update.metadata.keySet()) {
				AnnotationTypeMetadataItem annotationTypeMetadataItem = (AnnotationTypeMetadataItem) getEntityManager().createQuery("from AnnotationTypeMetadataItem as m where m.name=?1").setParameter(1, key).getSingleResult();
				if (annotationTypeMetadataItem != null) {
					AnnotationMetadataItem item = new AnnotationMetadataItem().setAnnotation(annotation).setAnnotationTypeMetadataItem(annotationTypeMetadataItem).setData(update.metadata.get(key));
					persist(item);
					annotationMetadataItems.add(item);
				}
			}

			Transcription transcription = annotation.getTranscription();
			ProjectEntry projectEntry = transcription.getProjectEntry();
			String logLine = MessageFormat.format(//
					"updated ''{0}'' annotation on ''{1}'' in transcription ''{2}'' in entry ''{3}''", //
					annotation.getAnnotationType().getName(), //
					annotation.getAnnotatedText(), //
					transcription.getTextLayer(), //
					projectEntry.getName()//
					);
			updateParents(projectEntry, user, logLine);

			//    String name = annotationMetadataItem.getAnnotationTypeMetadataItem().getName();
			//      annotationMetadataItem.setData(update.metadata.get(name));
			//      persist(annotationMetadataItem);
			//    }

		} finally {
			commitTransaction();
		}
	}

	public void deleteAnnotation(long project_id, long annotation_id, User user) {
		beginTransaction();
		try {
			abortUnlessUserHasWritePermissionsForProject(user, project_id);

			Annotation annotation = find(Annotation.class, annotation_id);
			remove(annotation);

			Transcription transcription = annotation.getTranscription();
			ProjectEntry projectEntry = transcription.getProjectEntry();

			String logLine = MessageFormat.format("deleted annotation ''{0}'' for transcription ''{1}'' for entry ''{2}''", annotation.getAnnotationType().getName(), transcription.getTextLayer(), projectEntry.getName());

			updateParents(projectEntry, user, logLine);
		} finally {
			commitTransaction();
		}
	}

	public ImmutableList<TranscriptionType> getTranscriptionTypes() {
		ImmutableList<TranscriptionType> list;
		TypedQuery<TranscriptionType> createQuery = getEntityManager().createQuery("from TranscriptionType", TranscriptionType.class);
		list = ImmutableList.copyOf(createQuery.getResultList());
		return list;
	}

	void removeOrphanedAnnotations(Transcription transcription) {
		for (Annotation annotation : transcription.getAnnotations()) {
			String annotatedText = annotation.getAnnotatedText();
			if ("".equals(annotatedText)) {
				getEntityManager().remove(annotation);
			}
		}
	}

	static Function<Annotation, Integer> EXTRACT_ANNOTATION_NO = new Function<Annotation, Integer>() {
		@Override
		public Integer apply(Annotation annotation) {
			return annotation.getAnnotationNo();
		}
	};

	void removeOrphanedAnnotationReferences(Transcription transcription) {
		List<Annotation> annotations = Lists.newArrayList(transcription.getAnnotations());
		Set<Integer> annotationNoSet = Sets.newHashSet(Iterables.transform(annotations, EXTRACT_ANNOTATION_NO));
		Set<String> orphanedAnnotationTags = Sets.newHashSet();
		String body = transcription.getBody();
		String format = "(?m)(?s)<%s id=\"(.*?)\"/>";
		String startRegex = String.format(format, Transcription.BodyTags.ANNOTATION_BEGIN); //
		Pattern startPattern = Pattern.compile(startRegex);
		Matcher startMatcher = startPattern.matcher(body);
		String endRegex = String.format(format, Transcription.BodyTags.ANNOTATION_END); //
		Pattern endPattern = Pattern.compile(endRegex);
		Matcher endMatcher = endPattern.matcher(body);
		processTags(annotationNoSet, orphanedAnnotationTags, startMatcher);
		processTags(annotationNoSet, orphanedAnnotationTags, endMatcher);
		if (!orphanedAnnotationTags.isEmpty()) {
			for (String id : orphanedAnnotationTags) {
				String beginTag = annotationBeginTag(id);
				String endTag = annotationEndTag(id);
				body = body.replace(beginTag, "").replace(endTag, "");
			}
			transcription.setBody(body);
			getEntityManager().persist(transcription);
		}
	}

	private String annotationEndTag(Object annotationNo) {
		String endtag = String.format("<%s id=\"%s\"/>", //
				Transcription.BodyTags.ANNOTATION_END, //
				annotationNo);
		return endtag;
	}

	private String annotationBeginTag(Object annotationNo) {
		String begintag = String.format("<%s id=\"%s\"/>", //
				Transcription.BodyTags.ANNOTATION_BEGIN, //
				annotationNo);
		return begintag;
	}

	private void processTags(Set<Integer> annotationNoSet, Set<String> orphanedAnnotationTags, Matcher matcher) {
		while (matcher.find()) {
			final String aNoString = matcher.group(1);
			try {
				final Integer aNo = Integer.valueOf(aNoString);
				if (!annotationNoSet.contains(aNo)) {
					orphanedAnnotationTags.add(aNoString);
				}
			} catch (final NumberFormatException e) {
				LOG.warn("found illegal annotationNo '{}'; removing.", aNoString);
				orphanedAnnotationTags.add(aNoString);
			}
		}
	}

	public void cleanupAnnotations(Transcription transcription) {
		removeOrphanedAnnotations(transcription);
		removeOrphanedAnnotationReferences(transcription);
	}

}
