package elaborate.editor.model.orm.service;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2022 Huygens ING
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.jersey.api.NotFoundException;

import nl.knaw.huygens.Log;
import nl.knaw.huygens.jaxrstools.exceptions.BadRequestException;
import nl.knaw.huygens.jaxrstools.exceptions.UnauthorizedException;

import elaborate.editor.model.orm.Facsimile;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.ProjectEntryMetadataItem;
import elaborate.editor.model.orm.Transcription;
import elaborate.editor.model.orm.User;
import elaborate.editor.resources.orm.MultipleProjectEntrySettings;
import elaborate.editor.resources.orm.wrappers.TranscriptionWrapper;

public class ProjectEntryService extends AbstractStoredEntityService<ProjectEntry> {
  private static final ProjectEntryService instance = new ProjectEntryService();
  final ProjectService projectService = ProjectService.instance();

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
    boolean canRead = true;
    Project project;
    ProjectEntry projectEntry;
    try {
      projectEntry = super.read(entry_id);
      project = projectEntry.getProject();
      canRead = user.getPermissionFor(project).canRead();
    } finally {
      closeEntityManager();
    }
    if (!canRead) {
      throw new UnauthorizedException(
          "user "
              + user.getUsername()
              + " is not allowed to read entry "
              + entry_id
              + " of project "
              + project.getName());
    }
    return projectEntry;
  }

  public void update(long entry_id, ProjectEntry updateEntry, User user) {
    boolean ok = true;
    beginTransaction();
    ProjectEntry projectEntry;
    try {
      projectEntry = super.read(entry_id);
      Project project = projectEntry.getProject();
      if (!user.getPermissionFor(project).canWrite()) {
        ok = false;
      } else {
        projectEntry.setName(updateEntry.getName());
        projectEntry.setShortName(updateEntry.getShortName());
        projectEntry.setPublishable(updateEntry.isPublishable());
        super.update(projectEntry);
        persist(
            projectEntry
                .getProject()
                .addLogEntry(MessageFormat.format("updated entry {0}", entry_id), user));
      }
    } finally {
      if (ok) {
        commitTransaction();
      } else {
        rollbackTransaction();
      }
    }
    if (ok) {
      beginTransaction();
      try {
        projectEntry = super.read(entry_id);
        setModifiedBy(projectEntry, user);
      } finally {
        commitTransaction();
      }
    } else {
      throw new UnauthorizedException("user is not allowed to read entry");
    }
  }

  public void delete(long entry_id, User user) {
    beginTransaction();
    try {
      ProjectEntry deletedProjectEntry = super.delete(entry_id);
      getSolrIndexer().deindex(entry_id);
      setModifiedBy(deletedProjectEntry.getProject(), user);
      persist(
          deletedProjectEntry
              .getProject()
              .addLogEntry(MessageFormat.format("deleted entry {0}", entry_id), user));
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

  public Transcription addTranscription(
      long id, TranscriptionWrapper transcriptionInput, User user) {
    beginTransaction();
    Transcription transcription;
    try {
      ProjectEntry projectEntry = find(getEntityClass(), id);
      transcription =
          projectEntry
              .addTranscription(user)
              .setBody(transcriptionInput.getBodyForDb())
              .setTextLayer(transcriptionInput.getTextLayer());
      persist(transcription);

      TranscriptionService transcriptionService = TranscriptionService.instance();
      transcriptionService.setEntityManager(getEntityManager());
      transcriptionService.cleanupAnnotations(transcription);
    } finally {
      commitTransaction();
    }
    return transcription;
  }

  /* facsimiles */
  private static final Comparator<? super Facsimile> ON_NAME =
      new Comparator<Facsimile>() {
        @Override
        public int compare(Facsimile f1, Facsimile f2) {
          return f1.getName().compareTo(f2.getName());
        }
      };

  public Collection<Facsimile> getFacsimiles(long id, User user) {
    openEntityManager();
    List<Facsimile> facsimiles;
    try {
      ProjectEntry projectEntry = find(getEntityClass(), id);
      facsimiles = Lists.newArrayList(projectEntry.getFacsimiles());
      Collections.sort(facsimiles, ON_NAME);
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

      facsimile =
          projectEntry
              .addFacsimile(facsimileData.getName(), facsimileData.getTitle(), user)
              .setFilename(facsimileData.getFilename())
              .setZoomableUrl(facsimileData.getZoomableUrl());
      persist(facsimile);
      persist(
          project.addLogEntry(
              MessageFormat.format(
                  "added facsimile ''{0}'' for entry ''{1}''",
                  facsimile.getFilename(), projectEntry.getName()),
              user));
    } finally {
      commitTransaction();
    }
    return facsimile;
  }

  public Facsimile readFacsimile(long facsimile_id, User user) {
    openEntityManager();
    Facsimile facsimile;
    try {
      facsimile = getFacsimile(facsimile_id);
      long project_id = facsimile.getProjectEntry().getProject().getId();
      projectService.getProjectIfUserCanRead(project_id, user);
    } finally {
      closeEntityManager();
    }
    return facsimile;
  }

  public Facsimile updateFacsimile(long facsimile_id, Facsimile facsimileData, User user) {
    beginTransaction();
    Facsimile facsimile;
    try {
      facsimile = getFacsimile(facsimile_id);
      ProjectEntry projectEntry = facsimile.getProjectEntry();
      long project_id = projectEntry.getProject().getId();
      Project project = projectService.getProjectIfUserCanRead(project_id, user);

      facsimile
          .setName(facsimileData.getName())
          .setFilename(facsimileData.getFilename())
          .setZoomableUrl(facsimileData.getZoomableUrl());
      persist(facsimile);
      persist(
          project.addLogEntry(
              MessageFormat.format(
                  "updated facsimile ''{0}'' for entry ''{1}''",
                  facsimile.getFilename(), projectEntry.getName()),
              user));
    } finally {
      commitTransaction();
    }
    return facsimile;
  }

  public Facsimile deleteFacsimile(long facsimile_id, User user) {
    beginTransaction();
    Facsimile facsimile;
    try {
      facsimile = getFacsimile(facsimile_id);
      ProjectEntry projectEntry = facsimile.getProjectEntry();
      Project project = projectEntry.getProject();
      long project_id = project.getId();
      projectService.getProjectIfUserCanRead(project_id, user);

      persist(
          project.addLogEntry(
              MessageFormat.format(
                  "deleted facsimile ''{0}'' for entry ''{1}''",
                  facsimile.getFilename(), projectEntry.getName()),
              user));
      remove(facsimile);
    } finally {
      commitTransaction();
    }
    return facsimile;
  }

  private Facsimile getFacsimile(long facsimile_id) {
    Facsimile facsimile;
    projectService.setEntityManager(getEntityManager());
    facsimile = find(Facsimile.class, facsimile_id);
    if (facsimile == null) {
      throw new NotFoundException("no facsimile with id " + facsimile_id + " found");
    }
    return facsimile;
  }

  /* projectentrysettings */
  public Map<String, String> getProjectEntrySettings(long entry_id, User user) {
    openEntityManager();
    Map<String, String> map = Maps.newHashMap();
    try {
      ProjectEntry pe = read(entry_id);
      Iterable<String> projectEntryMetadataFieldnames =
          pe.getProject().getProjectEntryMetadataFieldnames();
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

  public void updateProjectEntrySettings(
      long entry_id, Map<String, Object> projectEntrySettings, User creator) {
    beginTransaction();
    ProjectEntry pe;
    try {
      projectService.setEntityManager(getEntityManager());

      pe = read(entry_id);
      long project_id = pe.getProject().getId();
      projectService.getProjectIfUserCanRead(project_id, creator);

      for (ProjectEntryMetadataItem projectEntryMetadataItem : pe.getProjectEntryMetadataItems()) {
        getEntityManager().remove(projectEntryMetadataItem);
      }

      Object publishableSetting = projectEntrySettings.remove(ProjectEntry.PUBLISHABLE);
      if (publishableSetting != null) {
        pe.setPublishable((Boolean) publishableSetting);
      }

      for (Entry<String, Object> settingsEntry : projectEntrySettings.entrySet()) {
        String key = settingsEntry.getKey();
        String value = ((String) settingsEntry.getValue()).trim();
        ProjectEntryMetadataItem pemi = pe.addMetadataItem(key, value, creator);
        persist(pemi);
      }
    } finally {
      commitTransaction();
    }

    // This needs to go in a seperate transaction, because only after the previous commitTransaction
    // has
    // the projectentrymetadataitems been properly updated, which is needed for the reindex called
    // from
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

  public void updateMultipleProjectEntrySettings(
      long project_id, MultipleProjectEntrySettings mpes, User user) {
    boolean ok = true;
    beginTransaction();
    Set<Long> modifiedEntryIds = Sets.newHashSet();
    try {
      projectService.setEntityManager(getEntityManager());
      Project project = projectService.getProjectIfUserCanRead(project_id, user);

      Map<String, Object> settings = mpes.getSettings();
      Set<Entry<String, Object>> settingsEntrySet = settings.entrySet();
      for (Long entry_id : mpes.getProjectEntryIds()) {
        Log.info("entryId={}", entry_id);
        ProjectEntry pe = read(entry_id);

        if (pe.getProject().getId() != project_id) {
          ok = false;
          throw new BadRequestException(
              "entry " + entry_id + " does not belong in project " + project_id);
        }

        if (mpes.changePublishable()) {
          Log.info("change publishable to {}", mpes.getPublishableSetting());
          pe.setPublishable(mpes.getPublishableSetting());
          persist(pe);
        }

        for (Entry<String, Object> entry : settingsEntrySet) {
          String key = entry.getKey();
          String value = (String) entry.getValue();

          ProjectEntryMetadataItem pemItem = pe.getMetadataItem(key);
          if (pemItem == null) {
            Log.info("add new setting: {}={}", key, value);
            persist(pe.addMetadataItem(key, value, user));
          } else {
            Log.info("modify existing setting: {}={}", key, value);
            pemItem.setData(value);
            persist(pemItem);
            setModifiedBy(pemItem, user);
          }
        }

        modifiedEntryIds.add(entry_id);
      }
      setModifiedBy(project, user);

    } finally {
      if (ok) {
        commitTransaction();
      } else {
        rollbackTransaction();
      }
    }

    if (ok) {
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
