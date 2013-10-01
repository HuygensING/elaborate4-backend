package nl.knaw.huygens.elaborate.publication.metadata;

public class DublinCoreRecord extends AbstractMetadataRecord {
  private static final String TEMPLATE = "metadata-oai_dc.xml";

  /*
   * see http://dublincore.org/documents/dces/
   */
  String contributor = "";
  String coverage = "";
  String creator = "";
  String date = "";
  String description = "";
  String format = "";
  String identifier = "";
  String language = "";
  String publisher = "";
  String relation = "";
  String rights = "";
  String source = "";
  String subject = "";
  String title = "";
  String type = "";

  /**
   * @return An entity responsible for making contributions to the resource.
   */
  public String getContributor() {
    return contributor;
  }

  /**
   * @param contributor An entity responsible for making contributions to the resource.<p>
   *                    Examples of a Contributor include a person, an organization, or a service.
   *                    Typically, the name of a Contributor should be used to indicate the entity
   * @return the modified DublinCoreRecord
   */
  public DublinCoreRecord setContributor(String contributor) {
    this.contributor = contributor;
    return this;
  }

  /**
   * @return The spatial or temporal topic of the resource, the spatial applicability of the resource, or the jurisdiction under which the resource is relevant
   */
  public String getCoverage() {
    return coverage;
  }

  /**
   * @param coverage The spatial or temporal topic of the resource, the spatial applicability of the resource, or the jurisdiction under which the resource is relevant<p>
   *                 Spatial topic and spatial applicability may be a named place or a location specified by its geographic coordinates.<br>
   *                 Temporal topic may be a named period, date, or date range.<br>
   *                 A jurisdiction may be a named administrative entity or a geographic place to which the resource applies.<br>
   *                 Recommended best practice is to use a controlled vocabulary such as the Thesaurus of Geographic Names [TGN].<br>
   *                 Where appropriate, named places or time periods can be used in preference to numeric identifiers such as sets of coordinates or date ranges.<br>
   * @return the modified DublinCoreRecord
   */
  public DublinCoreRecord setCoverage(String coverage) {
    this.coverage = coverage;
    return this;
  }

  /**
   * @return An entity primarily responsible for making the resource
   */
  public String getCreator() {
    return creator;
  }

  /**
   * @param creator An entity primarily responsible for making the resource<p>
   * Examples of a Creator include a person, an organization, or a service. Typically, the name of a Creator should be used to indicate the entity.
   * @return the modified DublinCoreRecord
   */
  public DublinCoreRecord setCreator(String creator) {
    this.creator = creator;
    return this;
  }

  /**
   * @return A point or period of time associated with an event in the lifecycle of the resource
   */
  public String getDate() {
    return date;
  }

  /**
   * @param date A point or period of time associated with an event in the lifecycle of the resource<p>
   * Date may be used to express temporal information at any level of granularity. Recommended best practice is to use an encoding scheme, such as the W3CDTF profile of ISO 8601 [W3CDTF].
   * @return the modified DublinCoreRecord
   */
  public DublinCoreRecord setDate(String date) {
    this.date = date;
    return this;
  }

  /**
   * @return An account of the resource
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description An account of the resource.<p>
   * Description may include but is not limited to: an abstract, a table of contents, a graphical representation, or a free-text account of the resource.
   * @return the modified DublinCoreRecord
   */
  public DublinCoreRecord setDescription(String description) {
    this.description = description;
    return this;
  }

  /**
   * @return The file format, physical medium, or dimensions of the resource.
   */
  public String getFormat() {
    return format;
  }

  /**
   * @param format The file format, physical medium, or dimensions of the resource.<p>
   *               Examples of dimensions include size and duration.
   *               Recommended best practice is to use a controlled vocabulary such as the list of Internet Media Types [MIME].
   * @return the modified DublinCoreRecord
   */
  public DublinCoreRecord setFormat(String format) {
    this.format = format;
    return this;
  }

  /**
   * @return An unambiguous reference to the resource within a given context.
   */
  public String getIdentifier() {
    return identifier;
  }

  /**
   * @param identifier An unambiguous reference to the resource within a given context.<p>
   * Recommended best practice is to identify the resource by means of a string conforming to a formal identification system.
   * @return the modified DublinCoreRecord

   */
  public DublinCoreRecord setIdentifier(String identifier) {
    this.identifier = identifier;
    return this;
  }

  /**
   * @return A language of the resource.
   */
  public String getLanguage() {
    return language;
  }

  /**
   * @param language A language of the resource.<p>
   * Recommended best practice is to use a controlled vocabulary such as RFC 4646 [RFC4646].
   * @return the modified DublinCoreRecord

   */
  public DublinCoreRecord setLanguage(String language) {
    this.language = language;
    return this;
  }

  /**
   * @return An entity responsible for making the resource available.
   */
  public String getPublisher() {
    return publisher;
  }

  /**
   * @param publisher An entity responsible for making the resource available.<p>
   * Examples of a Publisher include a person, an organization, or a service. Typically, the name of a Publisher should be used to indicate the entity.
   * @return the modified DublinCoreRecord

   */
  public DublinCoreRecord setPublisher(String publisher) {
    this.publisher = publisher;
    return this;
  }

  /**
   * @return A related resource.
   */
  public String getRelation() {
    return relation;
  }

  /**
   * @param relation A related resource.<p>
   * Recommended best practice is to identify the related resource by means of a string conforming to a formal identification system.
   * @return the modified DublinCoreRecord

   */
  public DublinCoreRecord setRelation(String relation) {
    this.relation = relation;
    return this;
  }

  /**
   * @return Information about rights held in and over the resource.
   */
  public String getRights() {
    return rights;
  }

  /**
   * @param rights Information about rights held in and over the resource.<p>
   * Typically, rights information includes a statement about various property rights associated with the resource, including intellectual property rights.
   * @return the modified DublinCoreRecord

   */
  public DublinCoreRecord setRights(String rights) {
    this.rights = rights;
    return this;
  }

  /**
   * @return A related resource from which the described resource is derived.
   */
  public String getSource() {
    return source;
  }

  /**
   * @param source A related resource from which the described resource is derived.<p>
   * The described resource may be derived from the related resource in whole or in part. Recommended best practice is to identify the related resource by means of a string conforming to a formal identification system.
   * @return the modified DublinCoreRecord

   */
  public DublinCoreRecord setSource(String source) {
    this.source = source;
    return this;
  }

  /**
   * @return The topic of the resource.
   */
  public String getSubject() {
    return subject;
  }

  /**
   * @param subject The topic of the resource.<p>
   * Typically, the subject will be represented using keywords, key phrases, or classification codes. Recommended best practice is to use a controlled vocabulary.
   * @return the modified DublinCoreRecord

   */
  public DublinCoreRecord setSubject(String subject) {
    this.subject = subject;
    return this;
  }

  /**
   * @return A name given to the resource.
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param title A name given to the resource.<p>
   * Typically, a Title will be a name by which the resource is formally known.
   * @return the modified DublinCoreRecord

   */
  public DublinCoreRecord setTitle(String title) {
    this.title = title;
    return this;
  }

  /**
   * @return The nature or genre of the resource.
   */
  public String getType() {
    return type;
  }

  /**
   * @param type The nature or genre of the resource.<p>
   *             Recommended best practice is to use a controlled vocabulary such as the DCMI Type Vocabulary [DCMITYPE].<br>
   *             To describe the file format, physical medium, or dimensions of the resource, use the Format element.
   * @return the modified DublinCoreRecord
   */
  public DublinCoreRecord setType(String type) {
    this.type = type;
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

}
