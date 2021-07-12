package elaborate.editor.resources.orm.wrappers;

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

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import elaborate.editor.model.orm.Transcription;
import elaborate.editor.model.orm.service.ProjectService;
import elaborate.editor.model.orm.service.ProjectService.AnnotationData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TranscriptionWrapperTest {

  @Before
  public void setUp() {}

  @After
  public void tearDown() {}

  @Test
  public void testTranscriptionWrapper() {
    String textLayer = "textlayer";
    String title = "title";
    String body = "<body><ab id=\"9085822\"/>sdgdgdgsdgsdfg<ae id=\"9085822\"/></body>";

    Transcription transcription = mockTranscription(textLayer, title, body);

    Map<Integer, AnnotationData> annotationDataMap =
        ImmutableMap.of(9085822, new AnnotationData().setType("type"));
    TranscriptionWrapper tw = new TranscriptionWrapper(transcription, annotationDataMap);
    // assertThat( tw.title).isEqualTo(title);
    assertThat(tw.getTextLayer()).isEqualTo(textLayer);
    String expected =
        "<span data-marker=\"begin\" data-id=\"9085822\" data-type=\"type\"></span>sdgdgdgsdgsdfg<sup data-marker=\"end\" data-id=\"9085822\">1</sup>";
    assertThat(tw.getBody()).isEqualTo(expected);
  }

  @Test
  public void testTranscriptionWrapperWithBadXHtmlInput() {
    String textLayer = "textlayer";
    String title = "title";
    String body =
        "<body><span style=\"font-size:11.0pt;line-height:115%; font-family:\"Verdana\",\"sans-serif\";mso-fareast-font-family:Calibri;mso-fareast-theme-font: minor-latin;mso-bidi-font-family:\"Times New Roman\";mso-bidi-theme-font:minor-bidi; mso-ansi-language:NL;mso-fareast-language:EN-US;mso-bidi-language:AR-SA\">Hoezo mag ik niet copy-pasten vanuit Word? Maar ik wil het!</span></body>";

    Transcription transcription = mockTranscription(textLayer, title, body);

    TranscriptionWrapper tw = new TranscriptionWrapper(transcription, null);
    // assertThat( tw.title).isEqualTo(title);
    assertThat(tw.getTextLayer()).isEqualTo(textLayer);
    String expected = "Hoezo mag ik niet copy-pasten vanuit Word? Maar ik wil het!";
    assertThat(tw.getBody()).isEqualTo(expected);
  }

  private Transcription mockTranscription(String textLayer, String title, String body) {
    Transcription transcription = mock(Transcription.class);
    when(transcription.getTextLayer()).thenReturn(textLayer);
    when(transcription.getTitle()).thenReturn(title);
    when(transcription.getBody()).thenReturn(body);
    return transcription;
  }

  @Test
  public void testConvertBodyWithEmptyTagsForOutput() {
    String in = "<body>bla <i></i> bla <strong></strong> bla</body>";
    String expected = "bla  bla  bla";
    Transcription transcription = mockTranscription("textLayer", "title", in);
    TranscriptionWrapper tw = new TranscriptionWrapper(transcription, null);
    assertThat(tw.getBody()).isEqualTo(expected);
  }

  @Test
  public void testConvertBodyForOutput() {
    String in =
        "<body>" //
            + "  " //
            + "<ab id=\"9085822\"/>" //
            + "bla " //
            + "<ab id=\"9085821\"/>" //
            + "die" //
            + "<ae id=\"9085822\"/>" //
            + " bla" //
            + "<ae id=\"9085821\"/>" //
            + "\nhello world  " //
            + "</body>";
    String expected =
        "<span data-marker=\"begin\" data-id=\"9085822\" data-type=\"type2\"></span>" //
            + "bla " //
            + "<span data-marker=\"begin\" data-id=\"9085821\" data-type=\"type1\"></span>" //
            + "die" //
            + "<sup data-marker=\"end\" data-id=\"9085822\">1</sup>" //
            + " bla" //
            + "<sup data-marker=\"end\" data-id=\"9085821\">2</sup>" //
            + "<br>hello world";
    Transcription transcription = mockTranscription("textLayer", "title", in);
    Map<Integer, AnnotationData> annotationDataMap =
        ImmutableMap.<Integer, ProjectService.AnnotationData>builder() //
            .put(9085821, new AnnotationData().setType("type1")) //
            .put(9085822, new AnnotationData().setType("type2")) //
            .build();
    TranscriptionWrapper tw = new TranscriptionWrapper(transcription, annotationDataMap);
    assertThat(tw.getBody()).isEqualTo(expected);
    List<Integer> annotationNumbers = tw.annotationNumbers;
    assertThat(annotationNumbers.size()).isEqualTo(2);
    assertThat(annotationNumbers.get(0)).isEqualTo(Integer.valueOf(9085822));
    assertThat(annotationNumbers.get(1)).isEqualTo(Integer.valueOf(9085821));
  }

  @Test
  public void testConvertBodyForOutput_with_superfluous_newlines_at_the_end() {
    String in = "<body>lorem epsum doleres whatever\n \n\n\n</body>";
    String expected = "lorem epsum doleres whatever";
    Transcription transcription = mockTranscription("textLayer", "title", in);
    TranscriptionWrapper tw = new TranscriptionWrapper(transcription, null);
    assertThat(tw.getBody()).isEqualTo(expected);
  }

  @Test
  public void testConvertBodyForOutput_with_superfluous_whitespace_at_the_end() {
    String in = "<body>body\n \n </body>";
    String expected = "body";
    Transcription transcription = mockTranscription("textLayer", "title", in);
    TranscriptionWrapper tw = new TranscriptionWrapper(transcription, null);
    assertThat(tw.getBody()).isEqualTo(expected);
  }

  @Test
  public void testConvertBodyForOutput_with_shift_space() {
    String in = "<body>header\n  paragraph 1\n  paragraph 2   \n paragraph 3</body>";
    String expected =
        "header<br>&nbsp;&nbsp;paragraph 1<br>&nbsp;&nbsp;paragraph 2&nbsp;&nbsp;&nbsp;<br> paragraph 3";
    Transcription transcription = mockTranscription("textLayer", "title", in);
    TranscriptionWrapper tw = new TranscriptionWrapper(transcription, null);
    assertThat(tw.getBody()).isEqualTo(expected);
  }

  @Test
  public void testConvertFromInput() {
    String in =
        "<span data-marker=\"begin\" data-id=\"9085822\">bla die bla</span><sup data-marker=\"end\" data-id=\"9085822\">1</sup><br>hello world";
    String expected =
        "<body><ab id=\"9085822\"/>bla die bla<ae id=\"9085822\"/>\nhello world</body>";
    assertThat(TranscriptionWrapper.convertFromInput(in)).isEqualTo(expected);
  }

  @Test
  public void testConvertFromInputWithBadChar() {
    String in =
        "the smiling ones danced like blooming girls, I presumed boldly to rank the former";
    String expected =
        "<body>the smiling ones danced like blooming girls, I presumed boldly to rank  the former</body>";
    assertThat(TranscriptionWrapper.convertFromInput(in)).isEqualTo(expected);
  }

  @Test
  public void testConvertFromInputWithBreaks() {
    String in = "hop<br>on<br>pop";
    String expected = "<body>hop\non\npop</body>";
    assertThat(TranscriptionWrapper.convertFromInput(in)).isEqualTo(expected);
  }

  @Test
  public void testConvertFromInputRemovesXMLComments() {
    String in = "bla <!-- ignore comments --> bla";
    String expected = "<body>bla  bla</body>";
    assertThat(TranscriptionWrapper.convertFromInput(in)).isEqualTo(expected);
  }

  @Test
  public void testConvertFromInputConvertsEmToI() {
    String in = "<em style=\"white-space: normal;\">Casteleijn</em>";
    String expected = "<body><i>Casteleijn</i></body>";
    assertThat(TranscriptionWrapper.convertFromInput(in)).isEqualTo(expected);
  }

  @Test
  public void testConvertFromInputConvertsStrongToB() {
    String in = "<strong style=\"white-space: normal;\">TGIF</strong>";
    String expected = "<body><b>TGIF</b></body>";
    assertThat(TranscriptionWrapper.convertFromInput(in)).isEqualTo(expected);
  }

  @Test
  public void testConvertFromInputRemovesMostWordTags() {
    String in =
        "<p class=\"MsoNormal\" style=\"margin-right:29.9pt;text-align:justify\">" //
            + "I <i style=\"mso-bidi-font-style:normal\">HEART</i>" //
            + " <b style=\"mso-bidi-font-weight:normal\">WORD!!</b>" //
            + "</p>";
    String expected = "<body>I <i>HEART</i> <b>WORD!!</b></body>";
    assertThat(TranscriptionWrapper.convertFromInput(in)).isEqualTo(expected);
  }

  // @Test
  // public void testConvertFromInputRemovesMostWordTags1() throws Exception {
  // String in = "<br>(zie ook 253r (regel 7) hier staat voluit geschreven onse Raitsvrend<i>en</i>
  // (misschien hier ook Raitsvrend<i>e</i> ?,"//
  // + "maar vrint zou tot de sterke flextie moeten behoren dus moeten eindigen op
  // -en?))<br>KG:&nbsp; Samen graag nog even hebben over de"//
  // + " meervoudvorm.<br><br><br>34: oirer -&gt; oiren [?]<br>sendebode: m. acc. pl. (Zwakke
  // flextie?) het zou dan oire moeten worden. Is"//
  // + " hier misschien de letter 'r' afgekort?<br>KG: als boven. <br><br><p
  // class=\"MsoNormal\">‘raitsvrenden’: volgens Van Loey,"//
  // + " <i style=\"mso-bidi-font-style: normal\">Mndld Spraakkunst </i>I § 19.2 kan het bij
  // ‘vrient’ (en ‘viant’)<br>allebei -&gt; dan"//
  // + " beslist de paleografie</p><p class=\"MsoNormal\"><br></p><p class=\"MsoNormal\">260v \"onse
  // vrende\" voluit geschreven<br></p><br>"//
  // +
  // "<br><br><br><br>-------------------------------------------------------------------------<br>Translation<br>- Wijk ( bij Duurstede)?"//
  // + "<br><br>";
  // String expected = "<body></body>";
  // assertThat(TranscriptionWrapper.convertFromInput(in)).isEqualTo(expected);
  // }

  @Test
  public void testSuperscriptIsHandledWell() {
    TranscriptionWrapper tw = new TranscriptionWrapper();
    tw.setBody(
        "<sup>super</sup> normaal <sub>sub</sub><br><sup>super</sup> normaal <sub>sub<br></sub><sup>super</sup> normaal <sub>sub</sub><br>");
    String expected =
        "<body><sup>super</sup> normaal <sub>sub</sub>\n<sup>super</sup> normaal <sub>sub\n</sub><sup>super</sup> normaal <sub>sub</sub>\n</body>";
    assertThat(tw.getBodyForDb()).isEqualTo(expected);
  }

  @Test
  public void testAnnotationMarkerIsHandledWell() {
    TranscriptionWrapper tw = new TranscriptionWrapper();
    tw.setBody(
        "<sup>super</sup> " //
            + "<span data-id=\"9075405\" data-marker=\"begin\"></span>" //
            + "normaal <sub>" //
            + "<sup data-id=\"9075405\" data-marker=\"end\">1</sup>" //
            + "sub</sub><br><sup>super</sup> normaal <sub>sub<br></sub><sup>super</sup> normaal <sub>sub</sub><br>");
    String expected =
        "<body><sup>super</sup> " //
            + "<ab id=\"9075405\"/>" //
            + "normaal <sub>" //
            + "<ae id=\"9075405\"/>" //
            + "sub</sub>\n<sup>super</sup> normaal <sub>sub\n</sub><sup>super</sup> normaal <sub>sub</sub>\n</body>";
    assertThat(tw.getBodyForDb()).isEqualTo(expected);
  }

  //
}
