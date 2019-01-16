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

import org.assertj.core.api.AbstractAssert;
// Assertions is needed if an assertion for Iterable is generated
import org.assertj.core.api.Assertions;

/**
 * {@link User} specific assertions - Generated by CustomAssertionGenerator.
 */
public class UserAssert extends AbstractAssert<UserAssert, User> {

	/**
	 * Creates a new </code>{@link UserAssert}</code> to make assertions on actual User.
	 * 
	 * @param actual
	 *          the User we want to make assertions on.
	 */
	public UserAssert(User actual) {
		super(actual, UserAssert.class);
	}

	/**
	 * An entry point for UserAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
	 * With a static import, one's can write directly : <code>assertThat(myUser)</code> and get specific assertion with code completion.
	 * 
	 * @param actual
	 *          the User we want to make assertions on.
	 * @return a new </code>{@link UserAssert}</code>
	 */
	public static UserAssert assertThat(User actual) {
		return new UserAssert(actual);
	}

	/**
	 * Verifies that the actual User's email is equal to the given one.
	 * 
	 * @param email
	 *          the given email to compare the actual User's email to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual User's email is not equal to the given one.
	 */
	public UserAssert hasEmail(String email) {
		// check that actual User we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected email of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// null safe check
		String actualEmail = actual.getEmail();
		if (!org.assertj.core.util.Objects.areEqual(actualEmail, email)) {
			failWithMessage(assertjErrorMessage, actual, email, actualEmail);
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual User's encodedPassword contains the given byte elements.
	 * 
	 * @param encodedPassword
	 *          the given elements that should be contained in actual User's encodedPassword.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           if the actual User's encodedPassword does not contain all given byte elements.
	 */
	public UserAssert hasEncodedPassword(byte... encodedPassword) {
		// check that actual User we want to make assertions on is not null.
		isNotNull();

		// check that given byte varargs is not null.
		if (encodedPassword == null) throw new AssertionError("Expecting encodedPassword parameter not to be null.");

		// check with standard error message (see commented below to set your own message).
		Assertions.assertThat(actual.getEncodedPassword()).contains(encodedPassword);

		// To override the standard error message :
		// - remove the previous call to Assertions.assertThat(actual.getEncodedPassword().contains(encodedPassword)
		// - uncomment the line below and set your error message:
		// Assertions.assertThat(actual.getEncodedPassword()).overridingErrorMessage("\nmy error message %s", "arg1").contains(encodedPassword);

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual User has no encodedPassword.
	 * 
	 * @return this assertion object.
	 * @throws AssertionError
	 *           if the actual User's encodedPassword is not empty.
	 */
	public UserAssert hasNoEncodedPassword() {
		// check that actual User we want to make assertions on is not null.
		isNotNull();

		// we override the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected :\n  <%s>\nnot to have encodedPassword but had :\n  <%s>";

		// check
		if (actual.getEncodedPassword().length > 0) {
			failWithMessage(assertjErrorMessage, actual, java.util.Arrays.toString(actual.getEncodedPassword()));
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual User's firstName is equal to the given one.
	 * 
	 * @param firstName
	 *          the given firstName to compare the actual User's firstName to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual User's firstName is not equal to the given one.
	 */
	public UserAssert hasFirstName(String firstName) {
		// check that actual User we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected firstName of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// null safe check
		String actualFirstName = actual.getFirstName();
		if (!org.assertj.core.util.Objects.areEqual(actualFirstName, firstName)) {
			failWithMessage(assertjErrorMessage, actual, firstName, actualFirstName);
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual User's id is equal to the given one.
	 * 
	 * @param id
	 *          the given id to compare the actual User's id to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual User's id is not equal to the given one.
	 */
	public void hasId(long id) {
		// check that actual User we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected id of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// check
		long actualId = actual.getId();
		if (actualId != id) {
			failWithMessage(assertjErrorMessage, actual, id, actualId);
		}

		// return the current assertion for method chaining
  }

	/**
	 * Verifies that the actual User's lastName is equal to the given one.
	 * 
	 * @param lastName
	 *          the given lastName to compare the actual User's lastName to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual User's lastName is not equal to the given one.
	 */
	public UserAssert hasLastName(String lastName) {
		// check that actual User we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected lastName of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// null safe check
		String actualLastName = actual.getLastName();
		if (!org.assertj.core.util.Objects.areEqual(actualLastName, lastName)) {
			failWithMessage(assertjErrorMessage, actual, lastName, actualLastName);
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual User is loggedIn.
	 * 
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual User is not loggedIn.
	 */
	public UserAssert isLoggedIn() {
		// check that actual User we want to make assertions on is not null.
		isNotNull();

		// check
		if (!actual.isLoggedIn()) {
			failWithMessage("\nExpected actual User to be loggedIn but was not.");
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual User is not loggedIn.
	 * 
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual User is loggedIn.
	 */
	public UserAssert isNotLoggedIn() {
		// check that actual User we want to make assertions on is not null.
		isNotNull();

		// check
		if (actual.isLoggedIn()) {
			failWithMessage("\nExpected actual User not to be loggedIn but was.");
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual User's projects contains the given Project elements.
	 * 
	 * @param projects
	 *          the given elements that should be contained in actual User's projects.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           if the actual User's projects does not contain all given Project elements.
	 */
	public UserAssert hasProjects(Project... projects) {
		// check that actual User we want to make assertions on is not null.
		isNotNull();

		// check that given Project varargs is not null.
		if (projects == null) throw new AssertionError("Expecting projects parameter not to be null.");

		// check with standard error message (see commented below to set your own message).
		Assertions.assertThat(actual.getProjects()).contains(projects);

		// To override the standard error message :
		// - remove the previous call to Assertions.assertThat(actual.getProjects().contains(projects)
		// - uncomment the line below and set your error message:
		// Assertions.assertThat(actual.getProjects()).overridingErrorMessage("\nmy error message %s", "arg1").contains(projects);

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual User has no projects.
	 * 
	 * @return this assertion object.
	 * @throws AssertionError
	 *           if the actual User's projects is not empty.
	 */
	public UserAssert hasNoProjects() {
		// check that actual User we want to make assertions on is not null.
		isNotNull();

		// we override the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected :\n  <%s>\nnot to have projects but had :\n  <%s>";

		// check
		if (!actual.getProjects().isEmpty()) {
			failWithMessage(assertjErrorMessage, actual, actual.getProjects());
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual User's rev is equal to the given one.
	 * 
	 * @param rev
	 *          the given rev to compare the actual User's rev to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual User's rev is not equal to the given one.
	 */
	public UserAssert hasRev(long rev) {
		// check that actual User we want to make assertions on is not null.
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
	 * Verifies that the actual User's role is equal to the given one.
	 * 
	 * @param role
	 *          the given role to compare the actual User's role to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual User's role is not equal to the given one.
	 */
	public UserAssert hasRole(String role) {
		// check that actual User we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected role of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// null safe check
		String actualRole = actual.getRole();
		if (!org.assertj.core.util.Objects.areEqual(actualRole, role)) {
			failWithMessage(assertjErrorMessage, actual, role, actualRole);
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual User's roleString is equal to the given one.
	 * 
	 * @param roleString
	 *          the given roleString to compare the actual User's roleString to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual User's roleString is not equal to the given one.
	 */
	public UserAssert hasRoleString(String roleString) {
		// check that actual User we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected roleString of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// null safe check
		String actualRoleString = actual.getRoleString();
		if (!org.assertj.core.util.Objects.areEqual(actualRoleString, roleString)) {
			failWithMessage(assertjErrorMessage, actual, roleString, actualRoleString);
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual User is root.
	 * 
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual User is not root.
	 */
	public UserAssert isRoot() {
		// check that actual User we want to make assertions on is not null.
		isNotNull();

		// check
		if (!actual.isRoot()) {
			failWithMessage("\nExpected actual User to be root but was not.");
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual User is not root.
	 * 
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual User is root.
	 */
	public UserAssert isNotRoot() {
		// check that actual User we want to make assertions on is not null.
		isNotNull();

		// check
		if (actual.isRoot()) {
			failWithMessage("\nExpected actual User not to be root but was.");
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual User's title is equal to the given one.
	 * 
	 * @param title
	 *          the given title to compare the actual User's title to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual User's title is not equal to the given one.
	 */
	public UserAssert hasTitle(String title) {
		// check that actual User we want to make assertions on is not null.
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
	 * Verifies that the actual User's userSettings contains the given UserSetting elements.
	 * 
	 * @param userSettings
	 *          the given elements that should be contained in actual User's userSettings.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           if the actual User's userSettings does not contain all given UserSetting elements.
	 */
	public UserAssert hasUserSettings(UserSetting... userSettings) {
		// check that actual User we want to make assertions on is not null.
		isNotNull();

		// check that given UserSetting varargs is not null.
		if (userSettings == null) throw new AssertionError("Expecting userSettings parameter not to be null.");

		// check with standard error message (see commented below to set your own message).
		Assertions.assertThat(actual.getUserSettings()).contains(userSettings);

		// To override the standard error message :
		// - remove the previous call to Assertions.assertThat(actual.getUserSettings().contains(userSettings)
		// - uncomment the line below and set your error message:
		// Assertions.assertThat(actual.getUserSettings()).overridingErrorMessage("\nmy error message %s", "arg1").contains(userSettings);

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual User has no userSettings.
	 * 
	 * @return this assertion object.
	 * @throws AssertionError
	 *           if the actual User's userSettings is not empty.
	 */
	public UserAssert hasNoUserSettings() {
		// check that actual User we want to make assertions on is not null.
		isNotNull();

		// we override the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected :\n  <%s>\nnot to have userSettings but had :\n  <%s>";

		// check
		if (!actual.getUserSettings().isEmpty()) {
			failWithMessage(assertjErrorMessage, actual, actual.getUserSettings());
		}

		// return the current assertion for method chaining
		return this;
	}

	/**
	 * Verifies that the actual User's username is equal to the given one.
	 * 
	 * @param username
	 *          the given username to compare the actual User's username to.
	 * @return this assertion object.
	 * @throws AssertionError
	 *           - if the actual User's username is not equal to the given one.
	 */
	public void hasUsername(String username) {
		// check that actual User we want to make assertions on is not null.
		isNotNull();

		// overrides the default error message with a more explicit one
		String assertjErrorMessage = "\nExpected username of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

		// null safe check
		String actualUsername = actual.getUsername();
		if (!org.assertj.core.util.Objects.areEqual(actualUsername, username)) {
			failWithMessage(assertjErrorMessage, actual, username, actualUsername);
		}

		// return the current assertion for method chaining
  }

}
