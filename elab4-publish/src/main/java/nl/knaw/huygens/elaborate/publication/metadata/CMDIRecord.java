package nl.knaw.huygens.elaborate.publication.metadata;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class CMDIRecord extends AbstractMetadataRecord {
  private static final String TEMPLATE = "metadata-cmdi.xml.ftl";

  private String mdSelfLink = "";
  private String mdCreator = "";
  private Date mdCreationDate = new Date();
  private String mdProfile = "";
  private String mdCollectionDisplayName = "";

  private ResourceProxy resourceProxy = new ResourceProxy();

  protected CMDIRecord() {}

  public String getMdSelfLink() {
    return mdSelfLink;
  }

  private void setMdSelfLink(String mdSelfLink) {
    this.mdSelfLink = mdSelfLink;
  }

  public String getMdCreator() {
    return mdCreator;
  }

  private void setMdCreator(String mdCreator) {
    this.mdCreator = mdCreator;
  }

  public Date getMdCreationDate() {
    return mdCreationDate;
  }

  private void setMdCreationDate(Date mdCreationDate) {
    this.mdCreationDate = mdCreationDate;
  }

  public String getMdProfile() {
    return mdProfile;
  }

  private void setMdProfile(String mdProfile) {
    this.mdProfile = mdProfile;
  }

  public String getMdCollectionDisplayName() {
    return mdCollectionDisplayName;
  }

  private void setMdCollectionDisplayName(String mdCollectionDisplayName) {
    this.mdCollectionDisplayName = mdCollectionDisplayName;
  }

  public ResourceProxy getResourceProxy() {
    return resourceProxy;
  }

  private void setResourceProxy(ResourceProxy resourceProxy) {
    this.resourceProxy = resourceProxy;
  }

  @Override
  String getTemplate() {
    return TEMPLATE;
  }

  @Override
  Object getDataModel() {
    return this;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  public static class Builder {
    private final CMDIRecord cMDIRecord = new CMDIRecord();

    public Builder setMdSelfLink(String mdSelfLink) {
      cMDIRecord.setMdSelfLink(mdSelfLink);
      return this;
    }

    public Builder setMdCreationDate(Date mdCreationDate) {
      cMDIRecord.setMdCreationDate(mdCreationDate);
      return this;
    }

    public Builder setMdCollectionDisplayName(String mdCollectionDisplayName) {
      cMDIRecord.setMdCollectionDisplayName(mdCollectionDisplayName);
      return this;
    }

    public Builder setResourceProxy(ResourceProxy resourceProxy) {
      cMDIRecord.setResourceProxy(resourceProxy);
      return this;
    }

    public Builder setMdCreator(String mdCreator) {
      cMDIRecord.setMdCreator(mdCreator);
      return this;
    }

    public Builder setMdProfile(String mdProfile) {
      cMDIRecord.setMdProfile(mdProfile);
      return this;
    }

    public CMDIRecord build() {
      List<String> validationResults = Lists.newArrayListWithCapacity(3);
      validationResults.add(checkRequiredSetting(cMDIRecord.getMdCreator(), "MdCreator"));
      validationResults.add(checkRequiredSetting(cMDIRecord.getMdProfile(), "MdProfile"));
      validationResults.add(checkRequiredSetting(cMDIRecord.getMdSelfLink(), "MdSelfLink"));
      String join = Joiner.on("; ").skipNulls().join(validationResults);
      if (StringUtils.isNotEmpty(join)) {
        throw new InvalidCMDIRecordException("invalid CMDIRecord: " + join);
      }
      return cMDIRecord;
    }

    private String checkRequiredSetting(String value, String string) {
      if (StringUtils.isBlank(value)) {
        return "set " + string;
      }
      return null;
    }

  }

  public static class ResourceProxy {
    String id = "";
    String ref = "";

    public String getId() {
      return id;
    }

    public ResourceProxy setId(String id) {
      this.id = id;
      return this;
    }

    public String getRef() {
      return ref;
    }

    public ResourceProxy setRef(String ref) {
      this.ref = ref;
      return this;
    }

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
  }
}
