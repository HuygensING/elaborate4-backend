package elaborate.editor.export.mvn;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2021 Huygens ING
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

import elaborate.editor.model.orm.*;
import nl.knaw.huygens.*;
import nl.knaw.huygens.tei.*;

import java.util.Map.*;

class AnnotationHierarchyVisitor extends DelegatingVisitor<XmlContext> implements ElementHandler<XmlContext>, CommentHandler<XmlContext>, TextHandler<XmlContext> {
  private final Element root = new Element("xml");
  private Element currentElement;

  public AnnotationHierarchyVisitor() {
    super(new XmlContext());
    setDefaultElementHandler(this);
    setCommentHandler(this);
    setTextHandler(this);
    addElementHandler(new AnnotationBeginHandler(), Transcription.BodyTags.ANNOTATION_BEGIN);
    addElementHandler(new AnnotationEndHandler(), Transcription.BodyTags.ANNOTATION_END);
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

  public static class AnnotationBeginHandler implements ElementHandler<XmlContext> {
    @Override
    public Traversal enterElement(Element element, XmlContext context) {
      return Traversal.NEXT;
    }

    @Override
    public Traversal leaveElement(Element element, XmlContext context) {
      return Traversal.NEXT;
    }
  }

  public static class AnnotationEndHandler implements ElementHandler<XmlContext> {
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
