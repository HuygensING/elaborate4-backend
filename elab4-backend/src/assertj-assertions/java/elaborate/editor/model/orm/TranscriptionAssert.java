package elaborate.editor.model.orm;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2015 Huygens ING
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
 * {@link Transcription} specific assertions - Generated by CustomAssertionGenerator.
 */
public class TranscriptionAssert extends AbstractAssert<TranscriptionAssert, Transcription> {

	/**
	 * Creates a new </code>{@link TranscriptionAssert}</code> to make assertions on actual Transcription.
	 * 
	 * @param actual
	 *          the Transcription we want to make assertions on.
	 */
	public TranscriptionAssert(Transcription actual) {
		super(actual, TranscriptionAssert.class);
	}

	/**
	 * An entry point for TranscriptionAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
	 * With a static import, one's can write directly : <code>assertThat(myTranscription)</code> and get specific assertion with code completion.
	 * 
	 * @param actual
	 *          the Transcription we want to make assertions on.
	 * @return a new </code>{@link TranscriptionAssert}</code>
	 */
	public static TranscriptionAssert assertThat(Transcription actual) {
		return new TranscriptionAssert(actual);
	}

	/**
	 * Verifies that the actual Transcription's annotations contains the given Annotation elements.
	 * 
	 * @param annotations
	 *          the given elements that should be contained in actual Transcription's annotations.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           if the actual Transcription's annotations does not contain all given Annotation elements.
	 */
	public TranscriptionAssert hasAnnotations(Annotation... annotations) {
		// check that actual Transcription we want to make assertions on is not null.
		isNotNull();

		// check that given Annotation varargs is not null.
		if (annotations == null) throw new AssertionError("Expecting annotations parameter not to be null.");

		// check with standard error message (see commented below to set your own message).
		Assertions.assertThat(actual.getAnnotations()).contains(annotations);

		// To override the standard error message :
		// - remove the previous call to Assertions.assertThat(actual.getAnnotations().contains(annotations)
		// - uncomment the line below and set your error message:
		// Assertions.assertThat(actual.getAnnotations()).overridingErrorMessage("\nmy error message %s", "arg1").contains(annotations);

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual Transcription has no annotations.
	 * 
	 * @return this assertion object.
	 * @throws AssertionError
	 *           if the actual Transcription's annotations is not empty.
	 */
	public TranscriptionAssert hasNoAnnotations() {
		// check that actual Transcription we want to make assertions on is not null.
		isNotNull();

		// we override the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected :\n  <%s>\nnot to have annotations but had :\n  <%s>";

		// check
		if (!actual.getAnnotations().isEmpty()) {
			failWithMessage(assertjErrorMessage, actual, actual.getAnnotations());
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual Transcription's body is equal to the given one.
	 * 
	 * @param body
	 *          the given body to compare the actual Transcription's body to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Transcription's body is not equal to the given one.
	 */
	public TranscriptionAssert hasBody(String body) {
		// check that actual Transcription we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected body of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// null safe check
		String actualBody = actual.getBody();
		if (!org.assertj.core.util.Objects.areEqual(actualBody, body)) {
			failWithMessage(assertjErrorMessage, actual, body, actualBody);
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual Transcription's createdOn is equal to the given one.
	 * 
	 * @param createdOn
	 *          the given createdOn to compare the actual Transcription's createdOn to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Transcription's createdOn is not equal to the given one.
	 */
	public TranscriptionAssert hasCreatedOn(Date createdOn) {
		// check that actual Transcription we want to make assertions on is not null.
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
	 * Verifies that the actual Transcription's creator is equal to the given one.
	 * 
	 * @param creator
	 *          the given creator to compare the actual Transcription's creator to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Transcription's creator is not equal to the given one.
	 */
	public TranscriptionAssert hasCreator(User creator) {
		// check that actual Transcription we want to make assertions on is not null.
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
	 * Verifies that the actual Transcription's id is equal to the given one.
	 * 
	 * @param id
	 *          the given id to compare the actual Transcription's id to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Transcription's id is not equal to the given one.
	 */
	public TranscriptionAssert hasId(long id) {
		// check that actual Transcription we want to make assertions on is not null.
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
	 * Verifies that the actual Transcription's modifiedOn is equal to the given one.
	 * 
	 * @param modifiedOn
	 *          the given modifiedOn to compare the actual Transcription's modifiedOn to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Transcription's modifiedOn is not equal to the given one.
	 */
	public TranscriptionAssert hasModifiedOn(Date modifiedOn) {
		// check that actual Transcription we want to make assertions on is not null.
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
	 * Verifies that the actual Transcription's modifier is equal to the given one.
	 * 
	 * @param modifier
	 *          the given modifier to compare the actual Transcription's modifier to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Transcription's modifier is not equal to the given one.
	 */
	public TranscriptionAssert hasModifier(User modifier) {
		// check that actual Transcription we want to make assertions on is not null.
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
	 * Verifies that the actual Transcription's projectEntry is equal to the given one.
	 * 
	 * @param projectEntry
	 *          the given projectEntry to compare the actual Transcription's projectEntry to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Transcription's projectEntry is not equal to the given one.
	 */
	public TranscriptionAssert hasProjectEntry(ProjectEntry projectEntry) {
		// check that actual Transcription we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected projectEntry of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// null safe check
		ProjectEntry actualProjectEntry = actual.getProjectEntry();
		if (!org.assertj.core.util.Objects.areEqual(actualProjectEntry, projectEntry)) {
			failWithMessage(assertjErrorMessage, actual, projectEntry, actualProjectEntry);
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual Transcription's rev is equal to the given one.
	 * 
	 * @param rev
	 *          the given rev to compare the actual Transcription's rev to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Transcription's rev is not equal to the given one.
	 */
	public TranscriptionAssert hasRev(long rev) {
		// check that actual Transcription we want to make assertions on is not null.
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
	 * Verifies that the actual Transcription's textLayer is equal to the given one.
	 * 
	 * @param textLayer
	 *          the given textLayer to compare the actual Transcription's textLayer to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Transcription's textLayer is not equal to the given one.
	 */
	public TranscriptionAssert hasTextLayer(String textLayer) {
		// check that actual Transcription we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected textLayer of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// null safe check
		String actualTextLayer = actual.getTextLayer();
		if (!org.assertj.core.util.Objects.areEqual(actualTextLayer, textLayer)) {
			failWithMessage(assertjErrorMessage, actual, textLayer, actualTextLayer);
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual Transcription's title is equal to the given one.
	 * 
	 * @param title
	 *          the given title to compare the actual Transcription's title to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Transcription's title is not equal to the given one.
	 */
	public TranscriptionAssert hasTitle(String title) {
		// check that actual Transcription we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected title of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// null safe check
		String actualTitle = actual.getTitle();
		if (!org.assertj.core.util.Objects.areEqual(actualTitle, title)) {
			failWithMessage(assertjErrorMessage, actual, title, actualTitle);
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual Transcription's transcriptionType is equal to the given one.
	 * 
	 * @param transcriptionType
	 *          the given transcriptionType to compare the actual Transcription's transcriptionType to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual Transcription's transcriptionType is not equal to the given one.
	 */
	public TranscriptionAssert hasTranscriptionType(TranscriptionType transcriptionType) {
		// check that actual Transcription we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected transcriptionType of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// null safe check
		TranscriptionType actualTranscriptionType = actual.getTranscriptionType();
		if (!org.assertj.core.util.Objects.areEqual(actualTranscriptionType, transcriptionType)) {
			failWithMessage(assertjErrorMessage, actual, transcriptionType, actualTranscriptionType);
		}

		// return the current assertion for method chaining
		return this;
	}

}
