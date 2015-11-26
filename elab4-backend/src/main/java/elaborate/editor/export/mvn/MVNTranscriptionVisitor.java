package elaborate.editor.export.mvn;

import static nl.knaw.huygens.tei.Traversal.NEXT;
import static nl.knaw.huygens.tei.Traversal.STOP;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;
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

  static AnnotationService annotationService = AnnotationService.instance();
  static int lb = 1;
  static boolean firstText = true;
  static List<String> errors = Lists.newArrayList();
  private static String sigle;
  private static boolean ignoreText = false;
  public static boolean inParagraph = false;

  public MVNTranscriptionVisitor(String sigle) {
    super(new XmlContext());
    MVNTranscriptionVisitor.sigle = sigle;
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
    String lbTag = "<lb n=\"" + lb + "\" xml:id=\"" + sigle + "-lb-" + lb + "\"/>";
    lb++;
    return lbTag;
  }

  private static void handleFirstLB(XmlContext context) {
    if (firstText) {
      context.addLiteral(newLB());
      firstText = false;
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
      return Traversal.NEXT;
    }

    @Override
    public Traversal leaveElement(Element arg0, XmlContext arg1) {
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
      if (MVNAnnotationType.REGELNUMMERING_BLAD.equals(type)) {
        String body = annotation.getBody();
        if (StringUtils.isNumeric(body)) {
          lb = Integer.valueOf(body);
        } else {
          errors.add("");
        }

      } else {
        handleFirstLB(context);
        if (MVNAnnotationType.AFKORTING.equals(type)) {
          context.addOpenTag("choice");
          context.addOpenTag("abbr");
          context.addLiteral(annotation.getAnnotatedText().replace("i>", "ex>"));
          context.addCloseTag("abbr");
          context.addOpenTag("expan");
          context.addLiteral(annotation.getBody());
          context.addCloseTag("expan");
          context.addCloseTag("choice");

        } else if (MVNAnnotationType.ALINEA.equals(type)) {
          closeOpenParagraph(context);
          context.addOpenTag("p");
          inParagraph = true;

        } else if (MVNAnnotationType.CIJFERS.equals(type)) {
          context.addOpenTag(new Element("num", "type", "roman"));

        } else if (MVNAnnotationType.DEFECT.equals(type)) {

        } else if (MVNAnnotationType.DOORHALING.equals(type)) {
          context.addOpenTag("del");

        } else if (MVNAnnotationType.GEBRUIKERSNOTITIE.equals(type)) {

        } else if (MVNAnnotationType.INCIPIT.equals(type)) {

        } else if (MVNAnnotationType.INITIAAL.equals(type)) {

        } else if (MVNAnnotationType.INSPRINGEN.equals(type)) {

        } else if (MVNAnnotationType.KOLOM.equals(type)) {

        } else if (MVNAnnotationType.LETTERS.equals(type)) {

        } else if (MVNAnnotationType.LINKERMARGEKOLOM.equals(type)) {

        } else if (MVNAnnotationType.METAMARK.equals(type)) {

        } else if (MVNAnnotationType.ONDERSCHRIFT.equals(type)) {
          closeOpenParagraph(context);

        } else if (MVNAnnotationType.ONDUIDELIJK.equals(type)) {

        } else if (MVNAnnotationType.ONLEESBAAR.equals(type)) {

        } else if (MVNAnnotationType.OPHOGING_ROOD.equals(type)) {

        } else if (MVNAnnotationType.OPSCHRIFT.equals(type)) {
          closeOpenParagraph(context);

        } else if (MVNAnnotationType.PALEOGRAFISCH.equals(type)) {

        } else if (MVNAnnotationType.POEZIE.equals(type)) {
          closeOpenParagraph(context);

        } else if (MVNAnnotationType.RECHTERMARGEKOLOM.equals(type)) {

        } else if (MVNAnnotationType.REGELNUMMERING_BLAD.equals(type)) {

        } else if (MVNAnnotationType.REGELNUMMERING_TEKST.equals(type)) {

        } else if (MVNAnnotationType.TEKSTBEGIN.equals(type)) {

        } else if (MVNAnnotationType.TEKSTEINDE.equals(type)) {

        } else if (MVNAnnotationType.TEKSTKLEUR_ROOD.equals(type)) {

        } else if (MVNAnnotationType.VERSREGEL.equals(type)) {

        } else if (MVNAnnotationType.VREEMDTEKEN.equals(type)) {

        } else if (MVNAnnotationType.WITREGEL.equals(type)) {

        } else {
          throw new RuntimeException("uncaught MVNAnnotationType: " + type.getName());
        }
      }
    }

    private void closeOpenParagraph(XmlContext context) {
      if (inParagraph) {
        context.addCloseTag("p");
        inParagraph = false;
      }
    }

    private MVNAnnotationType getVerifiedType(Annotation annotation) {
      String typeName = annotation.getAnnotationType().getName();
      verifyAnnotationTypeIsAllowed(typeName);
      MVNAnnotationType type = MVNAnnotationType.valueOf(typeName);
      ignoreText = type.ignoreText();
      return type;
    }

    private void handleCloseAnnotation(Annotation annotation, XmlContext context) {
      MVNAnnotationType type = getVerifiedType(annotation);
      if (MVNAnnotationType.AFKORTING.equals(type)) {
        // no action on closeAnnotation

      } else if (MVNAnnotationType.ALINEA.equals(type)) {
        // no action on closeAnnotation

      } else if (MVNAnnotationType.CIJFERS.equals(type)) {
        context.addCloseTag("num");

      } else if (MVNAnnotationType.DEFECT.equals(type)) {

      } else if (MVNAnnotationType.DOORHALING.equals(type)) {
        context.addCloseTag("del");

      } else if (MVNAnnotationType.GEBRUIKERSNOTITIE.equals(type)) {

      } else if (MVNAnnotationType.INCIPIT.equals(type)) {

      } else if (MVNAnnotationType.INITIAAL.equals(type)) {

      } else if (MVNAnnotationType.INSPRINGEN.equals(type)) {

      } else if (MVNAnnotationType.KOLOM.equals(type)) {

      } else if (MVNAnnotationType.LETTERS.equals(type)) {

      } else if (MVNAnnotationType.LINKERMARGEKOLOM.equals(type)) {

      } else if (MVNAnnotationType.METAMARK.equals(type)) {

      } else if (MVNAnnotationType.ONDERSCHRIFT.equals(type)) {

      } else if (MVNAnnotationType.ONDUIDELIJK.equals(type)) {

      } else if (MVNAnnotationType.ONLEESBAAR.equals(type)) {

      } else if (MVNAnnotationType.OPHOGING_ROOD.equals(type)) {

      } else if (MVNAnnotationType.OPSCHRIFT.equals(type)) {

      } else if (MVNAnnotationType.PALEOGRAFISCH.equals(type)) {

      } else if (MVNAnnotationType.POEZIE.equals(type)) {

      } else if (MVNAnnotationType.RECHTERMARGEKOLOM.equals(type)) {

      } else if (MVNAnnotationType.REGELNUMMERING_BLAD.equals(type)) {

      } else if (MVNAnnotationType.REGELNUMMERING_TEKST.equals(type)) {

      } else if (MVNAnnotationType.TEKSTBEGIN.equals(type)) {

      } else if (MVNAnnotationType.TEKSTEINDE.equals(type)) {

      } else if (MVNAnnotationType.TEKSTKLEUR_ROOD.equals(type)) {

      } else if (MVNAnnotationType.VERSREGEL.equals(type)) {

      } else if (MVNAnnotationType.VREEMDTEKEN.equals(type)) {

      } else if (MVNAnnotationType.WITREGEL.equals(type)) {

      } else {
        throw new RuntimeException("uncaught MVNAnnotationType: " + type.getName());
      }
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

}
