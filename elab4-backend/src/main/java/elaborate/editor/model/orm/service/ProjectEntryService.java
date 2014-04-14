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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nl.knaw.huygens.jaxrstools.exceptions.NotFoundException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import elaborate.editor.model.orm.Facsimile;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.ProjectEntryMetadataItem;
import elaborate.editor.model.orm.Transcription;
import elaborate.editor.model.orm.User;
import elaborate.editor.resources.orm.MultipleProjectEntrySettings;
import elaborate.editor.resources.orm.wrappers.TranscriptionWrapper;

public class ProjectEntryService extends AbstractStoredEntityService<ProjectEntry> {
	private static ProjectEntryService instance = new ProjectEntryService();
	ProjectService projectService = ProjectService.instance();

	private ProjectEntryService() {}

	public static ProjectEntryService instance() {
		return instance;
	}

	@Override
	Class<ProjectEntry> getEntityClass() {
		return ProjectEntry.class;
	}

	@Override
	String getEntityName() {
		return "ProjectEntry";
	}

	/* CRUD methods */
	public ProjectEntry read(long entry_id, User user) {
		openEntityManager();
		ProjectEntry projectEntry;
		try {
			projectEntry = super.read(entry_id);
		} finally {
			closeEntityManager();
		}
		return projectEntry;
	}

	public void update(long entry_id, ProjectEntry updateEntry, User user) {
		beginTransaction();
		ProjectEntry projectEntry;
		try {
			projectEntry = super.read(entry_id);
			projectEntry.setName(updateEntry.getName());
			projectEntry.setShortName(updateEntry.getShortName());
			projectEntry.setPublishable(updateEntry.isPublishable());
			super.update(projectEntry);
			persist(projectEntry.getProject().addLogEntry(MessageFormat.format("updated entry {0}", entry_id), user));
		} finally {
			commitTransaction();
		}

		beginTransaction();
		try {
			projectEntry = super.read(entry_id);
			setModifiedBy(projectEntry, user);
		} finally {
			commitTransaction();
		}
	}

	public void delete(long entry_id, User user) {
		beginTransaction();
		try {
			ProjectEntry deletedProjectEntry = super.delete(entry_id);
			getSolrIndexer().deindex(entry_id);
			setModifiedBy(deletedProjectEntry.getProject(), user);
			persist(deletedProjectEntry.getProject().addLogEntry(MessageFormat.format("deleted entry {0}", entry_id), user));
		} finally {
			commitTransaction();
		}
	}

	/* other public methods */

	/* transcriptions */
	public Collection<Transcription> getTranscriptions(long id, User user) {
		addMissingTextLayers(id, user);
		openEntityManager();
		ImmutableList<Transcription> transcriptions;
		try {
			ProjectEntry projectEntry = find(getEntityClass(), id);
			transcriptions = ImmutableList.copyOf(projectEntry.getTranscriptions());
		} finally {
			closeEntityManager();
		}
		return transcriptions;
	}

	private void addMissingTextLayers(long id, User user) {
		ProjectEntry projectEntry;
		Set<String> textLayers;
		openEntityManager();
		try {
			projectEntry = find(getEntityClass(), id);
			Project project = projectEntry.getProject();
			textLayers = Sets.newHashSet(project.getTextLayers());
			for (Transcription transcription : projectEntry.getTranscriptions()) {
				textLayers.remove(transcription.getTextLayer());
			}
		} finally {
			closeEntityManager();
		}
		if (!textLayers.isEmpty()) {
			beginTransaction();
			for (String textLayer : textLayers) {
				Transcription transcription = projectEntry.addTranscription(user).setTextLayer(textLayer);
				persist(transcription);
			}
			commitTransaction();
		}
	}

	public Transcription addTranscription(long id, TranscriptionWrapper transcriptionInput, User user) {
		beginTransaction();
		Transcription transcription;
		try {
			ProjectEntry projectEntry = find(getEntityClass(), id);
			transcription = projectEntry.addTranscription(user)//
					.setBody(transcriptionInput.getBodyForDb())//
					.setTextLayer(transcriptionInput.getTextLayer());
			persist(transcription);
		} finally {
			commitTransaction();
		}
		return transcription;
	}

	/* facsimiles */
	public Collection<Facsimile> getFacsimiles(long id, User user) {
		openEntityManager();
		ImmutableList<Facsimile> facsimiles;
		try {
			ProjectEntry projectEntry = find(getEntityClass(), id);
			facsimiles = ImmutableList.copyOf(projectEntry.getFacsimiles());
		} finally {
			closeEntityManager();
		}
		return facsimiles;
	}

	public Facsimile addFacsimile(long entry_id, Facsimile facsimileData, User user) {
		beginTransaction();
		Facsimile facsimile;
		try {
			ProjectEntry projectEntry = find(getEntityClass(), entry_id);
			Project project = projectEntry.getProject();

			facsimile = projectEntry.addFacsimile(facsimileData.getName(), facsimileData.getTitle(), user)//
					.setFilename(facsimileData.getFilename())//
					.setZoomableUrl(facsimileData.getZoomableUrl());
			persist(facsimile);
			persist(project.addLogEntry(MessageFormat.format("added facsimile ''{0}'' for entry ''{1}''", facsimile.getFilename(), projectEntry.getName()), user));
		} finally {
			commitTransaction();
		}
		return facsimile;
	}

	public Facsimile readFacsimile(long project_id, long facsimile_id, User user) {
		openEntityManager();
		Facsimile facsimile;
		try {
			projectService.setEntityManager(getEntityManager());
			projectService.getProjectIfUserIsAllowed(project_id, user);

			facsimile = find(Facsimile.class, facsimile_id);
		} finally {
			closeEntityManager();
		}
		return facsimile;
	}

	public Facsimile updateFacsimile(long project_id, long facsimile_id, Facsimile facsimileData, User user) {
		beginTransaction();
		Facsimile facsimile;
		try {
			projectService.setEntityManager(getEntityManager());
			Project project = projectService.getProjectIfUserIsAllowed(project_id, user);

			facsimile = find(Facsimile.class, facsimile_id);
			if (facsimile == null) {
				throw new NotFoundException("no facsimile with id " + facsimile_id + " found");
			}
			facsimile.setName(facsimileData.getName())//
					.setFilename(facsimileData.getFilename())//
					.setZoomableUrl(facsimileData.getZoomableUrl());
			persist(facsimile);
			ProjectEntry projectEntry = facsimile.getProjectEntry();
			persist(project.addLogEntry(MessageFormat.format("updated facsimile ''{0}'' for entry ''{1}''", facsimile.getFilename(), projectEntry.getName()), user));
		} finally {
			commitTransaction();
		}
		return facsimile;
	}

	public Facsimile deleteFacsimile(long project_id, long facsimile_id, User user) {
		beginTransaction();
		Facsimile facsimile;
		try {
			projectService.setEntityManager(getEntityManager());
			projectService.getProjectIfUserIsAllowed(project_id, user);

			facsimile = find(Facsimile.class, facsimile_id);
			ProjectEntry projectEntry = facsimile.getProjectEntry();
			persist(projectEntry.getProject().addLogEntry(MessageFormat.format("deleted facsimile ''{0}'' for entry ''{1}''", facsimile.getFilename(), projectEntry.getName()), user));
			remove(facsimile);
		} finally {
			commitTransaction();
		}
		return facsimile;
	}

	/* projectentrysettings */
	public Map<String, String> getProjectEntrySettings(long entry_id, User user) {
		openEntityManager();
		Map<String, String> map = Maps.newHashMap();
		try {
			ProjectEntry pe = read(entry_id);
			Iterable<String> projectEntryMetadataFieldnames = pe.getProject().getProjectEntryMetadataFieldnames();
			for (String fieldname : projectEntryMetadataFieldnames) {
				map.put(fieldname, "");
			}
			for (ProjectEntryMetadataItem pemi : pe.getProjectEntryMetadataItems()) {
				map.put(pemi.getField(), pemi.getData());
			}

		} finally {
			closeEntityManager();
		}
		return map;
	}

	public void updateProjectEntrySettings(long project_id, long entry_id, Map<String, Object> projectEntrySettings, User creator) {
		beginTransaction();
		ProjectEntry pe;
		try {
			projectService.setEntityManager(getEntityManager());
			projectService.getProjectIfUserIsAllowed(project_id, creator);

			pe = read(entry_id);
			for (ProjectEntryMetadataItem projectEntryMetadataItem : pe.getProjectEntryMetadataItems()) {
				getEntityManager().remove(projectEntryMetadataItem);
			}

			Object publishableSetting = projectEntrySettings.remove(ProjectEntry.PUBLISHABLE);
			if (publishableSetting != null) {
				pe.setPublishable((Boolean) publishableSetting);
			}

			for (Entry<String, Object> settingsEntry : projectEntrySettings.entrySet()) {
				String key = settingsEntry.getKey();
				String value = (String) settingsEntry.getValue();
				ProjectEntryMetadataItem pemi = pe.addMetadataItem(key, value, creator);
				persist(pemi);
			}
		} finally {
			commitTransaction();
		}

		// This needs to go in a seperate transaction, because only after the previous commitTransaction has
		// the projectentrymetadataitems been properly updated, which is needed for the reindex called from
		// updateParents
		beginTransaction();
		try {
			pe = read(entry_id);
			String logLine = MessageFormat.format("updated metadata for entry ''{0}''", pe.getName());
			updateParents(pe, creator, logLine);
		} finally {
			commitTransaction();
		}
	}

	public void updateMultipleProjectEntrySettings(long project_id, MultipleProjectEntrySettings mpes, User user) {
		beginTransaction();
		Set<Long> modifiedEntryIds = Sets.newHashSet();
		try {
			projectService.setEntityManager(getEntityManager());
			Project project = projectService.getProjectIfUserIsAllowed(project_id, user);

			Map<String, Object> settings = mpes.getSettings();
			Set<Entry<String, Object>> settingsEntrySet = settings.entrySet();
			for (Long entry_id : mpes.getProjectEntryIds()) {
				LOG.info("entryId={}", entry_id);
				ProjectEntry pe = read(entry_id);

				if (mpes.changePublishable()) {
					LOG.info("changepublishable to {}", mpes.getPublishableSetting());
					pe.setPublishable(mpes.getPublishableSetting());
				}

				for (Entry<String, Object> entry : settingsEntrySet) {
					String key = entry.getKey();
					String value = (String) entry.getValue();

					ProjectEntryMetadataItem pemItem = pe.getMetadataItem(key);
					if (pemItem == null) {
						LOG.info("add new setting: {}={}", key, value);
						persist(pe.addMetadataItem(key, value, user));
					} else {
						LOG.info("modify existing setting: {}={}", key, value);
						pemItem.setData(value);
						persist(pemItem);
						setModifiedBy(pemItem, user);
					}
					modifiedEntryIds.add(entry_id);
				}
			}
			setModifiedBy(project, user);

		} finally {
			commitTransaction();
		}

		beginTransaction();
		try {
			for (Long id : modifiedEntryIds) {
				ProjectEntry pe = read(id);
				setModifiedBy(pe, user);
			}
		} finally {
			commitTransaction();
		}

	}

	public PrevNext getPrevNextProjectEntryIds(long entry_id) {
		openEntityManager();
		PrevNext pn = new PrevNext();
		try {
			projectService.setEntityManager(getEntityManager());

			ProjectEntry pe = read(entry_id);
			long project_id = pe.getProject().getId();
			List<Long> projectEntryIdsInOrder = projectService.getProjectEntryIdsInOrder(project_id);
			int index = projectEntryIdsInOrder.indexOf(entry_id);

			int prevIndex = index - 1;
			if (prevIndex > -1) {
				pn.prev = projectEntryIdsInOrder.get(prevIndex);
			}

			int nextIndex = index + 1;
			if (nextIndex < projectEntryIdsInOrder.size()) {
				pn.next = projectEntryIdsInOrder.get(nextIndex);
			}

		} finally {
			closeEntityManager();
		}
		return pn;
	}
	/* private methods */

}
