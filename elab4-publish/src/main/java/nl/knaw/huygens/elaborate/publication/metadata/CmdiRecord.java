package nl.knaw.huygens.elaborate.publication.metadata;

import java.util.Date;

public class CmdiRecord extends AbstractMetadataRecord {
  private static final String TEMPLATE = "metadata-cmdi.xml";

  private String mdSelfLink = "";
  private String mdCreator = "";
  private final Date mdCreationDate = new Date();
  private ResourceProxy resourceProxy = new ResourceProxy();

  public String getMdSelfLink() {
    return mdSelfLink;
  }

  public CmdiRecord setMdSelfLink(String mdSelfLink) {
    this.mdSelfLink = mdSelfLink;
    return this;
  }

  public String getMdCreator() {
    return mdCreator;
  }

  public CmdiRecord setMdCreator(String mdCreator) {
    this.mdCreator = mdCreator;
    return this;
  }

  public Date getMdCreationDate() {
    return mdCreationDate;
  }

  public ResourceProxy getResourceProxy() {
    return resourceProxy;
  }

  public CmdiRecord setResourceProxy(ResourceProxy resourceProxy) {
    this.resourceProxy = resourceProxy;
    return this;
  }

  @Override
  String getTemplate() {
    return TEMPLATE;
  }

  @Override
  Object getDataModel() {
    return this;
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
  }
}
