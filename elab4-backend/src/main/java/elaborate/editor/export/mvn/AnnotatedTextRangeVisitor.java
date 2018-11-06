package elaborate.editor.export.mvn;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2018 Huygens ING
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

import com.google.common.collect.Sets;
import nl.knaw.huygens.tei.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;

public class AnnotatedTextRangeVisitor extends DelegatingVisitor<XmlContext> implements ElementHandler<XmlContext>, TextHandler<XmlContext>, CommentHandler<XmlContext> {
  private final Set<RangeAnnotation> rangeAnnotations = Sets.newLinkedHashSet();
  private final Deque<Integer> elementOffsetStack = new ArrayDeque<Integer>();

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
    return Traversal.NEXT;
  }

  @Override
  public Traversal leaveElement(final Element element, final XmlContext context) {
    int startOffset = elementOffsetStack.pop();
    int currentOffset = context.getResult().length();
    rangeAnnotations.add(new RangeAnnotation().setElement(element).setStartOffset(startOffset).setEndOffset(currentOffset));
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
