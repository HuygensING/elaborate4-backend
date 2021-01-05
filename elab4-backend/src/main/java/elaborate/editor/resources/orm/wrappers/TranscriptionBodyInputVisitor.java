package elaborate.editor.resources.orm.wrappers;

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

import static nl.knaw.huygens.tei.Traversal.NEXT;
import static nl.knaw.huygens.tei.Traversal.STOP;

import elaborate.editor.model.orm.Transcription;
import nl.knaw.huygens.tei.Comment;
import nl.knaw.huygens.tei.CommentHandler;
import nl.knaw.huygens.tei.DelegatingVisitor;
import nl.knaw.huygens.tei.Element;
import nl.knaw.huygens.tei.ElementHandler;
import nl.knaw.huygens.tei.Traversal;
import nl.knaw.huygens.tei.XmlContext;
import nl.knaw.huygens.tei.handlers.RenderElementHandler;
import nl.knaw.huygens.tei.handlers.XmlTextHandler;

class TranscriptionBodyInputVisitor extends DelegatingVisitor<XmlContext> implements CommentHandler<XmlContext> {
  private static final String TAG_SUP = "sup";
  private static final String TAG_SPAN = "span";
  private static final String ATTR_DATA_MARKER = "data-marker";
  private static final String ATTR_DATA_ID = "data-id";

  public TranscriptionBodyInputVisitor() {
    super(new XmlContext());
    setTextHandler(new XmlTextHandler<XmlContext>());
    setDefaultElementHandler(new RenderElementHandler());
    setCommentHandler(this);
    addElementHandler(new SpanHandler(), TAG_SPAN);
    addElementHandler(new SupHandler(), TAG_SUP);
    addElementHandler(new BrHandler(), "br");
    addElementHandler(new IgnoreElementHandler(), "a", "div", "font", "h1", "h2", "h3", "h4", "h5", "h6", "p", "pre", //
        "style", "table", "tbody", "td", "tr");
    addElementHandler(new IgnoreElementAttributesHandler(), "i", "b", "strike", "del", "s", "sub", "u");
    addElementHandler(new ConvertToHandler("b"), "strong");
    addElementHandler(new ConvertToHandler("i"), "em");
  }

  @Override
  public Traversal visitComment(Comment comment, XmlContext context) {
    return Traversal.NEXT;
  }

  private static class SpanHandler implements ElementHandler<XmlContext> {
    @Override
    public Traversal enterElement(Element e, XmlContext c) {
      if (isBeginMarker(e)) {
        String id = e.getAttribute(ATTR_DATA_ID);
        Element ab = new Element(Transcription.BodyTags.ANNOTATION_BEGIN);
        ab.setAttribute("id", id);
        c.addEmptyElementTag(ab);
      } else {
        // all other spans should be ignored
        // c.addOpenTag(e);
      }
      return NEXT;
    }

    @Override
    public Traversal leaveElement(Element e, XmlContext c) {
      // if (!isBeginMarker(e)) {
      // c.addCloseTag(TAG_SPAN);
      // }
      return NEXT;
    }

    private boolean isBeginMarker(Element e) {
      return "begin".equals(e.getAttribute(ATTR_DATA_MARKER));
    }
  }

  private static class SupHandler implements ElementHandler<XmlContext> {
    @Override
    public Traversal enterElement(Element e, XmlContext c) {
      if (isEndMarker(e)) {
        String id = e.getAttribute(ATTR_DATA_ID);
        Element ae = new Element(Transcription.BodyTags.ANNOTATION_END);
        ae.setAttribute("id", id);
        c.addEmptyElementTag(ae);
        return STOP;
      } else {
        c.addOpenTag(e.getName());
        return NEXT;
      }
    }

    private boolean isEndMarker(Element e) {
      return "end".equals(e.getAttribute(ATTR_DATA_MARKER));
    }

    @Override
    public Traversal leaveElement(Element e, XmlContext c) {
      if (!isEndMarker(e)) {
        c.addCloseTag(e);
      }
      return NEXT;
    }
  }

  private static class BrHandler implements ElementHandler<XmlContext> {
    @Override
    public Traversal enterElement(Element e, XmlContext c) {
      c.addLiteral("\n");
      return STOP;
    }

    @Override
    public Traversal leaveElement(Element arg0, XmlContext arg1) {
      return NEXT;
    }

  }

  private static class IgnoreElementHandler implements ElementHandler<XmlContext> {
    @Override
    public Traversal enterElement(Element e, XmlContext c) {
      return NEXT;
    }

    @Override
    public Traversal leaveElement(Element arg0, XmlContext arg1) {
      return NEXT;
    }
  }

  private static class IgnoreElementAttributesHandler implements ElementHandler<XmlContext> {
    @Override
    public Traversal enterElement(Element e, XmlContext c) {
      c.addOpenTag(e.getName());
      return NEXT;
    }

    @Override
    public Traversal leaveElement(Element e, XmlContext c) {
      c.addCloseTag(e);
      return NEXT;
    }
  }

  private static class ConvertToHandler implements ElementHandler<XmlContext> {
    private final String newTag;

    public ConvertToHandler(String newTag) {
      this.newTag = newTag;
    }

    @Override
    public Traversal enterElement(Element e, XmlContext c) {
      c.addOpenTag(newTag);
      return NEXT;
    }

    @Override
    public Traversal leaveElement(Element e, XmlContext c) {
      c.addCloseTag(newTag);
      return NEXT;
    }
  }

}
