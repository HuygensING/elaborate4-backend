package elaborate.editor.export.mvn;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import elaborate.util.XmlUtil;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.tei.Element;

public class MVNTeiExporter {
  private static final String NL = "\n";
  private static final Context context = new Context();
  private static ParseResult parseresult;

  public static String from(ParseResult parseresult) {
    MVNTeiExporter.parseresult = parseresult;
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
  private static void handleOpeningAnnotations(StringBuilder teiBuilder, AnnotatedTextSegment annotatedTextSegment) {
    Multimap<String, XmlAnnotation> openingAnnotationIndex = indexXmlAnnotations(annotatedTextSegment.getOpeningAnnotations());
    handleRegelNummering(openingAnnotationIndex.get(MVNAnnotationType.REGELNUMMERING_BLAD.getName()));
    handleOpenTekst(teiBuilder, openingAnnotationIndex.get("tekst"));
    handleOpenPoezie(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.POEZIE.getName()));
    handleOpenAlinea(teiBuilder, openingAnnotationIndex.get(MVNAnnotationType.ALINEA.getName()));
    handlePageBreak(teiBuilder, openingAnnotationIndex.get("entry"));
    handleOpenRegel(teiBuilder, openingAnnotationIndex.get("l"));
  }

  private static void handleRegelNummering(Collection<XmlAnnotation> collection) {
    if (!collection.isEmpty()) {
      XmlAnnotation xmlAnnotation = collection.iterator().next();
      String customLineNumber = xmlAnnotation.getAttributes().get("body");
      Log.info("{}: n='{}'", MVNAnnotationType.REGELNUMMERING_BLAD.getName(), customLineNumber);
      context.foliumLineNumber = customLineNumber;
    }
  }

  private static void handleOpenTekst(StringBuilder teiBuilder, Collection<XmlAnnotation> collection) {
    for (XmlAnnotation xmlAnnotation : collection) {
      context.textLineNumber = 1;
      teiBuilder.append(NL);
      Map<String, String> attributes = xmlAnnotation.getAttributes();
      String tekstN = attributes.get("n");
      if (parseresult.isTextGroup(tekstN)) {
        Element group = new Element("group")//
            .withAttribute("n", tekstN)//
            .withAttribute("xml:id", attributes.get("xml:id"));
        teiBuilder.append(XmlUtil.openingTag(group));

      } else {
        Element text = new Element("text")//
            .withAttribute("n", tekstN)//
            .withAttribute("xml:id", attributes.get("xml:id"));
        teiBuilder.append(XmlUtil.openingTag(text)).append(XmlUtil.openingTag("body"));
      }
    }
  }

  private static void handleOpenPoezie(StringBuilder teiBuilder, Collection<XmlAnnotation> collection) {
    if (!collection.isEmpty()) {
      context.inPoetry = true;
      teiBuilder.append(XmlUtil.openingTag("lg"));
    }
  }

  private static void handleOpenAlinea(StringBuilder teiBuilder, Collection<XmlAnnotation> collection) {
    if (!collection.isEmpty()) {
      context.inParagraph = true;
      teiBuilder.append("p");
    }
  }

  private static void handlePageBreak(StringBuilder teiBuilder, Collection<XmlAnnotation> collection) {
    if (!collection.isEmpty()) {
      XmlAnnotation xmlAnnotation = collection.iterator().next();
      context.foliumLineNumber = "1";
      Map<String, String> entryAttributes = xmlAnnotation.getAttributes();
      context.foliumId = entryAttributes.get("xml:id");
      Element pb = new Element("pb");
      addOptionalAttribute(pb, "xml:id", entryAttributes);
      addOptionalAttribute(pb, "n", entryAttributes);
      addOptionalAttribute(pb, "facs", entryAttributes);
      teiBuilder//
          .append(NL)//
          .append(XmlUtil.milestoneTag(pb));
    }
  }

  private static void handleOpenRegel(StringBuilder teiBuilder, Collection<XmlAnnotation> collection) {
    if (!collection.isEmpty()) {
      String id = context.foliumId + "-lb-" + context.foliumLineNumber;
      Element lb = new Element("lb").withAttribute("n", String.valueOf(context.foliumLineNumber)).withAttribute("xml:id", id);
      teiBuilder.append(NL).append(XmlUtil.milestoneTag(lb));
      if (context.inPoetry) {
        String lId = context.foliumId + "-l-" + context.textLineNumber;
        Element l = new Element("l").withAttribute("n", String.valueOf(context.textLineNumber)).withAttribute("xml:id", lId);
        teiBuilder.append(XmlUtil.openingTag(l));
      }
    }
  }

  // closing annotations
  private static void handleClosingAnnotations(StringBuilder teiBuilder, AnnotatedTextSegment annotatedTextSegment) {
    Multimap<String, XmlAnnotation> closingAnnotationIndex = indexXmlAnnotations(annotatedTextSegment.getClosingAnnotations());
    handleCloseRegel(teiBuilder, closingAnnotationIndex);
    handleCloseAlinea(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.ALINEA.getName()));
    handleClosePoezie(teiBuilder, closingAnnotationIndex.get(MVNAnnotationType.POEZIE.getName()));
    handleCloseTekst(teiBuilder, closingAnnotationIndex.get("tekst"));
  }

  private static void handleCloseRegel(StringBuilder teiBuilder, Multimap<String, XmlAnnotation> closingAnnotationIndex) {
    if (closingAnnotationIndex.containsKey("l")) {
      context.incrementFoliumLineNumber();
      context.textLineNumber++;
      if (context.inPoetry) {
        teiBuilder.append(XmlUtil.closingTag("l"));
      }
    }
  }

  private static void handleCloseAlinea(StringBuilder teiBuilder, Collection<XmlAnnotation> collection) {
    if (!collection.isEmpty()) {
      context.inParagraph = false;
      teiBuilder.append(XmlUtil.closingTag("p"));
    }
  }

  private static void handleClosePoezie(StringBuilder teiBuilder, Collection<XmlAnnotation> collection) {
    if (!collection.isEmpty()) {
      context.inPoetry = false;
      teiBuilder.append(XmlUtil.closingTag("lg"));
    }
  }

  private static void handleCloseTekst(StringBuilder teiBuilder, Collection<XmlAnnotation> collection) {
    List<XmlAnnotation> xmlAnnotations = Lists.newArrayList(collection);
    Collections.reverse(xmlAnnotations);
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      Map<String, String> attributes = xmlAnnotation.getAttributes();
      teiBuilder.append(NL);
      if (parseresult.isTextGroup(attributes.get("n"))) {
        teiBuilder.append(XmlUtil.closingTag("group"));

      } else {
        teiBuilder.append(XmlUtil.closingTag("body")).append(XmlUtil.closingTag("text"));
      }
      teiBuilder.append(NL);
    }
  }

  //

  private static Multimap<String, XmlAnnotation> indexXmlAnnotations(Collection<XmlAnnotation> annotations) {
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

  private static class Context {
    public boolean inParagraph = false;
    public boolean inPoetry = false;
    public String text;
    public String foliumId;
    public String foliumLineNumber = "1";
    public int textLineNumber = 1;

    public void incrementFoliumLineNumber() {
      Integer asInt = Integer.valueOf(foliumLineNumber.replaceAll("[^0-9]", ""));
      Integer next = asInt + 1;
      foliumLineNumber = String.valueOf(next);
    }
  }
}
