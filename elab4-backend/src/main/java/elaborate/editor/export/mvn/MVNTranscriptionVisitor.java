package elaborate.editor.export.mvn;

import static nl.knaw.huygens.tei.Traversal.NEXT;
import static nl.knaw.huygens.tei.Traversal.STOP;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

import elaborate.editor.export.mvn.MVNConversionData.AnnotationData;
import elaborate.editor.model.orm.Transcription;
import nl.knaw.huygens.tei.Comment;
import nl.knaw.huygens.tei.CommentHandler;
import nl.knaw.huygens.tei.DelegatingVisitor;
import nl.knaw.huygens.tei.Element;
import nl.knaw.huygens.tei.ElementHandler;
import nl.knaw.huygens.tei.Text;
import nl.knaw.huygens.tei.TextHandler;
import nl.knaw.huygens.tei.Traversal;
import nl.knaw.huygens.tei.XmlContext;

public class MVNTranscriptionVisitor extends DelegatingVisitor<XmlContext> implements ElementHandler<XmlContext>, TextHandler<XmlContext>, CommentHandler<XmlContext> {

  private static final String HI = "hi";
  static int lb = 1;
  static boolean firstText = true;
  //  private static String sigle;
  private static boolean ignoreText = false;
  public static boolean inParagraph = false;
  private static boolean inLineGroup = false;
  private static String pageId;
  private static Map<Integer, AnnotationData> annotationIndex;
  private static MVNConversionResult result;
  private static String currentEntryId;

  public MVNTranscriptionVisitor(MVNConversionResult result, Map<Integer, AnnotationData> annotationIndex) {
    super(new XmlContext());
    MVNTranscriptionVisitor.pageId = "1";
    MVNTranscriptionVisitor.result = result;
    MVNTranscriptionVisitor.annotationIndex = annotationIndex;
    //    MVNTranscriptionVisitor.sigle = sigle;
    setTextHandler(this);
    setDefaultElementHandler(this);
    setCommentHandler(this);
    addElementHandler(new BodyHandler(), "body");
    addElementHandler(new PageBreakHandler(), "pb");
    addElementHandler(new AnnotationHandler(), "ab", "ae");
  }

  @Override
  public Traversal enterElement(Element element, XmlContext context) {
    //    Log.warn("ignoring {}", elemen  t);
    return Traversal.NEXT;
  }

  @Override
  public Traversal leaveElement(Element element, XmlContext context) {
    return Traversal.NEXT;
  }

  @Override
  public Traversal visitText(Text text, XmlContext context) {
    if (!ignoreText) {
      handleFirstLB(context);
      String normalized = text.getText();
      if (normalized.contains("\n")) {
        normalized = normalized.replace("\n", "\n" + newLB());
      }
      context.addLiteral(normalized);
    }
    return Traversal.NEXT;
  }

  @Override
  public Traversal visitComment(Comment comment, XmlContext context) {
    return Traversal.NEXT;
  }

  private static String newLB() {
    String lbTag = "<lb n=\"" + lb + "\" xml:id=\"" + pageId + "-lb-" + lb + "\"/>";
    lb++;
    return lbTag;
  }

  private static void handleFirstLB(XmlContext context) {
    if (firstText) {
      context.addLiteral(newLB());
      firstText = false;
    }
  }

  private static void closeOpenParagraph(XmlContext context) {
    if (inParagraph) {
      context.addCloseTag("p");
      context.addLiteral("\n");
      inParagraph = false;
    }
  }

  private static void closeOpenLineGroup(XmlContext context) {
    if (inLineGroup) {
      context.addCloseTag("lg");
      context.addLiteral("\n");
      inLineGroup = false;
    }
  }

  static class BodyHandler implements ElementHandler<XmlContext> {

    @Override
    public Traversal enterElement(Element arg0, XmlContext arg1) {
      lb = 1;
      firstText = true;
      ignoreText = false;
      inParagraph = false;
      inLineGroup = false;
      return Traversal.NEXT;
    }

    @Override
    public Traversal leaveElement(Element e, XmlContext c) {
      closeOpenParagraph(c);
      closeOpenLineGroup(c);
      return Traversal.NEXT;
    }

  }

  static class PageBreakHandler extends CopyElementHandler {
    @Override
    public Traversal enterElement(Element element, XmlContext context) {
      context.addLiteral("\n\n");
      pageId = element.getAttribute("id");
      currentEntryId = element.getAttribute("_entryId");
      element.removeAttribute("_entryId");
      lb = 1;
      return super.enterElement(element, context);
    }

    @Override
    public Traversal leaveElement(Element element, XmlContext context) {
      super.leaveElement(element, context);
      context.addLiteral("\n");
      context.addLiteral(newLB());
      return Traversal.NEXT;
    }

  }

  static class CopyElementHandler implements ElementHandler<XmlContext> {
    @Override
    public Traversal enterElement(Element element, XmlContext context) {
      if (element.hasChildren()) {
        context.addOpenTag(element);
      } else {
        context.addEmptyElementTag(element);
      }
      return Traversal.NEXT;
    }

    @Override
    public Traversal leaveElement(Element element, XmlContext context) {
      if (element.hasChildren()) {
        context.addCloseTag(element);
      }
      return Traversal.NEXT;
    }
  }

  static class AnnotationHandler implements ElementHandler<XmlContext> {

    @Override
    public Traversal enterElement(Element element, XmlContext context) {
      String id = element.getAttribute("id");
      AnnotationData annotationData = getAnnotationData(id);
      if (annotationData != null) {
        String name = element.getName();
        if (Transcription.BodyTags.ANNOTATION_BEGIN.equals(name)) {
          handleOpenAnnotation(annotationData, context);
        } else if (Transcription.BodyTags.ANNOTATION_END.equals(name)) {
          handleCloseAnnotation(annotationData, context);
        }
      }
      return STOP;
    }

    private void handleOpenAnnotation(AnnotationData annotationData, XmlContext context) {
      MVNAnnotationType type = getVerifiedType(annotationData);
      ignoreText = type.ignoreText();
      if (MVNAnnotationType.REGELNUMMERING_BLAD.equals(type)) {
        String body = annotationData.body;
        if (StringUtils.isNumeric(body)) {
          lb = Integer.valueOf(body);
        } else {
          result.addError(currentEntryId, MVNAnnotationType.REGELNUMMERING_BLAD.getName() + " body: '" + body + "' is not numeric");
        }

      } else {
        handleFirstLB(context);
        if (handlers.containsKey(type)) {
          handlers.get(type).handleOpenAnnotation(annotationData, context);

        } else {
          throw new RuntimeException("uncaught MVNAnnotationType: " + type.getName());
        }
      }
    }

    private void handleCloseAnnotation(AnnotationData annotationData, XmlContext context) {
      MVNAnnotationType type = getVerifiedType(annotationData);
      ignoreText = false; // but what if it's inside another mvnannotation that should ignore text?
      if (handlers.containsKey(type)) {
        handlers.get(type).handleCloseAnnotation(annotationData, context);

      } else {
        throw new RuntimeException("uncaught MVNAnnotationType: " + type.getName());
      }
    }

    private MVNAnnotationType getVerifiedType(AnnotationData annotationData) {
      String typeName = annotationData.type;
      verifyAnnotationTypeIsAllowed(typeName);
      MVNAnnotationType type = MVNAnnotationType.fromName(typeName);
      return type;
    }

    private void verifyAnnotationTypeIsAllowed(String type) {
      if (!MVNAnnotationType.getAllNames().contains(type)) {
        result.addError(currentEntryId, "onbekend annotatietype: " + type);
        throw new RuntimeException(Joiner.on("\n").join(result.getErrors()));
      }
    }

    @Override
    public Traversal leaveElement(Element element, XmlContext context) {
      return NEXT;
    }

    private AnnotationData getAnnotationData(String annotationId) {
      return annotationIndex.get(Integer.valueOf(annotationId));
    }

  }

  static Map<MVNAnnotationType, MVNAnnotationHandler> handlers = ImmutableMap.<MVNAnnotationType, MVNTranscriptionVisitor.MVNAnnotationHandler> builder()//
      .put(MVNAnnotationType.AFKORTING, new AfkortingHandler())//
      .put(MVNAnnotationType.ALINEA, new AlineaHandler())//
      .put(MVNAnnotationType.CIJFERS, new WrapInElementHandler(new Element("num").withAttribute("type", "roman")))//
      .put(MVNAnnotationType.DEFECT, new DefectHandler())//
      .put(MVNAnnotationType.DOORHALING, new WrapInElementHandler("del"))//
      .put(MVNAnnotationType.GEBRUIKERSNOTITIE, new GebruikersnotitieHandler())//
      .put(MVNAnnotationType.INCIPIT, new IncipitHandler())//
      .put(MVNAnnotationType.INITIAAL, new InitiaalHandler())//
      .put(MVNAnnotationType.INSPRINGEN, new InspringenHandler())//
      .put(MVNAnnotationType.KOLOM, new KolomHandler())//
      .put(MVNAnnotationType.LETTERS, new WrapInElementHandler("mentioned"))//
      .put(MVNAnnotationType.LINKERMARGEKOLOM, new WrapInElementHandler(new Element("note").withAttribute("place", "margin-left").withAttribute("type", "ms")))//
      .put(MVNAnnotationType.RECHTERMARGEKOLOM, new WrapInElementHandler(new Element("note").withAttribute("place", "margin-right").withAttribute("type", "ms")))//
      .put(MVNAnnotationType.METAMARK, new MetamarkHandler())//
      .put(MVNAnnotationType.ONDERSCHRIFT, new OnderschriftHandler())//
      .put(MVNAnnotationType.ONDUIDELIJK, new WrapInElementHandler("unclear"))//
      .put(MVNAnnotationType.ONLEESBAAR, new WrapInElementHandler("gap"))//
      .put(MVNAnnotationType.OPHOGING_ROOD, new WrapInElementHandler(new Element(HI).withAttribute("rend", "rubricated")))//
      .put(MVNAnnotationType.OPSCHRIFT, new OpschriftHandler())//
      .put(MVNAnnotationType.PALEOGRAFISCH, new PaleografischHandler())//
      .put(MVNAnnotationType.POEZIE, new PoezieHandler())//
      .put(MVNAnnotationType.REGELNUMMERING_BLAD, new RegelnummeringBladHandler())//
      .put(MVNAnnotationType.REGELNUMMERING_TEKST, new RegelnummeringTekstHandler())//
      .put(MVNAnnotationType.TEKSTBEGIN, new TekstbeginHandler())//
      .put(MVNAnnotationType.TEKSTEINDE, new TeksteindeHandler())//
      .put(MVNAnnotationType.TEKSTKLEUR_ROOD, new WrapInElementHandler(new Element(HI).withAttribute("rend", "rubric")))//
      .put(MVNAnnotationType.VERSREGEL, new VersregelHandler())//
      .put(MVNAnnotationType.VREEMDTEKEN, new VreemdtekenHandler())//
      .put(MVNAnnotationType.WITREGEL, new WitregelHandler())//
      .build();

  public static interface MVNAnnotationHandler {
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context);

    public void handleCloseAnnotation(AnnotationData annotationData, XmlContext context);
  }

  private static class WrapInElementHandler implements MVNAnnotationHandler {
    Element element;

    public WrapInElementHandler(Element element) {
      this.element = element;
    }

    public WrapInElementHandler(String elementName) {
      this.element = new Element(elementName);
    }

    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {
      context.addOpenTag(element);
    }

    @Override
    public void handleCloseAnnotation(AnnotationData annotation, XmlContext context) {
      context.addCloseTag(element);
    }
  }

  private static class AfkortingHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {
      context.openLayer();
    }

    @Override
    public void handleCloseAnnotation(AnnotationData annotation, XmlContext context) {
      context.addOpenTag("choice");
      context.addOpenTag("abbr");
      context.addLiteral(context.closeLayer());
      context.addCloseTag("abbr");
      context.addOpenTag("expan");
      context.addLiteral(cleanUpAnnotationBody(annotation).replace("i>", "ex>"));
      context.addCloseTag("expan");
      context.addCloseTag("choice");
    }

  }

  private static class AlineaHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {
      closeOpenParagraph(context);
      closeOpenLineGroup(context);
      context.addOpenTag("p");
      inParagraph = true;
    }

    @Override
    public void handleCloseAnnotation(AnnotationData annotation, XmlContext context) { // no action on closeAnnotation
    }
  }

  private static class DefectHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {
      context.addEmptyElementTag("gap");
    }

    @Override
    public void handleCloseAnnotation(AnnotationData annotation, XmlContext context) {}
  }

  private static class GebruikersnotitieHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(AnnotationData annotation, XmlContext context) {}
  }

  private static class IncipitHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(AnnotationData annotation, XmlContext context) {}
  }

  private static class InitiaalHandler implements MVNAnnotationHandler {
    Element hi = new Element("hi");

    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {
      Integer size = 0;
      if (StringUtils.isNumeric(annotation.body)) {
        size = Integer.valueOf(annotation.body.trim());
        if (size < 1 || size > 19) {
          addValidationError(annotation.body);
        }

      } else {
        addValidationError(annotation.body);
      }
      hi.setAttribute("rend", "capitalsize" + size);
      context.addOpenTag(hi);
    }

    private void addValidationError(String body) {
      result.addError(currentEntryId, "De inhoud van de annotatie ('" + body + "') is geen natuurlijk getal > 0 en < 20.");
    }

    @Override
    public void handleCloseAnnotation(AnnotationData annotation, XmlContext context) {
      context.addCloseTag(hi);
    }

  }

  private static class InspringenHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(AnnotationData annotation, XmlContext context) {}
  }

  private static class KolomHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(AnnotationData annotation, XmlContext context) {
      context.addEmptyElementTag("cb");
    }
  }

  private static class MetamarkHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(AnnotationData annotation, XmlContext context) {}
  }

  private static class OnderschriftHandler extends WrapInElementHandler {

    public OnderschriftHandler() {
      super("closer");
    }

    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {
      closeOpenParagraph(context);
      closeOpenLineGroup(context);
      super.handleOpenAnnotation(annotation, context);
    }
  }

  private static class OpschriftHandler extends WrapInElementHandler {
    public OpschriftHandler() {
      super("head");
    }

    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {
      closeOpenParagraph(context);
      closeOpenLineGroup(context);
      super.handleOpenAnnotation(annotation, context);
    }
  }

  private static class PaleografischHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(AnnotationData annotation, XmlContext context) {
      Element note = new Element("note").withAttribute("type", "c");
      context.addOpenTag(note);
      context.addLiteral(cleanUpAnnotationBody(annotation).replaceAll("<i>", "<mentioned>").replaceAll("</i>", "</mentioned>"));
      context.addCloseTag(note);
    }
  }

  private static class PoezieHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {
      closeOpenParagraph(context);
    }

    @Override
    public void handleCloseAnnotation(AnnotationData annotation, XmlContext context) {
      context.addLiteral("\n");
      context.addOpenTag("lg");
      inLineGroup = true;
    }
  }

  private static class RegelnummeringBladHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(AnnotationData annotation, XmlContext context) {}
  }

  private static class RegelnummeringTekstHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(AnnotationData annotation, XmlContext context) {}
  }

  private static class TekstbeginHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(AnnotationData annotation, XmlContext context) {}
  }

  private static class TeksteindeHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(AnnotationData annotation, XmlContext context) {}
  }

  private static class VersregelHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(AnnotationData annotation, XmlContext context) {}
  }

  private static class VreemdtekenHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(AnnotationData annotation, XmlContext context) {}
  }

  private static class WitregelHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(AnnotationData annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(AnnotationData annotation, XmlContext context) {
      context.addLiteral("\n");
      context.addEmptyElementTag("lb");
      context.addLiteral("\n");
    }
  }

  private static String cleanUpAnnotationBody(AnnotationData annotation) {
    return normalized(annotation.body).replaceAll("<span.*?>", "").replaceAll("</span>", "");
  }

  private static String normalized(String rawXml) {
    String normalized = rawXml//
        .replaceAll("<i .*?>", "<i>")//
        .replaceAll("<div>", "")//
        .replaceAll("</div>", "")//
        .replaceAll("<br>", "")//
        .replace("&nbsp;", " ");
    return normalized;
  }

}
