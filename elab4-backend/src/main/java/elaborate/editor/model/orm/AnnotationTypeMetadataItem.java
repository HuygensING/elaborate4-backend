package elaborate.editor.model.orm;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2016 Huygens ING
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

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;

import elaborate.editor.model.AbstractStoredEntity;

@Entity
@Table(name = "annotation_type_metadata_items")
@XmlRootElement
public class AnnotationTypeMetadataItem extends AbstractStoredEntity<AnnotationTypeMetadataItem> {
	private static final long serialVersionUID = 1L;

	private String name;
	private String description;

	@ManyToOne
	@JoinColumn(name = "annotation_type_id", columnDefinition = "int4")
	private AnnotationType annotationType;

	public String getName() {
		return name;
	}

	public AnnotationTypeMetadataItem setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public AnnotationTypeMetadataItem setDescription(String description) {
		this.description = description;
		return this;
	}

	@JsonIgnore
	public AnnotationType getAnnotationType() {
		return annotationType;
	}

	public AnnotationTypeMetadataItem setAnnotationType(AnnotationType annotationType) {
		this.annotationType = annotationType;
		return this;
	}

	@Transient
	@JsonIgnore
	public Map<String, Object> getDataMap() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("name", getName());
		map.put("description", getDescription());
		return map;
	}

}
