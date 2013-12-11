package elaborate.editor.model.orm.service;

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

import nl.knaw.huygens.jaxrstools.exceptions.BadRequestException;
import nl.knaw.huygens.jaxrstools.exceptions.UnauthorizedException;
import nl.knaw.huygens.solr.FacetInfo;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;

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
import elaborate.editor.model.ProjectTypes;
import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.AnnotationMetadataItem;
import elaborate.editor.model.orm.AnnotationType;
import elaborate.editor.model.orm.LogEntry;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.ProjectMetadataItem;
import elaborate.editor.model.orm.ProjectUser;
import elaborate.editor.model.orm.Transcription;
import elaborate.editor.model.orm.TranscriptionType;
import elaborate.editor.model.orm.User;
import elaborate.editor.publish.Publication;
import elaborate.editor.publish.Publisher;

public class ProjectService extends AbstractStoredEntityService<Project> {
	private static final String COUNT_KEY = "count";
	private static final List<String> DEFAULT_PROJECTENTRYMETADATAFIELDNAMES = Lists.newArrayList();
	private static ProjectService instance;

	private ProjectService() {}

	public static ProjectService instance() {
		if (instance == null) {
			instance = new ProjectService();
		}
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
			throw new UnauthorizedException();

		} else {
			beginTransaction();

			addDefaultFields(project, user);

			Project created = super.create(project);
			persist(created.addLogEntry("project created", user));
			commitTransaction();
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
		if (project.getName() == null) {
			project.setName(StringUtils.normalizeSpace(project.getTitle()));
		}
		if (project.getProjectLeaderId() == 0) {
			project.setProjectLeaderId(user.getId());
		}
		if (project.getProjectEntryMetadataFieldnames().length == 0) {
			project.setProjectEntryMetadataFieldnames(DEFAULT_PROJECTENTRYMETADATAFIELDNAMES);
		}
	}

	public Project read(long project_id, User user) {
		openEntityManager();
		Project project = getProjectIfUserIsAllowed(project_id, user);
		closeEntityManager();
		return project;
	}

	public void update(Project project, User user) {
		beginTransaction();
		super.update(project);
		setModifiedBy(project, user);
		commitTransaction();
	}

	public void delete(long project_id, User user) {
		beginTransaction();
		super.delete(project_id);
		commitTransaction();
	}

	/* ---------------------------------------------------------------------------------------------------- */

	public Collection<ProjectEntry> getProjectEntries(long id, User user) {
		openEntityManager();
		Project project = getProjectIfUserIsAllowed(id, user);
		List<ProjectEntry> projectEntriesInOrder = getProjectEntriesInOrder(id);
		closeEntityManager();
		return projectEntriesInOrder;
	}

	public List<ProjectEntry> getProjectEntriesInOrder0(long id) {
		find(getEntityClass(), id);
		List<ProjectEntry> resultList = getEntityManager()//.
				.createQuery("from ProjectEntry pe" + //
						" where project_id=:projectId" + //
						" order by pe.name",//
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
		List<Long> resultList = getEntityManager()//.
				.createQuery("select pe.id from ProjectEntry pe" + //
						" left join pe.projectEntryMetadataItems l1 with l1.field=:level1" + //
						" left join pe.projectEntryMetadataItems l2 with l2.field=:level2" + //
						" left join pe.projectEntryMetadataItems l3 with l3.field=:level3" + //
						" where project_id=:projectId" + //
						" order by l1.data,l2.data,l3.data,pe.name",//
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

		Project project = find(getEntityClass(), id);
		ProjectEntry entry = project.addEntry(projectEntry.getName(), user);
		persist(entry);
		String[] textLayers = project.getTextLayers();
		for (String textLayer : textLayers) {
			Transcription transcription = entry.addTranscription(user).setTextLayer(textLayer);
			persist(transcription);
		}
		persist(project.addLogEntry("added entry " + projectEntry.getName(), user));

		commitTransaction();
		return entry;
	}

	private static final Comparator<Project> SORT_PROJECTS = new Comparator<Project>() {
		@Override
		public int compare(Project p1, Project p2) {
			return p2.getModifiedOn().compareTo(p1.getModifiedOn());
		}
	};

	public List<Project> getAll(User user) {
		openEntityManager();

		List<Project> projects;
		if (rootOrAdmin(user)) {
			projects = Lists.newArrayList(super.getAll());
		} else {
			projects = getProjectsForUser(user);
		}
		Collections.sort(projects, SORT_PROJECTS);
		closeEntityManager();
		return projects;
	}

	public Map<String, String> getProjectSettings(long project_id, User user) {
		openEntityManager();

		Project project = getProjectIfUserIsAllowed(project_id, user);
		Map<String, String> map = Maps.newHashMap();
		Hibernate.initialize(project);
		List<ProjectMetadataItem> projectMetadataItems = project.getProjectMetadataItems();
		Hibernate.initialize(projectMetadataItems);
		for (ProjectMetadataItem projectMetadataItem : projectMetadataItems) {
			Hibernate.initialize(projectMetadataItem);
			map.put(projectMetadataItem.getField(), projectMetadataItem.getData());
		}

		closeEntityManager();
		return map;
	}

	/* private methods*/
	Project getProjectIfUserIsAllowed(long project_id, User user) {
		if (rootOrAdmin(user)) {
			return super.read(project_id);
		}

		Project project = null;
		List<ProjectUser> resultList = getEntityManager().createQuery("from ProjectUser where user_id=:userId and project_id=:projectId", ProjectUser.class).setParameter("userId", user.getId()).setParameter("projectId", project_id).getResultList();
		if (!resultList.isEmpty()) {
			project = resultList.get(0).getProject();
			Hibernate.initialize(project);
		}
		if (project == null) {
			closeEntityManager();
			throw new UnauthorizedException();
		}
		return project;
	}

	private List<Project> getProjectsForUser(User user) {
		TypedQuery<ProjectUser> createQuery = getEntityManager().createQuery("from ProjectUser where user_id=:userId", ProjectUser.class).setParameter("userId", user.getId());
		List<ProjectUser> resultList = createQuery.getResultList();
		ImmutableList<ProjectUser> projectUsers = ImmutableList.copyOf(resultList);
		List<Project> projects = Lists.newArrayList();
		for (ProjectUser projectUser : projectUsers) {
			Project project = projectUser.getProject();
			Hibernate.initialize(project);
			projects.add(project);
		}
		return projects;
	}

	public String[] getProjectEntryMetadataFields(long project_id, User user) {
		openEntityManager();
		Project project = getProjectIfUserIsAllowed(project_id, user);
		String[] projectEntryMetadataFieldnames = project.getProjectEntryMetadataFieldnames();
		closeEntityManager();
		return projectEntryMetadataFieldnames;
	}

	public void setProjectEntryMetadataFields(long project_id, List<String> fields, User user) {
		beginTransaction();

		Project project = getProjectIfUserIsAllowed(project_id, user);
		project.setProjectEntryMetadataFieldnames(fields);
		persist(project);
		persist(project.addLogEntry("projectentrymetadatafields changed", user));
		setModifiedBy(project, user);

		commitTransaction();
	}

	public Object getProjectAnnotationTypes(long project_id, User user) {
		openEntityManager();
		Project project = getProjectIfUserIsAllowed(project_id, user);
		Set<AnnotationType> annotationTypes = project.getAnnotationTypes();
		closeEntityManager();
		return annotationTypes;
	}

	public List<Long> getProjectAnnotationTypeIds(long project_id, User user) {
		List<Long> list = Lists.newArrayList();
		openEntityManager();
		Project project = getProjectIfUserIsAllowed(project_id, user);
		for (AnnotationType annotationType : project.getAnnotationTypes()) {
			list.add(annotationType.getId());
		}
		closeEntityManager();
		return list;
	}

	public void setProjectAnnotationTypes(long project_id, List<Long> annotationTypeIds, User user) {
		beginTransaction();
		Project project = getProjectIfUserIsAllowed(project_id, user);

		Set<AnnotationType> annotationTypes = Sets.newHashSet();
		for (Long id : annotationTypeIds) {
			AnnotationType at = getEntityManager().find(AnnotationType.class, id);
			annotationTypes.add(at);
		}
		project.setAnnotationTypes(annotationTypes);
		persist(project.addLogEntry("projectannotationtypes changed", user));

		setModifiedBy(project, user);

		commitTransaction();
	}

	public void setProjectAnnotationTypes(long project_id, Set<AnnotationType> annotationTypes, User user) {
		beginTransaction();
		Project project = getProjectIfUserIsAllowed(project_id, user);

		project.setAnnotationTypes(annotationTypes);
		persist(project.addLogEntry("projectannotationtypes changed", user));

		setModifiedBy(project, user);

		commitTransaction();
	}

	public Map<String, Object> getProjectStatistics(long project_id, User user) {
		openEntityManager();
		Project project = getProjectIfUserIsAllowed(project_id, user);

		Map<String, Object> statistics = ImmutableMap.<String, Object> of("entries", getProjectEntriesStatistics(project_id, getEntityManager(), project));

		closeEntityManager();
		return statistics;
	}

	private Map<String, Object> getProjectEntriesStatistics(long project_id, EntityManager entityManager, Project project) {
		Long transcriptionCount = getTranscriptionCount(project_id, entityManager);
		Long annotationCount = getAnnotationCount(project_id, entityManager);
		Long facsimileCount = getFacsimileCount(project_id, entityManager);

		Map<String, Object> textLayerCountMap = getTextLayerCountMap(project_id, entityManager, project);
		Map<String, Object> transcriptionStatistics = Maps.newHashMap();
		transcriptionStatistics.put(COUNT_KEY, transcriptionCount);
		transcriptionStatistics.put("textlayers", textLayerCountMap);

		Map<String, Object> annotationTypeCountMap = getAnnotationTypeCountMap(project_id, entityManager, project);
		Map<String, Object> annotationStatistics = Maps.newHashMap();
		annotationStatistics.put(COUNT_KEY, annotationCount);
		annotationStatistics.put("annotationtypes", annotationTypeCountMap);

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

	private Long getAnnotationCount(long project_id, EntityManager entityManager) {
		Long annotationCount = (Long) entityManager//
				.createQuery("select count(*) from Annotation"//
						+ " where transcription_id in"//
						+ "   (select id from Transcription"//
						+ "     where project_entry_id in"//
						+ "      (select id from ProjectEntry where project_id=:project_id))"//
				)//
				.setParameter("project_id", project_id)//
				.getSingleResult();
		return annotationCount;
	}

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
		Project project = getProjectIfUserIsAllowed(project_id, user);
		List<FacetInfo> list = ImmutableList.copyOf(project.getFacetInfo());
		closeEntityManager();
		return list;
	}

	public void setTextlayers(long project_id, List<String> textLayers, User user) {
		beginTransaction();

		Project project = getProjectIfUserIsAllowed(project_id, user);
		List<String> previous = Lists.newArrayList(project.getTextLayers());
		List<String> deletedTextLayers = previous;
		deletedTextLayers.removeAll(textLayers);
		project.setTextLayers(textLayers);

		persist(project);
		persist(project.addLogEntry("project textlayers changed", user));
		setModifiedBy(project, user);

		Set<Long> modifiedEntryIds = Sets.newHashSet();
		if (!deletedTextLayers.isEmpty()) {
			persist(project.addLogEntry("removing textlayer(s) " + Joiner.on(", ").join(deletedTextLayers), user));
			for (String textlayer : deletedTextLayers) {
				List resultList = getEntityManager()//
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
		commitTransaction();

		beginTransaction();
		for (Long id : modifiedEntryIds) {
			ProjectEntry entry = getEntityManager().find(ProjectEntry.class, id);
			setModifiedBy(entry, user);
		}
		commitTransaction();

	}

	public void setProjectSettings(long project_id, Map<String, String> settingsMap, User user) {
		beginTransaction();

		Project project = getProjectIfUserIsAllowed(project_id, user);
		if (!user.getPermission(project).can(Action.EDIT_PROJECT_SETTINGS)) {
			throw new UnauthorizedException();
		}

		List<ProjectMetadataItem> projectMetadataItems = project.getProjectMetadataItems();
		for (ProjectMetadataItem projectMetadataItem : projectMetadataItems) {
			getEntityManager().remove(projectMetadataItem);
		}

		for (Entry<String, String> entry : settingsMap.entrySet()) {
			ProjectMetadataItem pmi = project.addMetadata(entry.getKey(), entry.getValue(), user);
			persist(pmi);
		}
		persist(project);
		persist(project.addLogEntry("projectsettings changed", user));
		setModifiedBy(project, user);

		commitTransaction();
	}

	public Set<User> getProjectUsersFull(long project_id, User user) {
		openEntityManager();
		Project project = getProjectIfUserIsAllowed(project_id, user);
		Set<User> projectUsers = project.getUsers();
		closeEntityManager();
		return projectUsers;
	}

	public List<Long> getProjectUserIds(long project_id, User user) {
		List<Long> list = Lists.newArrayList();
		Set<User> users = getProjectUsersFull(project_id, user);
		for (User pUser : users) {
			list.add(pUser.getId());
		}
		return list;
	}

	public User addProjectUser(long project_id, long user_id, User user) {
		beginTransaction();

		Project project = getProjectIfUserIsAllowed(project_id, user);
		User projectUser = getProjectUser(user_id);

		project.getUsers().add(projectUser);
		persist(project);
		persist(project.addLogEntry("user " + projectUser.getUsername() + " added to project", user));
		setModifiedBy(project, user);

		commitTransaction();
		return projectUser;
	}

	public void deleteProjectUser(long project_id, long user_id, User user) {
		beginTransaction();

		Project project = getProjectIfUserIsAllowed(project_id, user);
		User projectUser = getProjectUser(user_id);

		project.getUsers().remove(projectUser);
		persist(project);
		persist(project.addLogEntry("user " + projectUser.getUsername() + " removed from project", user));
		setModifiedBy(project, user);

		commitTransaction();
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
		Project project = getProjectIfUserIsAllowed(project_id, user);
		List<LogEntry> logEntries = Ordering.natural().sortedCopy(project.getLogEntries());
		closeEntityManager();
		return logEntries;
	}

	public String exportTei(long project_id, User user) {
		openEntityManager();
		Project project = getProjectIfUserIsAllowed(project_id, user);
		AnnotationType versregels = getEntityManager().createQuery("from AnnotationType where name=:name", AnnotationType.class).setParameter("name", "versregel").getSingleResult();
		String tei = exportTei(project, null, versregels);
		closeEntityManager();
		return tei;
	}

	public void exportPdf(long project_id, User user, String filename) {
		openEntityManager();
		Project project = getProjectIfUserIsAllowed(project_id, user);
		PdfMaker pdfMaker = new PdfMaker(project, getEntityManager());
		pdfMaker.saveToFile(filename);
		closeEntityManager();
	}

	public Publication.Status createPublicationStatus(long project_id, User user) {
		Publisher publisher = Publisher.instance();
		openEntityManager();
		Project project = getProjectIfUserIsAllowed(project_id, user);
		boolean canPublish = user.getPermission(project).can(Action.PUBLISH);
		Map<String, String> projectMetadata = project.getMetadataMap();
		closeEntityManager();

		if (!canPublish) {
			throw new UnauthorizedException(MessageFormat.format("{0} has no publishing permission for {1}", user.getUsername(), project.getTitle()));
		};

		Lists.newArrayList();
		Lists.newArrayList();
		String projectType = StringUtils.defaultIfBlank(projectMetadata.get("projectType"), ProjectTypes.COLLECTION);
		Publication.Settings settings = new Publication.Settings()//
				.setProjectId(project_id)//
				.setUser(user)//
				//    .setAnnotationTypeIds(annotationTypeIds)//
				//    .setProjectEntryMetadataFields(projectEntryMetadataFields)//
				.setProjectType(projectType);
		Publication.Status publicationStatus = publisher.publish(settings);

		return publicationStatus;
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
				TagInfo tagInfo = new TagInfo().setName("l")/*.setSkipNewlineAfter(true)*/;
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

		openEntityManager();
		String xml = new TeiMaker(project, config, getEntityManager()).toXML();
		closeEntityManager();
		return xml;
	}

	public void updateProjectUserIds(long project_id, List<Long> userIds, User user) {
		beginTransaction();
		Project project = getProjectIfUserIsAllowed(project_id, user);

		Set<User> users = Sets.newHashSet();
		for (Long userId : userIds) {
			User puser = getProjectUser(userId);
			users.add(puser);
		}
		project.setUsers(users);
		persist(project.addLogEntry("projectusers changed", user));

		setModifiedBy(project, user);

		commitTransaction();
	}

	public void setMetadata(long project_id, String key, String value, User user) {
		beginTransaction();
		Project project = getProjectIfUserIsAllowed(project_id, user);

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

		commitTransaction();
	}

	public void setProjectSortLevels(long project_id, List<String> levels, User user) {
		beginTransaction();
		Project project = getProjectIfUserIsAllowed(project_id, user);
		if (!user.getPermission(project).can(Action.EDIT_PROJECT_SETTINGS)) {
			throw new UnauthorizedException();
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

}
