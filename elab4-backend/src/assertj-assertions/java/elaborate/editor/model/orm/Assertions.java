package elaborate.editor.model.orm;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2021 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

/**
 * Entry point for assertion of different data types. Each method in this class is a static factory
 * for the type-specific assertion objects.
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

  /** Creates a new </code>{@link Assertions}</code>. */
  protected Assertions() {
    // empty
  }
}
