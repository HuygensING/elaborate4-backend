package elaborate.editor.model.orm;


/**
 * Entry point for assertion of different data types. Each method in this class is a static factory for the
 * type-specific assertion objects.
 */
public class Assertions {

  /**
   * Creates a new instance of <code>{@link AnnotationAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static AnnotationAssert assertThat(Annotation actual) {
    return new AnnotationAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link AnnotationTypeAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static AnnotationTypeAssert assertThat(AnnotationType actual) {
    return new AnnotationTypeAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link FacsimileAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static FacsimileAssert assertThat(Facsimile actual) {
    return new FacsimileAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link ProjectAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static ProjectAssert assertThat(Project actual) {
    return new ProjectAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link ProjectEntryAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static ProjectEntryAssert assertThat(ProjectEntry actual) {
    return new ProjectEntryAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link TranscriptionAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static TranscriptionAssert assertThat(Transcription actual) {
    return new TranscriptionAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link UserAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static UserAssert assertThat(User actual) {
    return new UserAssert(actual);
  }

  /**
   * Creates a new </code>{@link Assertions}</code>.
   */
  protected Assertions() {
    // empty
  }
}
