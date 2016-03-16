package elaborate.editor.export.mvn;

import java.util.Set;
import java.util.Stack;

import com.google.common.collect.Sets;

import nl.knaw.huygens.tei.Comment;
import nl.knaw.huygens.tei.CommentHandler;
import nl.knaw.huygens.tei.DelegatingVisitor;
import nl.knaw.huygens.tei.Element;
import nl.knaw.huygens.tei.ElementHandler;
import nl.knaw.huygens.tei.Text;
import nl.knaw.huygens.tei.TextHandler;
import nl.knaw.huygens.tei.Traversal;
import nl.knaw.huygens.tei.XmlContext;

public class AnnotatedTextRangeVisitor extends DelegatingVisitor<XmlContext> implements ElementHandler<XmlContext>, TextHandler<XmlContext>, CommentHandler<XmlContext> {
  private final Set<RangeAnnotation> rangeAnnotations = Sets.newLinkedHashSet();
  private final Stack<Integer> elementOffsetStack = new Stack<Integer>();
  //  private final Stack<Integer> elementOrderStack = new Stack<Integer>();
  //  private int elementOrder = 0;

  public AnnotatedTextRangeVisitor() {
    super(new XmlContext());

    setTextHandler(this);
    setDefaultElementHandler(this);
    setCommentHandler(this);
  }

  @Override
  public Traversal enterElement(final Element element, final XmlContext context) {
    int offSet = context.getResult().length();
    elementOffsetStack.push(offSet);
    //    elementOrderStack.push(elementOrder++);
    return Traversal.NEXT;
  }

  @Override
  public Traversal leaveElement(final Element element, final XmlContext context) {
    int startOffset = elementOffsetStack.pop();
    int currentOffset = context.getResult().length();
    rangeAnnotations.add(new RangeAnnotation(0/*elementOrderStack.pop()*/).setElement(element).setStartOffset(startOffset).setEndOffset(currentOffset));
    return Traversal.NEXT;
  }

  @Override
  public Traversal visitText(final Text text, final XmlContext context) {
    String normalized = text.getText();
    context.addLiteral(normalized);
    return Traversal.NEXT;
  }

  @Override
  public Traversal visitComment(final Comment comment, final XmlContext context) {
    return Traversal.NEXT;
  }

  public String getText() {
    return getContext().getResult();
  }

  public Set<RangeAnnotation> getRangeAnnotations() {
    return rangeAnnotations;
  }

}
