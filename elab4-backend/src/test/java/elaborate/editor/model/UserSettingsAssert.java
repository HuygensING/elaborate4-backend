package elaborate.editor.model;

import org.assertj.core.api.AbstractAssert;
// Assertions is needed if an assertion for Iterable is generated
import org.assertj.core.api.Assertions;


/**
 * {@link UserSettings} specific assertions - Generated by CustomAssertionGenerator.
 */
public class UserSettingsAssert extends AbstractAssert<UserSettingsAssert, UserSettings> {

  /**
   * Creates a new </code>{@link UserSettingsAssert}</code> to make assertions on actual UserSettings.
   * @param actual the UserSettings we want to make assertions on.
   */
  public UserSettingsAssert(UserSettings actual) {
    super(actual, UserSettingsAssert.class);
  }

  /**
   * An entry point for UserSettingsAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one's can write directly : <code>assertThat(myUserSettings)</code> and get specific assertion with code completion.
   * @param actual the UserSettings we want to make assertions on.
   * @return a new </code>{@link UserSettingsAssert}</code>
   */
  public static UserSettingsAssert assertThat(UserSettings actual) {
    return new UserSettingsAssert(actual);
  }

}
