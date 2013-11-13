package elaborate.editor.model.orm;

import org.assertj.core.api.AbstractAssert;
// Assertions is needed if an assertion for Iterable is generated
import org.assertj.core.api.Assertions;


/**
 * {@link ProjectAnnotationType} specific assertions - Generated by CustomAssertionGenerator.
 */
public class ProjectAnnotationTypeAssert extends AbstractAssert<ProjectAnnotationTypeAssert, ProjectAnnotationType> {

  /**
   * Creates a new </code>{@link ProjectAnnotationTypeAssert}</code> to make assertions on actual ProjectAnnotationType.
   * @param actual the ProjectAnnotationType we want to make assertions on.
   */
  public ProjectAnnotationTypeAssert(ProjectAnnotationType actual) {
    super(actual, ProjectAnnotationTypeAssert.class);
  }

  /**
   * An entry point for ProjectAnnotationTypeAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one's can write directly : <code>assertThat(myProjectAnnotationType)</code> and get specific assertion with code completion.
   * @param actual the ProjectAnnotationType we want to make assertions on.
   * @return a new </code>{@link ProjectAnnotationTypeAssert}</code>
   */
  public static ProjectAnnotationTypeAssert assertThat(ProjectAnnotationType actual) {
    return new ProjectAnnotationTypeAssert(actual);
  }

  /**
   * Verifies that the actual ProjectAnnotationType's annotationType is equal to the given one.
   * @param annotationType the given annotationType to compare the actual ProjectAnnotationType's annotationType to.
   * @return this assertion object.
   * @throws AssertionError - if the actual ProjectAnnotationType's annotationType is not equal to the given one.
   */
  public ProjectAnnotationTypeAssert hasAnnotationType(AnnotationType annotationType) {
    // check that actual ProjectAnnotationType we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String errorMessage = "\nExpected annotationType of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    AnnotationType actualAnnotationType = actual.getAnnotationType();
    if (!org.assertj.core.util.Objects.areEqual(actualAnnotationType, annotationType)) {
      failWithMessage(errorMessage, actual, annotationType, actualAnnotationType);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual ProjectAnnotationType's id is equal to the given one.
   * @param id the given id to compare the actual ProjectAnnotationType's id to.
   * @return this assertion object.
   * @throws AssertionError - if the actual ProjectAnnotationType's id is not equal to the given one.
   */
  public ProjectAnnotationTypeAssert hasId(long id) {
    // check that actual ProjectAnnotationType we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String errorMessage = "\nExpected id of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // check
    long actualId = actual.getId();
    if (actualId != id) {
      failWithMessage(errorMessage, actual, id, actualId);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual ProjectAnnotationType's project is equal to the given one.
   * @param project the given project to compare the actual ProjectAnnotationType's project to.
   * @return this assertion object.
   * @throws AssertionError - if the actual ProjectAnnotationType's project is not equal to the given one.
   */
  public ProjectAnnotationTypeAssert hasProject(Project project) {
    // check that actual ProjectAnnotationType we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String errorMessage = "\nExpected project of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    Project actualProject = actual.getProject();
    if (!org.assertj.core.util.Objects.areEqual(actualProject, project)) {
      failWithMessage(errorMessage, actual, project, actualProject);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual ProjectAnnotationType's rev is equal to the given one.
   * @param rev the given rev to compare the actual ProjectAnnotationType's rev to.
   * @return this assertion object.
   * @throws AssertionError - if the actual ProjectAnnotationType's rev is not equal to the given one.
   */
  public ProjectAnnotationTypeAssert hasRev(long rev) {
    // check that actual ProjectAnnotationType we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String errorMessage = "\nExpected rev of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // check
    long actualRev = actual.getRev();
    if (actualRev != rev) {
      failWithMessage(errorMessage, actual, rev, actualRev);
    }

    // return the current assertion for method chaining
    return this;
  }

}
