package elaborate.editor.model.orm;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2019 Huygens ING
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
// Assertions is needed if an assertion for Iterable is generated
import org.assertj.core.api.Assertions;

/**
 * {@link Annotation} specific assertions - Generated by CustomAssertionGenerator.
 */
public class AnnotationAssert extends AbstractAssert<AnnotationAssert, Annotation> {

	/**
	 * Creates a new </code>{@link AnnotationAssert}</code> to make assertions on actual Annotation.
	 * 
	 * @param actual
	 *          the Annotation we want to make assertions on.
	 */
	public AnnotationAssert(Annotation actual) {
		super(actual, AnnotationAssert.class);
	}

	/**
	 * An entry point for AnnotationAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
	 * With a static import, one's can write directly : <code>assertThat(myAnnotation)</code> and get specific assertion with code completion.
	 * 
	 * @param actual
	 *          the Annotation we want to make assertions on.
	 * @return a new </code>{@link AnnotationAssert}</code>
	 */
	public static AnnotationAssert assertThat(Annotation actual) {
		return new AnnotationAssert(actual);
	}

	/**
	 * Verifies that the actual Annotation's annotatedText is equal to the given one.
	 * 
	 * @param annotatedText
	 *          the given annotatedText to compare the actual Annotation's annotatedText to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Annotation's annotatedText is not equal to the given one.
	 */
	public AnnotationAssert hasAnnotatedText(String annotatedText) {
		// check that actual Annotation we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected annotatedText of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// null safe check
		String actualAnnotatedText = actual.getAnnotatedText();
		if (!org.assertj.core.util.Objects.areEqual(actualAnnotatedText, annotatedText)) {
			failWithMessage(assertjErrorMessage, actual, annotatedText, actualAnnotatedText);
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual Annotation's annotationMetadataItems contains the given AnnotationMetadataItem elements.
	 * 
	 * @param annotationMetadataItems
	 *          the given elements that should be contained in actual Annotation's annotationMetadataItems.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           if the actual Annotation's annotationMetadataItems does not contain all given AnnotationMetadataItem elements.
	 */
	public AnnotationAssert hasAnnotationMetadataItems(AnnotationMetadataItem... annotationMetadataItems) {
		// check that actual Annotation we want to make assertions on is not null.
		isNotNull();

		// check that given AnnotationMetadataItem varargs is not null.
		if (annotationMetadataItems == null) throw new AssertionError("Expecting annotationMetadataItems parameter not to be null.");

		// check with standard error message (see commented below to set your own message).
		Assertions.assertThat(actual.getAnnotationMetadataItems()).contains(annotationMetadataItems);

		// To override the standard error message :
		// - remove the previous call to Assertions.assertThat(actual.getAnnotationMetadataItems().contains(annotationMetadataItems)
		// - uncomment the line below and set your error message:
		// Assertions.assertThat(actual.getAnnotationMetadataItems()).overridingErrorMessage("\nmy error message %s", "arg1").contains(annotationMetadataItems);

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual Annotation has no annotationMetadataItems.
	 * 
	 * @return this assertion object.
	 * @throws AssertionError
	 *           if the actual Annotation's annotationMetadataItems is not empty.
	 */
	public AnnotationAssert hasNoAnnotationMetadataItems() {
		// check that actual Annotation we want to make assertions on is not null.
		isNotNull();

		// we override the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected :\n  <%s>\nnot to have annotationMetadataItems but had :\n  <%s>";

		// check
		if (!actual.getAnnotationMetadataItems().isEmpty()) {
			failWithMessage(assertjErrorMessage, actual, actual.getAnnotationMetadataItems());
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual Annotation's annotationNo is equal to the given one.
	 * 
	 * @param annotationNo
	 *          the given annotationNo to compare the actual Annotation's annotationNo to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Annotation's annotationNo is not equal to the given one.
	 */
	public AnnotationAssert hasAnnotationNo(int annotationNo) {
		// check that actual Annotation we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected annotationNo of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// check
		int actualAnnotationNo = actual.getAnnotationNo();
		if (actualAnnotationNo != annotationNo) {
			failWithMessage(assertjErrorMessage, actual, annotationNo, actualAnnotationNo);
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual Annotation's annotationType is equal to the given one.
	 * 
	 * @param annotationType
	 *          the given annotationType to compare the actual Annotation's annotationType to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Annotation's annotationType is not equal to the given one.
	 */
	public AnnotationAssert hasAnnotationType(AnnotationType annotationType) {
		// check that actual Annotation we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected annotationType of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// null safe check
		AnnotationType actualAnnotationType = actual.getAnnotationType();
		if (!org.assertj.core.util.Objects.areEqual(actualAnnotationType, annotationType)) {
			failWithMessage(assertjErrorMessage, actual, annotationType, actualAnnotationType);
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual Annotation's body is equal to the given one.
	 * 
	 * @param body
	 *          the given body to compare the actual Annotation's body to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Annotation's body is not equal to the given one.
	 */
	public void hasBody(String body) {
		// check that actual Annotation we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected body of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// null safe check
		String actualBody = actual.getBody();
		if (!org.assertj.core.util.Objects.areEqual(actualBody, body)) {
			failWithMessage(assertjErrorMessage, actual, body, actualBody);
		}

		// return the current assertion for method chaining
	}

	/**
	 * Verifies that the actual Annotation's createdOn is equal to the given one.
	 * 
	 * @param createdOn
	 *          the given createdOn to compare the actual Annotation's createdOn to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Annotation's createdOn is not equal to the given one.
	 */
	public AnnotationAssert hasCreatedOn(Date createdOn) {
		// check that actual Annotation we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected createdOn of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// null safe check
		Date actualCreatedOn = actual.getCreatedOn();
		if (!org.assertj.core.util.Objects.areEqual(actualCreatedOn, createdOn)) {
			failWithMessage(assertjErrorMessage, actual, createdOn, actualCreatedOn);
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual Annotation's creator is equal to the given one.
	 * 
	 * @param creator
	 *          the given creator to compare the actual Annotation's creator to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Annotation's creator is not equal to the given one.
	 */
	public AnnotationAssert hasCreator(User creator) {
		// check that actual Annotation we want to make assertions on is not null.
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
	 * Verifies that the actual Annotation's id is equal to the given one.
	 * 
	 * @param id
	 *          the given id to compare the actual Annotation's id to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Annotation's id is not equal to the given one.
	 */
	public AnnotationAssert hasId(long id) {
		// check that actual Annotation we want to make assertions on is not null.
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
	 * Verifies that the actual Annotation's modifiedOn is equal to the given one.
	 * 
	 * @param modifiedOn
	 *          the given modifiedOn to compare the actual Annotation's modifiedOn to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Annotation's modifiedOn is not equal to the given one.
	 */
	public AnnotationAssert hasModifiedOn(Date modifiedOn) {
		// check that actual Annotation we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected modifiedOn of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// null safe check
		Date actualModifiedOn = actual.getModifiedOn();
		if (!org.assertj.core.util.Objects.areEqual(actualModifiedOn, modifiedOn)) {
			failWithMessage(assertjErrorMessage, actual, modifiedOn, actualModifiedOn);
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual Annotation's modifier is equal to the given one.
	 * 
	 * @param modifier
	 *          the given modifier to compare the actual Annotation's modifier to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Annotation's modifier is not equal to the given one.
	 */
	public AnnotationAssert hasModifier(User modifier) {
		// check that actual Annotation we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected modifier of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// null safe check
		User actualModifier = actual.getModifier();
		if (!org.assertj.core.util.Objects.areEqual(actualModifier, modifier)) {
			failWithMessage(assertjErrorMessage, actual, modifier, actualModifier);
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual Annotation's rev is equal to the given one.
	 * 
	 * @param rev
	 *          the given rev to compare the actual Annotation's rev to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Annotation's rev is not equal to the given one.
	 */
	public AnnotationAssert hasRev(long rev) {
		// check that actual Annotation we want to make assertions on is not null.
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

	/**
	 * Verifies that the actual Annotation's transcription is equal to the given one.
	 * 
	 * @param transcription
	 *          the given transcription to compare the actual Annotation's transcription to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Annotation's transcription is not equal to the given one.
	 */
	public AnnotationAssert hasTranscription(Transcription transcription) {
		// check that actual Annotation we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected transcription of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// null safe check
		Transcription actualTranscription = actual.getTranscription();
		if (!org.assertj.core.util.Objects.areEqual(actualTranscription, transcription)) {
			failWithMessage(assertjErrorMessage, actual, transcription, actualTranscription);
		}

		// return the current assertion for method chaining
		return this;
	}

}
