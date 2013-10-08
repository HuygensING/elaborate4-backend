package elaborate.editor.model.orm.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import nl.knaw.huygens.LoggableObject;

import org.junit.After;
import org.junit.Before;

import elaborate.editor.model.AnnotationInputWrapper;
import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.User;

public class TranscriptionServiceTest extends LoggableObject {

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  //  @Test
  public void testAddAnnotation() throws Exception {
    TranscriptionService ts = new TranscriptionService();
    AnnotationInputWrapper annotationInput = new AnnotationInputWrapper();
    annotationInput.body = "body";
    User root = mock(User.class);
    when(root.isRoot()).thenReturn(true);
    Annotation annotation = ts.addAnnotation(1, 1, annotationInput, root);
    LOG.info("annotation={}", annotation);
    assertTrue(annotation != null);
    assertEquals("body", annotation.getBody());
  }

}
