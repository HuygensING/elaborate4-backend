package elaborate.editor.model.orm;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2018 Huygens ING
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
 * {@link ProjectEntry} specific assertions - Generated by CustomAssertionGenerator.
 */
public class ProjectEntryAssert extends AbstractAssert<ProjectEntryAssert, ProjectEntry> {

	/**
	 * Creates a new </code>{@link ProjectEntryAssert}</code> to make assertions on actual ProjectEntry.
	 * 
	 * @param actual
	 *          the ProjectEntry we want to make assertions on.
	 */
	public ProjectEntryAssert(ProjectEntry actual) {
		super(actual, ProjectEntryAssert.class);
	}

	/**
	 * An entry point for ProjectEntryAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
	 * With a static import, one's can write directly : <code>assertThat(myProjectEntry)</code> and get specific assertion with code completion.
	 * 
	 * @param actual
	 *          the ProjectEntry we want to make assertions on.
	 * @return a new </code>{@link ProjectEntryAssert}</code>
	 */
	public static ProjectEntryAssert assertThat(ProjectEntry actual) {
		return new ProjectEntryAssert(actual);
	}

	/**
	 * Verifies that the actual ProjectEntry's createdOn is equal to the given one.
	 * 
	 * @param createdOn
	 *          the given createdOn to compare the actual ProjectEntry's createdOn to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual ProjectEntry's createdOn is not equal to the given one.
	 */
	public ProjectEntryAssert hasCreatedOn(Date createdOn) {
		// check that actual ProjectEntry we want to make assertions on is not null.
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
	 * Verifies that the actual ProjectEntry's creator is equal to the given one.
	 * 
	 * @param creator
	 *          the given creator to compare the actual ProjectEntry's creator to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual ProjectEntry's creator is not equal to the given one.
	 */
	public ProjectEntryAssert hasCreator(User creator) {
		// check that actual ProjectEntry we want to make assertions on is not null.
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
	 * Verifies that the actual ProjectEntry's facsimiles contains the given Facsimile elements.
	 * 
	 * @param facsimiles
	 *          the given elements that should be contained in actual ProjectEntry's facsimiles.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           if the actual ProjectEntry's facsimiles does not contain all given Facsimile elements.
	 */
	public ProjectEntryAssert hasFacsimiles(Facsimile... facsimiles) {
		// check that actual ProjectEntry we want to make assertions on is not null.
		isNotNull();

		// check that given Facsimile varargs is not null.
		if (facsimiles == null) throw new AssertionError("Expecting facsimiles parameter not to be null.");

		// check with standard error message (see commented below to set your own message).
		Assertions.assertThat(actual.getFacsimiles()).contains(facsimiles);

		// To override the standard error message :
		// - remove the previous call to Assertions.assertThat(actual.getFacsimiles().contains(facsimiles)
		// - uncomment the line below and set your error message:
		// Assertions.assertThat(actual.getFacsimiles()).overridingErrorMessage("\nmy error message %s", "arg1").contains(facsimiles);

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual ProjectEntry has no facsimiles.
	 * 
	 * @return this assertion object.
	 * @throws AssertionError
	 *           if the actual ProjectEntry's facsimiles is not empty.
	 */
	public ProjectEntryAssert hasNoFacsimiles() {
		// check that actual ProjectEntry we want to make assertions on is not null.
		isNotNull();

		// we override the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected :\n  <%s>\nnot to have facsimiles but had :\n  <%s>";

		// check
		if (!actual.getFacsimiles().isEmpty()) {
			failWithMessage(assertjErrorMessage, actual, actual.getFacsimiles());
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual ProjectEntry's id is equal to the given one.
	 * 
	 * @param id
	 *          the given id to compare the actual ProjectEntry's id to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual ProjectEntry's id is not equal to the given one.
	 */
	public ProjectEntryAssert hasId(long id) {
		// check that actual ProjectEntry we want to make assertions on is not null.
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
	 * Verifies that the actual ProjectEntry's modifiedOn is equal to the given one.
	 * 
	 * @param modifiedOn
	 *          the given modifiedOn to compare the actual ProjectEntry's modifiedOn to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual ProjectEntry's modifiedOn is not equal to the given one.
	 */
	public ProjectEntryAssert hasModifiedOn(Date modifiedOn) {
		// check that actual ProjectEntry we want to make assertions on is not null.
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
	 * Verifies that the actual ProjectEntry's modifier is equal to the given one.
	 * 
	 * @param modifier
	 *          the given modifier to compare the actual ProjectEntry's modifier to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual ProjectEntry's modifier is not equal to the given one.
	 */
	public ProjectEntryAssert hasModifier(User modifier) {
		// check that actual ProjectEntry we want to make assertions on is not null.
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
	 * Verifies that the actual ProjectEntry's name is equal to the given one.
	 * 
	 * @param name
	 *          the given name to compare the actual ProjectEntry's name to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual ProjectEntry's name is not equal to the given one.
	 */
	public ProjectEntryAssert hasName(String name) {
		// check that actual ProjectEntry we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected name of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// null safe check
		String actualName = actual.getName();
		if (!org.assertj.core.util.Objects.areEqual(actualName, name)) {
			failWithMessage(assertjErrorMessage, actual, name, actualName);
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual ProjectEntry's project is equal to the given one.
	 * 
	 * @param project
	 *          the given project to compare the actual ProjectEntry's project to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual ProjectEntry's project is not equal to the given one.
	 */
	public ProjectEntryAssert hasProject(Project project) {
		// check that actual ProjectEntry we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected project of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// null safe check
		Project actualProject = actual.getProject();
		if (!org.assertj.core.util.Objects.areEqual(actualProject, project)) {
			failWithMessage(assertjErrorMessage, actual, project, actualProject);
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual ProjectEntry's projectEntryMetadataItems contains the given ProjectEntryMetadataItem elements.
	 * 
	 * @param projectEntryMetadataItems
	 *          the given elements that should be contained in actual ProjectEntry's projectEntryMetadataItems.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           if the actual ProjectEntry's projectEntryMetadataItems does not contain all given ProjectEntryMetadataItem elements.
	 */
	public ProjectEntryAssert hasProjectEntryMetadataItems(ProjectEntryMetadataItem... projectEntryMetadataItems) {
		// check that actual ProjectEntry we want to make assertions on is not null.
		isNotNull();

		// check that given ProjectEntryMetadataItem varargs is not null.
		if (projectEntryMetadataItems == null) throw new AssertionError("Expecting projectEntryMetadataItems parameter not to be null.");

		// check with standard error message (see commented below to set your own message).
		Assertions.assertThat(actual.getProjectEntryMetadataItems()).contains(projectEntryMetadataItems);

		// To override the standard error message :
		// - remove the previous call to Assertions.assertThat(actual.getProjectEntryMetadataItems().contains(projectEntryMetadataItems)
		// - uncomment the line below and set your error message:
		// Assertions.assertThat(actual.getProjectEntryMetadataItems()).overridingErrorMessage("\nmy error message %s", "arg1").contains(projectEntryMetadataItems);

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual ProjectEntry has no projectEntryMetadataItems.
	 * 
	 * @return this assertion object.
	 * @throws AssertionError
	 *           if the actual ProjectEntry's projectEntryMetadataItems is not empty.
	 */
	public ProjectEntryAssert hasNoProjectEntryMetadataItems() {
		// check that actual ProjectEntry we want to make assertions on is not null.
		isNotNull();

		// we override the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected :\n  <%s>\nnot to have projectEntryMetadataItems but had :\n  <%s>";

		// check
		if (!actual.getProjectEntryMetadataItems().isEmpty()) {
			failWithMessage(assertjErrorMessage, actual, actual.getProjectEntryMetadataItems());
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual ProjectEntry is publishable.
	 * 
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual ProjectEntry is not publishable.
	 */
	public ProjectEntryAssert isPublishable() {
		// check that actual ProjectEntry we want to make assertions on is not null.
		isNotNull();

		// check
		if (!actual.isPublishable()) {
			failWithMessage("\nExpected actual ProjectEntry to be publishable but was not.");
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual ProjectEntry is not publishable.
	 * 
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual ProjectEntry is publishable.
	 */
	public ProjectEntryAssert isNotPublishable() {
		// check that actual ProjectEntry we want to make assertions on is not null.
		isNotNull();

		// check
		if (actual.isPublishable()) {
			failWithMessage("\nExpected actual ProjectEntry not to be publishable but was.");
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual ProjectEntry's rev is equal to the given one.
	 * 
	 * @param rev
	 *          the given rev to compare the actual ProjectEntry's rev to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual ProjectEntry's rev is not equal to the given one.
	 */
	public ProjectEntryAssert hasRev(long rev) {
		// check that actual ProjectEntry we want to make assertions on is not null.
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
	 * Verifies that the actual ProjectEntry's transcriptions contains the given Transcription elements.
	 * 
	 * @param transcriptions
	 *          the given elements that should be contained in actual ProjectEntry's transcriptions.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           if the actual ProjectEntry's transcriptions does not contain all given Transcription elements.
	 */
	public ProjectEntryAssert hasTranscriptions(Transcription... transcriptions) {
		// check that actual ProjectEntry we want to make assertions on is not null.
		isNotNull();

		// check that given Transcription varargs is not null.
		if (transcriptions == null) throw new AssertionError("Expecting transcriptions parameter not to be null.");

		// check with standard error message (see commented below to set your own message).
		Assertions.assertThat(actual.getTranscriptions()).contains(transcriptions);

		// To override the standard error message :
		// - remove the previous call to Assertions.assertThat(actual.getTranscriptions().contains(transcriptions)
		// - uncomment the line below and set your error message:
		// Assertions.assertThat(actual.getTranscriptions()).overridingErrorMessage("\nmy error message %s", "arg1").contains(transcriptions);

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual ProjectEntry has no transcriptions.
	 * 
	 * @return this assertion object.
	 * @throws AssertionError
	 *           if the actual ProjectEntry's transcriptions is not empty.
	 */
	public ProjectEntryAssert hasNoTranscriptions() {
		// check that actual ProjectEntry we want to make assertions on is not null.
		isNotNull();

		// we override the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected :\n  <%s>\nnot to have transcriptions but had :\n  <%s>";

		// check
		if (!actual.getTranscriptions().isEmpty()) {
			failWithMessage(assertjErrorMessage, actual, actual.getTranscriptions());
		}

		// return the current assertion for method chaining
		return this;
	}

}
