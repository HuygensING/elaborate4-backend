package elaborate.editor.export.tei;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2022 Huygens ING
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

import java.util.TreeSet;

import nl.knaw.huygens.Log;
import nl.knaw.huygens.tei.DelegatingVisitor;
import nl.knaw.huygens.tei.Document;
import nl.knaw.huygens.tei.Element;
import nl.knaw.huygens.tei.ElementHandler;
import nl.knaw.huygens.tei.Traversal;
import nl.knaw.huygens.tei.XmlContext;
import nl.knaw.huygens.tei.handlers.XmlTextHandler;
import elaborate.util.XmlUtil;

import static nl.knaw.huygens.tei.Traversal.NEXT;

public class AnnotationBodyConverter {
  static final TreeSet<String> unhandledTags = new TreeSet<>();

  @SuppressWarnings("synthetic-access")
  public static String convert(String xml) {
    String fixedXml = XmlUtil.fixXhtml(XmlUtil.wrapInXml(xml));
    try {
      Document document = Document.createFromXml(fixedXml, false);

      DelegatingVisitor<XmlContext> visitor = new DelegatingVisitor<>(new XmlContext());
      visitor.setTextHandler(new XmlTextHandler<>());
      visitor.setDefaultElementHandler(new DefaultElementHandler());
      visitor.addElementHandler(new IgnoreElementHandler(), "xml", "span");
      visitor.addElementHandler(
          new HiHandler(), TeiMaker.HI_TAGS.keySet().toArray(new String[] {}));
      visitor.addElementHandler(new DelHandler(), "strike");
      visitor.addElementHandler(new BrHandler(), "br");

      document.accept(visitor);
      if (!unhandledTags.isEmpty()) {
        Log.warn("unhandled tags: {} for annotation body {}", unhandledTags, fixedXml);
        unhandledTags.clear();
      }
      return visitor.getContext().getResult();

    } catch (Exception e) {
      e.printStackTrace();
      return " :: error in parsing annotation body ::" + e.getMessage();
    }
  }

  private static class DefaultElementHandler implements ElementHandler<XmlContext> {
    @Override
    public Traversal enterElement(Element e, XmlContext c) {
      unhandledTags.add(e.getName());
      c.addOpenTag(e);
      return NEXT;
    }

    @Override
    public Traversal leaveElement(Element e, XmlContext c) {
      c.addCloseTag(e);
      return NEXT;
    }
  }

  private static class BrHandler implements ElementHandler<XmlContext> {
    @Override
    public Traversal enterElement(Element e, XmlContext c) {
      return NEXT;
    }

    @Override
    public Traversal leaveElement(Element e, XmlContext c) {
      c.addEmptyElementTag("lb");
      return NEXT;
    }
  }

  private static class DelHandler implements ElementHandler<XmlContext> {
    private static final String DEL = "del";

    @Override
    public Traversal enterElement(Element e, XmlContext c) {
      c.addOpenTag(DEL);
      return NEXT;
    }

    @Override
    public Traversal leaveElement(Element e, XmlContext c) {
      c.addCloseTag(DEL);
      return NEXT;
    }
  }

  private static class HiHandler implements ElementHandler<XmlContext> {
    @Override
    public Traversal enterElement(Element e, XmlContext c) {
      Element hi = hiElement(e);
      c.addOpenTag(hi);
      return NEXT;
    }

    @Override
    public Traversal leaveElement(Element e, XmlContext c) {
      Element hi = hiElement(e);
      c.addCloseTag(hi);
      return NEXT;
    }

    private Element hiElement(Element e) {
      return new Element("hi", "rend", TeiMaker.HI_TAGS.get(e.getName()));
    }
  }

  private static class IgnoreElementHandler implements ElementHandler<XmlContext> {

    @Override
    public Traversal enterElement(Element e, XmlContext c) {
      return NEXT;
    }

    @Override
    public Traversal leaveElement(Element e, XmlContext c) {
      return NEXT;
    }
  }
}
