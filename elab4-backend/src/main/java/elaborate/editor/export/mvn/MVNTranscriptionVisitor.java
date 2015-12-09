package elaborate.editor.export.mvn;

import static nl.knaw.huygens.tei.Traversal.NEXT;
import static nl.knaw.huygens.tei.Traversal.STOP;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.Transcription;
import elaborate.editor.model.orm.service.AnnotationService;
import nl.knaw.huygens.tei.DelegatingVisitor;
import nl.knaw.huygens.tei.Element;
import nl.knaw.huygens.tei.ElementHandler;
import nl.knaw.huygens.tei.Text;
import nl.knaw.huygens.tei.TextHandler;
import nl.knaw.huygens.tei.Traversal;
import nl.knaw.huygens.tei.XmlContext;

public class MVNTranscriptionVisitor extends DelegatingVisitor<XmlContext> implements ElementHandler<XmlContext>, TextHandler<XmlContext> {

  private static final String HI = "hi";
  static int lb = 1;
  static boolean firstText = true;
  static List<String> errors = Lists.newArrayList();
  //  private static String sigle;
  private static boolean ignoreText = false;
  public static boolean inParagraph = false;
  private static boolean inLineGroup = false;
  private static String pageId = "1";
  private static AnnotationService annotationService;

  public MVNTranscriptionVisitor(String sigle, AnnotationService _annotationService) {
    super(new XmlContext());
    annotationService = _annotationService;
    //    MVNTranscriptionVisitor.sigle = sigle;
    setTextHandler(this);
    setDefaultElementHandler(this);
    addElementHandler(new BodyHandler(), "body");
    addElementHandler(new AnnotationHandler(), "ab", "ae");
  }

  public List<String> getErrors() {
    return errors;
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
      errors.clear();
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

  static class AnnotationHandler implements ElementHandler<XmlContext> {

    @Override
    public Traversal enterElement(Element element, XmlContext context) {
      String id = element.getAttribute("id");
      Annotation annotation = getAnnotation(id);
      if (annotation != null) {
        String name = element.getName();
        if (Transcription.BodyTags.ANNOTATION_BEGIN.equals(name)) {
          handleOpenAnnotation(annotation, context);
        } else if (Transcription.BodyTags.ANNOTATION_END.equals(name)) {
          handleCloseAnnotation(annotation, context);
        }
      }
      return STOP;
    }

    private void handleOpenAnnotation(Annotation annotation, XmlContext context) {
      MVNAnnotationType type = getVerifiedType(annotation);
      ignoreText = type.ignoreText();
      if (MVNAnnotationType.REGELNUMMERING_BLAD.equals(type)) {
        String body = annotation.getBody();
        if (StringUtils.isNumeric(body)) {
          lb = Integer.valueOf(body);
        } else {
          errors.add(MVNAnnotationType.REGELNUMMERING_BLAD.getName() + " body: '" + body + "' is not numeric");
        }

      } else {
        handleFirstLB(context);
        if (handlers.containsKey(type)) {
          handlers.get(type).handleOpenAnnotation(annotation, context);

        } else {
          throw new RuntimeException("uncaught MVNAnnotationType: " + type.getName());
        }
      }
    }

    private void handleCloseAnnotation(Annotation annotation, XmlContext context) {
      MVNAnnotationType type = getVerifiedType(annotation);
      ignoreText = false; // but what if it's inside another mvnannotation that should ignore text?
      if (handlers.containsKey(type)) {
        handlers.get(type).handleCloseAnnotation(annotation, context);

      } else {
        throw new RuntimeException("uncaught MVNAnnotationType: " + type.getName());
      }
    }

    private MVNAnnotationType getVerifiedType(Annotation annotation) {
      String typeName = annotation.getAnnotationType().getName();
      verifyAnnotationTypeIsAllowed(typeName);
      MVNAnnotationType type = MVNAnnotationType.fromName(typeName);
      return type;
    }

    private void verifyAnnotationTypeIsAllowed(String type) {
      if (!MVNAnnotationType.getAllNames().contains(type)) {
        errors.add("onbekend annotatietype: " + type);
        throw new RuntimeException(Joiner.on("\n").join(errors));
      }
    }

    @Override
    public Traversal leaveElement(Element element, XmlContext context) {
      return NEXT;
    }

    private Annotation getAnnotation(String annotationId) {
      return annotationService.getAnnotationByAnnotationNo(Integer.valueOf(annotationId));
    }

  }

  static Map<MVNAnnotationType, MVNAnnotationHandler> handlers = ImmutableMap.<MVNAnnotationType, MVNTranscriptionVisitor.MVNAnnotationHandler> builder()//
      .put(MVNAnnotationType.AFKORTING, new AfkortingHandler())//
      .put(MVNAnnotationType.ALINEA, new AlineaHandler())//
      .put(MVNAnnotationType.CIJFERS, new WrapInElementHandler(new Element("num", "type", "roman")))//
      .put(MVNAnnotationType.DEFECT, new DefectHandler())//
      .put(MVNAnnotationType.DOORHALING, new WrapInElementHandler("del"))//
      .put(MVNAnnotationType.GEBRUIKERSNOTITIE, new GebruikersnotitieHandler())//
      .put(MVNAnnotationType.INCIPIT, new IncipitHandler())//
      .put(MVNAnnotationType.INITIAAL, new InitiaalHandler())//
      .put(MVNAnnotationType.INSPRINGEN, new InspringenHandler())//
      .put(MVNAnnotationType.KOLOM, new KolomHandler())//
      .put(MVNAnnotationType.LETTERS, new WrapInElementHandler("mentioned"))//
      .put(MVNAnnotationType.LINKERMARGEKOLOM, new WrapInElementHandler(new Element("note", ImmutableMap.of("place", "margin-left", "type", "ms"))))//
      .put(MVNAnnotationType.RECHTERMARGEKOLOM, new WrapInElementHandler(new Element("note", ImmutableMap.of("place", "margin-right", "type", "ms"))))//
      .put(MVNAnnotationType.METAMARK, new MetamarkHandler())//
      .put(MVNAnnotationType.ONDERSCHRIFT, new OnderschriftHandler())//
      .put(MVNAnnotationType.ONDUIDELIJK, new WrapInElementHandler("unclear"))//
      .put(MVNAnnotationType.ONLEESBAAR, new WrapInElementHandler("gap"))//
      .put(MVNAnnotationType.OPHOGING_ROOD, new WrapInElementHandler(new Element(HI, "rend", "rubricated")))//
      .put(MVNAnnotationType.OPSCHRIFT, new OpschriftHandler())//
      .put(MVNAnnotationType.PALEOGRAFISCH, new PaleografischHandler())//
      .put(MVNAnnotationType.POEZIE, new PoezieHandler())//
      .put(MVNAnnotationType.REGELNUMMERING_BLAD, new RegelnummeringBladHandler())//
      .put(MVNAnnotationType.REGELNUMMERING_TEKST, new RegelnummeringTekstHandler())//
      .put(MVNAnnotationType.TEKSTBEGIN, new TekstbeginHandler())//
      .put(MVNAnnotationType.TEKSTEINDE, new TeksteindeHandler())//
      .put(MVNAnnotationType.TEKSTKLEUR_ROOD, new WrapInElementHandler(new Element(HI, "rend", "rubric")))//
      .put(MVNAnnotationType.VERSREGEL, new VersregelHandler())//
      .put(MVNAnnotationType.VREEMDTEKEN, new VreemdtekenHandler())//
      .put(MVNAnnotationType.WITREGEL, new WitregelHandler())//
      .build();

  public static interface MVNAnnotationHandler {
    public void handleOpenAnnotation(Annotation annotation, XmlContext context);

    public void handleCloseAnnotation(Annotation annotation, XmlContext context);
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
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {
      context.addOpenTag(element);
    }

    @Override
    public void handleCloseAnnotation(Annotation annotation, XmlContext context) {
      context.addCloseTag(element);
    }
  }

  private static class AfkortingHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {
      context.addOpenTag("choice");
      context.addOpenTag("abbr");
      context.addLiteral(annotation.getAnnotatedText());
      context.addCloseTag("abbr");
      context.addOpenTag("expan");
      context.addLiteral(normalized(annotation.getBody()).replace("i>", "ex>"));
      context.addCloseTag("expan");
      context.addCloseTag("choice");
    }

    @Override
    public void handleCloseAnnotation(Annotation annotation, XmlContext context) {
      // no action on closeAnnotation
    }
  }

  private static class AlineaHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {
      closeOpenParagraph(context);
      closeOpenLineGroup(context);
      context.addOpenTag("p");
      inParagraph = true;
    }

    @Override
    public void handleCloseAnnotation(Annotation annotation, XmlContext context) { // no action on closeAnnotation
    }
  }

  private static class DefectHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {
      context.addEmptyElementTag("gap");
    }

    @Override
    public void handleCloseAnnotation(Annotation annotation, XmlContext context) {}
  }

  private static class GebruikersnotitieHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(Annotation annotation, XmlContext context) {}
  }

  private static class IncipitHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(Annotation annotation, XmlContext context) {}
  }

  private static class InitiaalHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(Annotation annotation, XmlContext context) {}
  }

  private static class InspringenHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(Annotation annotation, XmlContext context) {}
  }

  private static class KolomHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(Annotation annotation, XmlContext context) {
      context.addEmptyElementTag("cb");
    }
  }

  private static class MetamarkHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(Annotation annotation, XmlContext context) {}
  }

  private static class OnderschriftHandler extends WrapInElementHandler {

    public OnderschriftHandler() {
      super("closer");
    }

    @Override
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {
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
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {
      closeOpenParagraph(context);
      closeOpenLineGroup(context);
      super.handleOpenAnnotation(annotation, context);
    }
  }

  private static class PaleografischHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(Annotation annotation, XmlContext context) {
      Element note = new Element("note", "type", "c");
      context.addOpenTag(note);
      context.addLiteral(normalized(annotation.getBody()).replaceAll("<i>", "<mentioned>").replaceAll("</i>", "</mentioned>"));
      context.addCloseTag(note);
    }
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

  private static class PoezieHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {
      closeOpenParagraph(context);
    }

    @Override
    public void handleCloseAnnotation(Annotation annotation, XmlContext context) {
      context.addLiteral("\n");
      context.addOpenTag("lg");
      inLineGroup = true;
    }
  }

  private static class RegelnummeringBladHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(Annotation annotation, XmlContext context) {}
  }

  private static class RegelnummeringTekstHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(Annotation annotation, XmlContext context) {}
  }

  private static class TekstbeginHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(Annotation annotation, XmlContext context) {}
  }

  private static class TeksteindeHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(Annotation annotation, XmlContext context) {}
  }

  private static class VersregelHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(Annotation annotation, XmlContext context) {}
  }

  private static class VreemdtekenHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(Annotation annotation, XmlContext context) {}
  }

  private static class WitregelHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(Annotation annotation, XmlContext context) {}

    @Override
    public void handleCloseAnnotation(Annotation annotation, XmlContext context) {
      context.addEmptyElementTag("lb");
    }
  }

}
