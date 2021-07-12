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

import java.util.Date;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

/** {@link AnnotationType} specific assertions - Generated by CustomAssertionGenerator. */
public class AnnotationTypeAssert extends AbstractAssert<AnnotationTypeAssert, AnnotationType> {

  /**
   * Creates a new </code>{@link AnnotationTypeAssert}</code> to make assertions on actual
   * AnnotationType.
   *
   * @param actual the AnnotationType we want to make assertions on.
   */
  public AnnotationTypeAssert(AnnotationType actual) {
    super(actual, AnnotationTypeAssert.class);
  }

  /**
   * An entry point for AnnotationTypeAssert to follow AssertJ standard <code>assertThat()</code>
   * statements.<br>
   * With a static import, one's can write directly : <code>assertThat(myAnnotationType)</code> and
   * get specific assertion with code completion.
   *
   * @param actual the AnnotationType we want to make assertions on.
   * @return a new </code>{@link AnnotationTypeAssert}</code>
   */
  public static AnnotationTypeAssert assertThat(AnnotationType actual) {
    return new AnnotationTypeAssert(actual);
  }

  /**
   * Verifies that the actual AnnotationType's createdOn is equal to the given one.
   *
   * @param createdOn the given createdOn to compare the actual AnnotationType's createdOn to.
   * @return this assertion object.
   * @throws AssertionError - if the actual AnnotationType's createdOn is not equal to the given
   *     one.
   */
  public AnnotationTypeAssert hasCreatedOn(Date createdOn) {
    // check that actual AnnotationType we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage =
        "\nExpected createdOn of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

    // null safe check
    Date actualCreatedOn = actual.getCreatedOn();
    if (!org.assertj.core.util.Objects.areEqual(actualCreatedOn, createdOn)) {
      failWithMessage(assertjErrorMessage, actual, createdOn, actualCreatedOn);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual AnnotationType's creator is equal to the given one.
   *
   * @param creator the given creator to compare the actual AnnotationType's creator to.
   * @return this assertion object.
   * @throws AssertionError - if the actual AnnotationType's creator is not equal to the given one.
   */
  public AnnotationTypeAssert hasCreator(User creator) {
    // check that actual AnnotationType we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpected creator of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

    // null safe check
    User actualCreator = actual.getCreator();
    if (!org.assertj.core.util.Objects.areEqual(actualCreator, creator)) {
      failWithMessage(assertjErrorMessage, actual, creator, actualCreator);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual AnnotationType's description is equal to the given one.
   *
   * @param description the given description to compare the actual AnnotationType's description to.
   * @return this assertion object.
   * @throws AssertionError - if the actual AnnotationType's description is not equal to the given
   *     one.
   */
  public void hasDescription(String description) {
    // check that actual AnnotationType we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage =
        "\nExpected description of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

    // null safe check
    String actualDescription = actual.getDescription();
    if (!org.assertj.core.util.Objects.areEqual(actualDescription, description)) {
      failWithMessage(assertjErrorMessage, actual, description, actualDescription);
    }

    // return the current assertion for method chaining
  }

  /**
   * Verifies that the actual AnnotationType's id is equal to the given one.
   *
   * @param id the given id to compare the actual AnnotationType's id to.
   * @return this assertion object.
   * @throws AssertionError - if the actual AnnotationType's id is not equal to the given one.
   */
  public AnnotationTypeAssert hasId(long id) {
    // check that actual AnnotationType we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpected id of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

    // check
    long actualId = actual.getId();
    if (actualId != id) {
      failWithMessage(assertjErrorMessage, actual, id, actualId);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual AnnotationType's metadataItems contains the given
   * AnnotationTypeMetadataItem elements.
   *
   * @param metadataItems the given elements that should be contained in actual AnnotationType's
   *     metadataItems.
   * @return this assertion object.
   * @throws AssertionError if the actual AnnotationType's metadataItems does not contain all given
   *     AnnotationTypeMetadataItem elements.
   */
  public AnnotationTypeAssert hasMetadataItems(AnnotationTypeMetadataItem... metadataItems) {
    // check that actual AnnotationType we want to make assertions on is not null.
    isNotNull();

    // check that given AnnotationTypeMetadataItem varargs is not null.
    if (metadataItems == null)
      throw new AssertionError("Expecting metadataItems parameter not to be null.");

    // check with standard error message (see commented below to set your own message).
    Assertions.assertThat(actual.getMetadataItems()).contains(metadataItems);

    // To override the standard error message :
    // - remove the previous call to
    // Assertions.assertThat(actual.getMetadataItems().contains(metadataItems)
    // - uncomment the line below and set your error message:
    // Assertions.assertThat(actual.getMetadataItems()).overridingErrorMessage("\nmy error message
    // %s", "arg1").contains(metadataItems);

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual AnnotationType has no metadataItems.
   *
   * @return this assertion object.
   * @throws AssertionError if the actual AnnotationType's metadataItems is not empty.
   */
  public AnnotationTypeAssert hasNoMetadataItems() {
    // check that actual AnnotationType we want to make assertions on is not null.
    isNotNull();

    // we override the default error message with a more explicit one
    String assertjErrorMessage =
        "\nExpected :\n  <%s>\nnot to have metadataItems but had :\n  <%s>";

    // check
    if (!actual.getMetadataItems().isEmpty()) {
      failWithMessage(assertjErrorMessage, actual, actual.getMetadataItems());
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual AnnotationType's modifiedOn is equal to the given one.
   *
   * @param modifiedOn the given modifiedOn to compare the actual AnnotationType's modifiedOn to.
   * @return this assertion object.
   * @throws AssertionError - if the actual AnnotationType's modifiedOn is not equal to the given
   *     one.
   */
  public AnnotationTypeAssert hasModifiedOn(Date modifiedOn) {
    // check that actual AnnotationType we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage =
        "\nExpected modifiedOn of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

    // null safe check
    Date actualModifiedOn = actual.getModifiedOn();
    if (!org.assertj.core.util.Objects.areEqual(actualModifiedOn, modifiedOn)) {
      failWithMessage(assertjErrorMessage, actual, modifiedOn, actualModifiedOn);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual AnnotationType's modifier is equal to the given one.
   *
   * @param modifier the given modifier to compare the actual AnnotationType's modifier to.
   * @return this assertion object.
   * @throws AssertionError - if the actual AnnotationType's modifier is not equal to the given one.
   */
  public AnnotationTypeAssert hasModifier(User modifier) {
    // check that actual AnnotationType we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage =
        "\nExpected modifier of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

    // null safe check
    User actualModifier = actual.getModifier();
    if (!org.assertj.core.util.Objects.areEqual(actualModifier, modifier)) {
      failWithMessage(assertjErrorMessage, actual, modifier, actualModifier);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual AnnotationType's name is equal to the given one.
   *
   * @param name the given name to compare the actual AnnotationType's name to.
   * @return this assertion object.
   * @throws AssertionError - if the actual AnnotationType's name is not equal to the given one.
   */
  public void hasName(String name) {
    // check that actual AnnotationType we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpected name of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

    // null safe check
    String actualName = actual.getName();
    if (!org.assertj.core.util.Objects.areEqual(actualName, name)) {
      failWithMessage(assertjErrorMessage, actual, name, actualName);
    }

    // return the current assertion for method chaining
  }

  /**
   * Verifies that the actual AnnotationType's rev is equal to the given one.
   *
   * @param rev the given rev to compare the actual AnnotationType's rev to.
   * @return this assertion object.
   * @throws AssertionError - if the actual AnnotationType's rev is not equal to the given one.
   */
  public AnnotationTypeAssert hasRev(long rev) {
    // check that actual AnnotationType we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpected rev of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

    // check
    long actualRev = actual.getRev();
    if (actualRev != rev) {
      failWithMessage(assertjErrorMessage, actual, rev, actualRev);
    }

    // return the current assertion for method chaining
    return this;
  }
}
