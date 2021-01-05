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

import java.util.Date;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

import elaborate.editor.model.AbstractStoredEntity;
import nl.knaw.huygens.facetedsearch.SearchData;

@Entity
@Table(name = "searchdata")
@XmlRootElement(name = "storable_searchdata")
@Access(value = AccessType.PROPERTY)
public class StorableSearchData extends AbstractStoredEntity<StorableSearchData> {
	@Transient
	private static SearchData delegate;

	private static final long serialVersionUID = 1L;

	public StorableSearchData() {
		delegate = new SearchData();
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedOn() {
		return delegate.getCreatedOn();
	}

	public StorableSearchData setCreatedOn(Date date) {
		delegate.setCreatedOn(date);
		return this;
	}

	public String getJson() {
		return delegate.getJson();
	}

	public StorableSearchData setJson(String json) {
		delegate.setJson(json);
		return this;
	}

	public StorableSearchData setResults(Map<String, Object> results) {
		delegate.setResults(results);
		return this;
	}

	@JsonIgnore
	@Transient
	public Map<String, Object> getResults() {
		return delegate.getResults();
	}
}
