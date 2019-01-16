package elaborate.editor.export.mvn;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2019 Huygens ING
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
      context.assertTextIsInValidScope();
      teiBuilder.append(context.text);
      handleClosingAnnotations(teiBuilder, annotatedTextSegment);
    }
    return teiBuilder.toString();
  }

  private static final List<AnnotationHandler> ANNOTATION_HANDLERS = ImmutableList.<AnnotationHandler> builder()//
      .add(new InspringenHandler(MVNAnnotationType.INSPRINGEN))//
      .add(new RegelnummeringBladHandler(MVNAnnotationType.REGELNUMMERING_BLAD))//
      .add(new PaleoHandler(MVNAnnotationType.PALEOGRAFISCH))//
      .add(new TekstHandler("tekst"))//
      .add(new PoezieHandler(MVNAnnotationType.POEZIE))//
      .add(new AlineaHandler(MVNAnnotationType.ALINEA))//
      .add(new EntryHandler("entry"))//
      .add(new OpschriftHandler(MVNAnnotationType.OPSCHRIFT))//
      .add(new OnderschriftHandler(MVNAnnotationType.ONDERSCHRIFT))//
      .add(new WitregelHandler(MVNAnnotationType.WITREGEL))//
      .add(new LHandler("l"))//
      .add(new LinkerMargeKolomHandler(MVNAnnotationType.LINKERMARGEKOLOM))//
      .add(new RechterMargeKolomHandler(MVNAnnotationType.RECHTERMARGEKOLOM))//
      .add(new SubHandler("sub"))//
      .add(new SupHandler("sup"))//
      .add(new OnduidelijkHandler(MVNAnnotationType.ONDUIDELIJK))//
      .add(new DefectHandler(MVNAnnotationType.DEFECT, MVNAnnotationType.ONLEESBAAR))//
      .add(new DoorhalingHandler(MVNAnnotationType.DOORHALING, "strike"))//
      .add(new AfkortingHandler(MVNAnnotationType.AFKORTING))//
      .add(new ItalicHandler("i"))//
      .add(new TekstKleurRoodHandler(MVNAnnotationType.TEKSTKLEUR_ROOD, "b"))//
      .add(new OphogingRoodHandler(MVNAnnotationType.OPHOGING_ROOD))//
      .add(new CijfersHandler(MVNAnnotationType.CIJFERS))//
      .add(new LettersHandler(MVNAnnotationType.LETTERS))//
      .add(new InitiaalHandler(MVNAnnotationType.INITIAAL))//
      .add(new KolomHandler(MVNAnnotationType.KOLOM))//
      .add(new VersregelHandler(MVNAnnotationType.VERSREGEL))//
      .build();

  // opening annotations
  private void handleOpeningAnnotations(StringBuilder teiBuilder, AnnotatedTextSegment annotatedTextSegment) {
    Multimap<String, XmlAnnotation> openingAnnotationIndex = indexXmlAnnotations(annotatedTextSegment.getOpeningAnnotations());
    for (AnnotationHandler annotationHandler : ANNOTATION_HANDLERS) {
      List<XmlAnnotation> xmlAnnotations = filterRelevantAnnotations(openingAnnotationIndex, annotationHandler);
      annotationHandler.onOpenAnnotation(teiBuilder, xmlAnnotations, context);
    }
  }

  // closing annotations
  private void handleClosingAnnotations(StringBuilder teiBuilder, AnnotatedTextSegment annotatedTextSegment) {
    Multimap<String, XmlAnnotation> closingAnnotationIndex = indexXmlAnnotations(annotatedTextSegment.getClosingAnnotations());
    for (AnnotationHandler annotationHandler : Lists.reverse(ANNOTATION_HANDLERS)) {
      List<XmlAnnotation> xmlAnnotations = filterRelevantAnnotations(closingAnnotationIndex, annotationHandler);
      annotationHandler.onCloseAnnotation(teiBuilder, xmlAnnotations, context);
    }
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
    public InspringenHandler(Object... tagObjects) {
      super(tagObjects);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      if (!xmlAnnotations.isEmpty()) {
        context.indent = true;
      }
    }
  }

  private static class RegelnummeringBladHandler extends DefaultAnnotationHandler {
    public RegelnummeringBladHandler(Object... tagObjects) {
      super(tagObjects);
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
    public TekstHandler(Object... tagObjects) {
      super(tagObjects);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        context.textLineNumber = 1;
        teiBuilder.append(NL);
        Map<String, String> attributes = xmlAnnotation.getAttributes();
        String tekstN = attributes.get("n");
        context.textId = attributes.get(XML_ID);
        if (context.parseresult.isTextGroup(tekstN)) {
          Element group = new Element("group")//
              .withAttribute("n", tekstN)//
              .withAttribute(XML_ID, context.textId);
          teiBuilder.append(openingTag(group));

        } else {
          Element text = new Element("text")//
              .withAttribute("n", tekstN)//
              .withAttribute(XML_ID, context.textId);
          teiBuilder.append(openingTag(text)).append(openingTag("body"));
          //        openLineGroup(teiBuilder); // only for testing purposes
        }
        if (attributes.containsKey("title")) {
          Element head = new Element("head").withAttribute("type", "assigned");
          teiBuilder.append(openingTag(head)).append(attributes.get("title")).append(closingTag(head));
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
        if (context.inPoetry) {
          teiBuilder.append(closingTag("lg"));
        }
        if (context.parseresult.isTextGroup(attributes.get("n"))) {
          teiBuilder.append(closingTag("group"));

        } else {
          //        closeLineGroup(teiBuilder, context); // only for testing purposes
          teiBuilder.append(closingTag("body")).append(closingTag("text"));
        }
        teiBuilder.append(NL);
      }
    }

    //    private void closeLineGroup(StringBuilder teiBuilder, Context context) {
    //      context.inPoetry = false;
    //      teiBuilder.append(closingTag("lg"));
    //    }

  }

  private static class PoezieHandler extends DefaultAnnotationHandler {
    private static final String LG = "lg";

    public PoezieHandler(Object... tagObjects) {
      super(tagObjects);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      if (!xmlAnnotations.isEmpty()) {
        context.inParagraph = false;
        context.inPoetry = true;
        openLineGroup(teiBuilder, context);
      }
      super.onOpenAnnotation(teiBuilder, xmlAnnotations, context);
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      if (!xmlAnnotations.isEmpty() && context.inPoetry) {
        teiBuilder.append(closingTag(LG));
        context.inPoetry = false;
      }
      super.onCloseAnnotation(teiBuilder, xmlAnnotations, context);
    }

    private static void openLineGroup(StringBuilder teiBuilder, Context context) {
      teiBuilder.append(openingTag(LG));
    }

  }

  private static class AlineaHandler extends ElementWrapper {
    public AlineaHandler(Object... tagObjects) {
      super(new Element("p"), tagObjects);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      if (!xmlAnnotations.isEmpty()) {
        context.inPoetry = false;
        context.inParagraph = true;
      }
      super.onOpenAnnotation(teiBuilder, xmlAnnotations, context);
    }

    //    @Override
    //    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
    //      if (!xmlAnnotations.isEmpty()) {
    //        context.inParagraph = false;
    //      }
    //      super.onCloseAnnotation(teiBuilder, xmlAnnotations, context);
    //    }
  }

  private static class EntryHandler extends DefaultAnnotationHandler {
    public EntryHandler(Object... tagObjects) {
      super(tagObjects);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      if (!xmlAnnotations.isEmpty()) {
        XmlAnnotation xmlAnnotation = xmlAnnotations.iterator().next();
        context.foliumLineNumber = "1";
        Map<String, String> entryAttributes = xmlAnnotation.getAttributes();
        context.foliumId = entryAttributes.get(XML_ID);
        context.currentEntryId = entryAttributes.get("_entryId");
        Element pb = new Element("pb");
        addOptionalAttribute(pb, XML_ID, entryAttributes);
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

    public OpschriftHandler(Object... tagObjects) {
      super(tagObjects);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      if (!xmlAnnotations.isEmpty()) {
        context.inOpener = true;
        openHeadOrCloser(teiBuilder, xmlAnnotations, HEAD, context);
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      if (!xmlAnnotations.isEmpty()) {
        closeHeadOrCloser(teiBuilder, xmlAnnotations, HEAD, context);
        context.inOpener = false;
      }
    }
  }

  private static class OnderschriftHandler extends DefaultAnnotationHandler {
    private static final String CLOSER = "closer";

    public OnderschriftHandler(Object... tagObjects) {
      super(tagObjects);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      if (!xmlAnnotations.isEmpty()) {
        context.inCloser = true;
        openHeadOrCloser(teiBuilder, xmlAnnotations, CLOSER, context);
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      if (!xmlAnnotations.isEmpty()) {
        closeHeadOrCloser(teiBuilder, xmlAnnotations, CLOSER, context);
        context.inCloser = false;
      }
    }
  }

  private static void openHeadOrCloser(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, String name, Context context) {
    context.inPoetry = false;
    Element head = new Element(name);
    for (int i = 0; i < xmlAnnotations.size(); i++) {
      teiBuilder.append(openingTag(head));
      context.countAsTextLine = true;
    }
  }

  private static void closeHeadOrCloser(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, String name, Context context) {
    for (int i = 0; i < xmlAnnotations.size(); i++) {
      teiBuilder.append(closingTag(name));
      context.countAsTextLine = true;
    }
  }

  private static class WitregelHandler extends DefaultAnnotationHandler {
    public WitregelHandler(Object... tagObjects) {
      super(tagObjects);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (int i = 0; i < xmlAnnotations.size(); i++) {
        teiBuilder.append(milestoneTag("lb"));
      }
    }
  }

  private static class VersregelHandler extends DefaultAnnotationHandler {
    public VersregelHandler(Object... tagObjects) {
      super(tagObjects);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      if (!xmlAnnotations.isEmpty()) {
        if (!context.inPoetry) {
          context.addError(MVNAnnotationType.VERSREGEL, "versregel annotatie buiten een mvn:poetry sectie");

        } else {
          teiBuilder.append(closingTag("l"));
          context.incrementTextLineNumber();
          String lId = context.textId + "-l-" + context.textLineNumber;
          Element l = new Element("l")//
              .withAttribute("n", String.valueOf(context.textLineNumber))//
              .withAttribute(XML_ID, lId);
          teiBuilder.append(openingTag(l));
        }
      }
    }
  }

  private static class LHandler extends DefaultAnnotationHandler {
    public LHandler(Object... tagObjects) {
      super(tagObjects);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      if (!xmlAnnotations.isEmpty()) {
        String id = context.foliumId + "-lb-" + context.foliumLineNumber;
        Element lb = new Element("lb").withAttribute("n", String.valueOf(context.foliumLineNumber)).withAttribute(XML_ID, id);
        if (context.indent) {
          lb.setAttribute("rend", "indent");
        }
        if (context.inParagraph) {
          lb.setAttribute("np", String.valueOf(context.textLineNumber));
        }
        teiBuilder.append(NL).append(milestoneTag(lb));
        if (context.inPoetry) {
          String lId = context.textId + "-l-" + context.textLineNumber;
          Element l = new Element("l")//
              .withAttribute("n", String.valueOf(context.textLineNumber))//
              .withAttribute(XML_ID, lId);
          teiBuilder.append(openingTag(l));
        }
        context.indent = false;
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      if (!xmlAnnotations.isEmpty()) {
        context.incrementFoliumLineNumber();
        context.incrementTextLineNumber();
        if (context.inPoetry) {
          teiBuilder.append(closingTag("l"));
        }
      }
    }
  }

  private static class MargeKolomHandler extends ElementWrapper {
    public MargeKolomHandler(String place, Object... tagObjects) {
      super(note(place), tagObjects);
    }

    private static Element note(String place) {
      return new Element("note")//
          .withAttribute("place", "margin-" + place)//
          .withAttribute("type", "ms");
    }
  }

  private static class LinkerMargeKolomHandler extends MargeKolomHandler {
    public LinkerMargeKolomHandler(Object... tagObjects) {
      super("left", tagObjects);
    }
  }

  private static class RechterMargeKolomHandler extends MargeKolomHandler {
    public RechterMargeKolomHandler(Object... tagObjects) {
      super("right", tagObjects);
    }
  }

  private static class ElementWrapper extends DefaultAnnotationHandler {
    private final Element element;

    public ElementWrapper(Element element, Object... tagObjects) {
      super(tagObjects);
      this.element = element;
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (int i = 0; i < xmlAnnotations.size(); i++) {
        teiBuilder.append(openingTag(element));
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (int i = 0; i < xmlAnnotations.size(); i++) {
        teiBuilder.append(closingTag(element));
      }
    }
  }

  private static class HiRendWrapper extends ElementWrapper {
    public HiRendWrapper(String rend, Object... tagObjects) {
      super(new Element("hi").withAttribute("rend", rend), tagObjects);
    }
  }

  private static class SubHandler extends HiRendWrapper {
    public SubHandler(Object... tagObjects) {
      super("subscript", tagObjects);
    }
  }

  private static class SupHandler extends HiRendWrapper {
    public SupHandler(Object... tagObjects) {
      super("superscript", tagObjects);
    }
  }

  private static class CijfersHandler extends ElementWrapper {
    private static final Element element = new Element("num").withAttribute("type", "roman");

    public CijfersHandler(Object... tagObjects) {
      super(element, tagObjects);
    }
  }

  private static class LettersHandler extends ElementWrapper {
    private static final Element element = new Element("mentioned");

    public LettersHandler(Object... tagObjects) {
      super(element, tagObjects);
    }
  }

  private static class OnduidelijkHandler extends ElementWrapper {
    private static final Element element = new Element("unclear");

    public OnduidelijkHandler(Object... tagObjects) {
      super(element, tagObjects);
    }
  }

  private static class DefectHandler extends DefaultAnnotationHandler {
    public DefectHandler(Object... tagObjects) {
      super(tagObjects);
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

  private static class DoorhalingHandler extends ElementWrapper {
    private static final Element del = new Element("del");

    public DoorhalingHandler(Object... tagObjects) {
      super(del, tagObjects);
    }
  }

  private static class AfkortingHandler extends DefaultAnnotationHandler {
    private static final String EXPAN = "expan";
    private static final String ABBR = "abbr";
    private static final String CHOICE = "choice";

    public AfkortingHandler(Object... tagObjects) {
      super(tagObjects);
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
            .append(openingTag(CHOICE))//
            .append(openingTag(ABBR))//
            .append(abbr)//
            .append(closingTag(ABBR))//
            .append(openingTag(EXPAN))//
            .append(expan)//
            .append(closingTag(EXPAN))//
            .append(closingTag(CHOICE));
        context.text = "";
      }
    }
  }

  private static class KolomHandler extends DefaultAnnotationHandler {
    public KolomHandler(Object... tagObjects) {
      super(tagObjects);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (int i = 0; i < xmlAnnotations.size(); i++) {
        teiBuilder.append(milestoneTag("cb"));
      }
    }
  }

  private static class ItalicHandler extends ElementWrapper {
    private static final Element element = new Element("ex");

    public ItalicHandler(Object... tagObjects) {
      super(element, tagObjects);
    }
  }

  private static class OphogingRoodHandler extends HiRendWrapper {
    public OphogingRoodHandler(Object... tagObjects) {
      super("rubricated", tagObjects);
    }
  }

  private static class TekstKleurRoodHandler extends HiRendWrapper {
    public TekstKleurRoodHandler(Object... tagObjects) {
      super("rubric", tagObjects);
    }
  }

  private static class InitiaalHandler extends DefaultAnnotationHandler {
    private static final String HI = "hi";

    public InitiaalHandler(Object... tagObjects) {
      super(tagObjects);
    }

    @Override
    public void onOpenAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
        String body = xmlAnnotation.getAttributes().get("body");
        if (body != null) {
          String capitalsize = body.trim();
          int size = 0;
          if (StringUtils.isNumeric(capitalsize)) {
            size = Integer.parseInt(capitalsize);
            if (size < 1 || size > 19) {
              addValidationError(body, context);
            }

          } else {
            addValidationError(body, context);
          }

          Element hi = new Element(HI).withAttribute("rend", "capitalsize" + capitalsize);
          teiBuilder.append(openingTag(hi));
        }
      }
    }

    @Override
    public void onCloseAnnotation(StringBuilder teiBuilder, Collection<XmlAnnotation> xmlAnnotations, Context context) {
      for (int i = 0; i < xmlAnnotations.size(); i++) {
        teiBuilder.append(closingTag(HI));
      }
    }

    public void addValidationError(final String body, Context context) {
      context.addError(MVNAnnotationType.INITIAAL, "De inhoud van de annotatie ('" + body + "') is geen natuurlijk getal > 0 en < 20.");
    }
  }

  private static class PaleoHandler extends DefaultAnnotationHandler {
    public PaleoHandler(Object... tagObjects) {
      super(tagObjects);
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
    Multimap<String, XmlAnnotation> index = ArrayListMultimap.create();
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
