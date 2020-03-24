package elaborate.editor.model.orm;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2020 Huygens ING
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

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import elaborate.editor.model.AbstractTrackedEntity;

@Entity
@Table(name = "project_metadata_fields")
@XmlRootElement
public class ProjectMetadataField extends AbstractTrackedEntity<ProjectMetadataField> {
	private static final long serialVersionUID = 1L;

	private String fieldName = "";
	private String valueOptions = "";

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getValueOptions() {
		return valueOptions;
	}

	public void setValueOptions(String valueOptions) {
		this.valueOptions = valueOptions;
	}

	public boolean hasValueOptions() {
		return StringUtils.isNotBlank(valueOptions);
	}

	@Transient
	public List<String> getValueOptionsAsList() {
		return Lists.newArrayList(Splitter.on(',').split(valueOptions));
	}

}
