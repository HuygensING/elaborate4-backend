package elaborate.editor.model.orm;

import org.assertj.core.api.AbstractAssert;
// Assertions is needed if an assertion for Iterable is generated
import org.assertj.core.api.Assertions;


/**
 * {@link ProjectUser} specific assertions - Generated by CustomAssertionGenerator.
 */
public class ProjectUserAssert extends AbstractAssert<ProjectUserAssert, ProjectUser> {

  /**
   * Creates a new </code>{@link ProjectUserAssert}</code> to make assertions on actual ProjectUser.
   * @param actual the ProjectUser we want to make assertions on.
   */
  public ProjectUserAssert(ProjectUser actual) {
    super(actual, ProjectUserAssert.class);
  }

  /**
   * An entry point for ProjectUserAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one's can write directly : <code>assertThat(myProjectUser)</code> and get specific assertion with code completion.
   * @param actual the ProjectUser we want to make assertions on.
   * @return a new </code>{@link ProjectUserAssert}</code>
   */
  public static ProjectUserAssert assertThat(ProjectUser actual) {
    return new ProjectUserAssert(actual);
  }

  /**
   * Verifies that the actual ProjectUser's id is equal to the given one.
   * @param id the given id to compare the actual ProjectUser's id to.
   * @return this assertion object.
   * @throws AssertionError - if the actual ProjectUser's id is not equal to the given one.
   */
  public ProjectUserAssert hasId(long id) {
    // check that actual ProjectUser we want to make assertions on is not null.
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
   * Verifies that the actual ProjectUser's project is equal to the given one.
   * @param project the given project to compare the actual ProjectUser's project to.
   * @return this assertion object.
   * @throws AssertionError - if the actual ProjectUser's project is not equal to the given one.
   */
  public ProjectUserAssert hasProject(Project project) {
    // check that actual ProjectUser we want to make assertions on is not null.
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
   * Verifies that the actual ProjectUser's user is equal to the given one.
   * @param user the given user to compare the actual ProjectUser's user to.
   * @return this assertion object.
   * @throws AssertionError - if the actual ProjectUser's user is not equal to the given one.
   */
  public ProjectUserAssert hasUser(User user) {
    // check that actual ProjectUser we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String errorMessage = "\nExpected user of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    User actualUser = actual.getUser();
    if (!org.assertj.core.util.Objects.areEqual(actualUser, user)) {
      failWithMessage(errorMessage, actual, user, actualUser);
    }

    // return the current assertion for method chaining
    return this;
  }

}
