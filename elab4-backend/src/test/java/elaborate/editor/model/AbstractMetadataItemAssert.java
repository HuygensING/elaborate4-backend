package elaborate.editor.model;

import java.util.Date;

import org.assertj.core.api.AbstractAssert;

import elaborate.editor.model.orm.User;

// Assertions is needed if an assertion for Iterable is generated

/**
 * {@link AbstractMetadataItem} specific assertions - Generated by CustomAssertionGenerator.
 */
public class AbstractMetadataItemAssert extends AbstractAssert<AbstractMetadataItemAssert, AbstractMetadataItem> {

  /**
   * Creates a new </code>{@link AbstractMetadataItemAssert}</code> to make assertions on actual AbstractMetadataItem.
   * @param actual the AbstractMetadataItem we want to make assertions on.
   */
  public AbstractMetadataItemAssert(AbstractMetadataItem actual) {
    super(actual, AbstractMetadataItemAssert.class);
  }

  /**
   * An entry point for AbstractMetadataItemAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one's can write directly : <code>assertThat(myAbstractMetadataItem)</code> and get specific assertion with code completion.
   * @param actual the AbstractMetadataItem we want to make assertions on.
   * @return a new </code>{@link AbstractMetadataItemAssert}</code>
   */
  public static AbstractMetadataItemAssert assertThat(AbstractMetadataItem actual) {
    return new AbstractMetadataItemAssert(actual);
  }

  /**
   * Verifies that the actual AbstractMetadataItem's createdOn is equal to the given one.
   * @param createdOn the given createdOn to compare the actual AbstractMetadataItem's createdOn to.
   * @return this assertion object.
   * @throws AssertionError - if the actual AbstractMetadataItem's createdOn is not equal to the given one.
   */
  public AbstractMetadataItemAssert hasCreatedOn(Date createdOn) {
    // check that actual AbstractMetadataItem we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String errorMessage = "\nExpected createdOn of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

    // null safe check
    Date actualCreatedOn = actual.getCreatedOn();
    if (!org.assertj.core.util.Objects.areEqual(actualCreatedOn, createdOn)) {
      failWithMessage(errorMessage, actual, createdOn, actualCreatedOn);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual AbstractMetadataItem's creator is equal to the given one.
   * @param creator the given creator to compare the actual AbstractMetadataItem's creator to.
   * @return this assertion object.
   * @throws AssertionError - if the actual AbstractMetadataItem's creator is not equal to the given one.
   */
  public AbstractMetadataItemAssert hasCreator(User creator) {
    // check that actual AbstractMetadataItem we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String errorMessage = "\nExpected creator of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

    // null safe check
    User actualCreator = actual.getCreator();
    if (!org.assertj.core.util.Objects.areEqual(actualCreator, creator)) {
      failWithMessage(errorMessage, actual, creator, actualCreator);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual AbstractMetadataItem's data is equal to the given one.
   * @param data the given data to compare the actual AbstractMetadataItem's data to.
   * @return this assertion object.
   * @throws AssertionError - if the actual AbstractMetadataItem's data is not equal to the given one.
   */
  public AbstractMetadataItemAssert hasData(String data) {
    // check that actual AbstractMetadataItem we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String errorMessage = "\nExpected data of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

    // null safe check
    String actualData = actual.getData();
    if (!org.assertj.core.util.Objects.areEqual(actualData, data)) {
      failWithMessage(errorMessage, actual, data, actualData);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual AbstractMetadataItem's field is equal to the given one.
   * @param field the given field to compare the actual AbstractMetadataItem's field to.
   * @return this assertion object.
   * @throws AssertionError - if the actual AbstractMetadataItem's field is not equal to the given one.
   */
  public AbstractMetadataItemAssert hasField(String field) {
    // check that actual AbstractMetadataItem we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String errorMessage = "\nExpected field of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

    // null safe check
    String actualField = actual.getField();
    if (!org.assertj.core.util.Objects.areEqual(actualField, field)) {
      failWithMessage(errorMessage, actual, field, actualField);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual AbstractMetadataItem's id is equal to the given one.
   * @param id the given id to compare the actual AbstractMetadataItem's id to.
   * @return this assertion object.
   * @throws AssertionError - if the actual AbstractMetadataItem's id is not equal to the given one.
   */
  public AbstractMetadataItemAssert hasId(long id) {
    // check that actual AbstractMetadataItem we want to make assertions on is not null.
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
   * Verifies that the actual AbstractMetadataItem's modifiedOn is equal to the given one.
   * @param modifiedOn the given modifiedOn to compare the actual AbstractMetadataItem's modifiedOn to.
   * @return this assertion object.
   * @throws AssertionError - if the actual AbstractMetadataItem's modifiedOn is not equal to the given one.
   */
  public AbstractMetadataItemAssert hasModifiedOn(Date modifiedOn) {
    // check that actual AbstractMetadataItem we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String errorMessage = "\nExpected modifiedOn of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

    // null safe check
    Date actualModifiedOn = actual.getModifiedOn();
    if (!org.assertj.core.util.Objects.areEqual(actualModifiedOn, modifiedOn)) {
      failWithMessage(errorMessage, actual, modifiedOn, actualModifiedOn);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual AbstractMetadataItem's modifier is equal to the given one.
   * @param modifier the given modifier to compare the actual AbstractMetadataItem's modifier to.
   * @return this assertion object.
   * @throws AssertionError - if the actual AbstractMetadataItem's modifier is not equal to the given one.
   */
  public AbstractMetadataItemAssert hasModifier(User modifier) {
    // check that actual AbstractMetadataItem we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String errorMessage = "\nExpected modifier of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

    // null safe check
    User actualModifier = actual.getModifier();
    if (!org.assertj.core.util.Objects.areEqual(actualModifier, modifier)) {
      failWithMessage(errorMessage, actual, modifier, actualModifier);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual AbstractMetadataItem's rev is equal to the given one.
   * @param rev the given rev to compare the actual AbstractMetadataItem's rev to.
   * @return this assertion object.
   * @throws AssertionError - if the actual AbstractMetadataItem's rev is not equal to the given one.
   */
  public AbstractMetadataItemAssert hasRev(long rev) {
    // check that actual AbstractMetadataItem we want to make assertions on is not null.
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
