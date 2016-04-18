package elaborate.editor.export.mvn;

import static elaborate.util.XmlUtil.closingTag;
import static elaborate.util.XmlUtil.milestoneTag;
import static elaborate.util.XmlUtil.openingTag;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import nl.knaw.huygens.Log;
import nl.knaw.huygens.tei.Element;

public class MVNTeiExporter {
  private static final String NL = "\n";
  private static final String XML_ID = "xml:id";

  private final Context context = new Context();

  public MVNTeiExporter(ParseResult parseresult, MVNConversionResult result) {
    context.parseresult = parseresult;
    context.result = result;
  }

  public String export() {
    StringBuilder teiBuilder = new StringBuilder();
    Iterator<AnnotatedTextSegment> iterator = context.parseresult.getAnnotatedTextSegmentIterator();
    while (iterator.hasNext()) {
      AnnotatedTextSegment annotatedTextSegment = iterator.next();
      context.text = annotatedTextSegment.getText();
      Log.info("textSegment= {}", annotatedTextSegment);
      handleOpeningAnnotations(teiBuilder, annotatedTextSegment);
      teiBuilder.append(context.text);
      handleClosingAnnotations(teiBuilder, annotatedTextSegment);
    }
    return teiBuilder.toString();
  }

  private static final List<AnnotationHandler> ANNOTATION_HANDLERS = ImmutableList.<AnnotationHandler> builder()//
      .add(new InspringenHandler())//
      .add(new RegelnummeringBladHandler())//
      .add(new PaleoHandler())//
      .add(new TekstHandler())//
      .add(new PoezieHandler())//
      .add(new AlineaHandler())//
      .add(new EntryHandler())//
      .add(new OpschriftHandler())//
      .add(new OnderschriftHandler())//
      .add(new WitregelHandler())//
      .add(new LHandler())//
      .add(new LinkerMargeKolomHandler())//
      .add(new RechterMargeKolomHandler())//
      .add(new SubHandler())//
      .add(new SupHandler())//
      .add(new OnduidelijkHandler())//
      .add(new DefectHandler())//
      .add(new DoorhalingHandler())//
      .add(new AfkortingHandler())//
      .add(new ItalicHandler())//
      .add(new TekstKleurRoodHandler())//
      .add(new OphogingRoodHandler())//
      .add(new CijfersHandler())//
      .add(new LettersHandler())//
      .add(new InitiaalHandler())//
      .build();

  // opening annotations
  private void handleOpeningAnnotations(StringBuilder teiBuilder, AnnotatedTextSegment annotatedTextSegment) {
    Multimap<String, XmlAnnotation> openingAnnotationIndex = indexXmlAnnotations(annotatedTextSegment.getOpeningAnnotations());
    for (AnnotationHandler annotationHandler : ANNOTATION_HANDLERS) {
      List<XmlAnnotation> xmlAnnotations = filterRelevantAnnotations(openingAnnotationIndex, annotationHandler);
      annotationHandler.onOpenAnnotation(teiBuilder, xmlAnnotations, context);
    }

    //    handleInspringen(openingAnnotationIndex.get(MVNAnnotationType.INSPRINGEN.getName()));
    //    handleRegelNummering(openingAnnotationIndex.get(MVNAnnotationType.REGELNUMMERING_BLAD.getName()));
    //    handleOpenTekst(teiBuilder, openingAnnotationIndex.get("tekst"));
    //    handleOpenPoezie(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.POEZIE.getName()));
    //    handleOpenAlinea(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.ALINEA.getName()));
    //    handlePageBreak(teiBuilder, openingAnnotationIndex.get("entry"));
    //    handleOpenOpschrift(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.OPSCHRIFT.getName()));
    //    handleOpenOnderschrift(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.ONDERSCHRIFT.getName()));
    //    handleOpenWitregel(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.WITREGEL.getName()));
    //    handleOpenRegel(teiBuilder, openingAnnotationIndex.get("l"));
    //    handleOpenLinkerMargeKolom(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.LINKERMARGEKOLOM.getName()));
    //    handleOpenRechterMargeKolom(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.RECHTERMARGEKOLOM.getName()));
    //    handleOpenSub(teiBuilder, openingAnnotationIndex.get("sub"));
    //    handleOpenSup(teiBuilder, openingAnnotationIndex.get("sup"));
    //    handleOpenOnduidelijk(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.ONDUIDELIJK.getName()));
    //    handleOpenDefect(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.DEFECT.getName()));
    //    handleOpenDefect(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.ONLEESBAAR.getName()));// Onleesbaar hetzelfde behandeld als defect
    //    handleOpenDoorhaling(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.DOORHALING.getName()));
    //    handleOpenDoorhaling(teiBuilder, openingAnnotationIndex.get("strike"));
    //    handleOpenAfkorting(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.AFKORTING.getName()));
    //    handleOpenItalic(teiBuilder, openingAnnotationIndex.get("i"));
    //    handleOpenTekstKleurRood(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.TEKSTKLEUR_ROOD.getName()));
    //    handleOpenTekstKleurRood(teiBuilder, openingAnnotationIndex.get("b"));
    //    handleOpenOphogingRood(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.OPHOGING_ROOD.getName()));
    //    handleOpenCijfers(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.CIJFERS.getName()));
    //    handleOpenLetters(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.LETTERS.getName()));
    //    handleOpenInitiaal(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.INITIAAL.getName()));
  }

  // closing annotations
  private void handleClosingAnnotations(StringBuilder teiBuilder, AnnotatedTextSegment annotatedTextSegment) {
    Multimap<String, XmlAnnotation> closingAnnotationIndex = indexXmlAnnotations(annotatedTextSegment.getClosingAnnotations());
    for (AnnotationHandler annotationHandler : Lists.reverse(ANNOTATION_HANDLERS)) {
      List<XmlAnnotation> xmlAnnotations = filterRelevantAnnotations(closingAnnotationIndex, annotationHandler);
      annotationHandler.onCloseAnnotation(teiBuilder, xmlAnnotations, context);
    }
    //    handleCloseInitiaal(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.INITIAAL.getName()));
    //    handleCloseLetters(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.LETTERS.getName()));
    //    handleCloseCijfers(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.CIJFERS.getName()));
    //    handleCloseOphogingRood(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.OPHOGING_ROOD.getName()));
    //    handleCloseTekstKleurRood(teiBuilder, closingAnnotationIndex.get("b"));
    //    handleCloseTekstKleurRood(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.TEKSTKLEUR_ROOD.getName()));
    //    handleCloseDoorhaling(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.DOORHALING.getName()));
    //    handleCloseDoorhaling(teiBuilder, closingAnnotationIndex.get("strike"));
    //    handleCloseItalic(teiBuilder, closingAnnotationIndex.get("i"));
    //    handleCloseOnduidelijk(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.ONDUIDELIJK.getName()));
    //    handleCloseSup(teiBuilder, closingAnnotationIndex.get("sup"));
    //    handleCloseSub(teiBuilder, closingAnnotationIndex.get("sub"));
    //    handleCloseRegel(teiBuilder, closingAnnotationIndex);
    //    handleCloseAlinea(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.ALINEA.getName()));
    //    handleClosePoezie(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.POEZIE.getName()));
    //    handleCloseOpschrift(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.OPSCHRIFT.getName()));
    //    handleCloseOnderschrift(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.ONDERSCHRIFT.getName()));
    //    handleClosePaleo(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.PALEOGRAFISCH.getName()));
    //    handleCloseLinkerMargeKolom(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.LINKERMARGEKOLOM.getName()));
    //    handleCloseRechterMargeKolom(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.RECHTERMARGEKOLOM.getName()));
    //    handleCloseTekst(teiBuilder, closingAnnotationIndex.get("tekst"));
  }

  private List<XmlAnnotation> filterRelevantAnnotations(Multimap<String, XmlAnnotation> closingAnnotationIndex, AnnotationHandler annotationHandler) {
    List<String> relevantTags = annotationHandler.relevantTags();
    List<XmlAnnotation> xmlAnnotations = Lists.newArrayList();
    for (String tagName : relevantTags) {
      xmlAnnotations.addAll(closingAnnotationIndex.get(tagName));
    }
    return xmlAnnotations;
  }

  private static class InspringenHandler extends DefaultAnnotationHandler {
    public InspringenHandler() {
      super(MVNAnnotationType.INSPRINGEN);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      if (!xmlAnnotations.isEmpty()) {
        context.indent = true;
      }
    }
  }

  private static class RegelnummeringBladHandler extends DefaultAnnotationHandler {
    public RegelnummeringBladHandler() {
      super(MVNAnnotationType.REGELNUMMERING_BLAD);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      if (!xmlAnnotations.isEmpty()) {
        XmlAnnotation xmlAnnotation = xmlAnnotations.iterator().next();
        String customLineNumber = xmlAnnotation.getAttributes().get("body");
        Log.info("{}: n='{}'", MVNAnnotationType.REGELNUMMERING_BLAD.getName(), customLineNumber);
        context.foliumLineNumber = customLineNumber;
      }
    }

  }

  private static class TekstHandler extends DefaultAnnotationHandler {
    public TekstHandler() {
      super("tekst");
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        context.textLineNumber = 1;
        teiBuilder.append(NL);
        Map<String, String> attributes = xmlAnnotation.getAttributes();
        String tekstN = attributes.get("n");
        if (context.parseresult.isTextGroup(tekstN)) {
          Element group = new Element("group")//
              .withAttribute("n", tekstN)//
              .withAttribute(XML_ID, attributes.get(XML_ID));
          teiBuilder.append(openingTag(group));

        } else {
          Element text = new Element("text")//
              .withAttribute("n", tekstN)//
              .withAttribute(XML_ID, attributes.get(XML_ID));
          teiBuilder.append(openingTag(text)).append(openingTag("body"));
          //        openLineGroup(teiBuilder); // only for testing purposes
        }
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> collection, Context context) {
      List<XmlAnnotation> xmlAnnotations = Lists.newArrayList(collection);
      Collections.reverse(xmlAnnotations);
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        Map<String, String> attributes = xmlAnnotation.getAttributes();
        teiBuilder.append(NL);
        if (context.parseresult.isTextGroup(attributes.get("n"))) {
          teiBuilder.append(closingTag("group"));

        } else {
          //        closeLineGroup(teiBuilder, context); // only for testing purposes
          teiBuilder.append(closingTag("body")).append(closingTag("text"));
        }
        teiBuilder.append(NL);
      }
    }

    private void closeLineGroup(StringBuilder teiBuilder, Context context) {
      context.inPoetry = false;
      teiBuilder.append(closingTag("lg"));
    }

  }

  private static class PoezieHandler extends DefaultAnnotationHandler {
    private static final String LG = "lg";

    public PoezieHandler() {
      super(MVNAnnotationType.POEZIE);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      if (!xmlAnnotations.isEmpty()) {
        openLineGroup(teiBuilder, context);
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      if (!xmlAnnotations.isEmpty()) {
        context.inPoetry = false;
        teiBuilder.append(closingTag(LG));
      }
    }

    private static void openLineGroup(StringBuilder teiBuilder, Context context) {
      context.inPoetry = true;
      teiBuilder.append(openingTag(LG));
    }

  }

  private static class AlineaHandler extends DefaultAnnotationHandler {
    public AlineaHandler() {
      super(MVNAnnotationType.ALINEA);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      if (!xmlAnnotations.isEmpty()) {
        context.inParagraph = true;
        teiBuilder.append(openingTag("p"));
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      if (!xmlAnnotations.isEmpty()) {
        context.inParagraph = false;
        teiBuilder.append(closingTag("p"));
      }
    }
  }

  private static class EntryHandler extends DefaultAnnotationHandler {
    public EntryHandler() {
      super("entry");
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      if (!xmlAnnotations.isEmpty()) {
        XmlAnnotation xmlAnnotation = xmlAnnotations.iterator().next();
        context.foliumLineNumber = "1";
        Map<String, String> entryAttributes = xmlAnnotation.getAttributes();
        context.foliumId = entryAttributes.get("xml:id");
        context.currentEntryId = entryAttributes.get("_entryId");
        Element pb = new Element("pb");
        addOptionalAttribute(pb, "xml:id", entryAttributes);
        addOptionalAttribute(pb, "n", entryAttributes);
        addOptionalAttribute(pb, "facs", entryAttributes);
        teiBuilder//
            .append(NL)//
            .append(milestoneTag(pb));
      }
    }
  }

  private static class OpschriftHandler extends DefaultAnnotationHandler {
    private static final String HEAD = "head";

    public OpschriftHandler() {
      super(MVNAnnotationType.OPSCHRIFT);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      openHeadOrCloser(teiBuilder, xmlAnnotations, HEAD, context);
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      closeHeadOrCloser(teiBuilder, xmlAnnotations, HEAD, context);
    }
  }

  private static class OnderschriftHandler extends DefaultAnnotationHandler {
    private static final String CLOSER = "closer";

    public OnderschriftHandler() {
      super(MVNAnnotationType.ONDERSCHRIFT);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      openHeadOrCloser(teiBuilder, xmlAnnotations, CLOSER, context);
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      closeHeadOrCloser(teiBuilder, xmlAnnotations, CLOSER, context);
    }
  }

  private static void openHeadOrCloser(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, String name, Context context) {
    Element head = new Element(name);
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(openingTag(head));
      context.countAsTextLine = true;
    }
  }

  private static void closeHeadOrCloser(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, String name, Context context) {
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(closingTag(name));
      context.countAsTextLine = true;
    }
  }

  private static class WitregelHandler extends DefaultAnnotationHandler {
    public WitregelHandler() {
      super(MVNAnnotationType.WITREGEL);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(milestoneTag("lb"));
      }
    }
  }

  private static class LHandler extends DefaultAnnotationHandler {
    public LHandler() {
      super("l");
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      if (!xmlAnnotations.isEmpty()) {
        String id = context.foliumId + "-lb-" + context.foliumLineNumber;
        Element lb = new Element("lb").withAttribute("n", String.valueOf(context.foliumLineNumber)).withAttribute("xml:id", id);
        if (context.indent) {
          lb.setAttribute("rend", "indent");
        }
        teiBuilder.append(NL).append(milestoneTag(lb));
        if (context.inPoetry) {
          String lId = context.foliumId + "-l-" + context.textLineNumber;
          Element l = new Element("l").withAttribute("n", String.valueOf(context.textLineNumber)).withAttribute("xml:id", lId);
          teiBuilder.append(openingTag(l));
        }
        context.indent = false;
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      //      if (closingAnnotationIndex.containsKey("l")) {
      if (!xmlAnnotations.isEmpty()) {
        context.incrementFoliumLineNumber();
        context.incrementTextLineNumber();
        if (context.inPoetry) {
          teiBuilder.append(closingTag("l"));
        }
      }
    }
  }

  private static class LinkerMargeKolomHandler extends DefaultAnnotationHandler {
    private static final Element note = new Element("note").withAttribute("place", "margin-left").withAttribute("type", "ms");

    public LinkerMargeKolomHandler() {
      super(MVNAnnotationType.LINKERMARGEKOLOM);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(openingTag(note));
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(closingTag(note));
      }
    }
  }

  private static class RechterMargeKolomHandler extends DefaultAnnotationHandler {
    private static final Element note = new Element("note").withAttribute("place", "margin-right").withAttribute("type", "ms");

    public RechterMargeKolomHandler() {
      super(MVNAnnotationType.RECHTERMARGEKOLOM);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(openingTag(note));
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(closingTag(note));
      }
    }
  }

  private static class SubHandler extends DefaultAnnotationHandler {
    Element hi = new Element("hi").withAttribute("rend", "subscript");

    public SubHandler() {
      super("sub");
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(openingTag(hi));
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(closingTag(hi));
      }
    }

  }

  private static class SupHandler extends DefaultAnnotationHandler {
    Element hi = new Element("hi").withAttribute("rend", "superscript");

    public SupHandler() {
      super("sup");
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(openingTag(hi));
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(closingTag(hi));
      }
    }

  }

  private static class OnduidelijkHandler extends DefaultAnnotationHandler {
    Element element = new Element("unclear");

    public OnduidelijkHandler() {
      super(MVNAnnotationType.ONDUIDELIJK);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(openingTag(element));
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(closingTag(element));
      }
    }

  }

  private static class DefectHandler extends DefaultAnnotationHandler {
    public DefectHandler() {
      super(MVNAnnotationType.DEFECT, MVNAnnotationType.ONLEESBAAR);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        Element gap = new Element("gap");
        String annotationBody = xmlAnnotation.getAttributes().get("body");
        if (StringUtils.isNumeric(annotationBody)) {
          gap.setAttribute("unit", "chars");
          gap.setAttribute("quantity", annotationBody);
        }
        teiBuilder.append(milestoneTag(gap));
        context.text = "";// ignore annotated text 
      }
    }

  }

  private static class DoorhalingHandler extends DefaultAnnotationHandler {
    private static final String DEL = "del";

    public DoorhalingHandler() {
      super(MVNAnnotationType.DOORHALING, "strike");
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(openingTag(DEL));
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(closingTag(DEL));
      }
    }

  }

  private static class AfkortingHandler extends DefaultAnnotationHandler {
    public AfkortingHandler() {
      super(MVNAnnotationType.AFKORTING);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        String abbr = context.text;
        String openSubstitute = "#i0#";
        String closeSubstitute = "#i1#";
        String expan = xmlAnnotation.getAttributes().get("body")//
            .replaceAll("&nbsp;", " ")//
            .replaceAll("<i[^>]*>", openSubstitute)//
            .replace("</i>", closeSubstitute)//
            .replaceAll("<[^>]*>", "")//
            .replace(openSubstitute, "<ex>")//
            .replace(closeSubstitute, "</ex>")//
            ;

        teiBuilder//
            .append(openingTag("choice"))//
            .append(openingTag("abbr"))//
            .append(abbr)//
            .append(closingTag("abbr"))//
            .append(openingTag("expan"))//
            .append(expan)//
            .append(closingTag("expan"))//
            .append(closingTag("choice"));
        context.text = "";
      }
    }

  }

  private static class ItalicHandler extends DefaultAnnotationHandler {
    private static final String EX = "ex";

    public ItalicHandler() {
      super("i");
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(openingTag(EX));
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(closingTag(EX));
      }
    }

  }

  private static class TekstKleurRoodHandler extends DefaultAnnotationHandler {
    Element hi = new Element("hi").withAttribute("rend", "rubric");

    public TekstKleurRoodHandler() {
      super(MVNAnnotationType.TEKSTKLEUR_ROOD, "b");
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(openingTag(hi));
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(closingTag(hi));
      }
    }

  }

  private static class OphogingRoodHandler extends DefaultAnnotationHandler {
    Element hi = new Element("hi").withAttribute("rend", "rubricated");

    public OphogingRoodHandler() {
      super(MVNAnnotationType.OPHOGING_ROOD);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(openingTag(hi));
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(closingTag(hi));
      }
    }

  }

  private static class CijfersHandler extends DefaultAnnotationHandler {
    Element num = new Element("num").withAttribute("type", "roman");

    public CijfersHandler() {
      super(MVNAnnotationType.CIJFERS);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(openingTag(num));
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(closingTag(num));
      }
    }

  }

  private static class LettersHandler extends DefaultAnnotationHandler {
    private static final String MENTIONED = "mentioned";

    public LettersHandler() {
      super(MVNAnnotationType.LETTERS);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(openingTag(MENTIONED));
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(closingTag(MENTIONED));
      }
    }

  }

  private static class InitiaalHandler extends DefaultAnnotationHandler {
    public InitiaalHandler() {
      super(MVNAnnotationType.INITIAAL);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        String body = xmlAnnotation.getAttributes().get("body");
        if (body != null) {
          String capitalsize = body.trim();
          Integer size = 0;
          if (StringUtils.isNumeric(capitalsize)) {
            size = Integer.valueOf(capitalsize);
            if (size < 1 || size > 19) {
              addValidationError(body, context);
            }

          } else {
            addValidationError(body, context);
          }

          Element hi = new Element("hi").withAttribute("rend", "capitalsize" + capitalsize);
          teiBuilder.append(openingTag(hi));
        }
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        teiBuilder.append(closingTag("hi"));
      }
    }

    public void addValidationError(final String body, Context context) {
      context.addError(MVNAnnotationType.INITIAAL, "De inhoud van de annotatie ('" + body + "') is geen natuurlijk getal > 0 en < 20.");
    }

  }

  private static class PaleoHandler extends DefaultAnnotationHandler {
    public PaleoHandler() {
      super(MVNAnnotationType.PALEOGRAFISCH);
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        Element note = new Element("note").withAttribute("type", "pc");
        String openSubstitute = "#i0#";
        String closeSubstitute = "#i1#";
        String content = xmlAnnotation.getAttributes().get("body")//
            .replaceAll("&nbsp;", " ")//
            .replaceAll("<i[^>]*>", openSubstitute)//
            .replace("</i>", closeSubstitute)//
            .replaceAll("<[^>]*>", "")//
            .replace(openSubstitute, "<mentioned>")//
            .replace(closeSubstitute, "</mentioned>")//
            ;
        teiBuilder//
            .append(openingTag(note))//
            .append(content)//
            .append(closingTag(note));
      }
    }
  }

  //

  private Multimap<String, XmlAnnotation> indexXmlAnnotations(Collection<XmlAnnotation> annotations) {
    Multimap<String, XmlAnnotation> index = ArrayListMultimap.<String, XmlAnnotation> create();
    for (XmlAnnotation xmlAnnotation : annotations) {
      index.put(xmlAnnotation.getName(), xmlAnnotation);
    }
    return index;
  }

  private static void addOptionalAttribute(Element pb, String key, Map<String, String> entryAttributes) {
    String value = entryAttributes.get(key);
    if (value != null && !"null".equals(value)) {
      pb.setAttribute(key, entryAttributes.get(key));
    }
  }

}
