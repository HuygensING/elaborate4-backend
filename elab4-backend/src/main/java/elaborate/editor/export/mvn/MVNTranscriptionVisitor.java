package elaborate.editor.export.mvn;

import static nl.knaw.huygens.tei.Traversal.NEXT;
import static nl.knaw.huygens.tei.Traversal.STOP;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
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
  static final String choiceTag = "choice";
  static final String abbrTag = "abbr";
  static final String expanTag = "expan";

  public static boolean inParagraph = false;

  private static final String HI = "hi";
  private static int lb = 1;
  private static boolean firstText = true;
  private static boolean ignoreText = false;
  private static boolean inLineGroup = false;
  private static String pageId;
  private static Map<Integer, AnnotationData> annotationIndex;
  private static MVNConversionResult result;
  private static String currentEntryId;
  private static Set<String> deepestTextNums;
  private static int indent;

  public MVNTranscriptionVisitor(final MVNConversionResult result, final Map<Integer, AnnotationData> annotationIndex, final Set<String> deepestTextNums) {
    super(new XmlContext());
    MVNTranscriptionVisitor.deepestTextNums = deepestTextNums;
    MVNTranscriptionVisitor.pageId = "1";
    MVNTranscriptionVisitor.result = result;
    MVNTranscriptionVisitor.annotationIndex = annotationIndex;
    MVNTranscriptionVisitor.indent = 3;

    //    MVNTranscriptionVisitor.sigle = sigle;
    setTextHandler(this);
    setDefaultElementHandler(this);
    setCommentHandler(this);
    addElementHandler(new BodyHandler(), "body");
    addElementHandler(new PageBreakHandler(), "pb");
    addElementHandler(new AnnotationHandler(), "ab", "ae");
  }

  @Override
  public Traversal enterElement(final Element element, final XmlContext context) {
    //    Log.warn("ignoring {}", elemen  t);
    return Traversal.NEXT;
  }

  @Override
  public Traversal leaveElement(final Element element, final XmlContext context) {
    return Traversal.NEXT;
  }

  @Override
  public Traversal visitText(final Text text, final XmlContext context) {
    if (!ignoreText) {
      handleFirstLB(context);
      String normalized = text.getText();
      if (normalized.contains("\n")) {
        normalized = normalized.replace("\n", "\n" + indent() + newLB());
      }
      context.addLiteral(normalized);
    }
    return Traversal.NEXT;
  }

  private static String indent() {
    return StringUtils.repeat(" ", indent * 2);
  }

  @Override
  public Traversal visitComment(final Comment comment, final XmlContext context) {
    return Traversal.NEXT;
  }

  private static String newLB() {
    final String lbTag = "<lb n=\"" + lb + "\" xml:id=\"" + pageId + "-lb-" + lb + "\"/>";
    lb++;
    return lbTag;
  }

  private static void handleFirstLB(final XmlContext context) {
    if (firstText) {
      context.addLiteral(newLB());
      firstText = false;
    }
  }

  private static void closeOpenParagraph(final XmlContext context) {
    if (inParagraph) {
      context.addCloseTag("p");
      context.addLiteral("\n");
      indent--;
      inParagraph = false;
    }
  }

  private static void closeOpenLineGroup(final XmlContext context) {
    if (inLineGroup) {
      context.addCloseTag("lg");
      context.addLiteral("\n");
      indent--;
      inLineGroup = false;
    }
  }

  static class BodyHandler implements ElementHandler<XmlContext> {

    @Override
    public Traversal enterElement(final Element arg0, final XmlContext arg1) {
      lb = 1;
      firstText = true;
      ignoreText = false;
      inParagraph = false;
      inLineGroup = false;
      return Traversal.NEXT;
    }

    @Override
    public Traversal leaveElement(final Element e, final XmlContext c) {
      closeOpenParagraph(c);
      closeOpenLineGroup(c);
      return Traversal.NEXT;
    }

  }

  static class PageBreakHandler extends CopyElementHandler {
    @Override
    public Traversal enterElement(final Element element, final XmlContext context) {
      context.addLiteral("\n\n" + indent());
      pageId = element.getAttribute("id");
      currentEntryId = element.getAttribute("_entryId");
      element.removeAttribute("_entryId");
      if (element.getAttribute("facs").equals("null")) {
        element.removeAttribute("facs");
      }
      lb = 1;
      return super.enterElement(element, context);
    }

    @Override
    public Traversal leaveElement(final Element element, final XmlContext context) {
      super.leaveElement(element, context);
      context.addLiteral("\n" + indent());
      context.addLiteral(newLB());
      return Traversal.NEXT;
    }

  }

  static class CopyElementHandler implements ElementHandler<XmlContext> {
    @Override
    public Traversal enterElement(final Element element, final XmlContext context) {
      if (element.hasChildren()) {
        context.addOpenTag(element);
      } else {
        context.addEmptyElementTag(element);
      }
      return Traversal.NEXT;
    }

    @Override
    public Traversal leaveElement(final Element element, final XmlContext context) {
      if (element.hasChildren()) {
        context.addCloseTag(element);
      }
      return Traversal.NEXT;
    }
  }

  static class AnnotationHandler implements ElementHandler<XmlContext> {

    @Override
    public Traversal enterElement(final Element element, final XmlContext context) {
      final String id = element.getAttribute("id");
      final AnnotationData annotationData = getAnnotationData(id);
      if (annotationData != null) {
        final String name = element.getName();
        if (Transcription.BodyTags.ANNOTATION_BEGIN.equals(name)) {
          handleOpenAnnotation(annotationData, context);
        } else if (Transcription.BodyTags.ANNOTATION_END.equals(name)) {
          handleCloseAnnotation(annotationData, context);
        }
      }
      return STOP;
    }

    private void handleOpenAnnotation(final AnnotationData annotationData, final XmlContext context) {
      final MVNAnnotationType type = getVerifiedType(annotationData);
      ignoreText = type.ignoreText();
      if (MVNAnnotationType.REGELNUMMERING_BLAD.equals(type)) {
        final String body = annotationData.body;
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

    private void handleCloseAnnotation(final AnnotationData annotationData, final XmlContext context) {
      final MVNAnnotationType type = getVerifiedType(annotationData);
      ignoreText = false; // but what if it's inside another mvnannotation that should ignore text?
      if (handlers.containsKey(type)) {
        handlers.get(type).handleCloseAnnotation(annotationData, context);

      } else {
        throw new RuntimeException("uncaught MVNAnnotationType: " + type.getName());
      }
    }

    private MVNAnnotationType getVerifiedType(final AnnotationData annotationData) {
      final String typeName = annotationData.type;
      verifyAnnotationTypeIsAllowed(typeName);
      final MVNAnnotationType type = MVNAnnotationType.fromName(typeName);
      return type;
    }

    private void verifyAnnotationTypeIsAllowed(final String type) {
      if (!MVNAnnotationType.getAllNames().contains(type)) {
        result.addError(currentEntryId, "onbekend annotatietype: " + type);
        throw new RuntimeException(Joiner.on("\n").join(result.getErrors()));
      }
    }

    @Override
    public Traversal leaveElement(final Element element, final XmlContext context) {
      return NEXT;
    }

    private AnnotationData getAnnotationData(final String annotationId) {
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
      .put(MVNAnnotationType.TEKSTBEGIN, new TekstBeginHandler())//
      .put(MVNAnnotationType.TEKSTEINDE, new TekstEindeHandler())//
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

    public WrapInElementHandler(final Element element) {
      this.element = element;
    }

    public WrapInElementHandler(final String elementName) {
      this.element = new Element(elementName);
    }

    @Override
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {
      context.addOpenTag(element);
    }

    @Override
    public void handleCloseAnnotation(final AnnotationData annotation, final XmlContext context) {
      context.addCloseTag(element);
    }
  }

  private static class AfkortingHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {
      context.openLayer();
    }

    @Override
    public void handleCloseAnnotation(final AnnotationData annotation, final XmlContext context) {
      final String annotatedText = context.closeLayer();
      context.addOpenTag(choiceTag);
      context.addOpenTag(abbrTag);
      context.addLiteral(annotatedText);
      context.addCloseTag(abbrTag);
      context.addOpenTag(expanTag);
      context.addLiteral(cleanUpAnnotationBody(annotation).replace("i>", "ex>"));
      context.addCloseTag(expanTag);
      context.addCloseTag(choiceTag);
    }

  }

  private static class AlineaHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {
      closeOpenParagraph(context);
      closeOpenLineGroup(context);
      context.addOpenTag("p");
      inParagraph = true;
    }

    @Override
    public void handleCloseAnnotation(final AnnotationData annotation, final XmlContext context) { // no action on closeAnnotation
    }
  }

  private static class DefectHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {
      context.addEmptyElementTag("gap");
    }

    @Override
    public void handleCloseAnnotation(final AnnotationData annotation, final XmlContext context) {}
  }

  private static class GebruikersnotitieHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {}

    @Override
    public void handleCloseAnnotation(final AnnotationData annotation, final XmlContext context) {}
  }

  private static class IncipitHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {}

    @Override
    public void handleCloseAnnotation(final AnnotationData annotation, final XmlContext context) {}
  }

  private static class InitiaalHandler implements MVNAnnotationHandler {
    Element hi = new Element("hi");

    @Override
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {
      Integer size = 0;
      final String body = annotation.body.trim();
      if (StringUtils.isNumeric(body)) {
        size = Integer.valueOf(body);
        if (size < 1 || size > 19) {
          addValidationError(body);
        }

      } else {
        addValidationError(body);
      }
      hi.setAttribute("rend", "capitalsize" + size);
      context.addOpenTag(hi);
    }

    private void addValidationError(final String body) {
      result.addError(currentEntryId, "De inhoud van de annotatie ('" + body + "') is geen natuurlijk getal > 0 en < 20.");
    }

    @Override
    public void handleCloseAnnotation(final AnnotationData annotation, final XmlContext context) {
      context.addCloseTag(hi);
    }

  }

  private static class InspringenHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {}

    @Override
    public void handleCloseAnnotation(final AnnotationData annotation, final XmlContext context) {}
  }

  private static class KolomHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {}

    @Override
    public void handleCloseAnnotation(final AnnotationData annotation, final XmlContext context) {
      context.addEmptyElementTag("cb");
    }
  }

  private static class MetamarkHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {}

    @Override
    public void handleCloseAnnotation(final AnnotationData annotation, final XmlContext context) {}
  }

  private static class OnderschriftHandler extends WrapInElementHandler {

    public OnderschriftHandler() {
      super("closer");
    }

    @Override
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {
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
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {
      closeOpenParagraph(context);
      closeOpenLineGroup(context);
      super.handleOpenAnnotation(annotation, context);
    }
  }

  private static class PaleografischHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {}

    @Override
    public void handleCloseAnnotation(final AnnotationData annotation, final XmlContext context) {
      final Element note = new Element("note").withAttribute("type", "pc");
      context.addOpenTag(note);
      context.addLiteral(cleanUpAnnotationBody(annotation).replaceAll("<i>", "<mentioned>").replaceAll("</i>", "</mentioned>"));
      context.addCloseTag(note);
    }
  }

  private static class PoezieHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {
      closeOpenParagraph(context);
    }

    @Override
    public void handleCloseAnnotation(final AnnotationData annotation, final XmlContext context) {
      context.addLiteral("\n" + indent());
      context.addOpenTag("lg");
      inLineGroup = true;
    }
  }

  private static class RegelnummeringBladHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {}

    @Override
    public void handleCloseAnnotation(final AnnotationData annotation, final XmlContext context) {}
  }

  private static class RegelnummeringTekstHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {}

    @Override
    public void handleCloseAnnotation(final AnnotationData annotation, final XmlContext context) {}
  }

  private static class TekstBeginHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {
      context.openLayer();
    }

    @Override
    public void handleCloseAnnotation(final AnnotationData annotation, final XmlContext context) {
      final String annotatedText = context.closeLayer();
      if (!"‡".equals(annotatedText)) {
        result.addError(currentEntryId, "Het geannoteerde teken moet ‘‡’ zijn bij mvn:tekstbegin, is '" + annotatedText + "'");
      }
      final List<String> parts = Splitter.on(";").trimResults().splitToList(annotation.body);
      final String textNum = parts.get(0);
      final boolean isText = isText(textNum);
      final Element element = new Element(isText ? "text" : "group")//
          .withAttribute("n", textNum)//
          .withAttribute("xml:id", result.getSigle() + "-" + textNum);
      context.addLiteral("\n" + indent());
      context.addOpenTag(element);
      indent++;
      context.addLiteral("\n" + indent());
      if (isText) {
        context.addOpenTag("body");
        indent++;
        context.addLiteral("\n" + indent());
      }
      if (parts.size() > 1) {
        final String title = parts.get(1);
        final Element head = new Element("head").withAttribute("type", "assigned");
        context.addOpenTag(head);
        context.addLiteral(title);
        context.addCloseTag(head);
      }
    }

  }

  private static boolean isText(final String textNum) {
    return deepestTextNums.contains(textNum);
  }

  private static class TekstEindeHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {
      context.openLayer();
    }

    @Override
    public void handleCloseAnnotation(final AnnotationData annotation, final XmlContext context) {
      final String annotatedText = context.closeLayer();
      if (!"‡".equals(annotatedText)) {
        result.addError(currentEntryId, "Het geannoteerde teken moet ‘‡’ zijn bij mvn:teksteinde, is '" + annotatedText + "'");
      }
      final List<String> parts = Splitter.on(";").trimResults().splitToList(annotation.body);
      final String textNum = parts.get(0);
      final boolean inTextBody = isText(textNum);
      if (inTextBody) {
        indent--;
        context.addLiteral("\n" + indent());
        context.addCloseTag("body");
      }
      indent--;
      final Element element = new Element(inTextBody ? "text" : "group");
      context.addLiteral("\n" + indent());
      context.addCloseTag(element);
      context.addLiteral("\n");
    }
  }

  private static class VersregelHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {}

    @Override
    public void handleCloseAnnotation(final AnnotationData annotation, final XmlContext context) {}
  }

  private static class VreemdtekenHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {}

    @Override
    public void handleCloseAnnotation(final AnnotationData annotation, final XmlContext context) {}
  }

  private static class WitregelHandler implements MVNAnnotationHandler {
    @Override
    public void handleOpenAnnotation(final AnnotationData annotation, final XmlContext context) {
      context.openLayer();
    }

    @Override
    public void handleCloseAnnotation(final AnnotationData annotation, final XmlContext context) {
      final String annotatedText = context.closeLayer();
      if (!"¤".equals(annotatedText)) {
        result.addError(currentEntryId, "Het geannoteerde teken moet ‘¤’ zijn, is '" + annotatedText + "'");
      }
      context.addLiteral("\n" + indent());
      context.addEmptyElementTag("lb");
      context.addLiteral("\n" + indent());
    }
  }

  private static String cleanUpAnnotationBody(final AnnotationData annotation) {
    return normalized(annotation.body).replaceAll("<span.*?>", "").replaceAll("</span>", "");
  }

  private static String normalized(final String rawXml) {
    final String normalized = rawXml//
        .replaceAll("<i .*?>", "<i>")//
        .replaceAll("<div>", "")//
        .replaceAll("</div>", "")//
        .replaceAll("<br>", "")//
        .replace("&nbsp;", " ");
    return normalized;
  }

}
