package elaborate.editor.export.mvn;

import java.util.Stack;

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

  public AnnotatedTranscriptionVisitor(ParseResult result) {
    super(new XmlContext());
    AnnotatedTranscriptionVisitor.result = result;
    setTextHandler(new TextSegmentHandler());
    setDefaultElementHandler(this);
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
        .setLastSegmentIndex(result.getTextSegments().size() - 1)//
        ;
    result.getXmlAnnotations().add(xmlAnnotation);
    return Traversal.NEXT;
  }

  public static class TextSegmentHandler extends XmlTextHandler<XmlContext> {
    @Override
    public Traversal visitText(Text text, XmlContext context) {
      String filteredText = filterText(text.getText());
      if (lastNodeWasText) {
        int lastIndex = result.getTextSegments().size() - 1;
        String segment = result.getTextSegments().get(lastIndex);
        result.getTextSegments().set(lastIndex, segment + filteredText);
      } else {
        result.getTextSegments().add(filteredText);
      }
      lastNodeWasText = true;
      return Traversal.NEXT;
    }
  }

}
