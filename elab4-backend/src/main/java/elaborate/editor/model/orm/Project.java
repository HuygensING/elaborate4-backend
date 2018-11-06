package elaborate.editor.model.orm;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2018 Huygens ING
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import elaborate.editor.model.AbstractDocument;
import elaborate.editor.model.ModelFactory;
import nl.knaw.huygens.facetedsearch.SolrFields;
import nl.knaw.huygens.facetedsearch.SolrUtils;
import nl.knaw.huygens.solr.FacetInfo;
import nl.knaw.huygens.solr.FacetType;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@Entity
@Table(name = "projects")
@XmlRootElement(name = "project")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Project extends AbstractDocument<Project> {
	private static final long serialVersionUID = 1L;
	private static final String FIELDNAME_SEPARATOR = ";";
	public static final String NEW_PROJECT_NAME = "new_project";
	private static final Set<String> DEFAULT_FACETFIELDS = Sets.newLinkedHashSet(Lists.newArrayList("publishable"));
	private static final Set<FacetInfo> DEFAULT_FACETINFO = Sets.newHashSet(//
			new FacetInfo().setName(SolrFields.PUBLISHABLE).setTitle(ProjectEntry.PUBLISHABLE).setType(FacetType.BOOLEAN)//
	);

	/* properties to persist */
	private String level_1 = "";
	private String level_2 = "";
	private String level_3 = "";

	@Column(columnDefinition = "text")
	private String project_entry_metadata_fieldnames = "";

	@Column(columnDefinition = "int4")
	private long project_leader_id;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
	private List<ProjectEntry> projectEntries;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
  private
  List<ProjectMetadataItem> project_metadata_items;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(//
	name = "project_annotation_types", //
	joinColumns = { @JoinColumn(name = "project_id", columnDefinition = "int4", nullable = false, updatable = false) }, //
	inverseJoinColumns = { @JoinColumn(name = "annotation_type_id", columnDefinition = "int4", nullable = false, updatable = false) //
	})
  private
  Set<AnnotationType> annotationTypes = Sets.newHashSet();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(//
	name = "project_users", //
	joinColumns = { @JoinColumn(name = "project_id", columnDefinition = "int4", nullable = false, updatable = false) }, //
	inverseJoinColumns = { @JoinColumn(name = "user_id", columnDefinition = "int4", nullable = false, updatable = false) //
	})
	private Set<User> users;

	@Column(columnDefinition = "text")
	private String text_layers = TranscriptionType.DIPLOMATIC;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
	private List<LogEntry> logEntries;

	/* persistent properties getters and setters */
	public String getLevel1() {
		return level_1;
	}

	public Project setLevel1(String level_1) {
		this.level_1 = level_1;
		return this;
	}

	public String getLevel2() {
		return level_2;
	}

	public Project setLevel2(String level_2) {
		this.level_2 = level_2;
		return this;
	}

	public String getLevel3() {
		return level_3;
	}

	public void setLevel3(String level_3) {
		this.level_3 = level_3;
  }

	public Project setProjectEntryMetadataFieldnames(List<String> project_entry_metadata_fieldnames) {
		this.project_entry_metadata_fieldnames = Joiner.on(FIELDNAME_SEPARATOR).join(project_entry_metadata_fieldnames);
		return this;
	}

	@JsonIgnore
	public Iterable<String> getProjectEntryMetadataFieldnames() {
		return Splitter.on(FIELDNAME_SEPARATOR).omitEmptyStrings().split(project_entry_metadata_fieldnames);
	}

	@JsonIgnore
	public String[] getFacetFields() {
		Set<String> facetFields = Sets.newLinkedHashSet(Project.DEFAULT_FACETFIELDS);
		for (String pemfn : getProjectEntryMetadataFieldnames()) {
			facetFields.add(SolrUtils.facetName(pemfn));
		}
		return facetFields.toArray(new String[0]);
	}

	@JsonIgnore
	public List<FacetInfo> getFacetInfo() {
		// TODO: move knowledge of multivaluedfacets here, from SearchConfig
		List<FacetInfo> list = Lists.newArrayList(DEFAULT_FACETINFO);
		for (String pemfn : getProjectEntryMetadataFieldnames()) {
			list.add(new FacetInfo()//
					.setName(SolrUtils.facetName(pemfn))//
					.setTitle(pemfn)//
					.setType(FacetType.LIST)//
			);
		}
		return list;
	}

	public long getProjectLeaderId() {
		return project_leader_id;
	}

	public void setProjectLeaderId(long project_leader_id) {
		this.project_leader_id = project_leader_id;
  }

	@JsonIgnore
	public List<ProjectEntry> getProjectEntries() {
		return projectEntries;
	}

	public Project setProjectEntries(List<ProjectEntry> projectEntries) {
		this.projectEntries = projectEntries;
		return this;
	}

	@JsonIgnore
	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	@JsonIgnore
	public List<ProjectMetadataItem> getProjectMetadataItems() {
		return project_metadata_items;
	}

	public void setProjectMetadataItems(List<ProjectMetadataItem> project_metadata_items) {
		this.project_metadata_items = project_metadata_items;
	}

	public String[] getTextLayers() {
		return text_layers.split(FIELDNAME_SEPARATOR);
	}

	public void setTextLayers(Collection<String> text_layers) {
		this.text_layers = Joiner.on(FIELDNAME_SEPARATOR).join(text_layers);
	}

	@JsonIgnore
	public Set<AnnotationType> getAnnotationTypes() {
		return annotationTypes;
	}

	public void setAnnotationTypes(Set<AnnotationType> annotationTypes) {
		this.annotationTypes = annotationTypes;
	}

	//
	// @OneToMany
	// ProjectAnnotationType[] getProjectAnnotationTypes();
	//

	//
	//
	// /**
	// * @deprecated use
	// * {@link #getLevel(1)}
	// * instead
	// */
	// @Deprecated
	// String getLevel1();
	//
	// void setLevel1(String projectEntryMetadataFieldname);
	//
	// /**
	// * @deprecated use
	// * {@link #getLevel(2)}
	// * instead
	// */
	// @Deprecated
	// String getLevel2();
	//
	// void setLevel2(String projectEntryMetadataFieldname);
	//
	// /**
	// * @deprecated use
	// * {@link #getLevel(3)}
	// * instead
	// */
	// @Deprecated
	// String getLevel3();
	//
	// void setLevel3(String projectEntryMetadataFieldname);
	//
	//
	// int getProjectLeaderId();
	//
	// void setProjectLeaderId(int id);
	//
	//
	// }
	//
	// // public List<ProjectEntryMetadataKey> getProjectEntryMetadataKeys() {
	// // Set<ProjectEntryMetadataKey> metadataKeys = Sets.newLinkedHashSet();
	// // // for (Corpus corpus : getCorpora()) {
	// // // metadataKeys.addAll(corpus.getFacsimileMetadataKeys());
	// // // }
	// // return Lists.newArrayList(metadataKeys);
	// // }
	//

	/* transient methods */

	// public String[] getLevels() {
	// return new String[] { getLevel(1), getLevel(2), getLevel(3) };
	// }
	//
	// @SuppressWarnings("deprecation")
	// public String getLevel(int i) {
	// switch (i) {
	// case 1:
	// return (project.getLevel1() != null) ? project.getLevel1() : ProjectEntry.FOLIO_NUMBER;
	// case 2:
	// return (project.getLevel2() != null) ? project.getLevel2() : ProjectEntry.FOLIO_SIDE;
	// case 3:
	// return (project.getLevel3() != null) ? project.getLevel3() : ProjectEntry.COLUMN_ON_PAGE;
	//
	// default:
	// return null;
	// }
	// }
	//
	// public void setLevel(int i, String facsimileMetadataKeyName) {
	// switch (i) {
	// case 1:
	// project.setLevel1(facsimileMetadataKeyName);
	// break;
	// case 2:
	// project.setLevel2(facsimileMetadataKeyName);
	// break;
	// case 3:
	// project.setLevel3(facsimileMetadataKeyName);
	// break;
	//
	// default:
	// break;
	// }
	// }
	//
	// // public List<ProjectEntry> getProjectEntriesInOrder0() {
	// // StopWatch sw = new StopWatch();
	// // sw.start();
	// // // TODO: this is a costly method, should be cachable or the order should be written to the database
	// // List<ProjectEntry> entries = Lists.newArrayList(project.getProjectEntries());
	// // Collections.sort(entries, getEntryComparator());
	// // sw.stop();
	// // Log.info("entrysort took:" + sw.getTime() + " milliseconds");
	// // return entries;
	// // }
	//
	// public List<ProjectEntryProxy> getProjectEntryProxiesInOrder(String[] levels) {
	// Map<String, String> filter = Maps.newHashMap();
	// return getProjectEntryProxiesInOrder(filter, levels);
	// }
	//
	// public List<ProjectEntryProxy> getProjectEntryProxiesInOrder(final Map<String, String> filter, String[] level) {
	// StopWatch sw = new StopWatch();
	// sw.start();
	// Integer projectId = Integer.valueOf(project.getId().toString());
	// List<ProjectEntryProxy> entries = ProjectEntryProxyFactory.select(projectId, filter, level);
	// // for (ProjectEntryProxy projectEntryProxy : entries) {
	// // Log.info("proxy='{}'", projectEntryProxy);
	// // }
	// sw.stop();
	// Log.info("entrysort of " + entries.size() + " entries took:" + sw.getTime() + " milliseconds");
	// return entries;
	// }
	//
	// public List<ProjectEntry> getProjectEntriesInOrder(String[] levels) {
	// Map<String, String> filter = Maps.newHashMap();
	// return getProjectEntriesInOrder(filter, levels);
	// }
	//
	// public List<ProjectEntry> getProjectEntriesInOrder(final Map<String, String> filter, String[] level) {
	// StopWatch sw = new StopWatch();
	// sw.start();
	// StringBuilder joinBuilder = new StringBuilder().//
	// append(" left outer join project_entry_metadata_items l1").//
	// append(" on (l1.project_entry_id = project_entries.id and l1.field='").append(level[0]).append("')").//
	// append(" left outer join project_entry_metadata_items l2").//
	// append(" on (l2.project_entry_id = project_entries.id and l2.field='").append(level[1]).append("')").//
	// append(" left outer join project_entry_metadata_items l3").//
	// append(" on (l3.project_entry_id = project_entries.id and l3.field='").append(level[2]).append("')");
	// Term projectTerm = new Term("project_id", project.getId());
	// Query q = new Query(projectTerm).join(joinBuilder.toString()).orderBy("l1.data,l2.data,l3.data,name");
	// StringBuilder sb = new StringBuilder();
	// q.appendToSQL(sb);
	// // Log.info("query={}", sb.toString());
	// List<ProjectEntry> entries = Lists.newArrayList(ModelFactory.getEntities(ProjectEntry.class, q));
	// if (!filter.isEmpty()) {
	// entries = Lists.newArrayList(Iterables.filter(entries, filterPredicate(filter)));
	// }
	//
	// sw.stop();
	// Log.info("entrysort of " + entries.size() + " entries took:" + sw.getTime() + " milliseconds");
	// return entries;
	// }
	//
	// private Predicate<ProjectEntry> filterPredicate(final Map<String, String> filter) {
	// return new Predicate<ProjectEntry>() {
	//
	// @Override
	// public boolean apply(ProjectEntry projectEntry) {
	// boolean result = true;
	// Set<Entry<String, String>> entrySet = filter.entrySet();
	// for (Entry<String, String> filterEntry : entrySet) {
	// String key = filterEntry.getKey();
	// String value = filterEntry.getValue();
	// result = result && value.equals(projectEntry.getMetadata(key));
	// }
	// return result;
	// }
	// };
	// }
	//
	// public ProjectEntry getProjectEntryByName(String name) {
	// for (ProjectEntry projectEntry : project.getProjectEntries()) {
	// if (projectEntry.getName().equals(name)) {
	// return projectEntry;
	// }
	// }
	// return null;
	// }
	//
	// public List<Transcription> getTranscriptionsInOrder(TranscriptionType transcriptionType, String[] levels) {
	// List<Transcription> transcriptionsInOrder = Lists.newArrayList();
	// List<ProjectEntry> projectEntriesInOrder = getProjectEntriesInOrder(levels);
	// for (ProjectEntry projectEntry : projectEntriesInOrder) {
	// if (projectEntry.hasTranscriptions()) {
	// for (Transcription transcription : projectEntry.getTranscriptions()) {
	// if (transcription.hasTranscriptionType(transcriptionType)) {
	// transcriptionsInOrder.add(transcription);
	// }
	// }
	// }
	// }
	// return transcriptionsInOrder;
	// }
	//
	// public Comparator<ProjectEntry> getEntryComparator() {
	// // TODO: is guava's Ordering niet beter in dit geval?
	// return new Comparator<ProjectEntry>() {
	//
	// @Override
	// public int compare(ProjectEntry e1, ProjectEntry e2) {
	// int compare1 = compareLevel(1, e1, e2);
	// if (compare1 != 0) {
	// return compare1;
	// }
	// int compare2 = compareLevel(2, e1, e2);
	// if (compare2 != 0) {
	// return compare2;
	// }
	// int compare3 = compareLevel(3, e1, e2);
	// return compare3;
	// }
	//
	// private int compareLevel(int i, ProjectEntry e1, ProjectEntry e2) {
	// String f1level = e1.getMetadata(getLevel(i));
	// String f2level = e2.getMetadata(getLevel(i));
	// return f1level.compareTo(f2level);
	// }
	// };
	// }
	//
	// public AnnotationType addAnnotationType(String name, User user, String description) {
	// return ModelFactory.createAnnotationType(name, description, user);
	// }
	//
	// public AnnotationType getDefaultAnnotationType() {
	// AnnotationType[] annotationTypes = ModelFactory.getAnnotationTypes();
	// AnnotationType annotationtype = annotationTypes.length > 0 ? annotationTypes[0] : null;
	// return annotationtype;
	// }
	//
	// public void addUser(User user) {
	// if (!getUsers().contains(user)) {
	// ModelFactory.createProjectUser(project, user);
	// }
	// }
	//
	// public void removeUser(User user) {
	// for (ProjectUser projectUser : project.getProjectUsers()) {
	// if (projectUser.getUser().equals(user)) {
	// try {
	// log(String.format("User %s removed from Project", user.getUsername()), null);
	// projectUser.delete();
	// } catch (StorageException e) {
	// throw new RuntimeException(e);
	// }
	// }
	// }
	// }
	//
	// public List<User> getUsers() {
	// ProjectUser[] projectUsers = project.getProjectUsers();
	// List<User> users = Lists.newArrayList();
	// for (ProjectUser projectUser : projectUsers) {
	// users.add(projectUser.getUser());
	// }
	// return users;
	// }
	//
	public ProjectEntry addEntry(String name, User creator) {
		return ModelFactory.createTrackedEntity(ProjectEntry.class, creator)//
				.setProject(this)//
				.setName(name)//
				.setFacsimiles(Lists.<Facsimile> newArrayList())//
				.setTranscriptions(Lists.<Transcription> newArrayList());
	}

	//
	// private void log(String comment, User _user) {
	// User user = _user;
	// if (user == null) {
	// try {
	// user = ElaborateSession.get().getCurrentUser();
	// } catch (IllegalStateException e) {
	// // thrown when run from test
	// return;
	// }
	// }
	// ModelFactory.createLogEntry(user, project, comment);
	// }
	//
	// public List<String> getProjectMetadataFieldnamesList() {
	// List<String> projectMetadataFieldnamesList = Lists.newArrayList();
	// for (ProjectMetadataFields projectMetadataField : ModelFactory.getProjectMetadataFields()) {
	// projectMetadataFieldnamesList.add(projectMetadataField.getFieldName());
	// }
	// return projectMetadataFieldnamesList;
	// }
	//
	// public List<String> getProjectEntryMetadataFieldnamesList() {
	// return Lists.newArrayList(Splitter.on(FIELDNAME_SEPARATOR).omitEmptyStrings().split(project.getProjectEntryMetadataFieldnames()));
	// // return project.getProjectEntryMetadataFieldnames().split(FIELDNAME_SEPARATOR);
	// }
	//
	// public synchronized void checkProjectEntryMetadataFieldname(String fieldname, User modifier) {
	// List<String> projectEntryMetadataFieldnamesList = getProjectEntryMetadataFieldnamesList();
	// if (!projectEntryMetadataFieldnamesList.contains(fieldname)) {
	// projectEntryMetadataFieldnamesList.add(fieldname);
	// String join = Joiner.on(FIELDNAME_SEPARATOR).join(projectEntryMetadataFieldnamesList);
	// project.setProjectEntryMetadataFieldnames(join);
	// ModelFactory.updateProjectEntryMetadataFields(join);
	// ModelFactory.save(project, modifier);
	// }
	// }
	//
	// public synchronized void checkProjectMetadataFieldname(String fieldname, User user) {
	// List<String> projectMetadataFieldnamesList = getProjectMetadataFieldnamesList();
	// if (!projectMetadataFieldnamesList.contains(fieldname)) {
	// ModelFactory.createProjectMetadataField(fieldname, user);
	// }
	// }
	//
	// public String getMetadata(String field) {
	// ProjectMetadataItem[] projectMetadataItems = project.getProjectMetadataItems();
	// for (ProjectMetadataItem projectMetadataItem : projectMetadataItems) {
	// if (field.equals(projectMetadataItem.getField())) {
	// return projectMetadataItem.getData();
	// }
	// }
	// return "";
	// }
	//
	//
	// public void setMetadata(Map<String, String> metadata, User creator) {
	// removeAllMetadata();
	// for (Entry<String, String> entry : metadata.entrySet()) {
	// addMetadata(entry.getKey(), entry.getValue(), creator);
	// }
	// }
	//
	// public AnnotationType getAnnotationType(String name) {
	// AnnotationType[] annotationTypes = ModelFactory.getAnnotationTypes();
	// for (AnnotationType annotationType : annotationTypes) {
	// if (name.equals(annotationType.getName())) {
	// return annotationType;
	// }
	// }
	// return null;
	// }
	//
	// public int getProjectEntryCount() {
	// return ModelFactory.getEntityCount(ProjectEntry.class, new Query("project_id", Integer.valueOf(project.getId().toString())));
	// }
	//
	// public List<String> getFolioNumbers(String[] levels) {
	// List<String> list = Lists.newArrayList();
	// for (ProjectEntry projectEntry : project.getProjectEntriesInOrder(levels)) {
	// String folioNumber = projectEntry.getMetadata(ProjectEntry.FOLIO_NUMBER);
	// if (!list.contains(folioNumber)) {
	// list.add(folioNumber);
	// }
	// }
	// return list;
	// }
	//
	// public User getProjectLeader() {
	// int projectLeaderId = project.getProjectLeaderId();
	// if (projectLeaderId == -1) {
	// ProjectUser[] projectUsers = project.getProjectUsers();
	// for (ProjectUser projectUser : projectUsers) {
	// if (projectUser.getUser().hasRole(ElaborateRoles.PROJECTLEADER)) {
	// return projectUser.getUser();
	// }
	// }
	//
	// } else {
	// return ModelFactory.getUserById(String.valueOf(projectLeaderId));
	// }
	// return null;
	// }
	//
	// public List<AnnotationType> getAnnotationTypes() {
	// List<AnnotationType> types = Lists.newArrayList();
	// for (ProjectAnnotationType projectAnnotationType : project.getProjectAnnotationTypes()) {
	// types.add(projectAnnotationType.getAnnotationType());
	// }
	// if (types.isEmpty()) {
	// AnnotationType defaultAnnotationType = getDefaultAnnotationType();
	// ModelFactory.createProjectAnnotationType(project, defaultAnnotationType);
	// types.add(defaultAnnotationType);
	// }
	// return types;
	// }
	//
	// public void addAnnotationType(AnnotationType at) {
	// if (!getAnnotationTypes().contains(at)) {
	// ModelFactory.createProjectAnnotationType(project, at);
	// }
	// }
	//
	// public boolean removeAnnotationType(AnnotationType at) {
	// ProjectAnnotationType[] projectAnnotationTypes = project.getProjectAnnotationTypes();
	// for (ProjectAnnotationType projectAnnotationType : projectAnnotationTypes) {
	// if (projectAnnotationType.getAnnotationType().equals(at)) {
	// ModelFactory.deleteEntity(projectAnnotationType);
	// return true;
	// }
	// }
	// return false;
	// }
	//
	// public Class<? extends ProjectPage> getProjectPageClass() {
	// if (isFaceted()) {
	// return FacetedProjectPage.class;
	// }
	// return TreeProjectPage.class;
	// }
	//
	// public boolean isFaceted() {
	// // return true;
	// return project.getMetadata("pagemode").equals("faceted");
	// }
	//
	// // public boolean removeAnnotationType(AnnotationType annotationType) {
	// // try {
	// // log(String.format("removed AnnotationType %s: %s", annotationType.getName(), annotationType.getDescription()), null);
	// // annotationType.delete();
	// // return true;
	// // } catch (StorageException e) {
	// // e.printStackTrace();
	// // return false;
	// // }
	// // }
	@JsonIgnore
	@Transient
	public Map<String, FacetInfo> getFacetInfoMap() {
		Map<String, FacetInfo> map = Maps.newLinkedHashMap();
		for (FacetInfo facetInfo : getFacetInfo()) {
			map.put(facetInfo.getName(), facetInfo);
		}
		return map;
	}

	// public void addMetadata(String _field, String value, User creator) {
	// String field = _field.replaceAll(ProjectImpl.FIELDNAME_SEPARATOR, "");
	// project.checkProjectMetadataFieldname(field, creator);
	// ModelFactory.createProjectMetadataItem(project, field, value, creator);
	// }
	//
	// void removeAllMetadata() {
	// for (ProjectMetadataItem projectMetadataItem : project.getProjectMetadataItems()) {
	// try {
	// projectMetadataItem.delete();
	// } catch (StorageException e) {
	// throw new RuntimeException(e);
	// }
	// }
	// }
	public ProjectMetadataItem addMetadata(String key, String value, User creator) {
		return ModelFactory.createTrackedEntity(ProjectMetadataItem.class, creator)//
				.setProject(this)//
				.setField(key)//
				.setData(value);
	}

	@JsonIgnore
	public Map<String, String> getMetadataMap() {
		Map<String, String> map = Maps.newHashMap();
		List<ProjectMetadataItem> projectMetadataItems = getProjectMetadataItems();
		for (ProjectMetadataItem projectMetadataItem : projectMetadataItems) {
			map.put(projectMetadataItem.getField(), projectMetadataItem.getData());
		}
		return map;
	}

	@JsonIgnore
	public List<LogEntry> getLogEntries() {
		return logEntries;
	}

	public LogEntry addLogEntry(String string, User user) {
		return ModelFactory.create(LogEntry.class)//
				.setProject(this)//
				.setComment(string)//
				.setCreatedOn(new Date())//
				.setUserName(user.getUsername());
	}

}
