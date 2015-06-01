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

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;

import elaborate.editor.model.AbstractProjectEntity;
import elaborate.editor.model.ModelFactory;
import elaborate.editor.model.Views;

@Entity
@Table(name = "project_entries")
@XmlRootElement(name = "project_entry")
public class ProjectEntry extends AbstractProjectEntity<ProjectEntry> {
	private static final long serialVersionUID = 1L;
	/* 
	 * default metadatafields for projectentries
	 */
	public static final String LOCATION = "Location";
	public static final String SHELF_NUMBER = "Shelf number";
	public static final String NAME_OF_OBJECT = "Name of object";
	public static final String TITLE_OF_TEXT = "Title of text";
	public static final String NAME_OF_AUTHOR = "Name of author";
	public static final String FOLIO_NUMBER = "Folio number";
	public static final String FOLIO_SIDE = "Folio side";
	public static final String COLUMN_ON_PAGE = "Column on page";
	public static final String PAGE = "Page number";

	public static final String PUBLISHABLE = "Publishable";

	/* 
	 * properties to persist 
	 */
	private String name;
	private String shortName;
	private boolean publishable = false;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "projectEntry")
	private List<Facsimile> facsimiles = Lists.newArrayList();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "projectEntry")
	private List<Transcription> transcriptions = Lists.newArrayList();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "projectEntry")
	private List<ProjectEntryMetadataItem> projectEntryMetadataItems = Lists.newArrayList();

	/*
	 * persistent properties getters and setters
	 */
	@JsonView({ Views.Minimal.class })
	public String getName() {
		return name;
	}

	public ProjectEntry setName(String name) {
		this.name = name;
		if (StringUtils.isEmpty(this.shortName)) {
			this.shortName = name.substring(0, Math.min(8, name.length() - 1));
		}
		return this;
	}

	@JsonView({ Views.Minimal.class })
	public String getShortName() {
		return shortName;
	}

	public ProjectEntry setShortName(String shortName) {
		this.shortName = shortName;
		return this;
	}

	@JsonView({ Views.Minimal.class })
	public boolean isPublishable() {
		return publishable;
	}

	public ProjectEntry setPublishable(boolean publishable) {
		this.publishable = publishable;
		return this;
	}

	@JsonIgnore
	public List<Facsimile> getFacsimiles() {
		return facsimiles;
	}

	public ProjectEntry setFacsimiles(List<Facsimile> facsimiles) {
		this.facsimiles = facsimiles;
		return this;

	};

	@JsonIgnore
	public List<Transcription> getTranscriptions() {
		return transcriptions;
	}

	public ProjectEntry setTranscriptions(List<Transcription> transcriptions) {
		this.transcriptions = transcriptions;
		return this;

	}

	@JsonIgnore
	public List<ProjectEntryMetadataItem> getProjectEntryMetadataItems() {
		return projectEntryMetadataItems;
	}

	public ProjectEntry setProjectEntryMetadataItems(List<ProjectEntryMetadataItem> projectEntryMetadataItems) {
		this.projectEntryMetadataItems = projectEntryMetadataItems;
		return this;
	}

	/*
	 * other functions
	 */
	public Transcription addTranscription(User creator) {
		return ModelFactory.createTrackedEntity(Transcription.class, creator)//
				.setProjectEntry(this)//
				.setBody(Transcription.DEFAULT_BODY)//
				.setTranscriptionType(ModelFactory.getDefaultTranscriptionType());
	}

	public Facsimile addFacsimile(String name, String title, User creator) {
		return ModelFactory.createTrackedEntity(Facsimile.class, creator)//
				.setProjectEntry(this)//
				.setName(name)//
				.setTitle(title);
	}

	public String getMetadataValue(String key) {
		ProjectEntryMetadataItem metadataItem = getMetadataItem(key);
		if (metadataItem != null) {
			return metadataItem.getData();
		}
		return null;
	}

	public ProjectEntryMetadataItem getMetadataItem(String key) {
		for (ProjectEntryMetadataItem projectEntryMetadataItem : getProjectEntryMetadataItems()) {
			if (projectEntryMetadataItem.getField().equals(key)) {
				return projectEntryMetadataItem;
			}
		}
		return null;
	}

	public ProjectEntryMetadataItem addMetadataItem(String key, String value, User creator) {
		return ModelFactory.createMetadataItem(ProjectEntryMetadataItem.class, key, value, creator)//
				.setProjectEntry(this);
	}

	//
	//  public int getFacsimileCount() {
	//    return ModelFactory.getEntityCount(ProjectFacsimile.class, getCurrentProjectEntryQuery());
	//  }
	//
	//  private Query getCurrentProjectEntryQuery() {
	//    Query currentProjectEntryQuery = new Query("project_entry_id", entry.getId());
	//    return currentProjectEntryQuery;
	//  }
	//
	//  public int getTranscriptionCount() {
	//    return ModelFactory.getEntityCount(ProjectTranscription.class, getCurrentProjectEntryQuery());
	//  }
	//
	//  public boolean hasFacsimiles() {
	//    return entry.getFacsimileCount() > 0;
	//  }
	//
	//  public boolean hasTranscriptions() {
	//    return entry.getTranscriptionCount() > 0;
	//  }
	//
	//  public String getMetadata(String field) {
	//    ProjectEntryMetadataItem[] projectEntryMetadataItems = entry.getProjectEntryMetadataItems();
	//    for (ProjectEntryMetadataItem projectEntryMetadataItem : projectEntryMetadataItems) {
	//      if (field.equals(projectEntryMetadataItem.getField())) {
	//        return projectEntryMetadataItem.getData();
	//      }
	//    }
	//    return "";
	//  }
	//
	//
	//  public ProjectEntryMetadataItem getProjectEntryMetadataItem(String field) {
	//    Term entryTerm = new Term("project_entry_id", Integer.valueOf(entry.getId().toString()));
	//    Term fieldTerm = new Term("field", field);
	//    Query query = new Query(entryTerm, fieldTerm);
	//    ProjectEntryMetadataItem[] entities = ModelFactory.getEntities(ProjectEntryMetadataItem.class, query);
	//    if (entities.length > 1) {
	//      Log.error("query {} gave too many ProjectMetadataItems: {}", query, entities);
	//    }
	//    return entities.length == 0 ? null : entities[0];
	//  }
	//
	//  void removeAllMetadata() {
	//    for (ProjectEntryMetadataItem projectEntryMetadataItem : entry.getProjectEntryMetadataItems()) {
	//      try {
	//        projectEntryMetadataItem.delete();
	//      } catch (StorageException e) {
	//        throw new RuntimeException(e);
	//      }
	//    }
	//  }
	//
	//  public void setMetadata(Map<String, String> metadata, User creator) {
	//    removeAllMetadata();
	//    for (Entry<String, String> metadataEntry : metadata.entrySet()) {
	//      setMetadata(metadataEntry.getKey(), metadataEntry.getValue().trim(), creator);
	//    }
	//  }
	//
	//  public String getLabel() {
	//    String label = "";
	//    String page = StringUtils.stripToNull(entry.getMetadata("Page number"));
	//    String title = StringUtils.stripToNull(entry.getMetadata("Title of text"));
	//    String author = StringUtils.stripToNull(entry.getMetadata("Name of author"));
	//    String folio = StringUtils.stripToNull(entry.getMetadata("Folio number"));
	//    String side = StringUtils.stripToNull(entry.getMetadata("Folio side"));
	//    String column = StringUtils.stripToNull(entry.getMetadata("Column on page"));
	//
	//    List<String> positionParts = Lists.newArrayList();
	//    if (page != null) {
	//      positionParts.add(String.format("page %s", page));
	//    }
	//    if (folio != null) {
	//      positionParts.add(String.format("folio %s", folio));
	//    }
	//    if (side != null) {
	//      positionParts.add(String.format("side %s", side));
	//    }
	//    if (column != null) {
	//      positionParts.add(String.format("column %s", column));
	//    }
	//    String position = Joiner.on(", ").join(positionParts);
	//
	//    if (title != null && author != null) {
	//      label = String.format("%s of '%s' by %s", position, title, author);
	//    } else if (title != null) {
	//      label = String.format("%s of '%s'", position, title);
	//    } else {
	//      label = position;
	//    }
	//    return label;
	//  }
	//
	//  public LastModified getLastModified() {
	//    Date modificationDate = entry.getProject().getCreatedOn();
	//    User modifiedBy = entry.getProject().getCreator();
	//    User modifiedBy2 = modifiedBy;
	//    LastModified lastModified = new LastModified(modificationDate, modifiedBy2);
	//
	//    updateLastModified(lastModified, entry.getProjectEntryMetadataItems());
	//    updateLastModified(lastModified, entry.getFacsimiles());
	//    updateLastModified(lastModified, entry.getTranscriptions());
	//
	//    return lastModified;
	//  }
	//
	//  private void updateLastModified(LastModified lastModified, AbstractTrackedEntity[] trackedEntryChildren) {
	//    for (AbstractTrackedEntity entity : trackedEntryChildren) {
	//      Date modifiedOn = entity.getModifiedOn();
	//      if (modifiedOn.after(lastModified.getDate())) {
	//        lastModified.setDate(modifiedOn);
	//        lastModified.setUser(entity.getModifier());
	//      }
	//    }
	//  }
	//

}
