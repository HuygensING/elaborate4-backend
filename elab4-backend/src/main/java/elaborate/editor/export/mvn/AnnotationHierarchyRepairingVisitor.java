package elaborate.editor.export.mvn;

import static elaborate.editor.model.orm.Transcription.BodyTags.ANNOTATION_BEGIN;
import static elaborate.editor.model.orm.Transcription.BodyTags.ANNOTATION_END;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import com.google.common.collect.Lists;

import nl.knaw.huygens.tei.Comment;
import nl.knaw.huygens.tei.CommentHandler;
import nl.knaw.huygens.tei.DelegatingVisitor;
import nl.knaw.huygens.tei.Element;
import nl.knaw.huygens.tei.Traversal;
import nl.knaw.huygens.tei.XmlContext;
import nl.knaw.huygens.tei.handlers.RenderElementHandler;
import nl.knaw.huygens.tei.handlers.XmlTextHandler;

public class AnnotationHierarchyRepairingVisitor extends DelegatingVisitor<XmlContext> implements CommentHandler<XmlContext> {
  Stack<String> openAnnotationStack = new Stack<String>();
  Stack<Element> openElements = new Stack<Element>();

  public AnnotationHierarchyRepairingVisitor() {
    super(new XmlContext());
    setDefaultElementHandler(new OtherElementHandler());
    setCommentHandler(this);
    setTextHandler(new XmlTextHandler<XmlContext>());
    addElementHandler(new LineBeginHandler(), "lb");
    addElementHandler(new LineEndHandler(), "le");
    addElementHandler(new AnnotationBeginHandler(), ANNOTATION_BEGIN);
    addElementHandler(new AnnotationEndHandler(), ANNOTATION_END);
  }

  @Override
  public Traversal visitComment(Comment comment, XmlContext context) {
    return Traversal.NEXT;
  }

  public class AnnotationBeginHandler extends RenderElementHandler {
    @Override
    public Traversal enterElement(Element element, XmlContext context) {
      String id = element.getAttribute("id");
      openAnnotationStack.push(id);
      return super.enterElement(element, context);
    }

  }

  public class AnnotationEndHandler extends RenderElementHandler {
    @Override
    public Traversal enterElement(Element element, XmlContext context) {
      String id = element.getAttribute("id");
      List<String> annotationsToReopen = new ArrayList<String>();
      while (!openAnnotationStack.peek().equals(id)) {
        String annotationId = openAnnotationStack.pop();
        annotationsToReopen.add(annotationId);
      }
      openAnnotationStack.pop();
      if (annotationsToReopen.isEmpty()) {
        return super.enterElement(element, context);

      } else {
        addAnnotationEnds(context, annotationsToReopen);
        context.addEmptyElementTag(element);
        addAnnotationBegins(context, annotationsToReopen);
        return Traversal.NEXT;
      }
    }

    private void addAnnotationBegins(XmlContext context, List<String> annotationsToReopen) {
      Iterator<String> openIterator = Lists.reverse(annotationsToReopen).iterator();
      while (openIterator.hasNext()) {
        String annotationId = openIterator.next();
        openAnnotationStack.push(annotationId);
        addAnnotationBeginElement(context, annotationId);
      }
    }

  }

  private void addAnnotationBeginElement(XmlContext context, String annotationId) {
    Element openAnnotation = new Element(ANNOTATION_BEGIN).withAttribute("id", annotationId);
    context.addEmptyElementTag(openAnnotation);
  }

  private void addAnnotationEnds(XmlContext context, List<String> annotationsToReopen) {
    Iterator<String> closeIterator = annotationsToReopen.iterator();
    while (closeIterator.hasNext()) {
      String annotationId = closeIterator.next();
      addAnnotationEndElement(context, annotationId);
    }
  }

  private void addAnnotationEndElement(XmlContext context, String annotationId) {
    Element closeAnnotation = new Element(ANNOTATION_END).withAttribute("id", annotationId);
    context.addEmptyElementTag(closeAnnotation);
  }

  public class LineBeginHandler extends RenderElementHandler {
    @Override
    public Traversal enterElement(Element element, XmlContext context) {
      context.addEmptyElementTag(element);
      addAnnotationBegins(context);
      addOpenOtherElements(context);
      return Traversal.NEXT;
    }

    private void addAnnotationBegins(XmlContext context) {
      Iterator<String> openIterator = openAnnotationStack.iterator();
      while (openIterator.hasNext()) {
        String annotationId = openIterator.next();
        addAnnotationBeginElement(context, annotationId);
      }
    }

    private void addOpenOtherElements(XmlContext context) {
      Iterator<Element> elementIterator = openElements.iterator();
      while (elementIterator.hasNext()) {
        Element element = elementIterator.next();
        context.addOpenTag(element);
      }

    }
  }

  public class LineEndHandler extends RenderElementHandler {
    @Override
    public Traversal enterElement(Element element, XmlContext context) {
      addCloseOtherElements(context, Lists.reverse(openElements));
      addAnnotationEnds(context, Lists.reverse(openAnnotationStack));
      return super.enterElement(element, context);
    }

    private void addCloseOtherElements(XmlContext context, List<Element> elementsToClose) {
      for (Element element : elementsToClose) {
        context.addCloseTag(element);
      }
    }
  }

  public class OtherElementHandler extends RenderElementHandler {
    @Override
    public Traversal enterElement(Element element, XmlContext context) {
      if (isRelevant(element)) {
        openElements.add(element);
      }
      return super.enterElement(element, context);
    }

    private boolean isRelevant(Element element) {
      return element.getParent() != null && element.hasChildren();
    }

    @Override
    public Traversal leaveElement(Element element, XmlContext context) {
      if (isRelevant(element)) {
        openElements.pop();
      }
      return super.leaveElement(element, context);
    }
  }

  // remove empty annotations
  public String getRepairedXml() {
    return getContext().getResult().replaceAll("<ab id=\"([0-9]+)\"/><ae id=\"\\1\"/>", "");
  }

}
