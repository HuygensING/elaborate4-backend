package elaborate.editor.export.mvn;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import elaborate.editor.export.mvn.MVNConversionData.AnnotationData;
import elaborate.editor.model.orm.Transcription;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.tei.DelegatingVisitor;
import nl.knaw.huygens.tei.Element;
import nl.knaw.huygens.tei.ElementHandler;
import nl.knaw.huygens.tei.Text;
import nl.knaw.huygens.tei.Traversal;
import nl.knaw.huygens.tei.XmlContext;
import nl.knaw.huygens.tei.handlers.XmlTextHandler;

public class AnnotatedTranscriptionVisitor extends DelegatingVisitor<XmlContext> implements ElementHandler<XmlContext> {
  private static boolean lastNodeWasText = false;
  private final Stack<Integer> startIndexStack = new Stack<Integer>();
  private final Stack<Element> elementStack = new Stack<Element>();
  public static Map<String, XmlAnnotation> textRangeAnnotationIndex = Maps.newHashMap();
  private static String sigle;
  private static ParseResult result;
  private static final Map<String, Integer> annotationStartIndexMap = Maps.newHashMap();
  private static Map<Integer, AnnotationData> annotationIndex;
  private static int lineStartIndex = 0;
  private static Deque<XmlAnnotation> poetryOrParagraphAnnotations = new ArrayDeque<XmlAnnotation>();

  public AnnotatedTranscriptionVisitor(Map<Integer, AnnotationData> annotationIndex, ParseResult result, String sigle) {
    super(new XmlContext());
    AnnotatedTranscriptionVisitor.annotationIndex = annotationIndex;
    AnnotatedTranscriptionVisitor.result = result;
    AnnotatedTranscriptionVisitor.sigle = sigle;
    setTextHandler(new TextSegmentHandler());
    setDefaultElementHandler(this);
    addElementHandler(new LineBeginsHandler(), "lb");
    addElementHandler(new LineEndsHandler(), "le");
    addElementHandler(new AnnotationHandler(), "ab", "ae");
    lineStartIndex = 0;
    poetryOrParagraphAnnotations.clear();
  }

  @Override
  public Traversal enterElement(final Element element, final XmlContext context) {
    elementStack.add(element);
    lastNodeWasText = false;
    startIndexStack.push(result.getTextSegments().size());
    if (element.hasNoChildren()) {
      result.getTextSegments().add("");
    }
    return Traversal.NEXT;
  }

  @Override
  public Traversal leaveElement(final Element element, final XmlContext context) {
    elementStack.pop();
    lastNodeWasText = false;
    XmlAnnotation xmlAnnotation = new XmlAnnotation(element.getName(), element.getAttributes(), elementStack.size())//
        .setMilestone(element.hasNoChildren())//
        .setFirstSegmentIndex(startIndexStack.pop())//
        .setLastSegmentIndex(currentTextSegmentIndex());
    result.getXmlAnnotations().add(xmlAnnotation);
    return Traversal.NEXT;
  }

  static final List<String> ANNOTATED_TEXT_TO_IGNORE = ImmutableList.<String> of("‡", "¤");

  public static class TextSegmentHandler extends XmlTextHandler<XmlContext> {
    @Override
    public Traversal visitText(Text text, XmlContext context) {
      String filteredText = filterText(text.getText());
      if (lastNodeWasText) {
        String segment = result.getTextSegments().get(currentTextSegmentIndex());
        result.getTextSegments().set(currentTextSegmentIndex(), segment + filteredText);
      } else {
        if (!ANNOTATED_TEXT_TO_IGNORE.contains(filteredText)) {
          result.getTextSegments().add(filteredText);
        }
      }
      lastNodeWasText = true;
      return Traversal.NEXT;
    }
  }

  static final List<String> POETRY_AND_PARAGRAPH_CLOSERS = ImmutableList.of(//
      MVNAnnotationType.ALINEA.getName(), //
      MVNAnnotationType.POEZIE.getName(), //
      MVNAnnotationType.ONDERSCHRIFT.getName(), //
      MVNAnnotationType.OPSCHRIFT.getName()//
  );

  public static class AnnotationHandler implements ElementHandler<XmlContext> {
    @Override
    public Traversal enterElement(Element element, XmlContext context) {
      lastNodeWasText = false;
      final String id = element.getAttribute("id");
      final String name = element.getName();
      if (Transcription.BodyTags.ANNOTATION_BEGIN.equals(name)) {
        handleAnnotationBegin(id);
      } else if (Transcription.BodyTags.ANNOTATION_END.equals(name)) {
        handleAnnotationEnd(id);
      }
      return Traversal.STOP;
    }

    private void handleAnnotationBegin(final String id) {
      int currentTextSegmentIndex = currentTextSegmentIndex();
      annotationStartIndexMap.put(id, currentTextSegmentIndex + 1); // opening annotation for the next text segment
      AnnotationData annotationData = annotationIndex.get(Integer.valueOf(id));
      if (annotationData == null) {
        Log.error("no annotationData for {}", id);

      } else {
        if (POETRY_AND_PARAGRAPH_CLOSERS.contains(annotationData.type)) {
          closeOpenPoetryOrParagraph();
        }
      }
    }

    private void handleAnnotationEnd(final String id) {
      AnnotationData annotationData = annotationIndex.get(Integer.valueOf(id));
      if (annotationData == null) {
        Log.error("no annotationData for {}", id);
        return;
      }

      String annotationBody = StringUtils.defaultIfEmpty(annotationData.body, "").trim();
      MVNAnnotationType mvnAnnotationType = MVNAnnotationType.fromName(annotationData.type);
      switch (mvnAnnotationType) {
        case TEKSTBEGIN:
          handleTekstBegin(annotationBody);
          break;

        case TEKSTEINDE:
          handleTekstEinde(annotationBody);
          break;

        case POEZIE:
        case ALINEA:
          handlePoezieAndAlinea(annotationData);
          break;

        default:
          handleDefault(id, annotationData, annotationBody);
          break;
      }
    }

    private void closeOpenPoetryOrParagraph() {
      if (poetryOrParagraphAnnotations.isEmpty()) return;

      XmlAnnotation lastAnnotation = poetryOrParagraphAnnotations.getLast();
      if (lastAnnotation.getLastSegmentIndex() == null) {
        lastAnnotation.setLastSegmentIndex(currentTextSegmentIndex());
        result.getXmlAnnotations().add(lastAnnotation);
      }
    }

    private static void handleTekstBegin(String annotationBody) {
      String n = annotationBody.replaceFirst(";.*$", "");
      Map<String, String> attributes = ImmutableMap.of("n", n, "xml:id", sigle + n);
      XmlAnnotation tekstAnnotation = new XmlAnnotation("tekst", attributes, 0)//
          .setFirstSegmentIndex(currentTextSegmentIndex() + 1);
      textRangeAnnotationIndex.put(n, tekstAnnotation);
    }

    private void handleTekstEinde(String annotationBody) {
      closeOpenPoetryOrParagraph();
      final String n = annotationBody;
      final XmlAnnotation tekstAnnotation = textRangeAnnotationIndex.remove(n);
      if (tekstAnnotation != null) {
        tekstAnnotation.setLastSegmentIndex(currentTextSegmentIndex());
        result.getXmlAnnotations().add(tekstAnnotation);
      } else {
        Log.error("tekst {} was not opened", n);
      }
    }

    private void handlePoezieAndAlinea(AnnotationData annotationData) {
      // puntannotatie, so ignore the annotated textsegment
      removeCurrentTextSegment();
      // start poezie or paragraph
      closeOpenPoetryOrParagraph();
      Map<String, String> attributes = new HashMap<String, String>();
      poetryOrParagraphAnnotations.add(new XmlAnnotation(annotationData.type, attributes, 0)//
          .setFirstSegmentIndex(currentTextSegmentIndex() + 1));
    }

    private void handleDefault(final String id, AnnotationData annotationData, String annotationBody) {
      if (POETRY_AND_PARAGRAPH_CLOSERS.contains(annotationData.type)) {
        closeOpenPoetryOrParagraph();
      }
      Map<String, String> attributes = ImmutableMap.of("body", annotationBody);
      XmlAnnotation xmlAnnotation = new XmlAnnotation(annotationData.type, attributes, 0)//
          .setFirstSegmentIndex(annotationStartIndexMap.get(id))//
          .setLastSegmentIndex(currentTextSegmentIndex());
      result.getXmlAnnotations().add(xmlAnnotation);
    }

    @Override
    public Traversal leaveElement(Element element, XmlContext context) {
      return Traversal.NEXT;
    }
  }

  public static class LineBeginsHandler implements ElementHandler<XmlContext> {
    @Override
    public Traversal enterElement(Element element, XmlContext context) {
      lastNodeWasText = false;
      lineStartIndex = result.getTextSegments().size();
      return Traversal.STOP;
    }

    @Override
    public Traversal leaveElement(Element element, XmlContext context) {
      return Traversal.NEXT;
    }
  }

  public static class LineEndsHandler implements ElementHandler<XmlContext> {
    @Override
    public Traversal enterElement(Element element, XmlContext context) {
      lastNodeWasText = false;
      Map<String, String> attributes = new HashMap<String, String>();
      XmlAnnotation xmlAnnotation = new XmlAnnotation("l", attributes, 0)//
          .setFirstSegmentIndex(lineStartIndex)//
          .setLastSegmentIndex(currentTextSegmentIndex());
      result.getXmlAnnotations().add(xmlAnnotation);
      return Traversal.STOP;
    }

    @Override
    public Traversal leaveElement(Element element, XmlContext context) {
      return Traversal.NEXT;
    }

  }

  private static int currentTextSegmentIndex() {
    return result.getTextSegments().size() - 1;
  }

  private static void removeCurrentTextSegment() {
    List<String> textSegments = result.getTextSegments();
    if (!textSegments.isEmpty()) {
      textSegments.remove(currentTextSegmentIndex());
    }
  }

}
