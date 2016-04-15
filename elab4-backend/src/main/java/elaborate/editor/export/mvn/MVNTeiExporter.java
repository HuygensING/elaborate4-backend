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
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import nl.knaw.huygens.Log;
import nl.knaw.huygens.tei.Element;

public class MVNTeiExporter {
  private static final String NL = "\n";
  private final Context context = new Context();
  private final ParseResult parseresult;
  private final MVNConversionResult result;

  public MVNTeiExporter(ParseResult parseresult, MVNConversionResult result) {
    this.parseresult = parseresult;
    this.result = result;
  }

  public String export() {
    StringBuilder teiBuilder = new StringBuilder();
    Iterator<AnnotatedTextSegment> iterator = parseresult.getAnnotatedTextSegmentIterator();
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

  // opening annotations
  private void handleOpeningAnnotations(StringBuilder teiBuilder, AnnotatedTextSegment annotatedTextSegment) {
    Multimap<String, XmlAnnotation> openingAnnotationIndex = indexXmlAnnotations(annotatedTextSegment.getOpeningAnnotations());
    handleInspringen(openingAnnotationIndex.get(MVNAnnotationType.INSPRINGEN.getName()));
    handleRegelNummering(openingAnnotationIndex.get(MVNAnnotationType.REGELNUMMERING_BLAD.getName()));
    handleOpenTekst(teiBuilder, openingAnnotationIndex.get("tekst"));
    handleOpenPoezie(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.POEZIE.getName()));
    handleOpenAlinea(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.ALINEA.getName()));
    handlePageBreak(teiBuilder, openingAnnotationIndex.get("entry"));
    handleOpenOpschrift(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.OPSCHRIFT.getName()));
    handleOpenOnderschrift(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.ONDERSCHRIFT.getName()));
    handleOpenWitregel(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.WITREGEL.getName()));
    handleOpenRegel(teiBuilder, openingAnnotationIndex.get("l"));
    handleOpenLinkerMargeKolom(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.LINKERMARGEKOLOM.getName()));
    handleOpenRechterMargeKolom(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.RECHTERMARGEKOLOM.getName()));
    handleOpenSub(teiBuilder, openingAnnotationIndex.get("sub"));
    handleOpenSup(teiBuilder, openingAnnotationIndex.get("sup"));
    handleOpenOnduidelijk(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.ONDUIDELIJK.getName()));
    handleOpenDefect(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.DEFECT.getName()));
    handleOpenDefect(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.ONLEESBAAR.getName()));// Onleesbaar hetzelfde behandeld als defect
    handleOpenDoorhaling(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.DOORHALING.getName()));
    handleOpenDoorhaling(teiBuilder, openingAnnotationIndex.get("strike"));
    handleOpenAfkorting(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.AFKORTING.getName()));
    handleOpenItalic(teiBuilder, openingAnnotationIndex.get("i"));
    handleOpenTekstKleurRood(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.TEKSTKLEUR_ROOD.getName()));
    handleOpenTekstKleurRood(teiBuilder, openingAnnotationIndex.get("b"));
    handleOpenOphogingRood(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.OPHOGING_ROOD.getName()));
    handleOpenCijfers(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.CIJFERS.getName()));
    handleOpenLetters(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.LETTERS.getName()));
    handleOpenInitiaal(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.INITIAAL.getName()));
  }

  private void handleInspringen(Collection<XmlAnnotation> collection) {
    if (!collection.isEmpty()) {
      context.indent = true;
    }
  }

  private void handleRegelNummering(Collection<XmlAnnotation> collection) {
    if (!collection.isEmpty()) {
      XmlAnnotation xmlAnnotation = collection.iterator().next();
      String customLineNumber = xmlAnnotation.getAttributes().get("body");
      Log.info("{}: n='{}'", MVNAnnotationType.REGELNUMMERING_BLAD.getName(), customLineNumber);
      context.foliumLineNumber = customLineNumber;
    }
  }

  private void handleOpenLinkerMargeKolom(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    Element note = new Element("note").withAttribute("place", "margin-left").withAttribute("type", "ms");
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(openingTag(note));
    }
  }

  private void handleOpenRechterMargeKolom(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    Element note = new Element("note").withAttribute("place", "margin-right").withAttribute("type", "ms");
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(openingTag(note));
    }
  }

  private void handleOpenDoorhaling(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(openingTag("del"));
    }
  }

  private void handleOpenItalic(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(openingTag("ex"));
    }
  }

  private void handleOpenCijfers(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    Element num = new Element("num").withAttribute("type", "roman");
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(openingTag(num));
    }
  }

  private void handleOpenLetters(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(openingTag("mentioned"));
    }
  }

  private void handleOpenTekstKleurRood(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    Element hi = new Element("hi").withAttribute("rend", "rubric");
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(openingTag(hi));
    }
  }

  private void handleOpenOphogingRood(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    Element hi = new Element("hi").withAttribute("rend", "rubricated");
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(openingTag(hi));
    }
  }

  private void handleOpenTekst(StringBuilder teiBuilder, Collection<XmlAnnotation> collection) {
    for (XmlAnnotation xmlAnnotation : collection) {
      context.textLineNumber = 1;
      teiBuilder.append(NL);
      Map<String, String> attributes = xmlAnnotation.getAttributes();
      String tekstN = attributes.get("n");
      if (parseresult.isTextGroup(tekstN)) {
        Element group = new Element("group")//
            .withAttribute("n", tekstN)//
            .withAttribute("xml:id", attributes.get("xml:id"));
        teiBuilder.append(openingTag(group));

      } else {
        Element text = new Element("text")//
            .withAttribute("n", tekstN)//
            .withAttribute("xml:id", attributes.get("xml:id"));
        teiBuilder.append(openingTag(text)).append(openingTag("body"));
        //        openLineGroup(teiBuilder); // only for testing purposes
      }
    }
  }

  private void openLineGroup(StringBuilder teiBuilder) {
    context.inPoetry = true;
    teiBuilder.append(openingTag("lg"));
  }

  private void handleOpenPoezie(StringBuilder teiBuilder, Collection<XmlAnnotation> collection) {
    if (!collection.isEmpty()) {
      openLineGroup(teiBuilder);
    }
  }

  private void handleOpenAlinea(StringBuilder teiBuilder, Collection<XmlAnnotation> collection) {
    if (!collection.isEmpty()) {
      context.inParagraph = true;
      teiBuilder.append(openingTag("p"));
    }
  }

  private void handlePageBreak(StringBuilder teiBuilder, Collection<XmlAnnotation> collection) {
    if (!collection.isEmpty()) {
      XmlAnnotation xmlAnnotation = collection.iterator().next();
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

  private void handleOpenWitregel(StringBuilder teiBuilder, Collection<XmlAnnotation> supAnnotations) {
    for (XmlAnnotation xmlAnnotation : supAnnotations) {
      teiBuilder.append(milestoneTag("lb"));
    }
  }

  private void handleOpenRegel(StringBuilder teiBuilder, Collection<XmlAnnotation> collection) {
    if (!collection.isEmpty()) {
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

  private void handleOpenSub(StringBuilder teiBuilder, Collection<XmlAnnotation> supAnnotations) {
    Element hi = new Element("hi").withAttribute("rend", "subscript");
    for (XmlAnnotation xmlAnnotation : supAnnotations) {
      teiBuilder.append(openingTag(hi));
    }
  }

  private void handleOpenSup(StringBuilder teiBuilder, Collection<XmlAnnotation> supAnnotations) {
    Element hi = new Element("hi").withAttribute("rend", "superscript");
    for (XmlAnnotation xmlAnnotation : supAnnotations) {
      teiBuilder.append(openingTag(hi));
    }
  }

  private void handleOpenOnduidelijk(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    Element hi = new Element("unclear");
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(openingTag(hi));
    }
  }

  private void handleOpenDefect(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
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

  private void handleOpenOpschrift(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    openHeadOrCloser(teiBuilder, xmlAnnotations, "head");
  }

  private void openHeadOrCloser(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, String name) {
    Element head = new Element(name);
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(openingTag(head));
      context.countAsTextLine = true;
    }
  }

  private void handleOpenOnderschrift(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    openHeadOrCloser(teiBuilder, xmlAnnotations, "closer");
  }

  private void handleOpenAfkorting(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
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

  private void handleOpenInitiaal(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      String body = xmlAnnotation.getAttributes().get("body");
      if (body != null) {
        String capitalsize = body.trim();
        Integer size = 0;
        if (StringUtils.isNumeric(capitalsize)) {
          size = Integer.valueOf(capitalsize);
          if (size < 1 || size > 19) {
            addValidationError(body);
          }

        } else {
          addValidationError(body);
        }

        Element hi = new Element("hi").withAttribute("rend", "capitalsize" + capitalsize);
        teiBuilder.append(openingTag(hi));
      }
    }
  }

  // closing annotations
  private void handleClosingAnnotations(StringBuilder teiBuilder, AnnotatedTextSegment annotatedTextSegment) {
    Multimap<String, XmlAnnotation> closingAnnotationIndex = indexXmlAnnotations(annotatedTextSegment.getClosingAnnotations());
    handleCloseInitiaal(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.INITIAAL.getName()));
    handleCloseLetters(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.LETTERS.getName()));
    handleCloseCijfers(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.CIJFERS.getName()));
    handleCloseOphogingRood(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.OPHOGING_ROOD.getName()));
    handleCloseTekstKleurRood(teiBuilder, closingAnnotationIndex.get("b"));
    handleCloseTekstKleurRood(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.TEKSTKLEUR_ROOD.getName()));
    handleCloseDoorhaling(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.DOORHALING.getName()));
    handleCloseDoorhaling(teiBuilder, closingAnnotationIndex.get("strike"));
    handleCloseItalic(teiBuilder, closingAnnotationIndex.get("i"));
    handleCloseOnduidelijk(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.ONDUIDELIJK.getName()));
    handleCloseSup(teiBuilder, closingAnnotationIndex.get("sup"));
    handleCloseSub(teiBuilder, closingAnnotationIndex.get("sub"));
    handleCloseRegel(teiBuilder, closingAnnotationIndex);
    handleCloseAlinea(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.ALINEA.getName()));
    handleClosePoezie(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.POEZIE.getName()));
    handleCloseOpschrift(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.OPSCHRIFT.getName()));
    handleCloseOnderschrift(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.ONDERSCHRIFT.getName()));
    handleClosePaleo(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.PALEOGRAFISCH.getName()));
    handleCloseLinkerMargeKolom(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.LINKERMARGEKOLOM.getName()));
    handleCloseRechterMargeKolom(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.RECHTERMARGEKOLOM.getName()));
    handleCloseTekst(teiBuilder, closingAnnotationIndex.get("tekst"));
  }

  private void handleCloseInitiaal(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(closingTag("hi"));
    }
  }

  private void handleCloseLetters(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(closingTag("mentioned"));
    }
  }

  private void handleCloseCijfers(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(closingTag("num"));
    }
  }

  private void handleCloseDoorhaling(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(closingTag("del"));
    }
  }

  private void handleCloseItalic(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(closingTag("ex"));
    }
  }

  private void handleCloseLinkerMargeKolom(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(closingTag("note"));
    }
  }

  private void handleCloseRechterMargeKolom(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(closingTag("note"));
    }
  }

  private void handleCloseOphogingRood(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(closingTag("hi"));
    }
  }

  private void handleCloseTekstKleurRood(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(closingTag("hi"));
    }
  }

  private void handleCloseOnduidelijk(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(closingTag("unclear"));
    }
  }

  private void handleCloseOnderschrift(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    closeHeadOrCloser(teiBuilder, xmlAnnotations, "closer");
  }

  private void closeHeadOrCloser(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, String name) {
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      teiBuilder.append(closingTag(name));
      context.countAsTextLine = true;
    }
  }

  private void handleCloseOpschrift(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations) {
    closeHeadOrCloser(teiBuilder, xmlAnnotations, "head");
  }

  private void handleCloseSub(StringBuilder teiBuilder, Collection<XmlAnnotation> supAnnotations) {
    for (XmlAnnotation xmlAnnotation : supAnnotations) {
      teiBuilder.append(closingTag("hi"));
    }
  }

  private void handleCloseSup(StringBuilder teiBuilder, Collection<XmlAnnotation> supAnnotations) {
    for (XmlAnnotation xmlAnnotation : supAnnotations) {
      teiBuilder.append(closingTag("hi"));
    }
  }

  private void handleCloseRegel(StringBuilder teiBuilder, Multimap<String, XmlAnnotation> closingAnnotationIndex) {
    if (closingAnnotationIndex.containsKey("l")) {
      context.incrementFoliumLineNumber();
      context.incrementTextLineNumber();
      if (context.inPoetry) {
        teiBuilder.append(closingTag("l"));
      }
    }
  }

  private void handleCloseAlinea(StringBuilder teiBuilder, Collection<XmlAnnotation> collection) {
    if (!collection.isEmpty()) {
      context.inParagraph = false;
      teiBuilder.append(closingTag("p"));
    }
  }

  private void handleClosePoezie(StringBuilder teiBuilder, Collection<XmlAnnotation> collection) {
    if (!collection.isEmpty()) {
      context.inPoetry = false;
      teiBuilder.append(closingTag("lg"));
    }
  }

  private void handleClosePaleo(StringBuilder teiBuilder, Collection<XmlAnnotation> collection) {
    for (XmlAnnotation xmlAnnotation : collection) {
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

  private void handleCloseTekst(StringBuilder teiBuilder, Collection<XmlAnnotation> collection) {
    List<XmlAnnotation> xmlAnnotations = Lists.newArrayList(collection);
    Collections.reverse(xmlAnnotations);
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      Map<String, String> attributes = xmlAnnotation.getAttributes();
      teiBuilder.append(NL);
      if (parseresult.isTextGroup(attributes.get("n"))) {
        teiBuilder.append(closingTag("group"));

      } else {
        //        closeLineGroup(teiBuilder); // only for testing purposes
        teiBuilder.append(closingTag("body")).append(closingTag("text"));
      }
      teiBuilder.append(NL);
    }
  }

  //

  private void closeLineGroup(StringBuilder teiBuilder) {
    context.inPoetry = false;
    teiBuilder.append(closingTag("lg"));
  }

  private Multimap<String, XmlAnnotation> indexXmlAnnotations(Collection<XmlAnnotation> annotations) {
    Multimap<String, XmlAnnotation> index = ArrayListMultimap.<String, XmlAnnotation> create();
    for (XmlAnnotation xmlAnnotation : annotations) {
      index.put(xmlAnnotation.getName(), xmlAnnotation);
    }
    return index;
  }

  private void addOptionalAttribute(Element pb, String key, Map<String, String> entryAttributes) {
    String value = entryAttributes.get(key);
    if (value != null && !"null".equals(value)) {
      pb.setAttribute(key, entryAttributes.get(key));
    }
  }

  private static class Context {
    public String currentEntryId;
    public boolean indent = false;
    public boolean countAsTextLine = true;
    public boolean inParagraph = false;
    public boolean inPoetry = false;
    public int textLineNumber = 1;
    public String foliumLineNumber = "1";
    public String foliumId = "";
    public String text = "";

    public void incrementFoliumLineNumber() {
      Integer asInt = Integer.valueOf(foliumLineNumber.replaceAll("[^0-9]", ""));
      Integer next = asInt + 1;
      foliumLineNumber = String.valueOf(next);
    }

    public void incrementTextLineNumber() {
      if (countAsTextLine) {
        textLineNumber++;
      }
    }
  }

  private void addValidationError(final String body) {
    addError(MVNAnnotationType.INITIAAL, "De inhoud van de annotatie ('" + body + "') is geen natuurlijk getal > 0 en < 20.");
  }

  private void addError(MVNAnnotationType type, String error) {
    result.addError(context.currentEntryId, type.getName() + " : " + error);
  }
}
