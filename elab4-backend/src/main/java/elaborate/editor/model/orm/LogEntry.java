package elaborate.editor.model.orm;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

import elaborate.editor.model.AbstractStoredEntity;

@Entity
@Table(name = "log_entries")
@XmlRootElement
public class LogEntry extends AbstractStoredEntity<LogEntry> implements Comparable<LogEntry> {
  private static final long serialVersionUID = 1L;

  String comment;

  //  @Column(name = "project_title")
  String projectTitle;

  //  @Column(name = "user_name")
  String userName;

  @Temporal(TemporalType.TIMESTAMP)
  //  @Column(name = "created_on")
  Date createdOn;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", columnDefinition = "int4")
  private Project project;

  @JsonIgnore
  public Project getProject() {
    return project;
  }

  public LogEntry setProject(Project project) {
    this.project = project;
    setProjectTitle(project.getTitle());
    return this;
  }

  public String getComment() {
    return comment;
  }

  public LogEntry setComment(String comment) {
    this.comment = comment;
    return this;
  }

  @JsonIgnore
  public String getProjectTitle() {
    return projectTitle;
  }

  public LogEntry setProjectTitle(String projectTitle) {
    this.projectTitle = projectTitle;
    return this;
  }

  public String getUserName() {
    return userName;
  }

  public LogEntry setUserName(String userName) {
    this.userName = userName;
    return this;
  }

  public Date getCreatedOn() {
    return createdOn;
  }

  public LogEntry setCreatedOn(Date createdOn) {
    this.createdOn = createdOn;
    return this;
  }

  @Override
  public int compareTo(LogEntry o) {
    return o.getCreatedOn().compareTo(this.getCreatedOn());
  }
}
