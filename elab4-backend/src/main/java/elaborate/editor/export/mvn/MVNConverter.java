package elaborate.editor.export.mvn;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import elaborate.editor.model.orm.Facsimile;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.Transcription;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.tei.Document;
import nl.knaw.huygens.tei.XmlContext;

public class MVNConverter {
  private final Project project;

  public MVNConverter(Project project) {
    this.project = project;
  }

  public MVNConversionResult validate() {
    MVNConversionResult result = new MVNConversionResult(project);
    List<ProjectEntry> projectEntries = project.getProjectEntries();
    for (ProjectEntry entry : projectEntries) {
      MVNFolium page = new MVNFolium();
      String n = entry.getName();
      page.setN(n);
      page.setId(result.getSigle() + "-pb-" + n);
      setFacs(entry, page, result);
      setOrder(page, entry);
      setBody(page, entry, result);
      result.addPages(page);
    }
    return result;
  }

  private void setFacs(ProjectEntry entry, MVNFolium page, MVNConversionResult result) {
    List<Facsimile> facsimiles = entry.getFacsimiles();
    if (facsimiles.isEmpty()) {
      result.addError(entry, "no facsimile");
    } else {
      if (facsimiles.size() > 1) {
        result.addError(entry, "multiple facsimiles, using first");
      }
      page.setFacs(facsimiles.get(0).getName());
    }
  }

  private void setBody(MVNFolium page, ProjectEntry entry, MVNConversionResult result) {
    String body = null;
    for (Transcription transcription : entry.getTranscriptions()) {
      if ("Diplomatic".equals(transcription.getTextLayer())) {
        body = transcription.getBody();
      } else {
        result.addError(entry, "incorrect textlayer found: " + transcription.getTextLayer());
      }
    }
    Log.info("body=[\n{}\n]", body);
    if (body == null) {
      result.addError(entry, "no Diplomatic textlayer");
    } else {
      page.setBody(toTei(body, entry, result));
    }
    Log.info("body=[\n{}\n]", page.getBody());
  }

  String toTei(String xml, ProjectEntry entry, MVNConversionResult result) {
    MVNTranscriptionVisitor visitor = new MVNTranscriptionVisitor(result.getSigle());

    final Document document = Document.createFromXml(xml, false);
    document.accept(visitor);

    final XmlContext c = visitor.getContext();
    String rawResult = c.getResult();
    for (String error : visitor.getErrors()) {
      result.addError(entry, error);
    }

    return rawResult;
  }

  private void setOrder(MVNFolium text, ProjectEntry projectEntry) {
    String order = projectEntry.getMetadataValue("order");
    if (StringUtils.isEmpty(order)) {
      text.setOrder("9999" + projectEntry.getName());
    } else {
      Integer orderValue = Integer.valueOf(order);
      text.setOrder(String.valueOf(1000 + orderValue));
    }
  }

  //	Er is besloten dat MVN-edities die worden voorbereid in eLaborate, gepubliceerd zullen worden op basis 
  //	van de XML-publicatiemodule voor de MVN. Het is dus nodig dat uit een eLaborate MVN-project XML 
  //	wordt afgeleid die overeenkomt met de indeling die gekozen is voor de MVN-delen die in XML worden 
  //	aangeleverd. Dit document beschrijft een mapping van de onderdelen van een eLaborate-project 
  //	(entries, hun metadata, transcripties en annotatie) naar TEI XML-elementen en attributen. 
  //	Deze mapping biedt een basis voor de conversie van de inhoud van een eLaborate-project naar 
  //	een TEI XML-bestand. Deze conversie moet herhaald kunnen worden uitgevoerd, zodat voor de editeur 
  //	de mogelijkheid ontstaat een draft van de editie te bekijken, op dezelfde manier zoals dat nu voor 
  //	gewone eLaborate publicaties geldt. 
  //	Onderdeel van de mapping is een validatie. Sommige annotaties kunnen alleen zinvol worden 
  //	omgezet naar XML wanneer ze aan bepaalde eisen voldoen. 
  //	Dit document is een eerste aanzet tot een goede werkwijze. Als tijdens de werkzaamheden blijkt 
  //	dat bepaalde annotaties bij de conversie of de publicatie problemen opleveren, zijn we nu nog in een 
  //	stadium waarin we kunnen besluiten de te gebruiken annotaties te veranderen. 

  //	Algemene opmerkingen
  //	Het eLaborate gebruikersinterface wordt voorzien van een optie om een draft publicatie te genereren. 
  //	Dan wordt een proces gestart dat bestaat uit drie stappen: validatie, XML-generatie en generatie van de 
  //	publicatie. Het is misschien wenselijk om de gebruiker de mogelijkheid te geven om te kiezen of hij alle 
  //	drie stappen wil uitvoeren. 
  //	Verder:
  //	- In elk geval voorlopig kunnen bij voorkeur alle bewerkers (niet alleen de projectleiders) de generatie 
  //	starten.
  //	- Eventuele foutboodschappen uit de validatie worden als een lijst aan de gebruiker getoond. 
  //	- De gegenereerde XML gaat niet alleen het publicatieproces in, maar gaat ook naar de gebruiker.
  //	Te genereren XML
  //	Alle elementen die worden aangemaakt behoren tot de tei-namespace (http://www.tei-c.org/ns/1.0). In 
  //	dit document wordt dat verder niet aangegeven. 
  //	Het gegenereerde XML document kan worden gevalideerd met het schema ... 
  //	Mapping
  //	Vooraf: Annotaties in eLaborate worden aangebracht op tekstpassages. In het normale geval heeft de 
  //	annotatie betrekking op de geannoteerde passage. Het kan ook voorkomen dat de annotatie een 
  //	tekstverschijnsel markeert dat op die plaats begint of eindigt. In dat geval is om de annotatie te kunnen 
  //	aanbrengen een speciaal teken in de tekst toegevoegd. Op het moment wordt gebruik gemaakt van ‘‡’ 
  //	of ‘¤’. Zulke annotaties noemen we hier puntannotaties.  

  //	Project
  //	Toelichting: De verschillende delen die in de MVN-reeks worden uitgegeven, worden 
  //	bewerkt in afzonderlijke eLaborate projecten. 
  //	Validatie: Er is alleen een diplomatische transcriptie aanwezig. 
  //	Conversie: Een TEI XML bestand volgens de indeling gegeven in de bijlage. 
  //	Hierin worden variabelen als volgt ingevuld:
  //	$titel: eLaborate Publication title
  //	$idno: eLaborate Project title
  //	$sigle: eLaborate Project name 

  //	Entry
  //	Toelichting: Elke entry komt overeen met een bladzijde in het manuscript 
  //	Validatie: Indien aanwezig bevat het metadataveld ‘order’ een integer waarde 
  //	Conversie: <pb/>
  //	De <pb/> wordt geplaatst voorafgaande aan alles wat betrekking heeft op de 
  //	inhoud van de betreffende pagina, dus in het bijzonder voorafgaand aan de 
  //	tekst die op die pagina begint. 
  //	Elke pb krijgt een n-attribuut (veld Name).
  //	Elke pb krijgt een xml:id attribuut (concatenatie sigle, ’pb’, n-attribuut)
  //	De volgorde van de entries wordt bepaald door
  //	- indien aanwezig, het metadataveld ‘order’
  //	- anders op entry Name (sorteren als string) 
  //	Elke <pb> wordt geplaatst op een nieuwe regel. 

  //	Nieuwe regel
  //	Toelichting: Een nieuwe regel in de transcriptie correspondeert met een nieuwe regel in het 
  //	manuscript 
  //	Validatie: Geen 
  //	Conversie: <lb/>
  //	De <lb/> wordt voor de regel geplaatst. Voor de eerste tekst op een foliumzijde 
  //	staat dus al een lb-element.  
  //	Elke lb krijgt een n-attribuut dat het regelnummer per pagina aangeeft. Het 
  //	wordt automatisch toegekend. Afwijkingen in de nummering worden 
  //	aangegeven met de annotatie mvn:regelnummering (blad). Zie aldaar.
  //	Elke lb krijgt een xml:id attribuut (concatenatie sigle, ’lb’, n-attribuut)
  //	Elke <lb> wordt geplaatst op een nieuwe regel. 

  //	mvn:afkorting
  //	Toelichting: Bevat een afkorting. De afgekorte vorm staat in de tekst, de opgeloste vorm in 
  //	Validatie: Geen 
  //	Conversie: <choice>
  //	de annotatie 
  //	    <abbr>[geannoteerde tekst]</abbr>
  //	    <expan>[inhoud annotatie]</expan>
  //	</choice>
  //	De inhoud van de annotatie bevat gecursiveerde tekst. Deze gecursiveerde 
  //	tekst overnemen binnen <ex>-elementen.
  //	Herman, doen we die cursivering in de opgeloste vorm, als we ook de 
  //	oorspronkelijke vorm geven? Daniël heeft het wel gedaan. 

  //	mvn:cijfers (romeins)
  //	Toelichting: Romeinse cijfers worden tbv de weergave als zodanig gecodeerd 
  //	Validatie: Geen 
  //	Conversie: <num type="roman">[geannoteerde tekst]</num> 

  //	mvn:defect
  //	Toelichting: Wordt gebruikt voor ontbrekende tekst, bijvoorbeeld doordat een deel van het 
  //	Validatie: Geen 
  //	Conversie: <gap/>
  //	manuscript is afgesneden of anderszins ontbreekt. 
  //	De geannoteerde tekst wordt genegeerd. 

  //	mvn:doorhaling
  //	Toelichting: Doorgehaalde of anderszins verwijderde tekst 
  //	Validatie: Geen 
  //	Conversie: <del>[geannoteerde tekst]</del> 

  //	mvn:gebruikersnotitie
  //	Toelichting: Notitie van een gebruiker van het manuscript. 
  //	Validatie: Geen 
  //	Conversie: <note type="user">[geannoteerde tekst]</note>
  //	Wanneer een mvn:gebruikersnotitie samenvalt met een 
  //	mvn:linkermargekolom of mvn:rechtermargekolom, kan één <note> worden 
  //	gemaakt met place right dan wel left en type=user. 

  //	mvn:incipit
  //	Toelichting: Geeft de eerste regel van een tekst 
  //	Validatie: Geen 
  //	Conversie: Geen 

  //	mvn:initiaal
  //	Toelichting: De geannoteerde tekst is een initiaal. De hoogte van de initiaal wordt 
  //	Validatie: De inhoud van de annotatie is een natuurlijk getal > 0 en < 20. 
  //	Conversie: <hi  rend="capitalsize[inhoud annotatie]>[geannoteerde tekst]</hi> 

  //	mvn:inspringen
  //	Toelichting: De tekst is niet links uitgelijnd. De puntannotatie heeft betrekking op de erop 
  //	Validatie: Het geannoteerde teken is ‘¤’ 
  //	Conversie: Rend-attribuut op voorafgaande <lb> met waarde "indent"
  //	aangegeven in de annotatie 
  //	volgende tekst.  
  //	PS: vooralsnog onderscheiden we geen verschillende niveaus van inspringing. 

  //	mvn:kolom
  //	Toelichting: Het begin van een kolom op een foliumzijde. Een puntannotatie. 
  //	Validatie: Het geannoteerde teken is ‘¤’ 
  //	Conversie: <cb/> 
  //	Prioriteit Laag 

  //	mvn:letters (zelfnoemfunctie)
  //	Toelichting: Letters worden gebruikt in zelfnoemfunctie 
  //	Validatie: Geen 
  //	Conversie: <mentioned>[geannoteerde tekst]</mentioned> 

  //	mvn:linkermargekolom
  //	Toelichting: Tekst staat in de linkermarge 
  //	Validatie: Geen 
  //	Conversie: <note place="margin-left" type="ms">[geannoteerde tekst]</note> 
  //	Zie ook mvn:gebruikersnotitie 

  //	mvn:metamark
  //	Toelichting: Geannoteerde bevat instructie over hoe de tekst moet worden gelezen 
  //	Validatie: Geen 
  //	Conversie: <metamark>[geannoteerde tekst]</metamark> 

  //	mvn:onderschrift
  //	Toelichting: Bevat een onderschrift bij de voorafgaande tekst 
  //	Validatie: Geen 
  //	Conversie: <closer>[geannoteerde tekst]</closer>
  //	Onderschriften worden niet meegenomen in de tekstregelnummering. 

  //	mvn:onleesbaar
  //	Toelichting: De geannoteerde tekst is onleesbaar 
  //	Validatie: Geen 
  //	Conversie: <gap></gap> 

  //	mvn:ophoging (rood)
  //	Toelichting: De geannoteerde tekst is rood opgehoogd 
  //	Validatie: Geen 
  //	Conversie: <hi rend="rubricated">[geannoteerde tekst]</hi>
  //	Wanneer een ander element aanwezig is waarop het rend-attribuut kan 
  //	worden aangebracht, is het <hi>- element niet noodzakelijk. 

  //	mvn:opschrift
  //	Toelichting: De geannoteerde tekst is een opschrift bij de erop volgende tekst 
  //	Validatie: Geen 
  //	Conversie: <head>[geannoteerde tekst]</head>
  //	Opschriften worden niet meegenomen in de tekstregelnummering. 

  //	mvn:paleografisch
  //	Toelichting: Bevat een paleografische annotatie van de editeur. 
  //	Validatie: Geen 
  //	Conversie: <note type="pc">[inhoud annotatie]</note>, te plaatsen na de geannoteerde 
  //	tekst.
  //	Cursieve passages binnen de inhoud van de annotatie worden gemarkeerd als 
  //	<mentioned>.
  //	Of moeten we de gehele geannoteerde passage kunnen markeren? 

  //	mvn:rechtermargekolom
  //	Toelichting: Tekst staat in de rechtermarge. 
  //	Het gaat hier alleen om markeringen, bijschriften en soortgelijke tekstjes, die 
  //	ook in de publicatie in de marge geplaatst moeten worden. Deze annotatie 
  //	wordt niet gebruikt voor toevoegingen aan de hoofdtekst. 
  //	Validatie: Geen 
  //	Conversie: <note place="margin-right" type="ms">[geannoteerde tekst]</note> 
  //	Zie ook mvn:gebruikersnotitie 

  //	mvn:regelnummering (blad)
  //	Toelichting: Geeft afwijkingen in de bladregelnummering aan. Een puntannotatie. 
  //	Validatie: Als de waarde geen geheel getal is, moet de volgende regel, als die er is, ook 
  //	Conversie: De annotatie overrulet het automatisch toegekende bladregelnummer. Het is 
  //	een mvn:regelnummering (blad)-annotatie hebben. 
  //	ook de basis voor de bladregelnummers van de volgende regels.
  //	Herman, wat doen we met de bladregelnummering in het geval van 
  //	gebruikersnotities zoals bij Daniël op 1v? Zijn dat inderdaad regel 1 en 2 waar 
  //	die notities staan? Of moeten we ook kunnen zeggen: regelnummering : geen 
  //	Ander voorbeeld: onderaan 2r
  //	Zie ook sectie Problemen: witregels 

  //	mvn:regelnummering (tekst)
  //	Toelichting: Geeft afwijkingen aan in de tekstregelnummering. Een puntannotatie. 
  //	Validatie: Als de waarde geen geheel getal is, moet de volgende regel, als die er is, ook 
  //	Conversie: Zie sectie Problemen: Poëzie 

  //	mvn:tekstbegin, mvn:teksteinde
  //	Toelichting: mvn:tekstbegin en mvn:teksteinde markeren begin en eind van een tekst of 
  //	Validatie: - de geannoteerde tekst is ‘‡’ 
  //	een mvn:regelnummering (tekst)-annotatie hebben. 
  //	een groep van teksten 
  //	- de inhoud van de annotatie bestaat uit het tekstnummer: groepjes van 
  //	tekens gescheiden door punten. De te gebruiken tekens zijn hoofdletters, 
  //	kleine letters en cijfers (bijvoorbeeld: A.17.4b); in het geval van 
  //	mvn:tekstbegin wordt het tekstnummer nog gevolgd door een puntkomma 
  //	en dan een door de editeur aan de tekst toegekende titel.
  //	- het aantal punten is maximaal 3
  //	- bij een beginmarkering moet een overeenkomstige eindmarkering bestaan 
  //	en omgekeerd
  //	- Als een tekst met nummer 1.2.3 bestaat, moet er ook een tekst 1.2 
  //	bestaan, die 1.2.3 omvat. Tekst 1.2 bevat in dat geval geen ‘eigen’ tekst: 
  //	alle erin bevatte tekst maakt deel uit van de deelteksten, met uitzondering 
  //	van opschriften, onderschriften, teksten in de marge en gebruikersnotities 
  //	- teksten kunnen niet overlappen (wel nesten) 
  //	Conversie: Op het diepste niveau (‘3’ uit 1.2.3):
  //	- mvn:tekstbegin: <text><body>
  //	- mvn:teksteind:</body> </text>
  //	Op hogere niveaus:
  //	- mvn:tekstbegin: <group>
  //	- mvn:teksteind:</group>
  //	De aan te maken text - en group-elementen krijgen een n-attribuut dat gelijk is 
  //	aan het tekstnummer. Ze krijgen een xml:id attribuut dat bestaat uit sigle 
  //	gevolgd door tekstnummer. 
  //	Binnen de <text> resp. <group> wordt als eerste element opgenomen:
  //	<head type="assigned">[toegekende titel]</head>
  //	Het teken waarop de annotatie geplaatst was, wordt genegeerd.
  //	Elke <text>, </text>, <group> en </group> worden geplaatst op een nieuwe 
  //	regel. 

  //	mvn:tekstkleur (rood)
  //	Toelichting: De geannoteerde tekst is in rood 
  //	Validatie: Geen 
  //	Conversie: <hi rend="rubric">[geannoteerde tekst]</hi>
  //	Wanneer een ander element aanwezig is waarop het rend-attribuut kan 
  //	worden aangebracht, is het <hi>- element niet noodzakelijk. 

  //	mvn:witregel
  //	Toelichting: Geeft een witregel in de tekst aan 
  //	Validatie: Het geannoteerde teken is ‘¤’ 
  //	Conversie: Genereer een extra <lb/>
  //	Maar zie sectie Problemen 

  //	Problemen en wensen
  //	Hoe gaan we om met poëzie en proza? We hebben in eLaborate geen manier om aan te duiden of een 
  //	tekst proza of poëzie is. Versregels of strofes bestaan dus niet, tenzij we daar annotaties voor 
  //	introduceren. Strofes zouden met puntannotaties moeten worden aangegeven, want overschrijden 
  //	foliogrenzen. Versregels zou je in theorie kunnen annoteren, maar dat is wel heel veel werk, en het 
  //	geheel wordt er niet overzichtelijker van. E.e.a. heeft vooral implicaties voor de versregelnummering. In 
  //	de XML plaatsen we die op het l-element. Wat doen we, nu we uit de eLaborate-omgeving eigenlijk geen 
  //	l-elementen kunnen afleiden? Gebruiken we dan een tweede attribuut op het lb-element om daarin de 
  //	versnummering op te slaan?  
  //	Wat doen we met witregels? Er bestaat een annotatietype voor. Maar in onze richtlijnen stellen we: 
  //	geef een witregel weer door een extra linebreak. Je zou een onderscheid kunnen maken naar witregels 
  //	die moeten meetellen in de bladregelnummering (extra <lb/>, in eLaborate termen een fysieke witregel) 
  //	en witregels waarbij dat niet moet (aan te geven via annotatie)? Daniël gebruikt de witregel-annotatie 
  //	voor wit dat gebruikersnotities en eigenlijk tekst scheidt. 
  //	Binnen eLaborate bestaat er nu weinig ruimte voor metadata op editieniveau. Er is geen manier om 
  //	goed aan te geven wie verantwoordelijk is voor de editie, en het sigle en de bronaanduiding worden op 
  //	een gekunstelde manier aangegeven. 
  //	Eventueel nog een annotatietype toevoegen voor bepaalde diakritische tekens. 

  //	Acties
  //	Daniël moet de project publicatie titel, project name en project titel goed invullen. 
  //	De toegekende teksttitels moeten nog worden aangebracht.

  //	Bijlage: XML template 
  //	<?xml version="1.0" encoding="utf-8"?>
  //	<TEI xmlns="http://www.tei-c.org/ns/1.0">
  //	    <teiHeader>
  //	        <fileDesc>
  //	            <titleStmt>
  //	                <title>$title</title>
  //	            </titleStmt>
  //	            <publicationStmt>
  //	                <p></p>
  //	            </publicationStmt>
  //	            <sourceDesc>
  //	                <msDesc>
  //	                    <msIdentifier>
  //	                        <idno>$idno</idno>
  //	                    </msIdentifier>
  //	                </msDesc>
  //	            </sourceDesc>
  //	        </fileDesc>
  //	    </teiHeader>
  //	    <text xml:id="$sigle">
  //	        <group>
  //	            <!-- hier de teksten -->
  //	         </group>
  //	</text>
  //	</TEI>
}
