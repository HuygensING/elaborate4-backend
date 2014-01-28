package elaborate.editor.model.orm;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import nl.knaw.huygens.solr.SolrUtils;
import elaborate.editor.model.AbstractMetadataItem;

@Entity
@Table(name = "project_entry_metadata_items")
@XmlRootElement
public class ProjectEntryMetadataItem extends AbstractMetadataItem<ProjectEntryMetadataItem> {
  private static final long serialVersionUID = 1L;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_entry_id", columnDefinition = "int4")
  private ProjectEntry projectEntry;

  public ProjectEntry getProjectEntry() {
    return projectEntry;
  }

  public ProjectEntryMetadataItem setProjectEntry(ProjectEntry _projectEntry) {
    this.projectEntry = _projectEntry;
    return this;
  }

  public String getFacetName() {
    return SolrUtils.facetName(getField());
  }

}
