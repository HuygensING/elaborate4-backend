package elaborate.editor.model;

import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;

import elaborate.editor.model.orm.ProjectEntry;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class AbstractProjectEntryElement<T extends AbstractProjectEntryElement<T>> extends AbstractTrackedEntity<T> {
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_entry_id", columnDefinition = "int4")
	private ProjectEntry projectEntry;

	@JsonIgnore
	public ProjectEntry getProjectEntry() {
		return projectEntry;
	}

	public T setProjectEntry(ProjectEntry projectEntry) {
		this.projectEntry = projectEntry;
		return (T) this;
	}
}
