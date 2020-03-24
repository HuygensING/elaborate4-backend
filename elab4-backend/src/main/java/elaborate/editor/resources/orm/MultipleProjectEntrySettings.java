package elaborate.editor.resources.orm;

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
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import elaborate.editor.model.orm.ProjectEntry;

@XmlRootElement
public class MultipleProjectEntrySettings {
	private List<Long> projectEntryIds;
	private Map<String, Object> settings;
	private boolean publishable;
	private boolean changePublishable = false;

	private MultipleProjectEntrySettings() {}

	public void setProjectEntryIds(List<Long> _projectEntryIds) {
		this.projectEntryIds = _projectEntryIds;
	}

	public List<Long> getProjectEntryIds() {
		return projectEntryIds;
	}

	public Map<String, Object> getSettings() {
		return settings;
	}

	public void setSettings(Map<String, Object> _settings) {
		if (_settings.containsKey(ProjectEntry.PUBLISHABLE)) {
			publishable = (Boolean) _settings.remove(ProjectEntry.PUBLISHABLE);
			changePublishable = true;
		}
		this.settings = _settings;
	}

	public boolean changePublishable() {
		return changePublishable;
	}

	public boolean getPublishableSetting() {
		return publishable;
	}

}
