package nl.knaw.huygens.solr;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class FacetInfo {
  String name = "";
  String title = "";
  FacetType type = FacetType.LIST;

  public String getName() {
    return name;
  }

  public FacetInfo setName(String name) {
    this.name = name;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public FacetInfo setTitle(String title) {
    this.title = title;
    return this;
  }

  public FacetType getType() {
    return type;
  }

  public FacetInfo setType(FacetType type) {
    this.type = type;
    return this;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE, false);
  }

}
