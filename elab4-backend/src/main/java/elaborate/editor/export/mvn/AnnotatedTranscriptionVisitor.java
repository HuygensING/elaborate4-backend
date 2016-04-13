package elaborate.editor.export.mvn;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

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
  private static ParseResult result;
  private static final Map<String, Integer> annotationStartIndexMap = Maps.newHashMap();
  private static Map<Integer, AnnotationData> annotationIndex;
  private static int lineStartIndex = 0;

  public AnnotatedTranscriptionVisitor(Map<Integer, AnnotationData> annotationIndex, ParseResult result) {
    super(new XmlContext());
    AnnotatedTranscriptionVisitor.annotationIndex = annotationIndex;
    AnnotatedTranscriptionVisitor.result = result;
    setTextHandler(new TextSegmentHandler());
    setDefaultElementHandler(this);
    addElementHandler(new LineBeginsHandler(), "lb");
    addElementHandler(new LineEndsHandler(), "le");
    addElementHandler(new AnnotationHandler(), "ab", "ae");
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

  public static class TextSegmentHandler extends XmlTextHandler<XmlContext> {
    @Override
    public Traversal visitText(Text text, XmlContext context) {
      String filteredText = filterText(text.getText());
      if (lastNodeWasText) {
        String segment = result.getTextSegments().get(currentTextSegmentIndex());
        result.getTextSegments().set(currentTextSegmentIndex(), segment + filteredText);
      } else {
        result.getTextSegments().add(filteredText);
      }
      lastNodeWasText = true;
      return Traversal.NEXT;
    }
  }

  public static class AnnotationHandler implements ElementHandler<XmlContext> {
    @Override
    public Traversal enterElement(Element element, XmlContext context) {
      lastNodeWasText = false;
      final String id = element.getAttribute("id");
      final String name = element.getName();
      int currentTextSegmentIndex = currentTextSegmentIndex();
      if (Transcription.BodyTags.ANNOTATION_BEGIN.equals(name)) {
        annotationStartIndexMap.put(id, currentTextSegmentIndex + 1); // opening annotation for the next text segment

      } else if (Transcription.BodyTags.ANNOTATION_END.equals(name)) {
        AnnotationData annotationData = annotationIndex.get(Integer.valueOf(id));
        if (annotationData == null) {
          Log.error("no annotationData for {}", id);
        } else {
          Map<String, String> attributes = ImmutableMap.of("body", annotationData.body.trim());
          XmlAnnotation xmlAnnotation = new XmlAnnotation(annotationData.type, attributes, 0)//
              .setFirstSegmentIndex(annotationStartIndexMap.get(id))//
              .setLastSegmentIndex(currentTextSegmentIndex);
          result.getXmlAnnotations().add(xmlAnnotation);
        }
      }
      return Traversal.STOP;
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

}
