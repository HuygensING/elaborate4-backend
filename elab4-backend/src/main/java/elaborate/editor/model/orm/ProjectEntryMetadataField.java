package elaborate.editor.model.orm;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import elaborate.editor.model.AbstractStoredEntity;

@Entity
@Table(name = "project_entry_metadata_fields")
@XmlRootElement
public class ProjectEntryMetadataField extends AbstractStoredEntity<ProjectEntryMetadataField> {
  private static final long serialVersionUID = 1L;

  private String field;

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

}
