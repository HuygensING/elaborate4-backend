package elaborate.editor.model.orm.service;

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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import elaborate.editor.export.pdf.PdfMaker;
import elaborate.editor.export.tei.TagInfo;
import elaborate.editor.export.tei.TeiConversionConfig;
import elaborate.editor.export.tei.TeiMaker;
import elaborate.editor.model.Action;
import elaborate.editor.model.ProjectMetadataFields;
import elaborate.editor.model.ProjectTypes;
import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.AnnotationMetadataItem;
import elaborate.editor.model.orm.AnnotationType;
import elaborate.editor.model.orm.LogEntry;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.ProjectEntryMetadataItem;
import elaborate.editor.model.orm.ProjectMetadataItem;
import elaborate.editor.model.orm.ProjectUser;
import elaborate.editor.model.orm.Transcription;
import elaborate.editor.model.orm.TranscriptionType;
import elaborate.editor.model.orm.User;
import elaborate.editor.publish.Publication;
import elaborate.editor.publish.Publisher;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.facetedsearch.SolrUtils;
import nl.knaw.huygens.jaxrstools.exceptions.BadRequestException;
import nl.knaw.huygens.jaxrstools.exceptions.UnauthorizedException;
import nl.knaw.huygens.solr.FacetInfo;

public class ProjectService extends AbstractStoredEntityService<Project> {
	private static final String PROJECT_NAME = "name";
	private static final String PROJECT_TITLE = "Project title";
	private static final String PROJECT_LEADER = "Project leader";
	private static final String COUNT_KEY = "count";
	private static final List<String> DEFAULT_PROJECTENTRYMETADATAFIELDNAMES = Lists.newArrayList();
	private static ProjectService instance = new ProjectService();

	private ProjectService() {}

	public static ProjectService instance() {
		return instance;
	}

	@Override
	Class<Project> getEntityClass() {
		return Project.class;
	}

	@Override
	String getEntityName() {
		return "Project";
	}

	/* CRUD methods */
	public Project create(Project project, User user) {
		if (!rootOrAdmin(user)) {
			throw new UnauthorizedException("user " + user.getUsername() + " has no admin rights");

		} else {
			addDefaultFields(project, user);

			beginTransaction();
			Project created;
			try {
				created = super.create(project);
				persist(created.addLogEntry("project created", user));
			} finally {
				commitTransaction();
			}
			return created;
		}
	}

	private void addDefaultFields(Project project, User user) {
		project.setCreator(user);
		project.setCreatedOn(new Date());
		project.setModifier(user);
		project.setModifiedOn(new Date());
		if (project.getTextLayers().length == 0) {
			project.setTextLayers(Lists.newArrayList(TranscriptionType.DIPLOMATIC));
		}
		if (StringUtils.isBlank(project.getName())) {
			project.setName(SolrUtils.normalize(project.getTitle().replace("_", "-")));
		}
		if (project.getProjectLeaderId() == 0) {
			project.setProjectLeaderId(user.getId());
		}
		if (!project.getProjectEntryMetadataFieldnames().iterator().hasNext()) {
			project.setProjectEntryMetadataFieldnames(DEFAULT_PROJECTENTRYMETADATAFIELDNAMES);
		}
		if (project.getAnnotationTypes().isEmpty()) {
			AnnotationType default_annotationType = AnnotationTypeService.instance().getDefaultAnnotationType();
			project.setAnnotationTypes(Sets.newHashSet(default_annotationType));
		}

	}

	public Project read(long project_id, User user) {
		Project project;
		openEntityManager();
		try {
			project = getProjectIfUserCanRead(project_id, user);
		} finally {
			closeEntityManager();
		}
		return project;
	}

	public void update(Project project, User user) {
		beginTransaction();
		try {
			super.update(project);
			setModifiedBy(project, user);
		} finally {
			commitTransaction();
		}
	}

	public void delete(long project_id, User user) {
		beginTransaction();
		try {
			super.delete(project_id);
			getSolrIndexer().deindexProject(project_id);
			Log.info("user {} deleting project {}", user.getUsername(), project_id);
		} finally {
			commitTransaction();
		}
	}

	/* ---------------------------------------------------------------------------------------------------- */

	public Collection<ProjectEntry> getProjectEntries(long id, User user) {
		List<ProjectEntry> projectEntriesInOrder;
		openEntityManager();
		try {
			getProjectIfUserCanRead(id, user);
			projectEntriesInOrder = getProjectEntriesInOrder(id);
		} finally {
			closeEntityManager();
		}
		return projectEntriesInOrder;
	}

	public Collection<Map<String, String>> getProjectEntryMetadata(long id, User user) {
		Collection<Map<String, String>> projectEntryMetadata = Lists.newArrayList();
		openEntityManager();
		try {
			Project project = getProjectIfUserCanRead(id, user);
			project.getProjectEntryMetadataFieldnames();

			// select l1.data as bewaarplaats,
			// l2.data as collectie,
			// l3.data as signatuur,
			// l4.data as afzenders,
			// l5.data as ontvangers,
			// l6.data as datum
			// from project_entries e
			// left outer join project_entry_metadata_items l0
			// on (l0.project_entry_id = e.id and l0.field='Scan(s)')
			// left outer join project_entry_metadata_items l1
			// on (l1.project_entry_id = e.id and l1.field='Bewaarplaats')
			// left outer join project_entry_metadata_items l2
			// on (l2.project_entry_id = e.id and l2.field='Collectie')
			// left outer join project_entry_metadata_items l3
			// on (l3.project_entry_id = e.id and l3.field='Signatuur')
			// left outer join project_entry_metadata_items l4
			// on (l4.project_entry_id = e.id and l4.field='Afzender(s)')
			// left outer join project_entry_metadata_items l5
			// on (l5.project_entry_id = e.id and l5.field='Ontvanger(s)')
			// left outer join project_entry_metadata_items l6
			// on (l6.project_entry_id = e.id and l6.field='Datum')
			// where e.project_id = 44 and l0.data=''
			// order by l0.data,l1.data, l2.data, l3.data ,l4.data, l5.data, l6.data;

			List<ProjectEntry> projectEntriesInOrder = getProjectEntriesInOrder(id);
			for (ProjectEntry projectEntry : projectEntriesInOrder) {
				Map<String, String> metadata = Maps.newHashMap();
				metadata.put("entryname", projectEntry.getName());
				List<ProjectEntryMetadataItem> projectEntryMetadataItems = projectEntry.getProjectEntryMetadataItems();
				for (ProjectEntryMetadataItem projectEntryMetadataItem : projectEntryMetadataItems) {
					metadata.put(projectEntryMetadataItem.getField(), projectEntryMetadataItem.getData());
				}
				projectEntryMetadata.add(metadata);
			}
		} finally {
			closeEntityManager();
		}
		return projectEntryMetadata;
	}

	public List<ProjectEntry> getProjectEntriesInOrder0(long id) {
		find(getEntityClass(), id);
		List<ProjectEntry> resultList = getEntityManager()// .
				.createQuery(
						"from ProjectEntry pe" + //
								" where project_id=:projectId" + //
								" order by pe.name", //
						ProjectEntry.class)//
				.setParameter("projectId", id)//
				.getResultList();
		ImmutableList<ProjectEntry> projectEntries = ImmutableList.copyOf(resultList);

		return projectEntries;
	}

	public List<ProjectEntry> getProjectEntriesInOrder(long id) {
		final List<Long> projectEntryIdsInOrder = getProjectEntryIdsInOrder(id);
		Project project = find(getEntityClass(), id);
		List<ProjectEntry> projectEntries = project.getProjectEntries();
		Collections.sort(projectEntries, new Comparator<ProjectEntry>() {
			@Override
			public int compare(ProjectEntry e1, ProjectEntry e2) {
				return projectEntryIdsInOrder.indexOf(e1.getId())//
						- projectEntryIdsInOrder.indexOf(e2.getId());
			}
		});
		return projectEntries;
	}

	public List<Long> getProjectEntryIdsInOrder(long id) {
		Project project = find(getEntityClass(), id);
		List<Long> resultList = getEntityManager()// .
				.createQuery(
						"select pe.id from ProjectEntry pe" + //
								" left join pe.projectEntryMetadataItems l1 with l1.field=:level1" + //
								" left join pe.projectEntryMetadataItems l2 with l2.field=:level2" + //
								" left join pe.projectEntryMetadataItems l3 with l3.field=:level3" + //
								" where project_id=:projectId" + //
								" order by l1.data,l2.data,l3.data,pe.name", //
						Long.class)//
				.setParameter("level1", project.getLevel1())//
				.setParameter("level2", project.getLevel2())//
				.setParameter("level3", project.getLevel3())//
				.setParameter("projectId", id)//
				.getResultList();
		ImmutableList<Long> projectEntryIds = ImmutableList.copyOf(resultList);

		return projectEntryIds;
	}

	public ProjectEntry createProjectEntry(long id, ProjectEntry projectEntry, User user) {
		beginTransaction();
		ProjectEntry entry;
		try {
			Project project = find(getEntityClass(), id);
			entry = project.addEntry(projectEntry.getName(), user);
			persist(entry);
			setModifiedBy(entry, user);
			String[] textLayers = project.getTextLayers();
			for (String textLayer : textLayers) {
				Transcription transcription = entry.addTranscription(user).setTextLayer(textLayer);
				persist(transcription);
			}
			persist(project.addLogEntry("added entry " + projectEntry.getName(), user));

		} finally {
			commitTransaction();
		}
		return entry;
	}

	private static final Comparator<Project> SORT_PROJECTS = new Comparator<Project>() {
		@Override
		public int compare(Project p1, Project p2) {
			return p2.getModifiedOn().compareTo(p1.getModifiedOn());
		}
	};

	public List<Project> getAll(User user) {
		List<Project> projects;
		openEntityManager();
		try {

			if (rootOrAdmin(user)) {
				projects = Lists.newArrayList(super.getAll());
			} else {
				projects = getProjectsForUser(user);
			}
			Collections.sort(projects, SORT_PROJECTS);
		} finally {
			closeEntityManager();
		}
		return projects;
	}

	public Map<String, String> getProjectSettings(long project_id, User user) {
		Map<String, String> map = Maps.newHashMap();
		openEntityManager();
		try {

			Project project = getProjectIfUserCanRead(project_id, user);
			Hibernate.initialize(project);
			List<ProjectMetadataItem> projectMetadataItems = project.getProjectMetadataItems();
			Hibernate.initialize(projectMetadataItems);
			for (ProjectMetadataItem projectMetadataItem : projectMetadataItems) {
				Hibernate.initialize(projectMetadataItem);
				map.put(projectMetadataItem.getField(), projectMetadataItem.getData());
			}
			map.put(PROJECT_TITLE, project.getTitle());
			map.put(PROJECT_NAME, project.getName().replace("_", "-"));
			map.put(PROJECT_LEADER, String.valueOf(project.getProjectLeaderId()));

		} finally {
			closeEntityManager();
		}
		return map;
	}

	/* private methods */
	// Throws Unauthorized acception when user has no read access rights to the project with id project_id
	Project getProjectIfUserCanRead(long project_id, User user) {
		if (rootOrAdmin(user)) {
			return super.read(project_id);
		}

		Project project = null;
		List<ProjectUser> resultList = getEntityManager()//
				.createQuery("from ProjectUser where user_id=:userId and project_id=:projectId", ProjectUser.class)//
				.setParameter("userId", user.getId())//
				.setParameter("projectId", project_id)//
				.getResultList();
		// logMemory();
		if (!resultList.isEmpty()) {
			project = resultList.get(0).getProject();
			Hibernate.initialize(project);
		}
		if (project == null) {
			closeEntityManager();
			throw new UnauthorizedException("user " + user.getUsername() + " has no read permission for project with id " + project_id);
		}
		return project;
	}

	private List<Project> getProjectsForUser(User user) {
		TypedQuery<ProjectUser> createQuery = getEntityManager().createQuery("from ProjectUser where user_id=:userId", ProjectUser.class).setParameter("userId", user.getId());
		List<ProjectUser> resultList = createQuery.getResultList();
		ImmutableList<ProjectUser> projectUsers = ImmutableList.copyOf(resultList);
		List<Project> projects = Lists.newArrayList();
		logMemory();
		for (ProjectUser projectUser : projectUsers) {
			Project project = projectUser.getProject();
			Hibernate.initialize(project);
			projects.add(project);
		}
		return projects;
	}

	void logMemory() {
		int mb = 1024 * 1024;
		System.gc();
		// Getting the runtime reference from system
		Runtime runtime = Runtime.getRuntime();
		Log.info("##### Heap utilization statistics [MB] #####");

		// Print used memory
		Log.info("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);

		// Print free memory
		Log.info("Free Memory:" + runtime.freeMemory() / mb);

		// Print total available memory
		Log.info("Total Memory:" + runtime.totalMemory() / mb);

		// Print Maximum available memory
		Log.info("Max Memory:" + runtime.maxMemory() / mb);
	}

	public Iterable<String> getProjectEntryMetadataFields(long project_id, User user) {
		openEntityManager();
		Iterable<String> projectEntryMetadataFieldnames;
		try {
			Project project = getProjectIfUserCanRead(project_id, user);
			projectEntryMetadataFieldnames = project.getProjectEntryMetadataFieldnames();
		} finally {
			closeEntityManager();
		}
		return projectEntryMetadataFieldnames;
	}

	public void setProjectEntryMetadataFields(long project_id, List<String> fields, User user) {
		beginTransaction();
		try {

			Project project = getProjectIfUserCanRead(project_id, user);
			project.setProjectEntryMetadataFieldnames(fields);
			persist(project);
			persist(project.addLogEntry("projectentrymetadatafields changed", user));
			setModifiedBy(project, user);

		} finally {
			commitTransaction();
		}
	}

	public Object getProjectAnnotationTypes(long project_id, User user) {
		Set<AnnotationType> annotationTypes;
		openEntityManager();
		try {
			Project project = getProjectIfUserCanRead(project_id, user);
			annotationTypes = project.getAnnotationTypes();
		} finally {
			closeEntityManager();
		}
		return annotationTypes;
	}

	public List<Long> getProjectAnnotationTypeIds(long project_id, User user) {
		List<Long> list = Lists.newArrayList();
		openEntityManager();
		try {
			Project project = getProjectIfUserCanRead(project_id, user);
			for (AnnotationType annotationType : project.getAnnotationTypes()) {
				list.add(annotationType.getId());
			}
		} finally {
			closeEntityManager();
		}
		return list;
	}

	public void setProjectAnnotationTypes(long project_id, List<Long> annotationTypeIds, User user) {
		beginTransaction();
		try {
			Project project = getProjectIfUserCanRead(project_id, user);

			Set<AnnotationType> annotationTypes = Sets.newHashSet();
			for (Long id : annotationTypeIds) {
				AnnotationType at = getEntityManager().find(AnnotationType.class, id);
				annotationTypes.add(at);
			}
			project.setAnnotationTypes(annotationTypes);
			persist(project.addLogEntry("projectannotationtypes changed", user));

			setModifiedBy(project, user);

		} finally {
			commitTransaction();
		}
	}

	public void setProjectAnnotationTypes(long project_id, Set<AnnotationType> annotationTypes, User user) {
		beginTransaction();
		try {
			Project project = getProjectIfUserCanRead(project_id, user);

			project.setAnnotationTypes(annotationTypes);
			persist(project.addLogEntry("projectannotationtypes changed", user));

			setModifiedBy(project, user);

		} finally {
			commitTransaction();
		}
	}

	public Map<String, Object> getProjectStatistics(long project_id, User user) {
		removeOrphanedAnnotations(project_id);
		openEntityManager();
		Map<String, Object> statistics;
		try {
			Project project = getProjectIfUserCanRead(project_id, user);

			statistics = ImmutableMap.<String, Object> of("entries", getProjectEntriesStatistics(project_id, getEntityManager(), project));

		} finally {
			closeEntityManager();
		}
		return statistics;
	}

	private Map<String, Object> getProjectEntriesStatistics(long project_id, EntityManager entityManager, Project project) {
		Long transcriptionCount = getTranscriptionCount(project_id, entityManager);
		// Long annotationCount = getAnnotationCount(project_id, entityManager);
		Long facsimileCount = getFacsimileCount(project_id, entityManager);

		Map<String, Object> textLayerCountMap = getTextLayerCountMap(project_id, entityManager, project);
		Map<String, Object> transcriptionStatistics = Maps.newHashMap();
		transcriptionStatistics.put(COUNT_KEY, transcriptionCount);
		transcriptionStatistics.put("textlayers", textLayerCountMap);

		Map<String, Object> annotationTypeCountMap = getAnnotationTypeCountMap(project_id, entityManager, project);
		Map<String, Object> annotationStatistics = Maps.newHashMap();
		annotationStatistics.put("annotationtypes", annotationTypeCountMap);
		Collection<Object> values = annotationTypeCountMap.values();
		int annotationCount = 0;
		for (Object typecount : values) {
			annotationCount += (Long) typecount;
		}
		annotationStatistics.put(COUNT_KEY, annotationCount);

		Map<String, Object> parts = Maps.newHashMap();
		parts.put("transcriptions", transcriptionStatistics);
		parts.put("annotations", annotationStatistics);
		parts.put("facsimiles", facsimileCount);

		Map<String, Object> entriesStatistics = Maps.newHashMap();
		entriesStatistics.put(COUNT_KEY, getEntriesCount(project_id, entityManager));
		entriesStatistics.put("parts", parts);
		return entriesStatistics;
	}

	private Long getTranscriptionCount(long project_id, EntityManager entityManager) {
		Long transcriptionCount = (Long) entityManager//
				.createQuery("select count(*) from Transcription"//
						+ " where project_entry_id in"//
						+ " (select id from ProjectEntry where project_id=:project_id)"//
		)//
				.setParameter("project_id", project_id)//
				.getSingleResult();
		return transcriptionCount;
	}

	// private Long getAnnotationCount(long project_id, EntityManager entityManager) {
	// Long annotationCount = (Long) entityManager//
	// .createQuery("select count(*) from Annotation"//
	// + " where transcription_id in"//
	// + " (select id from Transcription"//
	// + " where project_entry_id in"//
	// + " (select id from ProjectEntry where project_id=:project_id))"//
	// )//
	// .setParameter("project_id", project_id)//
	// .getSingleResult();
	// return annotationCount;
	// }

	private Long getFacsimileCount(long project_id, EntityManager entityManager) {
		Long facsimileCount = (Long) entityManager//
				.createQuery("select count(*) from"//
						+ " Facsimile where project_entry_id in"//
						+ " (select id from ProjectEntry where project_id=:project_id)"//
		)//
				.setParameter("project_id", project_id)//
				.getSingleResult();
		return facsimileCount;
	}

	private Map<String, Object> getTextLayerCountMap(long project_id, EntityManager entityManager, Project project) {
		Map<String, Object> textLayerCountMap = Maps.newHashMap();
		for (String textLayer : project.getTextLayers()) {
			Long textLayerCount = (Long) entityManager//
					.createQuery("select count(*) from Transcription"//
							+ " where text_layer = :text_layer"//
							+ " and project_entry_id in"//
							+ " (select id from ProjectEntry where project_id=:project_id)"//
			)//
					.setParameter("text_layer", textLayer)//
					.setParameter("project_id", project_id)//
					.getSingleResult();
			textLayerCountMap.put(textLayer, textLayerCount);
		}
		return textLayerCountMap;
	}

	private Map<String, Object> getAnnotationTypeCountMap(long project_id, EntityManager entityManager, Project project) {
		Map<String, Object> annotationTypeCountMap = Maps.newHashMap();
		for (AnnotationType annotationType : project.getAnnotationTypes()) {
			Long annotationTypeCount = (Long) entityManager//
					.createQuery("select count(*) from Annotation"//
							+ " where annotation_type_id = :annotation_type_id"//
							+ "   and transcription_id in"//
							+ "     (select id from Transcription"//
							+ "       where project_entry_id in"//
							+ "         (select id from ProjectEntry where project_id=:project_id))"//
			)//
					.setParameter("annotation_type_id", annotationType.getId())//
					.setParameter("project_id", project_id)//
					.getSingleResult();
			annotationTypeCountMap.put(annotationType.getName(), annotationTypeCount);
		}
		return annotationTypeCountMap;
	}

	private Long getEntriesCount(long project_id, EntityManager entityManager) {
		Long entriesCount = (Long) entityManager//
				.createQuery("select count(*) from ProjectEntry"//
						+ " where project_id=:project_id"//
		)//
				.setParameter("project_id", project_id)//
				.getSingleResult();
		return entriesCount;
	}

	public List<FacetInfo> getFacetInfo(long project_id, User user) {
		openEntityManager();
		List<FacetInfo> list;
		try {
			Project project = getProjectIfUserCanRead(project_id, user);
			list = ImmutableList.copyOf(project.getFacetInfo());
		} finally {
			closeEntityManager();
		}
		return list;
	}

	public void setTextlayers(long project_id, List<String> textLayers, User user) {
		beginTransaction();
		Set<Long> modifiedEntryIds = Sets.newHashSet();
		try {
			Project project = getProjectIfUserCanRead(project_id, user);
			List<String> previous = Lists.newArrayList(project.getTextLayers());
			List<String> deletedTextLayers = previous;
			deletedTextLayers.removeAll(textLayers);
			project.setTextLayers(textLayers);

			persist(project);
			persist(project.addLogEntry("project textlayers changed", user));
			setModifiedBy(project, user);

			if (!deletedTextLayers.isEmpty()) {
				persist(project.addLogEntry("removing textlayer(s) " + Joiner.on(", ").join(deletedTextLayers), user));
				for (String textlayer : deletedTextLayers) {
					List<?> resultList = getEntityManager()//
							.createQuery("select e.id, t.id from ProjectEntry e join e.transcriptions as t with t.text_layer=:textlayer where e.project=:project")//
							.setParameter("project", project)//
							.setParameter("textlayer", textlayer)//
							.getResultList();
					for (Object object : resultList) {
						Object[] ids = (Object[]) object;
						Transcription transcription = getEntityManager().find(Transcription.class, ids[1]);
						remove(transcription);
						modifiedEntryIds.add((Long) ids[0]);
					}
				}
			}
		} finally {
			commitTransaction();
		}

		beginTransaction();
		try {
			for (Long id : modifiedEntryIds) {
				ProjectEntry entry = getEntityManager().find(ProjectEntry.class, id);
				setModifiedBy(entry, user);
			}
		} finally {
			commitTransaction();
		}

	}

	public void setProjectSettings(long project_id, Map<String, String> settingsMap, User user) {
		Long userId = -1l;
		beginTransaction();
		try {

			Project project = getProjectIfUserCanRead(project_id, user);
			if (!user.getPermissionFor(project).can(Action.EDIT_PROJECT_SETTINGS)) {
				throw new UnauthorizedException("user " + user.getUsername() + " has no projectsettings permission for project " + project.getName());
			}

			List<ProjectMetadataItem> projectMetadataItems = project.getProjectMetadataItems();
			for (ProjectMetadataItem projectMetadataItem : projectMetadataItems) {
				getEntityManager().remove(projectMetadataItem);
			}

			for (Entry<String, String> entry : settingsMap.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				ProjectMetadataItem pmi = project.addMetadata(key, value, user);
				if (PROJECT_TITLE.equals(key)) {
					project.setTitle(value);
				} else if (PROJECT_NAME.equals(key)) {
					project.setName(value);
				} else if (PROJECT_LEADER.equals(key)) {
					userId = Long.valueOf(value);
					project.setProjectLeaderId(userId);
				}
				persist(pmi);
			}
			persist(project);
			persist(project.addLogEntry("projectsettings changed", user));
			setModifiedBy(project, user);

		} finally {
			commitTransaction();
			if (userId > -1) {
				UserService userService = UserService.instance();
				userService.makeProjectLeader(userId, user);
			}
		}
	}

	public Set<User> getProjectUsersFull(long project_id, User user) {
		openEntityManager();
		Set<User> projectUsers;
		try {
			Project project = getProjectIfUserCanRead(project_id, user);
			projectUsers = project.getUsers();
			Hibernate.initialize(projectUsers);
		} finally {
			closeEntityManager();
		}
		return projectUsers;
	}

	public List<Long> getProjectUserIds(long project_id, User user) {
		List<Long> list = Lists.newArrayList();
		openEntityManager();
		try {
			Project project = getProjectIfUserCanRead(project_id, user);
			for (User pUser : project.getUsers()) {
				list.add(pUser.getId());
			}
		} finally {
			closeEntityManager();
		}
		return list;
	}

	public User addProjectUser(long project_id, long user_id, User user) {
		beginTransaction();
		User projectUser;
		try {

			Project project = getProjectIfUserCanRead(project_id, user);
			projectUser = getProjectUser(user_id);

			project.getUsers().add(projectUser);
			persist(project);
			persist(project.addLogEntry("user " + projectUser.getUsername() + " added to project", user));
			setModifiedBy(project, user);

		} finally {
			commitTransaction();
		}
		return projectUser;
	}

	public void deleteProjectUser(long project_id, long user_id, User user) {
		beginTransaction();
		try {

			Project project = getProjectIfUserCanRead(project_id, user);
			User projectUser = getProjectUser(user_id);

			project.getUsers().remove(projectUser);
			persist(project);
			persist(project.addLogEntry("user " + projectUser.getUsername() + " removed from project", user));
			setModifiedBy(project, user);

		} finally {
			commitTransaction();
		}
	}

	private User getProjectUser(long user_id) {
		User projectUser = find(User.class, user_id);
		if (projectUser == null) {
			throw new BadRequestException("no user found with id " + user_id);
		}
		return projectUser;
	}

	public List<LogEntry> getLogEntries(long project_id, User user) {
		openEntityManager();
		List<LogEntry> logEntries;
		try {
			Project project = getProjectIfUserCanRead(project_id, user);
			logEntries = Ordering.natural().sortedCopy(project.getLogEntries());
		} finally {
			closeEntityManager();
		}
		return logEntries;
	}

	public String exportTei(long project_id, User user) {
		String tei;
		openEntityManager();
		try {
			Project project = getProjectIfUserCanRead(project_id, user);
			AnnotationType versregels = getEntityManager().createQuery("from AnnotationType where name=:name", AnnotationType.class).setParameter("name", "versregel").getSingleResult();
			tei = exportTei(project, null, versregels);
		} finally {
			closeEntityManager();
		}
		return tei;
	}

	public void exportPdf(long project_id, User user, String filename) {
		openEntityManager();
		try {
			Project project = getProjectIfUserCanRead(project_id, user);
			PdfMaker pdfMaker = new PdfMaker(project, getEntityManager());
			pdfMaker.saveToFile(filename);
		} finally {
			closeEntityManager();
		}
	}

	public Publication.Status createPublicationStatus(long project_id, User user) {
		Publisher publisher = Publisher.instance();
		boolean canPublish;
		Project project;
		Map<String, String> projectMetadata;
		openEntityManager();
		try {
			project = getProjectIfUserCanRead(project_id, user);
			canPublish = user.getPermissionFor(project).can(Action.PUBLISH);
			projectMetadata = project.getMetadataMap();
		} finally {
			closeEntityManager();
		}

		if (!canPublish) {
			throw new UnauthorizedException(MessageFormat.format("{0} has no publishing permission for {1}", user.getUsername(), project.getTitle()));
		};

		String projectType = StringUtils.defaultIfBlank(projectMetadata.get("projectType"), ProjectTypes.COLLECTION);
		List<Long> publishableAnnotationTypeIds = getPublishableAnnotationTypeIds(projectMetadata);
		List<String> publishableProjectEntryMetadataFields = getPublishableProjectEntryMetadataFields(projectMetadata);
		List<String> facetableProjectEntryMetadataFields = getFacetableProjectEntryMetadataFields(projectMetadata);
		List<String> publishableTextLayers = getPublishableTextLayers(projectMetadata);
		Publication.Settings settings = new Publication.Settings()//
				.setProjectId(project_id)//
				.setUser(user)//
				.setTextLayers(publishableTextLayers)//
				.setAnnotationTypeIds(publishableAnnotationTypeIds)//
				.setProjectEntryMetadataFields(publishableProjectEntryMetadataFields)//
				.setFacetFields(facetableProjectEntryMetadataFields)//
				.setProjectType(projectType);
		Publication.Status publicationStatus = publisher.publish(settings);

		return publicationStatus;
	}

	private List<String> getPublishableTextLayers(Map<String, String> projectMetadata) {
		return deserialize(projectMetadata, ProjectMetadataFields.PUBLISHABLE_TEXT_LAYERS);
	}

	private List<String> getPublishableProjectEntryMetadataFields(Map<String, String> projectMetadata) {
		return deserialize(projectMetadata, ProjectMetadataFields.PUBLISHABLE_PROJECT_ENTRY_METADATA_FIELDS);
	}

	private List<String> getFacetableProjectEntryMetadataFields(Map<String, String> projectMetadata) {
		return deserialize(projectMetadata, ProjectMetadataFields.FACETABLE_PROJECT_ENTRY_METADATA_FIELDS);
	}

	@SuppressWarnings("unchecked")
	private List<String> deserialize(Map<String, String> projectMetadata, String key) {
		String metadataString = projectMetadata.get(key);
		List<String> list = Lists.newArrayList();
		if (metadataString != null) {
			try {
				list = new ObjectMapper().readValue(metadataString, List.class);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	private List<Long> getPublishableAnnotationTypeIds(Map<String, String> projectMetadata) {
		String metadataString = projectMetadata.get(ProjectMetadataFields.PUBLISHABLE_ANNOTATION_TYPE_IDS);
		List<Long> publishableAnnotationTypeIds = Lists.newArrayList();
		if (metadataString != null) {
			try {
				publishableAnnotationTypeIds = new ObjectMapper().readValue(metadataString, new TypeReference<List<Long>>() {});
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return publishableAnnotationTypeIds;
	}

	public Publication.Status getPublicationStatus(String status_id) {
		Publisher publisher = Publisher.instance();
		return publisher.getStatus(status_id);
	}

	/** private **/
	private String exportTei(Project project, String groupTextsByMetadata, AnnotationType versregels) {
		Function<Annotation, TagInfo> mapToL = new Function<Annotation, TagInfo>() {
			@Override
			public TagInfo apply(Annotation annotation) {
				TagInfo tagInfo = new TagInfo().setName("l")/* .setSkipNewlineAfter(true) */;
				for (AnnotationMetadataItem annotationMetadataItem : annotation.getAnnotationMetadataItems()) {
					String name = annotationMetadataItem.getAnnotationTypeMetadataItem().getName();
					String value = annotationMetadataItem.getData();
					if ("n".equals(name)) {
						tagInfo.addAttribute("n", value);
					} else if ("inspringen".equals(name)) {
						tagInfo.addAttribute("rend", "indent");
					}
				}
				return tagInfo;
			}
		};
		TeiConversionConfig config = new TeiConversionConfig()//
				.setGroupTextsByMetadata(groupTextsByMetadata)//
				.addAnnotationTypeMapping(versregels, mapToL);

		String xml;
		openEntityManager();
		try {
			xml = new TeiMaker(project, config, getEntityManager()).toXML();
		} finally {
			closeEntityManager();
		}
		return xml;
	}

	public void updateProjectUserIds(long project_id, List<Long> userIds, User user) {
		beginTransaction();
		try {
			Project project = getProjectIfUserCanRead(project_id, user);

			Set<User> users = Sets.newHashSet();
			for (Long userId : userIds) {
				User puser = getProjectUser(userId);
				users.add(puser);
			}
			Set<User> prevUsers = project.getUsers();
			String diff = getDiff(prevUsers, users);
			project.setUsers(users);
			persist(project.addLogEntry("projectusers changed: " + diff, user));

			setModifiedBy(project, user);

		} finally {
			commitTransaction();
		}
	}

	private String getDiff(Set<User> prevUsers, Set<User> users) {
		List<String> added = Lists.newArrayList();
		for (User user : users) {
			if (!prevUsers.contains(user)) {
				added.add(user.getUsername());
			}
		}
		List<String> deleted = Lists.newArrayList();
		for (User user : prevUsers) {
			if (!users.contains(user)) {
				deleted.add(user.getUsername());
			}
		}
		// updateProjectUserIds is called on every single add/delete, so only 1 add or del should be found.
		if (!added.isEmpty()) {
			return "user " + added.get(0) + " added";
		} else if (!deleted.isEmpty()) {
			return "user " + deleted.get(0) + " removed";
		}
		return "";
	}

	public void setMetadata(long project_id, String key, String value, User user) {
		beginTransaction();
		try {
			Project project = getProjectIfUserCanRead(project_id, user);

			ProjectMetadataItem item = null;
			List<ProjectMetadataItem> projectMetadataItems = project.getProjectMetadataItems();
			for (ProjectMetadataItem projectMetadataItem : projectMetadataItems) {
				if (projectMetadataItem.getField().equals(key)) {
					item = projectMetadataItem;
					break;
				}
			}
			if (item == null) {
				ProjectMetadataItem pmi = project.addMetadata(key, value, user);
				persist(pmi);

			} else {
				item.setData(value);
				persist(item);
			}

		} finally {
			commitTransaction();
		}
	}

	public void setProjectSortLevels(long project_id, List<String> levels, User user) {
		beginTransaction();
		Project project = getProjectIfUserCanRead(project_id, user);
		if (!user.getPermissionFor(project).can(Action.EDIT_PROJECT_SETTINGS)) {
			throw new UnauthorizedException("user " + user.getUsername() + " has no projectsettings permission for project " + project.getName());
		}
		List<String> newLevels = Lists.newArrayList("", "", "");

		List<String> allowed = ImmutableList.copyOf(project.getProjectEntryMetadataFieldnames());
		List<String> disallowed = Lists.newArrayListWithCapacity(3);
		for (int i = 0; i < 3; i++) {
			if (levels.size() > i) {
				String level = levels.get(i);
				newLevels.add(i, level);
				if (StringUtils.isNotBlank(level) && !allowed.contains(level)) {
					disallowed.add(level);
				}
			}
		}
		if (disallowed.isEmpty()) {
			project.setLevel1(newLevels.get(0)).setLevel2(newLevels.get(1)).setLevel3(newLevels.get(2));
			persist(project);
			commitTransaction();

		} else {
			rollbackTransaction();
			throw new BadRequestException("invalid sortlevel value(s): " + Joiner.on(", ").join(disallowed));
		}
	}

	public ReindexStatus createReindexStatus(long project_id) {
		openEntityManager();
		try {
			read(project_id);
		} finally {
			closeEntityManager();
		}

		ReindexStatus status = new ReindexStatus();
		return status;
	}

	/**
	 * Remove annotations that have no corresponding annotationmarkers (begin and end) in the Transcription Body
	 * 
	 * @param modifier
	 *          The User credited with the removal
	 */
	public void removeOrphanedAnnotations(long project_id) {
		beginTransaction();
		Project project = read(project_id);
		TranscriptionService transcriptionService = TranscriptionService.instance();
		transcriptionService.setEntityManager(getEntityManager());
		for (ProjectEntry projectEntry : project.getProjectEntries()) {
			for (Transcription transcription : projectEntry.getTranscriptions()) {
				transcriptionService.removeOrphanedAnnotations(transcription);
			}
		}
		commitTransaction();
	}

	//	public Map<Integer, String> getAnnotationTypesForProject(Long projectId) {
	//		Map<Integer, String> annotationTypes = Maps.newHashMap();
	//		Project project = read(projectId);
	//		List<?> resultList = getEntityManager()//
	//				.createQuery("select a.annotationNo, at.name from Annotation as a inner join a.annotationType as at where a.transcription.projectEntry.project=:project")//
	//				.setParameter("project", project)//
	//				.getResultList();
	//		for (Object result : resultList) {
	//			Object[] objects = (Object[]) result;
	//			annotationTypes.put((Integer) objects[0], (String) objects[1]);
	//		}
	//
	//		return annotationTypes;
	//	}
	//
	//	public Map<Integer, Map<String, String>> getAnnotationParametersForProject(Long projectId) {
	//		Map<Integer, Map<String, String>> annotationParameters = Maps.newHashMap();
	//		Project project = read(projectId);
	//		List<?> resultList = getEntityManager()//
	//				.createQuery("select a.annotationNo, am.annotationTypeMetadataItem.name, am.data from Annotation as a join a.annotationMetadataItems as am where a.transcription.projectEntry.project=:project")//
	//				.setParameter("project", project)//
	//				.getResultList();
	//		for (Object result : resultList) {
	//			Object[] objects = (Object[]) result;
	//			Integer annotationNo = (Integer) objects[0];
	//			Map<String, String> map = annotationParameters.get(annotationNo);
	//			if (map == null) {
	//				map = Maps.newHashMap();
	//				annotationParameters.put(annotationNo, map);
	//			}
	//			map.put((String) objects[1], (String) objects[2]);
	//		}
	//		return annotationParameters;
	//	}

	public Map<Integer, AnnotationData> getAnnotationDataForProject(Long projectId) {
		beginTransaction();
		Project project = read(projectId);
		Map<Integer, AnnotationData> annotationDataMap = Maps.newHashMap();
		List<?> resultList = getEntityManager()//
				.createQuery("select a.annotationNo, at.id, at.name from Annotation as a inner join a.annotationType as at where a.transcription.projectEntry.project=:project")//
				.setParameter("project", project)//
				.getResultList();
		for (Object result : resultList) {
			Object[] objects = (Object[]) result;
			Integer annotationId = (Integer) objects[0];
			Long annotationTypeId = (Long) objects[1];
			String annotationType = (String) objects[2];
			annotationDataMap.put(annotationId, new AnnotationData().setType(annotationType).setTypeId(annotationTypeId));
		}

		resultList = getEntityManager()//
				.createQuery("select a.annotationNo, am.annotationTypeMetadataItem.name, am.data from Annotation as a join a.annotationMetadataItems as am where a.transcription.projectEntry.project=:project")//
				.setParameter("project", project)//
				.getResultList();
		for (Object result : resultList) {
			Object[] objects = (Object[]) result;
			Integer annotationNo = (Integer) objects[0];
			String parameterKey = (String) objects[1];
			String parameterValue = (String) objects[2];
			annotationDataMap.get(annotationNo).getParameters().put(parameterKey, parameterValue);
		}
		rollbackTransaction(); // since it's read-only
		return annotationDataMap;
	}

	public static class AnnotationData {
		Long typeId;
		String type;
		Map<String, String> parameters = Maps.newHashMap();

		public Long getTypeId() {
			return typeId;
		}

		public AnnotationData setTypeId(Long typeId) {
			this.typeId = typeId;
			return this;
		}

		public String getType() {
			return type;
		}

		public AnnotationData setType(String type) {
			this.type = type;
			return this;
		}

		public Map<String, String> getParameters() {
			return parameters;
		}

		public AnnotationData setParameters(Map<String, String> parameters) {
			this.parameters = parameters;
			return this;
		}
	}

}
