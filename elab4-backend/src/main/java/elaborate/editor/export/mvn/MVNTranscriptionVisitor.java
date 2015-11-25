package elaborate.editor.export.mvn;

import static nl.knaw.huygens.tei.Traversal.NEXT;
import static nl.knaw.huygens.tei.Traversal.STOP;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.Transcription;
import elaborate.editor.model.orm.service.AnnotationService;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.tei.DelegatingVisitor;
import nl.knaw.huygens.tei.Element;
import nl.knaw.huygens.tei.ElementHandler;
import nl.knaw.huygens.tei.Text;
import nl.knaw.huygens.tei.TextHandler;
import nl.knaw.huygens.tei.Traversal;
import nl.knaw.huygens.tei.XmlContext;

public class MVNTranscriptionVisitor extends DelegatingVisitor<XmlContext> implements ElementHandler<XmlContext>, TextHandler<XmlContext> {
  private static final String MVN_AFKORTING = "mvn:afkorting";
  private static final String MVN_REGELNUMMERING_BLAD = "mvn:regelnummering (blad)";

  static AnnotationService annotationService = AnnotationService.instance();
  static int lb = 1;
  static boolean firstText = true;
  static List<String> errors = Lists.newArrayList();
  private static String sigle;
  private static boolean ignoreText = false;

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
    Log.warn("ignoring {}", element);
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
    String lbTag = "<lb n=\"" + lb + "\" xml:id=\"" + sigle + "lb" + lb + "\"/>";
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
      String type = annotation.getAnnotationType().getName();
      if (MVN_REGELNUMMERING_BLAD.equals(type)) {
        ignoreText = true;
        String body = annotation.getBody();
        if (StringUtils.isNumeric(body)) {
          lb = Integer.valueOf(body);
        } else {
          errors.add("");
        }

      } else {
        handleFirstLB(context);
        if (MVN_AFKORTING.equals(type)) {
          context.addOpenTag("choice");
          context.addOpenTag("abbr");
          context.addLiteral(annotation.getAnnotatedText().replace("i>", "ex>"));
          context.addCloseTag("abbr");
          context.addOpenTag("expan");
          context.addLiteral(annotation.getBody());
          context.addCloseTag("expan");
          context.addCloseTag("choice");
          ignoreText = true;
        } else {
          context.addOpenTag(new Element("annotation", "type", type));
        }
      }
    }

    private void handleCloseAnnotation(Annotation annotation, XmlContext context) {
      String type = annotation.getAnnotationType().getName();
      if (MVN_REGELNUMMERING_BLAD.equals(type)) {
        ignoreText = false;

      } else if (MVN_AFKORTING.equals(type)) {
        ignoreText = false;

      } else {
        context.addCloseTag("annotation");
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
