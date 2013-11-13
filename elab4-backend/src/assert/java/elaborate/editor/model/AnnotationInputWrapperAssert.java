package elaborate.editor.model;

import org.assertj.core.api.AbstractAssert;
// Assertions is needed if an assertion for Iterable is generated
import org.assertj.core.api.Assertions;


/**
 * {@link AnnotationInputWrapper} specific assertions - Generated by CustomAssertionGenerator.
 */
public class AnnotationInputWrapperAssert extends AbstractAssert<AnnotationInputWrapperAssert, AnnotationInputWrapper> {

  /**
   * Creates a new </code>{@link AnnotationInputWrapperAssert}</code> to make assertions on actual AnnotationInputWrapper.
   * @param actual the AnnotationInputWrapper we want to make assertions on.
   */
  public AnnotationInputWrapperAssert(AnnotationInputWrapper actual) {
    super(actual, AnnotationInputWrapperAssert.class);
  }

  /**
   * An entry point for AnnotationInputWrapperAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one's can write directly : <code>assertThat(myAnnotationInputWrapper)</code> and get specific assertion with code completion.
   * @param actual the AnnotationInputWrapper we want to make assertions on.
   * @return a new </code>{@link AnnotationInputWrapperAssert}</code>
   */
  public static AnnotationInputWrapperAssert assertThat(AnnotationInputWrapper actual) {
    return new AnnotationInputWrapperAssert(actual);
  }

}