package elaborate.editor.resources.orm.wrappers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import elaborate.editor.model.orm.Transcription;

public class TranscriptionWrapperTest {

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testTranscriptionWrapper() throws Exception {
    String textLayer = "textlayer";
    String title = "title";
    String body = "<body><ab id=\"9085822\"/>sdgdgdgsdgsdfg<ae id=\"9085822\"/></body>";

    Transcription transcription = mockTranscription(textLayer, title, body);

    TranscriptionWrapper tw = new TranscriptionWrapper(transcription);
    //    assertEquals(title, tw.title);
    assertEquals(textLayer, tw.textLayer);
    String expected = "<span data-id=\"9085822\" data-marker=\"begin\"></span>sdgdgdgsdgsdfg<sup data-id=\"9085822\" data-marker=\"end\">1</sup>";
    assertEquals(expected, tw.body);
  }

  private Transcription mockTranscription(String textLayer, String title, String body) {
    Transcription transcription = mock(Transcription.class);
    when(transcription.getTextLayer()).thenReturn(textLayer);
    when(transcription.getTitle()).thenReturn(title);
    when(transcription.getBody()).thenReturn(body);
    return transcription;
  }

  @Test
  public void testConvertBodyForOutput() throws Exception {
    String in = "<body>  <ab id=\"9085822\"/>bla <ab id=\"9085821\"/>die<ae id=\"9085822\"/> bla<ae id=\"9085821\"/>\nhello world  </body>";
    String expected = "<span data-id=\"9085822\" data-marker=\"begin\"></span>bla <span data-id=\"9085821\" data-marker=\"begin\"></span>die<sup data-id=\"9085822\" data-marker=\"end\">1</sup> bla<sup data-id=\"9085821\" data-marker=\"end\">2</sup><br>hello world";
    Transcription transcription = mockTranscription("textLayer", "title", in);
    TranscriptionWrapper tw = new TranscriptionWrapper(transcription);
    assertEquals(expected, tw.body);
    List<Integer> annotationNumbers = tw.annotationNumbers;
    assertEquals(2, annotationNumbers.size());
    assertEquals(Integer.valueOf(9085822), annotationNumbers.get(0));
    assertEquals(Integer.valueOf(9085821), annotationNumbers.get(1));
  }

  @Test
  public void testConvertFromInput() throws Exception {
    String in = "<span data-marker=\"begin\" data-id=\"9085822\">bla die bla</span><sup data-marker=\"end\" data-id=\"9085822\">1</sup><br>hello world";
    String expected = "<body><ab id=\"9085822\"/>bla die bla<ae id=\"9085822\"/>\nhello world</body>";
    assertEquals(expected, TranscriptionWrapper.convertFromInput(in));
  }

}
