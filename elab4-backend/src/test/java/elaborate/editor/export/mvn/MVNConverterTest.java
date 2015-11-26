package elaborate.editor.export.mvn;

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

import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

import elaborate.editor.model.ProjectMetadataFields;
import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.AnnotationType;
import elaborate.editor.model.orm.Project;
import nl.knaw.huygens.Log;

public class MVNConverterTest {

  @Test
  public void test() {
    Project project = mock(Project.class);
    when(project.getName()).thenReturn("mvn_project");
    when(project.getTitle()).thenReturn("MVN Project");
    when(project.getMetadataMap()).thenReturn(ImmutableMap.of(ProjectMetadataFields.PUBLICATION_TITLE, "MVN Project Publication"));

    MVNConverter converter = new MVNConverter(project);
    MVNConversionResult report = converter.validate();
    Log.info("report={}", report);
    assertThat(report).isNotNull();
    Log.info("errors={}", Joiner.on("\n").join(report.getErrors()));
    assertThat(report.isOK()).isTrue();
    Log.info("tei={}", report.getTEI());
  }

  @Test
  public void testProjectEntryWithoutDiplomaticTranscriptionGeneratesValidationError() {
    // TODO: make test
    // Er is alleen een diplomatische transcriptie aanwezig.
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
  //    Conversie:   <pb/>
  //      De <pb/> wordt geplaatst voorafgaande aan alles wat betrekking heeft op de inhoud van de betreffende pagina,
  //      dus in het bijzonder voorafgaand aan de tekst die op die pagina begint. 
  //      Elke pb krijgt een n-attribuut (veld Name).
  //      Elke pb krijgt een xml:id attribuut (concatenatie sigle, ’pb’, n-attribuut)
  //      De pb krijgt een facs-attibuut waarin de naam van de bijbehorende afbeelding komt te staan.
  //      De volgorde van de entries wordt bepaald door
  //      - indien aanwezig, het metadataveld ‘order’
  //      - anders op entry Name (sorteren als string) 
  //      Elke <pb> wordt geplaatst op een nieuwe regel. 
  @Test
  public void testEveryProjectEntryLeadsToPagebreakInGeneratedTEI() {
    // TODO: make test
  }

  @Test
  public void testTranscriptionWithEmptyLinesGeneratesValidationError() {
    // TODO: make test
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
    Annotation annotation = mockAnnotationOfType(AFKORTING);
    when(annotation.getBody()).thenReturn("<i>expanded</i>");
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
  public void testAlineaConversie() {
    Annotation annotation = mockAnnotationOfType(ALINEA);
    //
  }

  //  mvn:cijfers (romeins)
  //  Toelichting: Romeinse cijfers worden tbv de weergave als zodanig gecodeerd
  //  Validatie:   Geen
  //  Conversie:   <num type="roman">[geannoteerde tekst]</num>
  @Test
  public void testCijfersConversie() {
    Annotation annotation = mockAnnotationOfType(CIJFERS);
    //
  }

  //  mvn:defect
  //  Toelichting: Wordt gebruikt voor ontbrekende tekst, bijvoorbeeld doordat een deel van het manuscript is afgesneden of anderszins ontbreekt.
  //  Validatie:   Geen
  //  Conversie:   <gap/>
  //               De geannoteerde tekst wordt genegeerd.
  @Test
  public void testDefectConversie() {
    Annotation annotation = mockAnnotationOfType(DEFECT);
    //
  }

  //  mvn:doorhaling
  //  Toelichting: Doorgehaalde of anderszins verwijderde tekst
  //  Validatie:   Geen
  //  Conversie:   <del>[geannoteerde tekst]</del>
  @Test
  public void testDoorhalingConversie() {
    Annotation annotation = mockAnnotationOfType(DOORHALING);
    //
  }

  //  mvn:gebruikersnotitie
  //  Toelichting: Notitie van een gebruiker van het manuscript (komt te vervallen)
  //  Validatie:   Geen
  //  Conversie:   Geen
  @Test
  public void testGebruikersNotitieConversie() {
    Annotation annotation = mockAnnotationOfType(GEBRUIKERSNOTITIE);
    //
  }

  //  mvn:incipit
  //  Toelichting: Geeft de eerste regel van een tekst (komt te vervallen)
  //  Validatie:   Geen
  //  Conversie:   Geen
  @Test
  public void testIncipitConversie() {
    Annotation annotation = mockAnnotationOfType(INCIPIT);
    //
  }

  //  mvn:initiaal
  //  Toelichting: De geannoteerde tekst is een initiaal. De hoogte van de initiaal wordt aangegeven in de annotatie
  //               (in aantal regels)
  //  Validatie:   De inhoud van de annotatie is een natuurlijk getal > 0 en < 20. 
  //  Conversie:   <hi rend="capitalsize[inhoud annotatie]>[geannoteerde tekst]</hi>
  @Test
  public void testInitiaalConversie() {
    Annotation annotation = mockAnnotationOfType(INITIAAL);
    //
  }

  //  mvn:inspringen
  //  Toelichting: De tekst is niet links uitgelijnd. De puntannotatie heeft betrekking op de erop volgende tekst.  
  //  Validatie:   Het geannoteerde teken is ‘¤’
  //  Conversie:   Rend-attribuut op voorafgaande <lb> met waarde "indent"
  //               PS: vooralsnog onderscheiden we geen verschillende niveaus van inspringing.
  @Test
  public void testInspringenConversie() {
    Annotation annotation = mockAnnotationOfType(INSPRINGEN);
    //
  }

  //  mvn:kolom
  //  Toelichting: Het begin van een kolom op een foliumzijde. Een puntannotatie.
  //  Validatie:   Het geannoteerde teken is ‘¤’
  //  Conversie:   <cb/>
  //  Prioriteit:  Laag
  @Test
  public void testKolomConversie() {
    Annotation annotation = mockAnnotationOfType(KOLOM);
    //
  }

  //  mvn:letters (zelfnoemfunctie)
  //  Toelichting: Letters worden gebruikt in zelfnoemfunctie
  //  Validatie:   Geen
  //  Conversie:   <mentioned>[geannoteerde tekst]</mentioned>
  @Test
  public void testLettersConversie() {
    Annotation annotation = mockAnnotationOfType(LETTERS);
    //
  }

  //  mvn:linkermargekolom
  //  Toelichting: Tekst of teken staat in de linkermarge
  //  Validatie:   Geen
  //  Conversie:   <note place="margin-left" type="ms">[geannoteerde tekst]</note>
  @Test
  public void testLinkermargekolomConversie() {
    Annotation annotation = mockAnnotationOfType(LINKERMARGEKOLOM);
    //
  }

  //  mvn:metamark
  //  Toelichting: Geannoteerde tekst bevat instructie over hoe de tekst moet worden gelezen. Komt te vervallen
  //  Validatie:   Geen
  //  Conversie:   Geen
  @Test
  public void testMetamarkConversie() {
    Annotation annotation = mockAnnotationOfType(METAMARK);
    //
  }

  //  mvn:onderschrift
  //  Toelichting: Bevat een onderschrift bij de voorafgaande tekst
  //  Validatie:   Geen
  //  Conversie:   <closer>[geannoteerde tekst]</closer>
  //               Onderschriften worden niet meegenomen in de tekstregelnummering, wel in de bladregelnumering.

  @Test
  public void testOnderschriftConversie() {
    Annotation annotation = mockAnnotationOfType(ONDERSCHRIFT);
    //
  }

  //  mvn:onduidelijk
  //  Toelichting: De geannoteerde tekst is slecht leesbaar
  //  Validatie:   Geen
  //  Conversie:   <unclear>[geannoteerde tekst]</unclear>
  @Test
  public void testOnduidelijkConversie() {
    Annotation annotation = mockAnnotationOfType(ONDUIDELIJK);
    //
  }

  //  mvn:onleesbaar
  //  Toelichting: De geannoteerde tekst is onleesbaar
  //  Validatie:   Geen
  //  Conversie:   <gap></gap>
  @Test
  public void testOnleesbaarConversie() {
    Annotation annotation = mockAnnotationOfType(ONLEESBAAR);
  }

  //  mvn:ophoging (rood)
  //  Toelichting: De geannoteerde tekst is rood opgehoogd
  //  Validatie:   Geen
  //  Conversie:   <hi rend="rubricated">[geannoteerde tekst]</hi>
  //               Wanneer een ander element aanwezig is waarop het rend-attribuut kan worden aangebracht,
  //               is het <hi>- element niet noodzakelijk. 
  @Test
  public void testOphogingConversie() {
    Annotation annotation = mockAnnotationOfType(OPHOGING_ROOD);
    String body = "<body>pre <ab id=\"1\"/>geannoteerde tekst<ae id=\"1\"/> post</body>";
    String expected = "pre <hi rend=\"rubricated\">[geannoteerde tekst]</hi> post";
    assertConversion(body, expected);
  }

  //  mvn:opschrift
  //  Toelichting: De geannoteerde tekst is een opschrift bij de erop volgende tekst
  //  Validatie:   Geen
  //  Conversie:   <head>[geannoteerde tekst]</head>
  //               Opschriften worden niet meegenomen in de tekstregelnummering, wel in de bladregelnummering..
  @Test
  public void testOpschriftConversie() {
    Annotation annotation = mockAnnotationOfType(OPSCHRIFT);
  }

  //  mvn:paleografisch
  //  Toelichting: Bevat een paleografische annotatie van de editeur.
  //  Validatie:   Geen
  //  Conversie:   <note type="pc">[inhoud annotatie]</note>, te plaatsen na de geannoteerde tekst.
  //               Cursieve passages binnen de inhoud van de annotatie worden gemarkeerd als <mentioned>.
  @Test
  public void testPaleografishConversie() {
    Annotation annotation = mockAnnotationOfType(PALEOGRAFISCH);
  }

  //  mvn:poëzie
  //  Toelichting: Er volgt een verstekst. Een puntannotatie.
  //  Validatie:   Geen
  //  Conversie:   Er beging een <lg>-element (linegroup). De linegroup wordt afgesloten bij
  //               - een mvn:alinea-element
  //               - een mvn:onderschrift, mvn:opschrift
  //               - het einde van de tekst
  @Test
  public void testPoezieConversie() {
    Annotation annotation = mockAnnotationOfType(POEZIE);
  }

  //  mvn:rechtermargekolom
  //  Toelichting: Tekst staat in de rechtermarge. 
  //               Het gaat hier alleen om markeringen, bijschriften en soortgelijke tekstjes, die ook in de publicatie
  //               in de marge geplaatst moeten worden. Deze annotatie wordt niet gebruikt voor toevoegingen aan de hoofdtekst. 
  //  Validatie:   Geen
  //  Conversie:   <note place="margin-right" type="ms">[geannoteerde tekst]</note>
  @Test
  public void testRechtermargeKolomConversie() {
    Annotation annotation = mockAnnotationOfType(RECHTERMARGEKOLOM);
  }

  //  mvn:regelnummering (blad)
  //  Toelichting: Geeft afwijkingen in de bladregelnummering aan. Een puntannotatie.
  //  Validatie:   Als de waarde geen geheel getal is, moet de volgende regel, als die er is,
  //               ook een mvn:regelnummering (blad)-annotatie hebben. 
  //  Conversie:   De annotatie overrulet het automatisch toegekende bladregelnummer. Het is ook de basis voor
  //               de bladregelnummers van de volgende regels.
  @Test
  public void testRegelnummeringBladConversie() {
    Annotation annotation = mockAnnotationOfType(REGELNUMMERING_BLAD);

  }

  //  mvn:regelnummering (tekst)
  //  Toelichting: Geeft afwijkingen aan in de tekstregelnummering. Een puntannotatie. Komt te vervallen.
  //  Validatie:   Geen
  //  Conversie:   Geen
  @Test
  public void testRegelnummeringTekstConversie() {
    Annotation annotation = mockAnnotationOfType(REGELNUMMERING_TEKST);
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
  public void testTekstBeginEindeConversie() {
    Annotation beginAnnotation = mockAnnotationOfType(TEKSTBEGIN);
    Annotation eindeAnnotation = mockAnnotationOfType(TEKSTEINDE);
  }

  //  mvn:tekstkleur (rood)
  //  Toelichting: De geannoteerde tekst is in rood
  //  Validatie:   Geen
  //  Conversie:   <hi rend="rubric">[geannoteerde tekst]</hi>
  //               Wanneer een ander element aanwezig is waarop het rend-attribuut kan worden aangebracht,
  //               is het <hi>- element niet noodzakelijk.
  @Test
  public void testTekstkleurConversie() {
    Annotation annotation = mockAnnotationOfType(TEKSTKLEUR_ROOD);
  }

  //  mvn:vreemdteken
  //  Toelichting: Geeft korte omschrijving van teken (Nog niet in gebruik)
  //  Validatie:   Toegestane omschrijvingen: nog aanvullen
  //  Conversie:   Naar gewenst teken
  //  Prioriteit   Laag
  @Test
  public void testVreemdtekenConversie() {
    Annotation annotation = mockAnnotationOfType(VREEMDTEKEN);
  }

  //  mvn:versregel
  //  Toelichting: Geeft het beginpunt van een versregel wanneer dat niet samenvalt met het begin van de fysieke regel.
  //               Een puntannotatie.
  //  Validatie:   Geen
  //  Conversie:   Zie sectie Problemen: Poëzie
  //  Prioriteit:  Laag
  @Test
  public void testVersregelConversie() {
    Annotation annotation = mockAnnotationOfType(VERSREGEL);
  }

  //  mvn:witregel
  //  Toelichting: Geeft een witregel in de tekst aan
  //  Validatie:   Het geannoteerde teken is ‘¤’
  //  Conversie:   Genereer een extra <lb/>. 
  //               Deze lb krijgt geen n, np of xml:id attribuut en telt ook niet mee in de nummering. 
  @Test
  public void testWitregelConversie() {
    Annotation annotation = mockAnnotationOfType(WITREGEL);

  }

  /* private methods */
  private Annotation mockAnnotationOfType(MVNAnnotationType type) {
    AnnotationType witregel = mockAnnotationType(type.getName());
    Annotation annotation = mock(Annotation.class);
    when(annotation.getAnnotationType()).thenReturn(witregel);
    return annotation;
  }

  private AnnotationType mockAnnotationType(String value) {
    AnnotationType witregel = mock(AnnotationType.class);
    when(witregel.getName()).thenReturn(value);
    return witregel;
  }

  private void assertConversion(String body, String expected) {
    String converted = convert(body);
    assertThat(converted).isEqualTo(expected);
  }

  private String convert(String body) {
    return body;
  }
}
