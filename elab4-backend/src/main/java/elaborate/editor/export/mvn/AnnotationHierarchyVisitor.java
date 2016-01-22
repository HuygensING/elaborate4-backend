package elaborate.editor.export.mvn;

import java.util.Map.Entry;

import nl.knaw.huygens.Log;
import nl.knaw.huygens.tei.Comment;
import nl.knaw.huygens.tei.CommentHandler;
import nl.knaw.huygens.tei.DelegatingVisitor;
import nl.knaw.huygens.tei.Element;
import nl.knaw.huygens.tei.ElementHandler;
import nl.knaw.huygens.tei.Text;
import nl.knaw.huygens.tei.TextHandler;
import nl.knaw.huygens.tei.Traversal;
import nl.knaw.huygens.tei.XmlContext;

public class AnnotationHierarchyVisitor extends DelegatingVisitor<XmlContext> implements ElementHandler<XmlContext>, CommentHandler<XmlContext>, TextHandler<XmlContext> {
  private final Element root = new Element("xml");
  private Element currentElement;

  public AnnotationHierarchyVisitor() {
    super(new XmlContext());
    setDefaultElementHandler(this);
    setCommentHandler(this);
    setTextHandler(this);
    addElementHandler(new AnnotationBeginHandler(), "ab");
    addElementHandler(new AnnotationEndHandler(), "ae");
    currentElement = root;
  }

  @Override
  public Traversal visitText(Text text, XmlContext context) {
    Text newText = new Text(text.getText());
    newText.setParent(currentElement);
    currentElement.addNode(newText);
    return Traversal.NEXT;
  }

  @Override
  public Traversal visitComment(Comment comment, XmlContext context) {
    return Traversal.NEXT;
  }

  @Override
  public Traversal enterElement(Element element, XmlContext context) {
    Element newElement = new Element(element.getName());
    newElement.setParent(currentElement);
    for (Entry<String, String> entry : element.getAttributes().entrySet()) {
      newElement.setAttribute(entry.getKey(), entry.getValue());
    }
    currentElement.addNode(newElement);
    currentElement = newElement;
    return Traversal.NEXT;
  }

  @Override
  public Traversal leaveElement(Element element, XmlContext context) {
    currentElement = currentElement.getParent();
    return Traversal.NEXT;
  }

  public class AnnotationBeginHandler implements ElementHandler<XmlContext> {
    @Override
    public Traversal enterElement(Element element, XmlContext context) {
      return Traversal.NEXT;
    }

    @Override
    public Traversal leaveElement(Element element, XmlContext context) {
      return Traversal.NEXT;
    }
  }

  public class AnnotationEndHandler implements ElementHandler<XmlContext> {
    @Override
    public Traversal enterElement(Element element, XmlContext context) {
      return Traversal.NEXT;
    }

    @Override
    public Traversal leaveElement(Element element, XmlContext context) {
      return Traversal.NEXT;
    }
  }

  public void status() {
    Log.info("root={}", root);
  }

}
