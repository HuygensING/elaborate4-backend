package elaborate.editor.export.mvn;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2019 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.common.collect.Lists;
import nl.knaw.huygens.tei.*;
import nl.knaw.huygens.tei.handlers.RenderElementHandler;
import nl.knaw.huygens.tei.handlers.XmlTextHandler;

import java.util.*;

import static elaborate.editor.model.orm.Transcription.BodyTags.ANNOTATION_BEGIN;
import static elaborate.editor.model.orm.Transcription.BodyTags.ANNOTATION_END;

public class AnnotationHierarchyRepairingVisitor extends DelegatingVisitor<XmlContext> implements CommentHandler<XmlContext> {
  final Stack<String> openAnnotationStack = new Stack<String>();
  final Deque<Element> openElements = new ArrayDeque<Element>();

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
      for (String annotationId : Lists.reverse(annotationsToReopen)) {
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
    for (String annotationId : annotationsToReopen) {
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
      for (String annotationId : openAnnotationStack) {
        addAnnotationBeginElement(context, annotationId);
      }
    }

    private void addOpenOtherElements(XmlContext context) {
      for (Element element : openElements) {
        context.addOpenTag(element);
      }

    }
  }

  public class LineEndHandler extends RenderElementHandler {
    @Override
    public Traversal enterElement(Element element, XmlContext context) {
      addCloseOtherElements(context, openElements);
      addAnnotationEnds(context, Lists.reverse(openAnnotationStack));
      return super.enterElement(element, context);
    }

    private void addCloseOtherElements(XmlContext context, Deque<Element> openElements) {
      Iterator<Element> descendingIterator = openElements.descendingIterator();
      while (descendingIterator.hasNext()) {
        Element element = descendingIterator.next();
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
