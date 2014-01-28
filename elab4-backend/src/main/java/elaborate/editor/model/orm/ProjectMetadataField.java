package elaborate.editor.model.orm;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import elaborate.editor.model.AbstractTrackedEntity;

@Entity
@Table(name = "project_metadata_fields")
@XmlRootElement
public class ProjectMetadataField extends AbstractTrackedEntity<ProjectMetadataField> {
  private static final long serialVersionUID = 1L;

  String fieldName = "";
  String valueOptions = "";

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getValueOptions() {
    return valueOptions;
  }

  public void setValueOptions(String valueOptions) {
    this.valueOptions = valueOptions;
  }

  public boolean hasValueOptions() {
    return StringUtils.isNotBlank(valueOptions);
  }

  @Transient
  public List<String> getValueOptionsAsList() {
    return Lists.newArrayList(Splitter.on(',').split(valueOptions));
  }

}
