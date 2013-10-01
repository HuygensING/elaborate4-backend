package nl.knaw.huygens.elaborate.publication.metadata;

public class CmdiRecord extends AbstractMetadataRecord {
  private static final String TEMPLATE = "metadata-cmdi.xml";

  @Override
  String getTemplate() {
    return TEMPLATE;
  }

  @Override
  Object getDataModel() {
    return this;
  }

}
