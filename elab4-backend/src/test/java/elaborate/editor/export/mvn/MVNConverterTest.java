package elaborate.editor.export.mvn;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2016 Huygens ING
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

import static elaborate.editor.export.mvn.MVNAnnotationType.AFKORTING;
import static elaborate.editor.export.mvn.MVNAnnotationType.ALINEA;
import static elaborate.editor.export.mvn.MVNAnnotationType.CIJFERS;
import static elaborate.editor.export.mvn.MVNAnnotationType.DEFECT;
import static elaborate.editor.export.mvn.MVNAnnotationType.DOORHALING;
import static elaborate.editor.export.mvn.MVNAnnotationType.GEBRUIKERSNOTITIE;
import static elaborate.editor.export.mvn.MVNAnnotationType.INCIPIT;
import static elaborate.editor.export.mvn.MVNAnnotationType.INITIAAL;
import static elaborate.editor.export.mvn.MVNAnnotationType.INSPRINGEN;
import static elaborate.editor.export.mvn.MVNAnnotationType.KOLOM;
import static elaborate.editor.export.mvn.MVNAnnotationType.LETTERS;
import static elaborate.editor.export.mvn.MVNAnnotationType.LINKERMARGEKOLOM;
import static elaborate.editor.export.mvn.MVNAnnotationType.METAMARK;
import static elaborate.editor.export.mvn.MVNAnnotationType.ONDERSCHRIFT;
import static elaborate.editor.export.mvn.MVNAnnotationType.ONDUIDELIJK;
import static elaborate.editor.export.mvn.MVNAnnotationType.ONLEESBAAR;
import static elaborate.editor.export.mvn.MVNAnnotationType.OPHOGING_ROOD;
import static elaborate.editor.export.mvn.MVNAnnotationType.OPSCHRIFT;
import static elaborate.editor.export.mvn.MVNAnnotationType.PALEOGRAFISCH;
import static elaborate.editor.export.mvn.MVNAnnotationType.POEZIE;
import static elaborate.editor.export.mvn.MVNAnnotationType.RECHTERMARGEKOLOM;
import static elaborate.editor.export.mvn.MVNAnnotationType.REGELNUMMERING_BLAD;
import static elaborate.editor.export.mvn.MVNAnnotationType.REGELNUMMERING_TEKST;
import static elaborate.editor.export.mvn.MVNAnnotationType.TEKSTBEGIN;
import static elaborate.editor.export.mvn.MVNAnnotationType.TEKSTEINDE;
import static elaborate.editor.export.mvn.MVNAnnotationType.TEKSTKLEUR_ROOD;
import static elaborate.editor.export.mvn.MVNAnnotationType.VERSREGEL;
import static elaborate.editor.export.mvn.MVNAnnotationType.VREEMDTEKEN;
import static elaborate.editor.export.mvn.MVNAnnotationType.WITREGEL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

import elaborate.editor.export.mvn.MVNConversionData.AnnotationData;
import elaborate.editor.model.ProjectMetadataFields;
import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.AnnotationType;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.TranscriptionType;
import elaborate.editor.publish.Publication.Status;
import nl.knaw.huygens.Log;

public class MVNConverterTest {

  private static final String BASEURL = "http://example.org";

  @Test
  public void test() {
    final Project project = mock(Project.class);
    when(project.getName()).thenReturn("mvn_project");
    when(project.getTitle()).thenReturn("MVN Project");
    when(project.getMetadataMap()).thenReturn(ImmutableMap.of(ProjectMetadataFields.PUBLICATION_TITLE, "MVN Project Publication"));
    when(project.getTextLayers()).thenReturn(new String[] { TranscriptionType.DIPLOMATIC });

    //    AnnotationService annotationService = mock(AnnotationService.class);
    final MVNConversionData data = new MVNConversionData();
    final Status logger = new Status(1);
    final MVNConverter converter = new MVNConverter(project, data, logger, BASEURL);
    final MVNConversionResult report = converter.convert();
    Log.info("report={}", report);
    assertThat(report).isNotNull();
    Log.info("errors={}", Joiner.on("\n").join(logger.getErrors()));
    assertThat(report.isOK()).isFalse();
    Log.info("tei={}", report.getTEI());
  }

  @Test
  public void testProjectEntryWithoutDiplomaticTranscriptionGeneratesValidationError() {
    final Status logger = new Status(1);
    final Project project = mockProject();
    final String[] textLayers = new String[] { "Translation" };
    when(project.getTextLayers()).thenReturn(textLayers);
    final MVNConverter c = new MVNConverter(project, mockData(), logger, BASEURL);
    final MVNConversionResult result = c.convert();

    assertThat(result.isOK()).isFalse();
    assertThat(logger.getErrors()).containsExactly("MVN projecten mogen alleen een Diplomatic textlayer hebben. Dit project heeft textlayer(s): Translation");
  }

  @Test
  public void testProjectEntryWithOnlyDiplomaticTranscriptionPassesValidation() {
    final Status logger = new Status(1);
    final Project project = mockProject();
    final String[] textLayers = new String[] { TranscriptionType.DIPLOMATIC };
    when(project.getTextLayers()).thenReturn(textLayers);
    final MVNConverter c = new MVNConverter(project, mockData(), logger, BASEURL);
    final boolean onlyTextLayerIsDiplomatic = c.onlyTextLayerIsDiplomatic();

    assertThat(onlyTextLayerIsDiplomatic).isTrue();
  }

  @Test
  public void testProjectEntryWithTranscriptionOtherThanDiplomaticGeneratesValidationError() {
    // TODO: make test
    // Er is alleen een diplomatische transcriptie aanwezig.
  }

  //    Een TEI XML bestand volgens de indeling gegeven in de bijlage. 
  //    Hierin worden variabelen als volgt ingevuld:
  //    $titel: eLaborate Publication title
  //    $idno: eLaborate Project title
  //    $sigle: eLaborate Project name
  @Test
  public void testProjectTitlesAndNamesAreUsedInGeneratedTEI() {
    // TODO: make test
  }

  //    Toelichting: Elke entry komt overeen met een bladzijde in het manuscript
  //    Validatie:   Indien aanwezig bevat het metadataveld ‘order’ een integer waarde 
  //                 wanneer het veld ‘order’ aanwezig en voor één entry gevuld is (met niet-whitespace),
  //                 moet het voor alle entries gevuld zijn (met niet-whitespace)
  //    Conversie:   <pb/>
  //                 De <pb/> wordt geplaatst voorafgaande aan alles wat betrekking heeft op de inhoud van de betreffende pagina,
  //                 dus in het bijzonder voorafgaand aan de tekst die op die pagina begint. 
  //                 Elke pb krijgt een n-attribuut (veld Name).
  //                 Elke pb krijgt een xml:id attribuut (concatenatie sigle, ’pb’, n-attribuut)
  //                 De pb krijgt een facs-attibuut waarin de naam van de bijbehorende afbeelding komt te staan.
  //                 De volgorde van de entries wordt bepaald door
  //                  - indien aanwezig, het metadataveld ‘order’
  //                  - anders op entry Name (sorteren als string) 
  //                 Elke <pb> wordt geplaatst op een nieuwe regel. 
  @Test
  public void testEveryProjectEntryLeadsToPagebreakInGeneratedTEI() {
    // TODO: make test
  }

  @Test
  public void testTranscriptionWithEmptyLinesGeneratesValidationError() {
    assertPassesNoEmptyLinesValidation("<body>Alea\niacta\nest</body>");

    assertFailsNoEmptyLinesValidation("<body>Pecunia\n\nnon olet</body>");
    assertFailsNoEmptyLinesValidation("<body>Luctor\n \net emergo</body>");
    assertFailsNoEmptyLinesValidation("<body>tab\n\t\ntab</body>");
    assertFailsNoEmptyLinesValidation("<body>We schrijven hier een regel\nWe schrijven hier een regel\nWe schrijven hier een regel\nWe schrijven hier een regel\nWe schrijven hier een regel\nWe schrijven hier een regel\nEr volgt nu een witregel, dat mag niet:\n\nEn een regel met alleen spaties, mag ook niet:\n       \nWe schrijven hier een regel\nWe schrijven hier een regel\nWe schrijven hier een regel\nWe schrijven hier een regel\nWe schrijven hier een regel\nWe schrijven hier een regel</body>");
  }

  private void assertFailsNoEmptyLinesValidation(final String body) {
    final Project project = mockProject();
    final Status logger = new Status(1);
    final MVNConversionResult result = new MVNConversionResult(project, logger, "http://baseurl.org");
    MVNConverter.validateTranscriptionContainsNoEmptyLines(body, result, "1");
    assertThat(result.isOK()).isFalse();
    final String expectedError = "http://baseurl.org/projects/projectName/entries/1/transcriptions/diplomatic : Lege regels mogen niet voorkomen.";
    assertThat(logger.getErrors()).contains(expectedError);
  }

  private void assertPassesNoEmptyLinesValidation(final String body) {
    final Status logger = new Status(1);
    final Project project = mockProject();
    final MVNConversionResult result = new MVNConversionResult(project, logger, "http://baseurl.org");
    MVNConverter.validateTranscriptionContainsNoEmptyLines(body, result, "1");
    assertThat(result.isOK()).isTrue();
  }

  @Test
  public void testNewlinesInTranscriptionLeadsToLineBreakInGeneratedTEI() {
    // TODO: make test
  }

  @Test
  public void testNewlinesInPoetryScopeBecomeVerseLines() {
    // TODO: make test
  }

  //  Regel
  //  Toelichting: Een regel in de transcriptie correspondeert met een regel in het manuscript
  //  Validatie:   Lege regels mogen niet voorkomen (=regel zonder inhoud of met alleen whitespace als inhoud)
  //  Conversie:   <lb/>
  //               De <lb/> wordt vóór de regel geplaatst. Voor de eerste tekst op een foliumzijde staat dus al een lb-element.  
  //               Elke lb krijgt een n-attribuut dat het regelnummer per pagina aangeeft. Het wordt automatisch toegekend.
  //               Afwijkingen in de nummering worden aangegeven met de annotatie mvn:regelnummering (blad). Zie aldaar.
  //               Elke lb krijgt een xml:id attribuut (concatenatie sigle, ’lb’, n-attribuut)
  //               Elke <lb> wordt geplaatst op een nieuwe regel.
  //
  //               Indien we ons bevinden in de scope van een mvn:poëzie, wordt bovendien voor elke regel gegenereerd:
  //               <l>regeltekst</l> (een versregel).
  //               Elke l krijgt een n-attribuut dat regelnummer per tekst aangeeft. Het wordt automatisch toegekend.
  //               Elke l krijgt een xml:id attribuut (concatenatie sigle, ’l’, n-attribuut)
  //
  //               Met lage prioriteit: Versregels die niet overeenkomen met fysieke regelafbreking worden aangegeven met
  //               mvn:versregel. Zie aldaar.
  //
  //               Met lagere prioriteit: 
  //               Als we te maken hebben met proza (dat wil zeggen: we bevinden ons binnen de scope van een mvn:alinea) krijgt de lb een np-attribuut. Het np-attribuut bevat een regelnummering vanaf het begin van de betreffende tekst. 
  @Test
  public void testRegelConversie() {}

  //  mvn:afkorting
  //  Toelichting: Bevat een afkorting. De afgekorte vorm staat in de tekst, de opgeloste vorm in de annotatie
  //  Validatie:   Geen
  //  Conversie:   <choice>
  //                  <abbr>[geannoteerde tekst]</abbr>
  //                  <expan>[inhoud annotatie]</expan>
  //               </choice>
  //               De inhoud van de annotatie bevat gecursiveerde tekst.
  //               Deze gecursiveerde tekst overnemen binnen <ex>-elementen.
  @Test
  public void testAfkortingConversie() {
    final Annotation annotation = mockAnnotationOfType(AFKORTING);
    when(annotation.getBody()).thenReturn("<i>inhoud annotatie</i>");
    when(annotation.getAnnotatedText()).thenReturn("geannoteerde tekst");
    final String body = "<body>pre "//
        + "<ab id=\"1\"/>geannoteerde tekst<ae id=\"1\"/>"//
        + " post</body>";
    final String expected = "pre "//
        + "<choice><abbr>geannoteerde tekst</abbr><expan><ex>inhoud annotatie</ex></expan></choice>"//
        + " post";
    assertConversion(body, mockData(1, annotation), expected);
  }

  //  mvn:alinea
  //  Toelichting: Begint een nieuwe alinea. Een puntannotatie
  //  Validatie:   Geen
  //  Conversie:   Begin een nieuwe alinea (<p>). De alinea wordt afgesloten
  //               - bij de volgende mvn:alinea
  //               - bij een mvn:poëzie
  //               - aan het eind van de tekst
  //               - bij het begin van een mvn:opschrift of mvn:onderschrift
  @Test
  public void testAlineaEndsAtEndOfText() {
    final Annotation tekstbegin = mockAnnotationOfType(TEKSTBEGIN);
    when(tekstbegin.getBody()).thenReturn("1");
    final Annotation alinea = mockAnnotationOfType(ALINEA);
    final Annotation teksteinde = mockAnnotationOfType(TEKSTEINDE);
    when(teksteinde.getBody()).thenReturn("1");
    final String body = "<body>"//
        + "<ab id=\"1\"/>‡<ae id=\"1\"/>"//
        + "<ab id=\"2\"/>‡<ae id=\"2\"/>"//
        + "line 1\n"//
        + "line 2\n"//
        + "line 3"//
        + "<ab id=\"3\"/>‡<ae id=\"3\"/>"//
        + "</body>";
    final String expected = "\n"//
        + "<text n=\"1\" xml:id=\"PROJECTNAME-1\"><body>"//
        + "<p>"//
        + "line 1\n"//
        + "line 2\n"//
        + "line 3"//
        + "</p>\n"//
        + "</body></text>\n";
    assertConversion(body, mockData(1, tekstbegin, 2, alinea, 3, teksteinde), expected);
  }

  @Test
  public void testAlineaEndsAtNextAlinea() {
    final Annotation tekstbegin = mockAnnotationOfType(TEKSTBEGIN);
    when(tekstbegin.getBody()).thenReturn("1");
    final Annotation alinea = mockAnnotationOfType(ALINEA);
    final Annotation teksteinde = mockAnnotationOfType(TEKSTEINDE);
    when(teksteinde.getBody()).thenReturn("1");
    final String body = "<body>"//
        + "<ab id=\"1\"/>‡<ae id=\"1\"/>"//
        + "<ab id=\"2\"/>‡<ae id=\"2\"/>"//
        + "line 1\n"//
        + "line 2\n"//
        + "<ab id=\"3\"/>‡<ae id=\"3\"/>"//
        + "line 3"//
        + "<ab id=\"4\"/>‡<ae id=\"4\"/>"//
        + "</body>";
    final String expected = "\n"//
        + "<text n=\"1\" xml:id=\"PROJECTNAME-1\"><body><p>"//
        + "line 1\n"//
        + "line 2\n"//
        + "</p>" //
        + "<p>"//
        + "line 3"//
        + "</p>\n"//
        + "</body></text>\n";
    assertConversion(body, mockData(1, tekstbegin, 2, alinea, 3, alinea, 4, teksteinde), expected);
  }

  @Test
  public void testAlineaEndsAtMvnPoezie() {
    final Annotation tekstbegin = mockAnnotationOfType(TEKSTBEGIN);
    when(tekstbegin.getBody()).thenReturn("1");
    final Annotation alinea = mockAnnotationOfType(ALINEA);
    final Annotation poetry = mockAnnotationOfType(POEZIE);
    final Annotation teksteinde = mockAnnotationOfType(TEKSTEINDE);
    when(teksteinde.getBody()).thenReturn("1");
    final String body = "<body>"//
        + "<ab id=\"1\"/>‡<ae id=\"1\"/>"//
        + "<ab id=\"2\"/>‡<ae id=\"2\"/>"//
        + "line 1\n"//
        + "line 2\n"//
        + "<ab id=\"3\"/>‡<ae id=\"3\"/>"//
        + "dichtregel 1"//
        + "<ab id=\"4\"/>‡<ae id=\"4\"/>"//
        + "</body>";
    final String expected = "\n"//
        + "<text n=\"1\" xml:id=\"PROJECTNAME-1\"><body><p>"//
        + "line 1\n"//
        + "line 2\n"//
        + "</p>" //
        + "<lg>"//
        + "dichtregel 1"//
        + "</lg>\n"//
        + "</body></text>\n";
    assertConversion(body, mockData(1, tekstbegin, 2, alinea, 3, poetry, 4, teksteinde), expected);
  }

  @Test
  public void testAlineaEndsAtMvnOpschrift() {
    final Annotation annotation = mockAnnotationOfType(ALINEA);
    final Annotation opschrift = mockAnnotationOfType(OPSCHRIFT);
    final String body = "<body>"//
        + "<ab id=\"1\"/>‡<ae id=\"1\"/>"//
        + "line 1\n"//
        + "line 2\n"//
        + "<ab id=\"2\"/>Opschrift<ae id=\"2\"/>"//
        + "</body>";
    final String expected = "<p>"//
        + "line 1\n"//
        + "line 2\n"//
        + "</p>" //
        + "<head>Opschrift</head>";
    assertConversion(body, mockData(1, annotation, 2, opschrift), expected);
  }

  @Test
  public void testAlineaEndsAtMvnOnderschrift() {
    final Annotation annotation = mockAnnotationOfType(ALINEA);
    final Annotation onderschrift = mockAnnotationOfType(ONDERSCHRIFT);
    final String body = "<body>"//
        + "<ab id=\"1\"/>‡<ae id=\"1\"/>"//
        + "line 1\n"//
        + "line 2\n"//
        + "<ab id=\"2\"/>Onderschrift<ae id=\"2\"/>"//
        + "</body>";
    final String expected = "<p>"//
        + "line 1\n"//
        + "line 2\n"//
        + "</p>" //
        + "<closer>Onderschrift</closer>";
    assertConversion(body, mockData(1, annotation, 2, onderschrift), expected);
  }

  //  mvn:cijfers (romeins)
  //  Toelichting: Romeinse cijfers worden tbv de weergave als zodanig gecodeerd
  //  Validatie:   Geen
  //  Conversie:   <num type="roman">[geannoteerde tekst]</num>
  @Test
  public void testCijfersConversie() {
    final Annotation annotation = mockAnnotationOfType(CIJFERS);
    final String body = "<body>Weapon "//
        + "<ab id=\"1\"/>X<ae id=\"1\"/>"//
        + " to the rescue!</body>";
    final String expected = "Weapon "//
        + "<num type=\"roman\">X</num>"//
        + " to the rescue!";
    assertConversion(body, mockData(1, annotation), expected);
  }

  //  mvn:defect
  //  Toelichting: Wordt gebruikt voor ontbrekende tekst, bijvoorbeeld doordat een deel van het manuscript is afgesneden of anderszins ontbreekt.
  //  Validatie:   Geen
  //  Conversie:   <gap/>
  //               De geannoteerde tekst wordt genegeerd.
  @Test
  public void testDefectConversie() {
    final Annotation annotation = mockAnnotationOfType(DEFECT);
    final String body = "<body>pre "//
        + "<ab id=\"1\"/>geannoteerde tekst<ae id=\"1\"/>"//
        + " post</body>";
    final String expected = "pre "//
        + "<gap/>"//
        + " post";
    assertConversion(body, mockData(1, annotation), expected);
  }

  //  mvn:onleesbaar
  //  Toelichting: De geannoteerde tekst is onleesbaar
  //  Validatie:   Geen
  //  Conversie:   <gap></gap>
  @Test
  public void testOnleesbaarConversie() {
    final Annotation annotation = mockAnnotationOfType(ONLEESBAAR);
    final String body = "<body>pre "//
        + "<ab id=\"1\"/>geannoteerde tekst<ae id=\"1\"/>"//
        + " post</body>";
    final String expected = "pre "//
        + "<gap/>"//
        + " post";
    assertConversion(body, mockData(1, annotation), expected);
  }

  //  mvn:doorhaling
  //  Toelichting: Doorgehaalde of anderszins verwijderde tekst
  //  Validatie:   Geen
  //  Conversie:   <del>[geannoteerde tekst]</del>
  @Test
  public void testDoorhalingConversie() {
    final Annotation annotation = mockAnnotationOfType(DOORHALING);
    final String body = "<body>pre "//
        + "<ab id=\"1\"/>geannoteerde tekst<ae id=\"1\"/>"//
        + " post</body>";
    final String expected = "pre "//
        + "<del>geannoteerde tekst</del>"//
        + " post";
    assertConversion(body, mockData(1, annotation), expected);
  }

  //  mvn:initiaal
  //  Toelichting: De geannoteerde tekst is een initiaal. De hoogte van de initiaal wordt aangegeven in de annotatie
  //               (in aantal regels)
  //  Validatie:   De inhoud van de annotatie is een natuurlijk getal > 0 en < 20. 
  //  Conversie:   <hi rend="capitalsize[inhoud annotatie]>[geannoteerde tekst]</hi>
  @Test
  public void testInitiaalConversieWith_SpacesAreAllowedInTheAnnotationBody() {
    final Annotation annotation = mockAnnotationOfType(INITIAAL);
    when(annotation.getBody()).thenReturn("2 ");
    final String body = "<body>pre "//
        + "<ab id=\"1\"/>geannoteerde tekst<ae id=\"1\"/>"//
        + " post</body>";
    final String expected = "pre "//
        + "<hi rend=\"capitalsize2\">geannoteerde tekst</hi>"//
        + " post";
    assertConversion(body, mockData(1, annotation), expected);
    //
  }

  @Test
  public void testInitiaalConversie_AnnotationBodyOfZeroFailsValidation() {
    assertAnnotationBodyIsInvalidForInitiaal("0");
  }

  @Test
  public void testInitiaalConversie_AnnotationBodyOfTwentyFailsValidation() {
    assertAnnotationBodyIsInvalidForInitiaal("20");
  }

  @Test
  public void testInitiaalConversie_AnnotationBodyOfNotAnIntegerFailsValidation() {
    assertAnnotationBodyIsInvalidForInitiaal("whatever");
  }

  private void assertAnnotationBodyIsInvalidForInitiaal(final String annotationBody) {
    final Annotation annotation = mockAnnotationOfType(INITIAAL);
    when(annotation.getBody()).thenReturn(annotationBody);
    final Long entryId = 1l;
    final String body = "<body>"//
        + "<entry n=\"01r\" xml:id=\"mvn-brussel-kb-ii-116-pb-01r\" facs=\"http://localhost:8080/jp2/14165714814681.jp2\" _entryId=\"" + entryId + "\">"//
        + "pre "//
        + "<ab id=\"1\"/>geannoteerde tekst<ae id=\"1\"/>"//
        + " post"//
        + "</entry></body>";
    final String entryPrefix = BASEURL + "/projects/projectName/entries/" + entryId + "/transcriptions/diplomatic : mvn:initiaal : ";
    final String validationError = entryPrefix + "De inhoud van de annotatie ('" + annotationBody + "') is geen natuurlijk getal > 0 en < 20.";
    assertConversionFailsValidation(body, mockData(1, annotation), validationError);
  }

  //  mvn:letters (zelfnoemfunctie)
  //  Toelichting: Letters worden gebruikt in zelfnoemfunctie
  //  Validatie:   Geen
  //  Conversie:   <mentioned>[geannoteerde tekst]</mentioned>
  @Test
  public void testLettersConversie() {
    final Annotation annotation = mockAnnotationOfType(LETTERS);
    final String body = "<body>pre "//
        + "<ab id=\"1\"/>geannoteerde tekst<ae id=\"1\"/>"//
        + " post</body>";
    final String expected = "pre "//
        + "<mentioned>geannoteerde tekst</mentioned>"//
        + " post";
    assertConversion(body, mockData(1, annotation), expected);
  }

  //  mvn:linkermargekolom
  //  Toelichting: Tekst of teken staat in de linkermarge
  //  Validatie:   Geen
  //  Conversie:   <note place="margin-left" type="ms">[geannoteerde tekst]</note>
  @Test
  public void testLinkermargekolomConversie() {
    final Annotation annotation = mockAnnotationOfType(LINKERMARGEKOLOM);
    final String body = "<body>pre "//
        + "<ab id=\"1\"/>geannoteerde tekst<ae id=\"1\"/>"//
        + " post</body>";
    final String expected = "pre "//
        + "<note place=\"margin-left\" type=\"ms\">geannoteerde tekst</note>"//
        + " post";
    assertConversion(body, mockData(1, annotation), expected);
  }

  //  mvn:onderschrift
  //  Toelichting: Bevat een onderschrift bij de voorafgaande tekst
  //  Validatie:   Geen
  //  Conversie:   <closer>[geannoteerde tekst]</closer>
  //               Onderschriften worden niet meegenomen in de tekstregelnummering, wel in de bladregelnumering.
  @Test
  public void testOnderschriftConversie() {
    final Annotation annotation = mockAnnotationOfType(ONDERSCHRIFT);
    final String body = "<body>pre "//
        + "<ab id=\"1\"/>geannoteerde tekst<ae id=\"1\"/>"//
        + " post</body>";
    final String expected = "pre "//
        + "<closer>geannoteerde tekst</closer>"//
        + " post";
    assertConversion(body, mockData(1, annotation), expected);
  }

  //  mvn:onduidelijk
  //  Toelichting: De geannoteerde tekst is slecht leesbaar
  //  Validatie:   Geen
  //  Conversie:   <unclear>[geannoteerde tekst]</unclear>
  @Test
  public void testOnduidelijkConversie() {
    final Annotation annotation = mockAnnotationOfType(ONDUIDELIJK);
    final String body = "<body>pre "//
        + "<ab id=\"1\"/>geannoteerde tekst<ae id=\"1\"/>"//
        + " post</body>";
    final String expected = "pre "//
        + "<unclear>geannoteerde tekst</unclear>"//
        + " post";
    assertConversion(body, mockData(1, annotation), expected);
  }

  //  mvn:ophoging (rood)
  //  Toelichting: De geannoteerde tekst is rood opgehoogd
  //  Validatie:   Geen
  //  Conversie:   <hi rend="rubricated">[geannoteerde tekst]</hi>
  //               Wanneer een ander element aanwezig is waarop het rend-attribuut kan worden aangebracht,
  //               is het <hi>- element niet noodzakelijk. 
  @Test
  public void testOphogingConversie() {
    final Annotation annotation = mockAnnotationOfType(OPHOGING_ROOD);
    final String body = "<body>pre "//
        + "<ab id=\"1\"/>geannoteerde tekst<ae id=\"1\"/>"//
        + " post</body>";
    final String expected = "pre "//
        + "<hi rend=\"rubricated\">geannoteerde tekst</hi>"//
        + " post";
    assertConversion(body, mockData(1, annotation), expected);
  }

  //  mvn:opschrift
  //  Toelichting: De geannoteerde tekst is een opschrift bij de erop volgende tekst
  //  Validatie:   Geen
  //  Conversie:   <head>[geannoteerde tekst]</head>
  //               Opschriften worden niet meegenomen in de tekstregelnummering, wel in de bladregelnummering..
  @Test
  public void testOpschriftConversie() {
    final Annotation annotation = mockAnnotationOfType(OPSCHRIFT);
    final String body = "<body>pre "//
        + "<ab id=\"1\"/>geannoteerde tekst<ae id=\"1\"/>"//
        + " post</body>";
    final String expected = "pre "//
        + "<head>geannoteerde tekst</head>"//
        + " post";
    assertConversion(body, mockData(1, annotation), expected);
  }

  //  mvn:paleografisch
  //  Toelichting: Bevat een paleografische annotatie van de editeur.
  //  Validatie:   Geen
  //  Conversie:   <note type="pc">[inhoud annotatie]</note>, te plaatsen na de geannoteerde tekst.
  //               Cursieve passages binnen de inhoud van de annotatie worden gemarkeerd als <mentioned>.
  @Test
  public void testPaleografishConversie() {
    final Annotation annotation = mockAnnotationOfType(PALEOGRAFISCH);
    when(annotation.getBody()).thenReturn("inhoud <i>annotatie</i>");
    final String body = "<body>pre "//
        + "<ab id=\"1\"/>geannoteerde tekst<ae id=\"1\"/>"//
        + " post</body>";
    final String expected = "pre "//
        + "geannoteerde tekst<note type=\"pc\">inhoud <mentioned>annotatie</mentioned></note>"//
        + " post";
    assertConversion(body, mockData(1, annotation), expected);
  }

  //  mvn:tekstkleur (rood)
  //  Toelichting: De geannoteerde tekst is in rood
  //  Validatie:   Geen
  //  Conversie:   <hi rend="rubric">[geannoteerde tekst]</hi>
  //               Wanneer een ander element aanwezig is waarop het rend-attribuut kan worden aangebracht,
  //               is het <hi>- element niet noodzakelijk.
  @Test
  public void testTekstkleurConversie() {
    final Annotation annotation = mockAnnotationOfType(TEKSTKLEUR_ROOD);
    final String body = "<body>pre "//
        + "<ab id=\"1\"/>geannoteerde tekst<ae id=\"1\"/>"//
        + " post</body>";
    final String expected = "pre "//
        + "<hi rend=\"rubric\">geannoteerde tekst</hi>"//
        + " post";
    assertConversion(body, mockData(1, annotation), expected);
  }

  //  mvn:witregel
  //  Toelichting: Geeft een witregel in de tekst aan
  //  Validatie:   Het geannoteerde teken is ‘¤’
  //  Conversie:   Genereer een extra <lb/>. 
  //               Deze lb krijgt geen n, np of xml:id attribuut en telt ook niet mee in de nummering. 
  @Test
  public void testWitregelConversie() {
    final Annotation annotation = mockAnnotationOfType(WITREGEL);
    final String body = "<body><entry n=\"1\" xml:id=\"X\"><lb/>"//
        + "<ab id=\"1\"/>¤<ae id=\"1\"/>"//
        + "Lorem ipsum<le/>"//
        + "</entry></body>";
    final String expected = "\n"//
        + "<pb xml:id=\"X\" n=\"1\"/><lb/>\n"//
        + "<lb n=\"1\" xml:id=\"X-lb-1\"/>Lorem ipsum";
    assertConversion(body, mockData(1, annotation), expected);
  }

  @Ignore
  @Test
  public void testWitregelConversieOfIllegalCharacterGivesValidationError() {
    final Annotation annotation = mockAnnotationOfType(WITREGEL);
    final Long entryId = 12234l;
    final String body = "<body>"//
        + "<entry n=\"01r\" xml:id=\"mvn-brussel-kb-ii-116-pb-01r\" facs=\"http://localhost:8080/jp2/14165714814681.jp2\" _entryId=\"" + entryId + "\">"//
        + " pre "//
        + "<ab id=\"1\"/>somethingelse<ae id=\"1\"/>"//
        + " post"//
        + "</entry>"//
        + "</body>";
    final String validationError = "https://www.elaborate.huygens.knaw.nl/projects/projectName/entries/"//
        + entryId + "/transcriptions/diplomatic :"//
        + " mvn:witregel :"//
        + " Het geannoteerde teken moet ‘¤’ zijn, is 'somethingelse'";
    assertConversionFailsValidation(body, mockData(1, annotation), validationError);
  }

  //  mvn:poëzie
  //  Toelichting: Er volgt een verstekst. Een puntannotatie.
  //  Validatie:   Geen
  //  Conversie:   Er begint een <lg>-element (linegroup). De linegroup wordt afgesloten bij
  //               - een mvn:alinea-element
  //               - een mvn:onderschrift, mvn:opschrift
  //               - het einde van de tekst
  @Test
  public void testPoezieConversie_LineGroupEndsAtEndOfText() {
    final Annotation tekstbegin = mockAnnotationOfType(TEKSTBEGIN);
    when(tekstbegin.getBody()).thenReturn("1");
    final Annotation poezieAnnotation = mockAnnotationOfType(POEZIE);
    final Annotation teksteinde = mockAnnotationOfType(TEKSTEINDE);
    when(teksteinde.getBody()).thenReturn("1");
    final String body = "<body>"//
        + "<entry n=\"42r\" xml:id=\"ABC-pb-42r\">"//
        + "<ab id=\"1\"/>‡<ae id=\"1\"/>"//
        + "<ab id=\"2\"/>‡<ae id=\"2\"/>"//
        + "<lb/>Er was eens een neushoorn uit Assen,<le/>"//
        + "<lb/>Die moest echt verschrikkelijk nodig plassen.<le/>"//
        + "<ab id=\"3\"/>‡<ae id=\"3\"/>"//
        + "</entry>"//
        + "</body>";
    final String expected = "\n"//
        + "<text n=\"1\" xml:id=\"PROJECTNAME-1\"><body>"//
        + "<lg>\n"// 
        + "<pb xml:id=\"ABC-pb-42r\" n=\"42r\"/>\n"//
        + "<lb n=\"1\" xml:id=\"ABC-pb-42r-lb-1\"/><l n=\"1\" xml:id=\"PROJECTNAME-1-l-1\">Er was eens een neushoorn uit Assen,</l>\n"//
        + "<lb n=\"2\" xml:id=\"ABC-pb-42r-lb-2\"/><l n=\"2\" xml:id=\"PROJECTNAME-1-l-2\">Die moest echt verschrikkelijk nodig plassen.</l>"// 
        + "</lg>\n"//
        + "</body></text>\n";;
    assertConversion(body, mockData(1, tekstbegin, 2, poezieAnnotation, 3, teksteinde), expected);
  }

  @Test
  public void testPoezieConversie_LineGroupEndsAtAlinea() {
    final Annotation tekstbegin = mockAnnotationOfType(TEKSTBEGIN);
    when(tekstbegin.getBody()).thenReturn("1");
    final Annotation poezie = mockAnnotationOfType(POEZIE);
    final Annotation alinea = mockAnnotationOfType(ALINEA);
    final Annotation teksteinde = mockAnnotationOfType(TEKSTEINDE);
    when(teksteinde.getBody()).thenReturn("1");
    final String body = "<body>"//
        + "<ab id=\"1\"/><ab id=\"2\"/>‡<ae id=\"1\"/><ae id=\"2\"/>"//
        + " line 1\n"//
        + " line 2\n"//
        + "<ab id=\"3\"/>‡<ae id=\"3\"/>"//
        + "THE END"//
        + "<ab id=\"4\"/>‡<ae id=\"4\"/>"//
        + "</body>";
    final String expected = "\n"//
        + "<text n=\"1\" xml:id=\"PROJECTNAME-1\"><body>"//
        + "<lg> line 1\n"// 
        + " line 2\n"// 
        + "</lg>"// 
        + "<p>THE END</p>\n"// 
        + "</body></text>\n";
    assertConversion(body, mockData(1, tekstbegin, 2, poezie, 3, alinea, 4, teksteinde), expected);
  }

  @Test
  public void testPoezieConversie_LineGroupEndsAtOnderschrift() {
    final Annotation poezieAnnotation = mockAnnotationOfType(POEZIE);
    final Annotation onderschriftAnnotation = mockAnnotationOfType(ONDERSCHRIFT);
    final String body = "<body>"//
        + "<ab id=\"1\"/>‡<ae id=\"1\"/>"//
        + " line 1\n"//
        + " line 2\n"//
        + "<ab id=\"2\"/>onderschrift<ae id=\"2\"/>"//
        + "</body>";
    final String expected = "<lg> line 1\n"//
        + " line 2\n"//
        + "</lg>"//
        + "<closer>onderschrift</closer>";
    assertConversion(body, mockData(1, poezieAnnotation, 2, onderschriftAnnotation), expected);
  }

  @Test
  public void testPoezieConversie_LineGroupEndsAtOpchrift() {
    final Annotation poezieAnnotation = mockAnnotationOfType(POEZIE);
    final Annotation opAnnotation = mockAnnotationOfType(OPSCHRIFT);
    final String body = "<body>"//
        + "<ab id=\"1\"/>‡<ae id=\"1\"/>"//
        + " line 1\n"//
        + " line 2\n"//
        + "<ab id=\"2\"/>opschrift<ae id=\"2\"/>"//
        + "</body>";
    final String expected = "<lg> line 1\n"//
        + " line 2\n"//
        + "</lg>"//
        + "<head>opschrift</head>";
    assertConversion(body, mockData(1, poezieAnnotation, 2, opAnnotation), expected);
  }

  //  mvn:rechtermargekolom
  //  Toelichting: Tekst staat in de rechtermarge. 
  //               Het gaat hier alleen om markeringen, bijschriften en soortgelijke tekstjes, die ook in de publicatie
  //               in de marge geplaatst moeten worden. Deze annotatie wordt niet gebruikt voor toevoegingen aan de hoofdtekst. 
  //  Validatie:   Geen
  //  Conversie:   <note place="margin-right" type="ms">[geannoteerde tekst]</note>
  @Test
  public void testRechtermargeKolomConversie() {
    final Annotation annotation = mockAnnotationOfType(RECHTERMARGEKOLOM);
    final String body = "<body>pre "//
        + "<ab id=\"1\"/>geannoteerde tekst<ae id=\"1\"/>"//
        + " post</body>";
    final String expected = "pre "//
        + "<note place=\"margin-right\" type=\"ms\">geannoteerde tekst</note>"//
        + " post";
    assertConversion(body, mockData(1, annotation), expected);
  }

  //  mvn:regelnummering (blad)
  //  Toelichting: Geeft afwijkingen in de bladregelnummering aan. Een puntannotatie.
  //  Validatie:   Als de waarde geen geheel getal is, moet de volgende regel, als die er is,
  //               ook een mvn:regelnummering (blad)-annotatie hebben. 
  //  Conversie:   De annotatie overrulet het automatisch toegekende bladregelnummer. Het is ook de basis voor
  //               de bladregelnummers van de volgende regels.
  @Test
  public void testRegelnummeringBladConversie() {
    final Annotation annotation = mockAnnotationOfType(REGELNUMMERING_BLAD);
  }

  //  mvn:inspringen
  //  Toelichting: De tekst is niet links uitgelijnd. De puntannotatie heeft betrekking op de erop volgende tekst.  
  //  Validatie:   Het geannoteerde teken is ‘¤’
  //  Conversie:   Rend-attribuut op voorafgaande <lb> met waarde "indent"
  //               PS: vooralsnog onderscheiden we geen verschillende niveaus van inspringing.
  @Test
  public void testInspringenConversie() {
    final Annotation annotation = mockAnnotationOfType(INSPRINGEN);
    final String body = "<body>"//
        + "<entry n=\"36v\" xml:id=\"VDS-pb-36v\">"//
        + "<lb/><ab id=\"1\"/>¤<ae id=\"1\"/>Lorem ipsum...<le/>"//
        + "</entry>"//
        + "</body>";

    final String expected = "\n"//
        + "<pb xml:id=\"VDS-pb-36v\" n=\"36v\"/>\n"//
        + "<lb n=\"1\" xml:id=\"VDS-pb-36v-lb-1\" rend=\"indent\"/>Lorem ipsum...";
    assertConversion(body, mockData(1, annotation), expected);
  }

  //  mvn:tekstbegin, mvn:teksteinde
  //  Toelichting: mvn:tekstbegin en mvn:teksteinde markeren begin en eind van een tekst of een groep van teksten
  //  Validatie:   • de geannoteerde tekst is ‘‡’ 
  //               • de inhoud van de annotatie bestaat uit het tekstnummer: groepjes van tekens gescheiden door punten.
  //                 De te gebruiken tekens zijn hoofdletters, kleine letters en cijfers (bijvoorbeeld: A.17.4b);
  //                 in het geval van mvn:tekstbegin wordt het tekstnummer nog gevolgd door een puntkomma en dan een door
  //                 de editeur aan de tekst toegekende titel.
  //               • het aantal punten in het tekstnummer is maximaal 3
  //               • bij een beginmarkering moet een overeenkomstige eindmarkering bestaan en omgekeerd
  //               • Als een tekst met nummer 1.2.3 bestaat, moet er ook een tekst 1.2 bestaan, die 1.2.3 omvat.
  //                 Tekst 1.2 bevat in dat geval geen ‘eigen’ tekst: alle erin bevatte tekst maakt deel uit van de deelteksten,
  //                 met uitzondering van opschriften, onderschriften en teksten in de marge 
  //               • teksten kunnen niet overlappen (wel nesten)
  //  Conversie:   Op het diepste niveau (‘3’ uit 1.2.3):
  //               • mvn:tekstbegin: <text><body>
  //               • mvn:teksteind:</body> </text>
  //               Op hogere niveaus:
  //               • mvn:tekstbegin: <group>
  //               • mvn:teksteind:</group>
  //
  //               De aan te maken text - en group-elementen krijgen een n-attribuut dat gelijk is aan het tekstnummer.
  //               Ze krijgen een xml:id attribuut dat bestaat uit sigle gevolgd door tekstnummer. 
  //
  //               Binnen de <text> resp. <group> wordt als eerste element opgenomen:
  //               <head type="assigned">[toegekende titel]</head>
  //
  //               Het teken waarop de annotatie geplaatst was, wordt genegeerd.
  //               Elke <text>, </text>, <group> en </group> worden geplaatst op een nieuwe regel.
  @Test
  public void testTekstBeginEindeConversieFailsValidationWhenTextNumHasMoreThatThreeDots() {
    final Annotation beginAnnotation = mockAnnotationOfType(TEKSTBEGIN);
    when(beginAnnotation.getBody()).thenReturn("1.2.3.4.5");
    final Annotation eindeAnnotation = mockAnnotationOfType(TEKSTEINDE);
    when(eindeAnnotation.getBody()).thenReturn("1.2.3.4.5");
  }

  /* low-priority or cancelled requirements */

  //  mvn:vreemdteken
  //  Toelichting: Geeft korte omschrijving van teken (Nog niet in gebruik)
  //  Validatie:   Toegestane omschrijvingen: nog aanvullen
  //  Conversie:   Naar gewenst teken
  //  Prioriteit   Laag
  @Test
  public void testVreemdtekenConversie() {
    final Annotation annotation = mockAnnotationOfType(VREEMDTEKEN);
  }

  //  mvn:versregel
  //  Toelichting: Geeft het beginpunt van een versregel wanneer dat niet samenvalt met het begin van de fysieke regel.
  //               Een puntannotatie.
  //  Validatie:   Geen
  //  Conversie:   Zie sectie Problemen: Poëzie
  //  Prioriteit:  Laag
  @Test
  public void testVersregelConversie() {
    final Annotation annotation = mockAnnotationOfType(VERSREGEL);
  }

  //  mvn:kolom
  //  Toelichting: Het begin van een kolom op een foliumzijde. Een puntannotatie.
  //  Validatie:   Het geannoteerde teken is ‘¤’
  //  Conversie:   <cb/>
  //  Prioriteit:  Laag
  @Test
  public void testKolomConversie() {
    final Annotation annotation = mockAnnotationOfType(KOLOM);
  }

  //  mvn:regelnummering (tekst)
  //  Toelichting: Geeft afwijkingen aan in de tekstregelnummering. Een puntannotatie.
  //  Validatie:   Geen
  //  Conversie:   Geen
  //  Prioriteit:  Komt te vervallen.
  @Test
  public void testRegelnummeringTekstConversie() {
    final Annotation annotation = mockAnnotationOfType(REGELNUMMERING_TEKST);
  }

  //  mvn:gebruikersnotitie
  //  Toelichting: Notitie van een gebruiker van het manuscript
  //  Validatie:   Geen
  //  Conversie:   Geen
  //  Prioriteit:  komt te vervallen
  @Test
  public void testGebruikersNotitieConversie() {
    final Annotation annotation = mockAnnotationOfType(GEBRUIKERSNOTITIE);
  }

  //  mvn:incipit
  //  Toelichting: Geeft de eerste regel van een tekst
  //  Validatie:   Geen
  //  Conversie:   Geen
  //  Prioriteut:  komt te vervallen
  @Test
  public void testIncipitConversie() {
    final Annotation annotation = mockAnnotationOfType(INCIPIT);
  }

  //  mvn:metamark
  //  Toelichting: Geannoteerde tekst bevat instructie over hoe de tekst moet worden gelezen.
  //  Validatie:   Geen
  //  Conversie:   Geen
  //  Prioriteit:  Komt te vervallen
  @Test
  public void testMetamarkConversie() {
    final Annotation annotation = mockAnnotationOfType(METAMARK);
  }

  //  b: <hi rend=rubric>
  @Test
  public void testBoldConvertsToHiRendRubric() {
    final String body = "<body>pre "//
        + "<b>bold</b>"//
        + " post</body>";
    final String expected = "pre <hi rend=\"rubric\">bold</hi> post";
    assertConversion(body, mockData(), expected);
  }

  //  i: <ex>
  @Test
  public void testItalicConvertsToEx() {
    final String body = "<body>pre "//
        + "<i>italic</i>"//
        + " post</body>";
    final String expected = "pre <ex>italic</ex> post";
    assertConversion(body, mockData(), expected);
  }

  //  strike: <del>
  @Test
  public void testStrikeConvertsToDel() {
    final String body = "<body>pre "//
        + "<strike>strike</strike>"//
        + " post</body>";
    final String expected = "pre <del>strike</del> post";
    assertConversion(body, mockData(), expected);
  }

  //  sup: <hi rend=superscript>
  @Test
  public void testSupConvertsToHiRendSuperscript() {
    final String body = "<body>pre "//
        + "<sup>superscript</sup>"//
        + " post</body>";
    final String expected = "pre <hi rend=\"superscript\">superscript</hi> post";
    assertConversion(body, mockData(), expected);
  }

  //  sub: <hi rend=subscript>
  @Test
  public void testSubConvertsToHiRendSubscript() {
    final String body = "<body>pre "//
        + "<sub>subscript</sub>"//
        + " post</body>";
    final String expected = "pre <hi rend=\"subscript\">subscript</hi> post";
    assertConversion(body, mockData(), expected);
  }

  @Test
  public void testAnnotationHierarchyRepair1() {
    final String body = "<body>"//
        + "<ab id=\"1\"/>Lorem ipsum dolor "//
        + "<ab id=\"2\"/>sit amet, "//
        + "<ae id=\"1\"/>"//
        + "consectetur adipiscing elit."//
        + "<ae id=\"2\"/>"//
        + "</body>";
    final String expected = "<body>"//
        + "<ab id=\"1\"/>Lorem ipsum dolor "//
        + "<ab id=\"2\"/>sit amet, "//
        + "<ae id=\"2\"/>"//
        + "<ae id=\"1\"/>"//
        + "<ab id=\"2\"/>"//
        + "consectetur adipiscing elit."//
        + "<ae id=\"2\"/>"//
        + "</body>";
    final Project project = mockProject();
    final Status logger = new Status(1);
    final MVNConverter mc = new MVNConverter(project, mockData(1, mockAnnotationOfType(WITREGEL)), logger, BASEURL);
    final String result = mc.repairAnnotationHierarchy(body);
    assertThat(result).isEqualTo(expected);
  }

  @Test
  public void testAnnotationHierarchyRepair2() {
    final String body = "<body>"//
        + "<ab id=\"1\"/>Lorem ipsum dolor "//
        + "<ab id=\"2\"/>sit amet, "//
        + "<ab id=\"3\"/>dolor "//
        + "<ab id=\"4\"/>four "//
        + "<ae id=\"1\"/>"//
        + "consectetur adipiscing elit."//
        + "<ae id=\"3\"/>"//
        + "<ae id=\"2\"/>"//
        + "<ae id=\"4\"/>"//
        + "</body>";
    final String expected = "<body>"//
        + "<ab id=\"1\"/>Lorem ipsum dolor "//
        + "<ab id=\"2\"/>sit amet, "//
        + "<ab id=\"3\"/>dolor "//
        + "<ab id=\"4\"/>four "//
        + "<ae id=\"4\"/>"//
        + "<ae id=\"3\"/>"//
        + "<ae id=\"2\"/>"//
        + "<ae id=\"1\"/>"//
        + "<ab id=\"2\"/>"//
        + "<ab id=\"3\"/>"//
        + "<ab id=\"4\"/>"//
        + "consectetur adipiscing elit."//
        + "<ae id=\"4\"/>"//
        + "<ae id=\"3\"/>"//
        + "<ae id=\"2\"/>"//
        + "</body>";
    final Project project = mockProject();
    final Status logger = new Status(1);
    final MVNConverter mc = new MVNConverter(project, mockData(1, mockAnnotationOfType(WITREGEL)), logger, BASEURL);
    final String result = mc.repairAnnotationHierarchy(body);
    assertThat(result).isEqualTo(expected);
  }

  @Test
  public void testAnnotationHierarchyRepairWithMultipleLines() {
    final String body = "<body>\n"//
        + "<lb/><ab id=\"1\"/>Lorem ipsum <ab id=\"2\"/>dolor<le/>\n"//
        + "<lb/>whatever<le/>\n"//
        + "<lb/>sit amet censect...<ae id=\"1\"/><ae id=\"2\"/><le/>\n"//
        + "</body>";
    final String expected = "<body>\n"//
        + "<lb/><ab id=\"1\"/>Lorem ipsum <ab id=\"2\"/>dolor<ae id=\"2\"/><ae id=\"1\"/><le/>\n"//
        + "<lb/><ab id=\"1\"/><ab id=\"2\"/>whatever<ae id=\"2\"/><ae id=\"1\"/><le/>\n"//
        + "<lb/><ab id=\"1\"/><ab id=\"2\"/>sit amet censect...<ae id=\"2\"/><ae id=\"1\"/><le/>\n"//
        + "</body>";
    final Project project = mockProject();
    final Status logger = new Status(1);
    final MVNConverter mc = new MVNConverter(project, mockData(1, mockAnnotationOfType(WITREGEL)), logger, BASEURL);
    final String result = mc.repairAnnotationHierarchy(body);
    assertThat(result).isEqualTo(expected);
  }

  @Test
  public void testProblemWithVDS02() {
    final Annotation tekstbegin1 = mockAnnotationOfType(TEKSTBEGIN);
    when(tekstbegin1.getBody()).thenReturn("1");
    final Annotation poezie = mockAnnotationOfType(POEZIE);
    final Annotation teksteinde1 = mockAnnotationOfType(TEKSTEINDE);
    when(teksteinde1.getBody()).thenReturn("1");
    final Annotation tekstbegin2 = mockAnnotationOfType(TEKSTBEGIN);
    when(tekstbegin2.getBody()).thenReturn("2");
    final Annotation alinea = mockAnnotationOfType(ALINEA);
    final Annotation teksteinde2 = mockAnnotationOfType(TEKSTEINDE);
    when(teksteinde2.getBody()).thenReturn("2");
    final String body = "<body><entry n=\"02v\" xml:id=\"VDS-pb-02v\" facs=\"Brussel - KB - ii 116 - 002v.jpg\" _entryId=\"26831\">"//
        + "<lb/><ab id=\"1\"/>‡<ae id=\"1\"/><ab id=\"2\"/>‡<ae id=\"2\"/>Tekst 1<le/>"//
        + "<lb/>Diet ondersouct en can gronderen<ab id=\"3\"/>‡<ae id=\"3\"/><le/>"//
        + "<lb/><ab id=\"4\"/>‡<ae id=\"4\"/><ab id=\"5\"/>‡<ae id=\"5\"/><b>Van den zeuen vraghen van</b><b><le/>"//
        + "<lb/>Iherusalem zeghelijn</b><le/>"//
        + "<lb/><ab id=\"6\"/>‡<ae id=\"6\"/><le/>"//
        + "</entry></body>";
    final String expected = "\n"//
        + "<text n=\"1\" xml:id=\"PROJECTNAME-1\"><body><lg>\n"//
        + "<pb xml:id=\"VDS-pb-02v\" n=\"02v\" facs=\"Brussel - KB - ii 116 - 002v.jpg\"/>\n"//
        + "<lb n=\"1\" xml:id=\"VDS-pb-02v-lb-1\"/><l n=\"1\" xml:id=\"PROJECTNAME-1-l-1\">Tekst 1</l>\n"//
        + "<lb n=\"2\" xml:id=\"VDS-pb-02v-lb-2\"/><l n=\"2\" xml:id=\"PROJECTNAME-1-l-2\">Diet ondersouct en can gronderen</l></lg>\n"//
        + "</body></text>\n"//
        + "\n"//
        + "<text n=\"2\" xml:id=\"PROJECTNAME-2\"><body><p>\n"//
        + "<lb n=\"3\" xml:id=\"VDS-pb-02v-lb-3\"/><hi rend=\"rubric\">Van den zeuen vraghen van</hi>\n"//
        + "<lb n=\"4\" xml:id=\"VDS-pb-02v-lb-4\"/><hi rend=\"rubric\">Iherusalem zeghelijn</hi>"//
        + "</p>\n"//
        + "</body></text>\n";
    assertConversion(body,
        mockData(//
            1, tekstbegin1, //
            2, poezie, //
            3, teksteinde1, //
            4, tekstbegin2, //
            5, alinea, //
            6, teksteinde2//
        ), expected);
  }

  /* private methods */
  private Annotation mockAnnotationOfType(final MVNAnnotationType type) {
    final AnnotationType witregel = mockAnnotationType(type.getName());
    final Annotation annotation = mock(Annotation.class);
    when(annotation.getAnnotationType()).thenReturn(witregel);
    return annotation;
  }

  private AnnotationType mockAnnotationType(final String value) {
    final AnnotationType witregel = mock(AnnotationType.class);
    when(witregel.getName()).thenReturn(value);
    return witregel;
  }

  private MVNConversionData mockData(final int annotationNo, final Annotation annotation) {
    final MVNConversionData conversionData = new MVNConversionData();
    addAnnotation(conversionData, annotationNo, annotation);
    return conversionData;
  }

  private MVNConversionData mockData() {
    return mock(MVNConversionData.class);
  }

  private MVNConversionData mockData(final int annotationNo1, final Annotation annotation1, final int annotationNo2, final Annotation annotation2) {
    final MVNConversionData conversionData = mockData(annotationNo1, annotation1);
    addAnnotation(conversionData, annotationNo2, annotation2);
    return conversionData;
  }

  private MVNConversionData mockData(final int annotationNo1, final Annotation annotation1, final int annotationNo2, final Annotation annotation2, final int annotationNo3, final Annotation annotation3) {
    final MVNConversionData conversionData = mockData(annotationNo1, annotation1, annotationNo2, annotation2);
    addAnnotation(conversionData, annotationNo3, annotation3);
    return conversionData;
  }

  private MVNConversionData mockData(final int annotationNo1, final Annotation annotation1, final int annotationNo2, final Annotation annotation2, final int annotationNo3, final Annotation annotation3, final int annotationNo4, final Annotation annotation4) {
    final MVNConversionData conversionData = mockData(annotationNo1, annotation1, annotationNo2, annotation2, annotationNo3, annotation3);
    addAnnotation(conversionData, annotationNo4, annotation4);
    return conversionData;
  }

  private MVNConversionData mockData(final int annotationNo1, final Annotation annotation1, final int annotationNo2, final Annotation annotation2, final int annotationNo3, final Annotation annotation3, final int annotationNo4, final Annotation annotation4, final int annotationNo5, final Annotation annotation5) {
    final MVNConversionData conversionData = mockData(annotationNo1, annotation1, annotationNo2, annotation2, annotationNo3, annotation3, annotationNo4, annotation4);
    addAnnotation(conversionData, annotationNo5, annotation5);
    return conversionData;
  }

  private MVNConversionData mockData(final int annotationNo1, final Annotation annotation1, final int annotationNo2, final Annotation annotation2, final int annotationNo3, final Annotation annotation3, final int annotationNo4, final Annotation annotation4, final int annotationNo5, final Annotation annotation5, final int annotationNo6, final Annotation annotation6) {
    final MVNConversionData conversionData = mockData(annotationNo1, annotation1, annotationNo2, annotation2, annotationNo3, annotation3, annotationNo4, annotation4, annotationNo5, annotation5);
    addAnnotation(conversionData, annotationNo6, annotation6);
    return conversionData;
  }

  private void addAnnotation(final MVNConversionData conversionData, final int annotationNo4, final Annotation annotation4) {
    final AnnotationData annotationData = new AnnotationData();
    annotationData.body = annotation4.getBody();
    annotationData.type = annotation4.getAnnotationType().getName();
    conversionData.getAnnotationIndex().put(annotationNo4, annotationData);
  }

  private void assertConversion(final String body, final MVNConversionData data, final String expected) {
    final String converted = convert(body, data);
    assertThat(converted).isEqualTo(expected);
  }

  private String convert(final String body, final MVNConversionData data) {
    final Project project = mockProject();
    final Status logger = new Status(1);
    final MVNConversionResult result = new MVNConversionResult(project, logger, BASEURL);
    final String tei = new MVNConverter(project, data, logger, BASEURL).toTei(body, result);
    //    assertThat(result.isOK()).overridingErrorMessage("validation error(s): %s", logger.getErrors()).isTrue();
    return tei;
  }

  private void assertConversionFailsValidation(final String body, final MVNConversionData mockData, final String validationError) {
    final Project project = mockProject();
    final Status logger = new Status(1);
    final MVNConversionResult result = new MVNConversionResult(project, logger, BASEURL);
    new MVNConverter(project, mockData, logger, BASEURL).toTei(body, result);
    assertThat(result.isOK()).isFalse();
    assertThat(logger.getErrors()).contains(validationError);
  }

  private Project mockProject() {
    final Project project = mock(Project.class);
    when(project.getName()).thenReturn("projectName");
    when(project.getTitle()).thenReturn("Title");
    when(project.getMetadataMap()).thenReturn(ImmutableMap.of(ProjectMetadataFields.PUBLICATION_TITLE, "Publication Title"));
    return project;
  }

  @Test
  public void testValidateEntryOrderAndName() throws Exception {
    assertThat("valid_xml:id-1.2").matches(MVNConverter.VALID_XML_ID_REGEXP);
    assertThat("invalid xml:id!").doesNotMatch(MVNConverter.VALID_XML_ID_REGEXP);
  }
}
