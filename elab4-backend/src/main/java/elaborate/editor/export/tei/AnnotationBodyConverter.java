package elaborate.editor.export.tei;

import static nl.knaw.huygens.tei.Traversal.NEXT;

import java.util.TreeSet;

import nl.knaw.huygens.tei.DelegatingVisitor;
import nl.knaw.huygens.tei.Document;
import nl.knaw.huygens.tei.Element;
import nl.knaw.huygens.tei.ElementHandler;
import nl.knaw.huygens.tei.Traversal;
import nl.knaw.huygens.tei.XmlContext;
import nl.knaw.huygens.tei.handlers.XmlTextHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import elaborate.util.XmlUtil;

public class AnnotationBodyConverter {
  static Logger LOG = LoggerFactory.getLogger(AnnotationBodyConverter.class);
  static TreeSet<String> unhandledTags = new TreeSet<String>();

  @SuppressWarnings("synthetic-access")
  public static String convert(String xml) {
    String fixedXml = XmlUtil.fixXhtml(XmlUtil.wrapInXml(xml));
    try {
      Document document = Document.createFromXml(fixedXml, false);

      DelegatingVisitor<XmlContext> visitor = new DelegatingVisitor<XmlContext>(new XmlContext());
      visitor.setTextHandler(new XmlTextHandler<XmlContext>());
      visitor.setDefaultElementHandler(new DefaultElementHandler());
      visitor.addElementHandler(new IgnoreElementHandler(), "xml", "span");
      visitor.addElementHandler(new HiHandler(), TeiMaker.HI_TAGS.keySet().toArray(new String[] {}));
      visitor.addElementHandler(new DelHandler(), "strike");
      visitor.addElementHandler(new BrHandler(), "br");

      document.accept(visitor);
      if (!unhandledTags.isEmpty()) {
        LOG.warn("unhandled tags: {} for annotation body {}", unhandledTags, fixedXml);
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
