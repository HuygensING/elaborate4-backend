package elaborate.editor.model.orm.service;

import java.util.Date;

import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.User;

public class ExtendedProjectEntry {
	ProjectEntry projectEntry;
	private String l1;
	private String l2;
	private String l3;

	public ProjectEntry getProjectEntry() {
		return projectEntry;
	}

	public ProjectEntry setProject(Project _project) {
		return projectEntry.setProject(_project);
	}

	public ProjectEntry setId(long id) {
		return projectEntry.setId(id);
	}

	public ProjectEntry setCreator(User user) {
		return projectEntry.setCreator(user);
	}

	public ProjectEntry setCreatedOn(Date date) {
		return projectEntry.setCreatedOn(date);
	}

	public ProjectEntry setModifier(User user) {
		return projectEntry.setModifier(user);
	}

	public ProjectEntry setModifiedOn(Date date) {
		return projectEntry.setModifiedOn(date);
	}

	public ProjectEntry setName(String name) {
		return projectEntry.setName(name);
	}

	public void setModifiedBy(User _modifier) {
		projectEntry.setModifiedBy(_modifier);
	}

	public void setCreatedBy(User creator) {
		projectEntry.setCreatedBy(creator);
	}

	public ProjectEntry setPublishable(boolean publishable) {
		return projectEntry.setPublishable(publishable);
	}

	public String getL1() {
		return l1;
	}

	public ExtendedProjectEntry setL1(String l1) {
		this.l1 = l1;
		return this;
	}

	public String getL2() {
		return l2;
	}

	public ExtendedProjectEntry setL2(String l2) {
		this.l2 = l2;
		return this;
	}

	public String getL3() {
		return l3;
	}

	public ExtendedProjectEntry setL3(String l3) {
		this.l3 = l3;
		return this;
	}

}
