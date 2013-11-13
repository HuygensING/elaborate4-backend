package elaborate.editor.model;

import org.assertj.core.api.AbstractAssert;

// Assertions is needed if an assertion for Iterable is generated

/**
 * {@link KeyValuePair} specific assertions - Generated by CustomAssertionGenerator.
 */
public class KeyValuePairAssert extends AbstractAssert<KeyValuePairAssert, KeyValuePair> {

  /**
   * Creates a new </code>{@link KeyValuePairAssert}</code> to make assertions on actual KeyValuePair.
   * @param actual the KeyValuePair we want to make assertions on.
   */
  public KeyValuePairAssert(KeyValuePair actual) {
    super(actual, KeyValuePairAssert.class);
  }

  /**
   * An entry point for KeyValuePairAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one's can write directly : <code>assertThat(myKeyValuePair)</code> and get specific assertion with code completion.
   * @param actual the KeyValuePair we want to make assertions on.
   * @return a new </code>{@link KeyValuePairAssert}</code>
   */
  public static KeyValuePairAssert assertThat(KeyValuePair actual) {
    return new KeyValuePairAssert(actual);
  }

  /**
   * Verifies that the actual KeyValuePair's key is equal to the given one.
   * @param key the given key to compare the actual KeyValuePair's key to.
   * @return this assertion object.
   * @throws AssertionError - if the actual KeyValuePair's key is not equal to the given one.
   */
  public KeyValuePairAssert hasKey(String key) {
    // check that actual KeyValuePair we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String errorMessage = "\nExpected key of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

    // null safe check
    String actualKey = actual.getKey();
    if (!org.assertj.core.util.Objects.areEqual(actualKey, key)) {
      failWithMessage(errorMessage, actual, key, actualKey);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual KeyValuePair's value is equal to the given one.
   * @param value the given value to compare the actual KeyValuePair's value to.
   * @return this assertion object.
   * @throws AssertionError - if the actual KeyValuePair's value is not equal to the given one.
   */
  public KeyValuePairAssert hasValue(Object value) {
    // check that actual KeyValuePair we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String errorMessage = "\nExpected value of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

    // null safe check
    Object actualValue = actual.getValue();
    if (!org.assertj.core.util.Objects.areEqual(actualValue, value)) {
      failWithMessage(errorMessage, actual, value, actualValue);
    }

    // return the current assertion for method chaining
    return this;
  }

}
