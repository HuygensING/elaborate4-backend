package elaborate.editor.model.orm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;

import elaborate.AbstractTest;
import elaborate.editor.model.AnnotationInputWrapper;
import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.User;

public class TranscriptionServiceTest extends AbstractTest {

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  //  @Test
  public void testAddAnnotation() throws Exception {
    TranscriptionService ts = TranscriptionService.instance();
    AnnotationInputWrapper annotationInput = new AnnotationInputWrapper();
    annotationInput.body = "body";
    User root = mock(User.class);
    when(root.isRoot()).thenReturn(true);
    Annotation annotation = ts.addAnnotation(1, 1, annotationInput, root);
    LOG.info("annotation={}", annotation);
    assertThat(annotation).isNotNull();
    assertThat(annotation.getBody()).isEqualTo("body");
  }

}
