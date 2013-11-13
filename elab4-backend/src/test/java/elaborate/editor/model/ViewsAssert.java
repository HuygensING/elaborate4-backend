package elaborate.editor.model;

import org.assertj.core.api.AbstractAssert;
// Assertions is needed if an assertion for Iterable is generated
import org.assertj.core.api.Assertions;


/**
 * {@link Views} specific assertions - Generated by CustomAssertionGenerator.
 */
public class ViewsAssert extends AbstractAssert<ViewsAssert, Views> {

  /**
   * Creates a new </code>{@link ViewsAssert}</code> to make assertions on actual Views.
   * @param actual the Views we want to make assertions on.
   */
  public ViewsAssert(Views actual) {
    super(actual, ViewsAssert.class);
  }

  /**
   * An entry point for ViewsAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one's can write directly : <code>assertThat(myViews)</code> and get specific assertion with code completion.
   * @param actual the Views we want to make assertions on.
   * @return a new </code>{@link ViewsAssert}</code>
   */
  public static ViewsAssert assertThat(Views actual) {
    return new ViewsAssert(actual);
  }

}
