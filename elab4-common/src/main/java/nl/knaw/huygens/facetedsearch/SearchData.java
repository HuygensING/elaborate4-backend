package nl.knaw.huygens.facetedsearch;

/*
 * #%L
 * elab4-common
 * =======
 * Copyright (C) 2013 - 2021 Huygens ING
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

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@XmlRootElement(name = "searchdata")
public class SearchData {
	private final long id;
	private Date created_on = new Date();
	private String json = "{}";

	public SearchData() {
		setCreatedOn(new Date());
		id = created_on.getTime();
	}

	private final ObjectMapper objectMapper = new ObjectMapper();

	public Date getCreatedOn() {
		return created_on;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
  }

	public SearchData setResults(Map<String, Object> result) {
		StringWriter stringWriter = new StringWriter();
		try {
			objectMapper.writeValue(stringWriter, result);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		setJson(stringWriter.toString());
		return this;
	}

	@JsonIgnore
	public Map<String, Object> getResults() {
		try {
			return objectMapper.readValue(json, Map.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public long getId() {
		return id;
	}

	public void setCreatedOn(Date date) {
		created_on = date;
	}
}
