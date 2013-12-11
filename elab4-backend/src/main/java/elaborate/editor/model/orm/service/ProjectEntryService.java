package elaborate.editor.model.orm.service;

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
	private static ProjectEntryService instance;
	ProjectService projectService = ProjectService.instance();

	private ProjectEntryService() {}

	public static ProjectEntryService instance() {
		if (instance == null) {
			instance = new ProjectEntryService();
		}
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
		ProjectEntry projectEntry = super.read(entry_id);
		closeEntityManager();
		return projectEntry;
	}

	public void update(long entry_id, ProjectEntry updateEntry, User user) {
		beginTransaction();
		ProjectEntry projectEntry = super.read(entry_id);
		projectEntry.setName(updateEntry.getName());
		projectEntry.setPublishable(updateEntry.isPublishable());
		super.update(projectEntry);
		persist(projectEntry.getProject().addLogEntry(MessageFormat.format("updated entry {0}", entry_id), user));
		commitTransaction();

		beginTransaction();
		projectEntry = super.read(entry_id);
		setModifiedBy(projectEntry, user);
		commitTransaction();
	}

	public void delete(long entry_id, User user) {
		beginTransaction();
		ProjectEntry deletedProjectEntry = super.delete(entry_id);
		setModifiedBy(deletedProjectEntry.getProject(), user);
		persist(deletedProjectEntry.getProject().addLogEntry(MessageFormat.format("deleted entry {0}", entry_id), user));
		commitTransaction();
	}

	/* other public methods */

	/* transcriptions */
	public Collection<Transcription> getTranscriptions(long id, User user) {
		addMissingTextLayers(id, user);
		openEntityManager();
		ProjectEntry projectEntry = find(getEntityClass(), id);
		ImmutableList<Transcription> transcriptions = ImmutableList.copyOf(projectEntry.getTranscriptions());
		closeEntityManager();
		return transcriptions;
	}

	private void addMissingTextLayers(long id, User user) {
		beginTransaction();
		ProjectEntry projectEntry = find(getEntityClass(), id);
		Project project = projectEntry.getProject();
		Set<String> textLayers = Sets.newHashSet(project.getTextLayers());
		for (Transcription transcription : projectEntry.getTranscriptions()) {
			textLayers.remove(transcription.getTextLayer());
		}
		if (!textLayers.isEmpty()) {
			for (String textLayer : textLayers) {
				Transcription transcription = projectEntry.addTranscription(user).setTextLayer(textLayer);
				persist(transcription);
			}
		}
		commitTransaction();
	}

	public Transcription addTranscription(long id, TranscriptionWrapper transcriptionInput, User user) {
		beginTransaction();
		ProjectEntry projectEntry = find(getEntityClass(), id);
		Transcription transcription = projectEntry.addTranscription(user)//
				.setBody(transcriptionInput.getBodyForDb())//
				.setTextLayer(transcriptionInput.getTextLayer());
		persist(transcription);
		commitTransaction();
		return transcription;
	}

	/* facsimiles */
	public Collection<Facsimile> getFacsimiles(long id, User user) {
		openEntityManager();
		ProjectEntry projectEntry = find(getEntityClass(), id);
		ImmutableList<Facsimile> facsimiles = ImmutableList.copyOf(projectEntry.getFacsimiles());
		closeEntityManager();
		return facsimiles;
	}

	public Facsimile addFacsimile(long entry_id, Facsimile facsimileData, User user) {
		beginTransaction();
		ProjectEntry projectEntry = find(getEntityClass(), entry_id);
		Project project = projectEntry.getProject();

		Facsimile facsimile = projectEntry.addFacsimile(facsimileData.getName(), facsimileData.getTitle(), user)//
				.setFilename(facsimileData.getFilename())//
				.setZoomableUrl(facsimileData.getZoomableUrl());
		persist(facsimile);
		persist(project.addLogEntry(MessageFormat.format("added facsimile ''{0}'' for entry ''{1}''", facsimile.getFilename(), projectEntry.getName()), user));
		commitTransaction();
		return facsimile;
	}

	public Facsimile readFacsimile(long project_id, long facsimile_id, User user) {
		openEntityManager();
		projectService.setEntityManager(getEntityManager());
		projectService.getProjectIfUserIsAllowed(project_id, user);

		Facsimile facsimile = find(Facsimile.class, facsimile_id);
		closeEntityManager();
		return facsimile;
	}

	public Facsimile updateFacsimile(long project_id, long facsimile_id, Facsimile facsimileData, User user) {
		beginTransaction();
		projectService.setEntityManager(getEntityManager());
		Project project = projectService.getProjectIfUserIsAllowed(project_id, user);

		Facsimile facsimile = find(Facsimile.class, facsimile_id);
		if (facsimile == null) {
			throw new NotFoundException("no facsimile with id " + facsimile_id + " found");
		}
		facsimile.setName(facsimileData.getName())//
				.setFilename(facsimileData.getFilename())//
				.setZoomableUrl(facsimileData.getZoomableUrl());
		persist(facsimile);
		ProjectEntry projectEntry = facsimile.getProjectEntry();
		persist(project.addLogEntry(MessageFormat.format("updated facsimile ''{0}'' for entry ''{1}''", facsimile.getFilename(), projectEntry.getName()), user));
		commitTransaction();
		return facsimile;
	}

	public Facsimile deleteFacsimile(long project_id, long facsimile_id, User user) {
		beginTransaction();
		projectService.setEntityManager(getEntityManager());
		projectService.getProjectIfUserIsAllowed(project_id, user);

		Facsimile facsimile = find(Facsimile.class, facsimile_id);
		ProjectEntry projectEntry = facsimile.getProjectEntry();
		persist(projectEntry.getProject().addLogEntry(MessageFormat.format("deleted facsimile ''{0}'' for entry ''{1}''", facsimile.getFilename(), projectEntry.getName()), user));
		remove(facsimile);
		commitTransaction();
		return facsimile;
	}

	/* projectentrysettings */
	public Map<String, String> getProjectEntrySettings(long entry_id, User user) {
		openEntityManager();

		ProjectEntry pe = read(entry_id);
		Map<String, String> map = Maps.newHashMap();
		String[] projectEntryMetadataFieldnames = pe.getProject().getProjectEntryMetadataFieldnames();
		for (String fieldname : projectEntryMetadataFieldnames) {
			map.put(fieldname, "");
		}
		for (ProjectEntryMetadataItem pemi : pe.getProjectEntryMetadataItems()) {
			map.put(pemi.getField(), pemi.getData());
		}

		closeEntityManager();
		return map;
	}

	public void updateProjectEntrySettings(long project_id, long entry_id, Map<String, String> projectEntrySettings, User creator) {
		beginTransaction();
		projectService.setEntityManager(getEntityManager());
		projectService.getProjectIfUserIsAllowed(project_id, creator);

		ProjectEntry pe = read(entry_id);
		for (ProjectEntryMetadataItem projectEntryMetadataItem : pe.getProjectEntryMetadataItems()) {
			getEntityManager().remove(projectEntryMetadataItem);
		}

		for (Entry<String, String> settingsEntry : projectEntrySettings.entrySet()) {
			String key = settingsEntry.getKey();
			String value = settingsEntry.getValue();
			ProjectEntryMetadataItem pemi = pe.addMetadataItem(key, value, creator);
			persist(pemi);
		}
		String logLine = MessageFormat.format("updated metadata for entry ''{0}''", pe.getName());
		updateParents(pe, creator, logLine);

		commitTransaction();
	}

	public void updateMultipleProjectEntrySettings(long project_id, MultipleProjectEntrySettings mpes, User user) {
		beginTransaction();
		projectService.setEntityManager(getEntityManager());
		Project project = projectService.getProjectIfUserIsAllowed(project_id, user);

		Map<String, Object> settings = mpes.getSettings();
		Set<Entry<String, Object>> settingsEntrySet = settings.entrySet();
		Set<Long> modifiedEntryIds = Sets.newHashSet();
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

		commitTransaction();

		beginTransaction();
		for (Long id : modifiedEntryIds) {
			ProjectEntry pe = read(id);
			setModifiedBy(pe, user);
		}
		commitTransaction();

	}

	public void delete(long project_id, long transcription_id, User user) {
		// TODO Auto-generated method stub

	}

	public PrevNext getPrevNextProjectEntryIds(long entry_id) {
		openEntityManager();
		projectService.setEntityManager(getEntityManager());

		ProjectEntry pe = read(entry_id);
		long project_id = pe.getProject().getId();
		List<Long> projectEntryIdsInOrder = projectService.getProjectEntryIdsInOrder(project_id);
		int index = projectEntryIdsInOrder.indexOf(entry_id);

		PrevNext pn = new PrevNext();
		int prevIndex = index - 1;
		if (prevIndex > -1) {
			pn.prev = projectEntryIdsInOrder.get(prevIndex);
		}

		int nextIndex = index + 1;
		if (nextIndex < projectEntryIdsInOrder.size()) {
			pn.next = projectEntryIdsInOrder.get(nextIndex);
		}

		closeEntityManager();
		return pn;
	}

	/* private methods */

}
