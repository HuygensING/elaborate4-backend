package elaborate.editor.model.orm;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2021 Huygens ING
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

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;

import elaborate.editor.model.AbstractTrackedEntity;

@Entity
@Table(name = "annotations")
@XmlRootElement(name = "annotation")
public class Annotation extends AbstractTrackedEntity<Annotation> {
	private static final long serialVersionUID = 1L;
	public static final String TYPE = "annotation";

	/*
	 * properties to persist
	 */

	private int annotationNo;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "annotation_type_id", columnDefinition = "int4")
  private
  AnnotationType annotationType;

	private String body;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "annotation")
  private
  Set<AnnotationMetadataItem> annotationMetadataItems = Sets.newHashSet();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transcription_id", columnDefinition = "int4")
  private
  Transcription transcription;

	/*
	 * persistent properties getters and setters
	 */

	public int getAnnotationNo() {
		return annotationNo;
	}

	public void setAnnotationNo(int annotationNo) {
		this.annotationNo = annotationNo;
  }

	public AnnotationType getAnnotationType() {
		return annotationType;
	}

	public void setAnnotationType(AnnotationType annotationType) {
		this.annotationType = annotationType;
  }

	public String getBody() {
		return body;
	}

	public Annotation setBody(String body) {
		this.body = body;
		return this;
	}

	public Set<AnnotationMetadataItem> getAnnotationMetadataItems() {
		return annotationMetadataItems;
	}

	public void setAnnotationMetadataItems(Set<AnnotationMetadataItem> annotationMetadataItems) {
		this.annotationMetadataItems = annotationMetadataItems;
	}

	@JsonIgnore
	public Transcription getTranscription() {
		return transcription;
	}

	public void setTranscription(Transcription transcription) {
		this.transcription = transcription;
  }

	/* transient methods */

	//
	// @OneToMany
	// AnnotationMetadataItem[] getAnnotationMetadataItems();
	//
	// @Implemented
	// AnnotationMetadataItem addMetadataItem(AnnotationTypeMetadataItem annotationTypeMetadataItem, String value, User user);
	//
	// @Implemented
	// void clearMetadata();
	//
	// @Implemented
	// void addMetadata(String name, String value, User user);
	//
	// @Implemented
	// String getAnnotatedText();
	//
	// @Implemented
	// String getLabel();
	//
	// @Implemented
	// /**
	// *
	// * returns body with clickable urls
	// */
	// String getActiveBody();
	// private final Annotation annotation;
	//
	// public AnnotationImpl(Annotation _annotation) {
	// this.annotation = _annotation;
	// }
	//
	// public AnnotationMetadataItem addMetadataItem(AnnotationTypeMetadataItem annotationTypeMetadataItem, String value, User user) {
	// if (!annotationTypeMetadataItem.getAnnotationType().equals(annotation.getAnnotationType())) {
	// throw new RuntimeException("annotationTypeMetadataItem.getAnnotationType() != annotation.getAnnotationType()");
	// }
	// return ModelFactory.createAnnotationMetadataItem(annotation, annotationTypeMetadataItem, value, user);
	// }
	//
	// public void clearMetadata() {
	// for (AnnotationMetadataItem annotationMetadataItem : annotation.getAnnotationMetadataItems()) {
	// try {
	// annotationMetadataItem.delete();
	// } catch (StorageException e) {
	// Log.error("Couldn't delete");
	// throw new RuntimeException(e);
	// }
	// }
	// }
	//
	// public void addMetadata(String name, String value, User user) {
	// ModelFactory.createAnnotationMetadataItem(annotation, getAnnotationTypeMetadataItem(name), value, user);
	// }
	//
	// // Convenience method
	// public Project getProject() {
	// return annotation.getTranscription().getProject();
	// }
	//
	// private AnnotationTypeMetadataItem getAnnotationTypeMetadataItem(String name) {
	// for (AnnotationTypeMetadataItem annotationTypeMetadataItem : annotation.getAnnotationType().getAnnotationTypeMetadataItems()) {
	// if (name.equals(annotationTypeMetadataItem.getName())) {
	// return annotationTypeMetadataItem;
	// }
	// }
	// return null;
	// }
	//
	// public void index(boolean commitNow) {
	// new SolrIndexer().index(annotation, commitNow);
	// }
	//
	// public void deindex() {
	// new SolrIndexer().deindex(annotation);
	// }
	//
	// @SuppressWarnings("boxing")

	@JsonIgnore
	public String getAnnotatedText() {
		String body = getTranscription().getBody();
		if (body == null) {
			return "";
		}
		String regex = String.format("(?m)(?s)<%s id=\"%s\"/>(.*)<%s id=\"%s\"/>", //
				Transcription.BodyTags.ANNOTATION_BEGIN, //
				getAnnotationNo(), //
				Transcription.BodyTags.ANNOTATION_END, //
				getAnnotationNo());
		// Log.info("regex={}", regex);
		// Log.info("body={}", body);
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(body);
		return matcher.find() ? matcher.group(1) : "";
	}
	//
	// public String getSolrId() {
	// return Annotation.TYPE + annotation.getId();
	// }
	//
	// public String getActiveBody() {
	// return StringUtil.activateURLs(annotation.getBody());
	// }
	//
	// public String getLabel() {
	// String cleanBody = annotation.getBody().replaceAll("<.*?>", "");
	// String normalizedType = SolrUtil.normalize(annotation.getAnnotationType().getName());
	// String value = StringUtils.defaultIfBlank(cleanBody, annotation.getAnnotatedText());
	// return String.format("%s:%s", normalizedType, value);
	// }
	//
	// public void save(User modifier) {
	// ModelFactory.save(annotation, modifier);
	// AnnotationMetadataItem[] annotationMetadataItems = annotation.getAnnotationMetadataItems();
	// for (AnnotationMetadataItem annotationMetadataItem : annotationMetadataItems) {
	// annotationMetadataItem.refresh();
	// }
	// }

}
