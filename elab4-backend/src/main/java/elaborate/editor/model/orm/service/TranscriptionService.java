package elaborate.editor.model.orm.service;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Singleton;

import nl.knaw.huygens.jaxrstools.exceptions.BadRequestException;

import com.google.common.collect.ImmutableList;
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
		checkProjectPermissions(project_id, user);

		Transcription transcription = read(transcription_id);
		closeEntityManager();
		return transcription;
	}

	public void update(long project_id, long transcription_id, TranscriptionWrapper wrapper, User user) {
		beginTransaction();

		checkProjectPermissions(project_id, user);
		Transcription transcription = read(transcription_id);
		if (wrapper.getBody() != null) {
			transcription.setBody(wrapper.getBodyForDb());
		}
		merge(transcription);
		ProjectEntry projectEntry = transcription.getProjectEntry();
		String logLine = MessageFormat.format("updated transcription ''{0}'' for entry ''{1}''", transcription.getTextLayer(), projectEntry.getName());
		updateParents(projectEntry, user, logLine);

		commitTransaction();
	}

	public void delete(long project_id, long transcription_id, User user) {
		beginTransaction();

		checkProjectPermissions(project_id, user);

		Transcription transcription = find(Transcription.class, transcription_id);
		checkEntityFound(transcription, transcription_id);
		for (Annotation annotation : transcription.getAnnotations()) {
			remove(annotation);
		}
		remove(transcription);

		ProjectEntry projectEntry = transcription.getProjectEntry();

		String logLine = MessageFormat.format("deleted transcription ''{0}'' and its annotations for entry ''{1}''", transcription.getTextLayer(), projectEntry.getName());

		updateParents(projectEntry, user, logLine);

		commitTransaction();
	}

	/* annotations */
	public Collection<Annotation> getAnnotations(long project_id, long transcription_id, User user) {
		openEntityManager();

		checkProjectPermissions(project_id, user);

		Transcription transcription = read(transcription_id);
		List<Annotation> annotations = ImmutableList.copyOf(transcription.getAnnotations());

		closeEntityManager();
		return annotations;
	}

	private static final int ANNOTATION_NO_START = 9000000;

	public Annotation addAnnotation(long project_id, long transcription_id, AnnotationInputWrapper annotationInput, User user) {
		beginTransaction();

		checkProjectPermissions(project_id, user);
		Transcription transcription = read(transcription_id);
		Annotation annotation = ModelFactory.createTrackedEntity(Annotation.class, user);
		annotation.setTranscription(transcription);
		annotation.setBody(annotationInput.body);

		AnnotationType annotationType = getAnnotationType(annotationInput);
		annotation.setAnnotationType(annotationType);

		persist(annotation);
		if (!annotationInput.metadata.isEmpty()) {
			createAnnotationMetadataItems(annotation, annotationInput, annotationType);
		}

		ProjectEntry projectEntry = transcription.getProjectEntry();

		String logLine = MessageFormat.format("added annotation ''{0}'' for transcription ''{1}'' for entry ''{2}''", annotationType.getName(), transcription.getTextLayer(), projectEntry.getName());
		updateParents(projectEntry, user, logLine);
		commitTransaction();

		long id = annotation.getId();

		beginTransaction();
		annotation = find(Annotation.class, id);
		annotation.setAnnotationNo((int) (ANNOTATION_NO_START + id));
		persist(annotation);
		commitTransaction();

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
		checkProjectPermissions(project_id, user);
		Annotation annotation = find(Annotation.class, annotation_id);
		closeEntityManager();
		return annotation;
	}

	public void updateAnnotation(long project_id, long annotation_id, AnnotationInputWrapper update, User user) {
		beginTransaction();
		checkProjectPermissions(project_id, user);

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

		//    String name = annotationMetadataItem.getAnnotationTypeMetadataItem().getName();
		//      annotationMetadataItem.setData(update.metadata.get(name));
		//      persist(annotationMetadataItem);
		//    }

		commitTransaction();
	}

	public void deleteAnnotation(long project_id, long annotation_id, User user) {
		beginTransaction();

		checkProjectPermissions(project_id, user);

		Annotation annotation = find(Annotation.class, annotation_id);
		remove(annotation);

		Transcription transcription = annotation.getTranscription();
		ProjectEntry projectEntry = transcription.getProjectEntry();

		String logLine = MessageFormat.format("deleted annotation ''{0}'' for transcription ''{1}'' for entry ''{2}''", annotation.getAnnotationType().getName(), transcription.getTextLayer(), projectEntry.getName());

		updateParents(projectEntry, user, logLine);

		commitTransaction();
	}

}
