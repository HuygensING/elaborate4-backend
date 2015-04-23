package elaborate.editor.resources.orm.wrappers;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
		//    assertThat( tw.title).isEqualTo(title);
		assertThat(tw.getTextLayer()).isEqualTo(textLayer);
		String expected = "<span data-id=\"9085822\" data-marker=\"begin\"></span>sdgdgdgsdgsdfg<sup data-id=\"9085822\" data-marker=\"end\">1</sup>";
		assertThat(tw.getBody()).isEqualTo(expected);
	}

	@Test
	public void testTranscriptionWrapperWithBadXHtmlInput() throws Exception {
		String textLayer = "textlayer";
		String title = "title";
		String body = "<body><span style=\"font-size:11.0pt;line-height:115%; font-family:\"Verdana\",\"sans-serif\";mso-fareast-font-family:Calibri;mso-fareast-theme-font: minor-latin;mso-bidi-font-family:\"Times New Roman\";mso-bidi-theme-font:minor-bidi; mso-ansi-language:NL;mso-fareast-language:EN-US;mso-bidi-language:AR-SA\">Hoezo mag ik niet copy-pasten vanuit Word? Maar ik wil het!</span></body>";

		Transcription transcription = mockTranscription(textLayer, title, body);

		TranscriptionWrapper tw = new TranscriptionWrapper(transcription);
		//    assertThat( tw.title).isEqualTo(title);
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
	public void testConvertBodyWithEmptyTagsForOutput() throws Exception {
		String in = "<body>bla <i></i> bla <strong></strong> bla</body>";
		String expected = "bla  bla  bla";
		Transcription transcription = mockTranscription("textLayer", "title", in);
		TranscriptionWrapper tw = new TranscriptionWrapper(transcription);
		assertThat(tw.getBody()).isEqualTo(expected);
	}

	@Test
	public void testConvertBodyForOutput() throws Exception {
		String in = "<body>  <ab id=\"9085822\"/>bla <ab id=\"9085821\"/>die<ae id=\"9085822\"/> bla<ae id=\"9085821\"/>\nhello world  </body>";
		String expected = "<span data-id=\"9085822\" data-marker=\"begin\"></span>bla <span data-id=\"9085821\" data-marker=\"begin\"></span>die<sup data-id=\"9085822\" data-marker=\"end\">1</sup> bla<sup data-id=\"9085821\" data-marker=\"end\">2</sup><br>hello world";
		Transcription transcription = mockTranscription("textLayer", "title", in);
		TranscriptionWrapper tw = new TranscriptionWrapper(transcription);
		assertThat(tw.getBody()).isEqualTo(expected);
		List<Integer> annotationNumbers = tw.annotationNumbers;
		assertThat(annotationNumbers.size()).isEqualTo(2);
		assertThat(annotationNumbers.get(0)).isEqualTo(Integer.valueOf(9085822));
		assertThat(annotationNumbers.get(1)).isEqualTo(Integer.valueOf(9085821));
	}

	@Test
	public void testConvertBodyForOutput_with_superfluous_newlines_at_the_end() throws Exception {
		String in = "<body>lorem epsum doleres whatever\n \n\n\n</body>";
		String expected = "lorem epsum doleres whatever";
		Transcription transcription = mockTranscription("textLayer", "title", in);
		TranscriptionWrapper tw = new TranscriptionWrapper(transcription);
		assertThat(tw.getBody()).isEqualTo(expected);
	}

	@Test
	public void testConvertBodyForOutput_with_superfluous_whitespace_at_the_end() throws Exception {
		String in = "<body>body\n \n </body>";
		String expected = "body";
		Transcription transcription = mockTranscription("textLayer", "title", in);
		TranscriptionWrapper tw = new TranscriptionWrapper(transcription);
		assertThat(tw.getBody()).isEqualTo(expected);
	}

	@Test
	public void testConvertBodyForOutput_with_shift_space() throws Exception {
		String in = "<body>header\n  paragraph 1\n  paragraph 2   \n paragraph 3</body>";
		String expected = "header<br>&nbsp;&nbsp;paragraph 1<br>&nbsp;&nbsp;paragraph 2&nbsp;&nbsp;&nbsp;<br> paragraph 3";
		Transcription transcription = mockTranscription("textLayer", "title", in);
		TranscriptionWrapper tw = new TranscriptionWrapper(transcription);
		assertThat(tw.getBody()).isEqualTo(expected);
	}

	@Test
	public void testConvertFromInput() throws Exception {
		String in = "<span data-marker=\"begin\" data-id=\"9085822\">bla die bla</span><sup data-marker=\"end\" data-id=\"9085822\">1</sup><br>hello world";
		String expected = "<body><ab id=\"9085822\"/>bla die bla<ae id=\"9085822\"/>\nhello world</body>";
		assertThat(TranscriptionWrapper.convertFromInput(in)).isEqualTo(expected);
	}

	@Test
	public void testConvertFromInputWithBadChar() throws Exception {
		String in = "the smiling ones danced like blooming girls, I presumed boldly to rank the former";
		String expected = "<body>the smiling ones danced like blooming girls, I presumed boldly to rank  the former</body>";
		assertThat(TranscriptionWrapper.convertFromInput(in)).isEqualTo(expected);
	}

	@Test
	public void testSuperscriptIsHandledWell() throws Exception {
		TranscriptionWrapper tw = new TranscriptionWrapper();
		tw.setBody("<sup>super</sup> normaal <sub>sub</sub><br><sup>super</sup> normaal <sub>sub<br></sub><sup>super</sup> normaal <sub>sub</sub><br>");
		String expected = "<body><sup>super</sup> normaal <sub>sub</sub>\n<sup>super</sup> normaal <sub>sub\n</sub><sup>super</sup> normaal <sub>sub</sub>\n</body>";
		assertThat(tw.getBodyForDb()).isEqualTo(expected);
	}

	@Test
	public void testAnnotationMarkerIsHandledWell() throws Exception {
		TranscriptionWrapper tw = new TranscriptionWrapper();
		tw.setBody("<sup>super</sup> <span data-id=\"9075405\" data-marker=\"begin\"></span>normaal <sub><sup data-id=\"9075405\" data-marker=\"end\">1</sup>sub</sub><br><sup>super</sup> normaal <sub>sub<br></sub><sup>super</sup> normaal <sub>sub</sub><br>");
		String expected = "<body><sup>super</sup> <ab id=\"9075405\"/>normaal <sub><ae id=\"9075405\"/>sub</sub>\n<sup>super</sup> normaal <sub>sub\n</sub><sup>super</sup> normaal <sub>sub</sub>\n</body>";
		assertThat(tw.getBodyForDb()).isEqualTo(expected);
	}

	//	
}
