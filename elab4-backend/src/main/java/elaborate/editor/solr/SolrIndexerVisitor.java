package elaborate.editor.solr;

import nl.knaw.huygens.tei.DelegatingVisitor;
import nl.knaw.huygens.tei.Element;
import nl.knaw.huygens.tei.ElementHandler;
import nl.knaw.huygens.tei.Traversal;
import nl.knaw.huygens.tei.XmlContext;

public class SolrIndexerVisitor extends DelegatingVisitor<XmlContext> {

  public SolrIndexerVisitor() {
    super(new XmlContext());
    setDefaultElementHandler(new DefaultHandler());
    //    addElementHandler(spanHandler(), Element.SPAN_TAG, Element.DIV_TAG);
  }

  static class DefaultHandler implements ElementHandler<XmlContext> {

    @Override
    public Traversal enterElement(Element e, XmlContext c) {
      return Traversal.NEXT;
    }

    @Override
    public Traversal leaveElement(Element e, XmlContext c) {
      return Traversal.NEXT;
    }
  }

}
