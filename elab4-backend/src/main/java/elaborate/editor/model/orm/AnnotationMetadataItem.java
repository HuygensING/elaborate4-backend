package elaborate.editor.model.orm;

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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

import elaborate.editor.model.AbstractStoredEntity;

@Entity
@Table(name = "annotation_metadata_items")
@XmlRootElement
public class AnnotationMetadataItem extends AbstractStoredEntity<AnnotationMetadataItem> {
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "annotation_id", columnDefinition = "int4")
	private Annotation annotation;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "annotation_type_metadata_item_id", columnDefinition = "int4")
	private AnnotationTypeMetadataItem annotationTypeMetadataItem;

	private String data;

	@JsonIgnore
	public Annotation getAnnotation() {
		return annotation;
	}

	public AnnotationMetadataItem setAnnotation(Annotation annotation) {
		this.annotation = annotation;
		return this;
	}

	public AnnotationTypeMetadataItem getAnnotationTypeMetadataItem() {
		return annotationTypeMetadataItem;
	}

	public AnnotationMetadataItem setAnnotationTypeMetadataItem(AnnotationTypeMetadataItem annotationTypeMetadataItem) {
		this.annotationTypeMetadataItem = annotationTypeMetadataItem;
		return this;
	}

	public String getData() {
		return data;
	}

	public AnnotationMetadataItem setData(String data) {
		this.data = data;
		return this;
	}

}
