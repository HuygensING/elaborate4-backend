package elaborate.editor.export.mvn;

import java.util.ArrayDeque;
import java.util.Iterator;

import nl.knaw.huygens.tei.Comment;
import nl.knaw.huygens.tei.CommentHandler;
import nl.knaw.huygens.tei.DelegatingVisitor;
import nl.knaw.huygens.tei.Element;
import nl.knaw.huygens.tei.ElementHandler;
import nl.knaw.huygens.tei.Text;
import nl.knaw.huygens.tei.TextHandler;
import nl.knaw.huygens.tei.Traversal;
import nl.knaw.huygens.tei.XmlContext;

public class TextDecorationVisitor extends DelegatingVisitor<XmlContext> implements ElementHandler<XmlContext>, CommentHandler<XmlContext>, TextHandler<XmlContext> {

  ArrayDeque<Element> textDecoratorDeque = new ArrayDeque<Element>();

  public TextDecorationVisitor() {
    super(new XmlContext());
    setDefaultElementHandler(this);
    setCommentHandler(this);
    setTextHandler(this);
    addElementHandler(new TextDecorationElementHandler(), "b", "i", "sub", "sup", "em", "strike", "del");
    addElementHandler(new IgnoreElementTagHandler(), "font", "span");
    //    addElementHandler(new SpanHandler(), "span");
  }

  @Override
  public Traversal visitText(Text text, XmlContext context) {
    addDecoratedText(context, text.getText());
    return Traversal.NEXT;
  }

  private void addDecoratedText(XmlContext context, String text) {
    Iterator<Element> descendingIterator = textDecoratorDeque.descendingIterator();
    while (descendingIterator.hasNext()) {
      Element element = descendingIterator.next();
      context.addOpenTag(element);
    }
    context.addLiteral(text);
    for (Element element : textDecoratorDeque) {
      context.addCloseTag(element);
    }
  }

  @Override
  public Traversal visitComment(Comment comment, XmlContext context) {
    return Traversal.NEXT;
  }

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

  public class TextDecorationElementHandler implements ElementHandler<XmlContext> {
    @Override
    public Traversal enterElement(Element element, XmlContext context) {
      for (String attributeName : element.getAttributeNames()) {
        element.removeAttribute(attributeName);
      }
      textDecoratorDeque.push(element);
      return Traversal.NEXT;
    }

    @Override
    public Traversal leaveElement(Element element, XmlContext context) {
      textDecoratorDeque.pop();
      return Traversal.NEXT;
    }
  }

  public class IgnoreElementTagHandler implements ElementHandler<XmlContext> {
    @Override
    public Traversal enterElement(Element element, XmlContext context) {
      return Traversal.NEXT;
    }

    @Override
    public Traversal leaveElement(Element element, XmlContext context) {
      return Traversal.NEXT;
    }
  }

  //  public class SpanHandler extends IgnoreElementTagHandler {
  //    @Override
  //    public Traversal enterElement(Element element, XmlContext context) {
  //      return Traversal.nextIf(element.hasAttribute("data-marker"));
  //    }
  //
  //  }

}
