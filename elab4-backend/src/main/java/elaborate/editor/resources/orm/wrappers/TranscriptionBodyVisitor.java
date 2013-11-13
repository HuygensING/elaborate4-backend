package elaborate.editor.resources.orm.wrappers;

import static nl.knaw.huygens.tei.Traversal.NEXT;
import static nl.knaw.huygens.tei.Traversal.STOP;

import java.util.List;

import nl.knaw.huygens.tei.DelegatingVisitor;
import nl.knaw.huygens.tei.Element;
import nl.knaw.huygens.tei.ElementHandler;
import nl.knaw.huygens.tei.Traversal;
import nl.knaw.huygens.tei.XmlContext;
import nl.knaw.huygens.tei.handlers.RenderElementHandler;
import nl.knaw.huygens.tei.handlers.XmlTextHandler;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

import elaborate.editor.model.orm.Transcription;

public class TranscriptionBodyVisitor extends DelegatingVisitor<XmlContext> {
  private static int notenum;
  private static List<Integer> annotationIds;

  public TranscriptionBodyVisitor() {
    super(new XmlContext());
    notenum = 1;
    annotationIds = Lists.newArrayList();
    setTextHandler(new XmlTextHandler<XmlContext>());
    setDefaultElementHandler(new RenderElementHandler());
    addElementHandler(new IgnoreHandler(), Transcription.BodyTags.BODY);
    addElementHandler(new AnnotationBeginHandler(), Transcription.BodyTags.ANNOTATION_BEGIN);
    addElementHandler(new AnnotationEndHandler(), Transcription.BodyTags.ANNOTATION_END);
  }

  private static class IgnoreHandler implements ElementHandler<XmlContext> {
    @Override
    public Traversal enterElement(Element e, XmlContext c) {
      return NEXT;
    }

    @Override
    public Traversal leaveElement(Element e, XmlContext c) {
      return NEXT;
    }
  }

  private static class AnnotationBeginHandler implements ElementHandler<XmlContext> {
    private static final String TAG_SPAN = "span";

    @Override
    public Traversal enterElement(Element e, XmlContext c) {
      String id = e.getAttribute("id");
      if (StringUtils.isNotBlank(id)) {
        Element span = new Element(TAG_SPAN);
        span.setAttribute("data-marker", "begin");
        span.setAttribute("data-id", id);
        c.addOpenTag(span);
      }
      return STOP;
    }

    @Override
    public Traversal leaveElement(Element e, XmlContext c) {
      String id = e.getAttribute("id");
      if (StringUtils.isNotBlank(id)) {
        c.addCloseTag(TAG_SPAN);
      }
      return NEXT;
    }
  }

  private static class AnnotationEndHandler implements ElementHandler<XmlContext> {
    private static final String TAG_SUP = "sup";

    @Override
    public Traversal enterElement(Element e, XmlContext c) {
      String id = e.getAttribute("id");
      if (StringUtils.isNotBlank(id)) {
        Element sup = new Element(TAG_SUP);
        sup.setAttribute("data-marker", "end");
        sup.setAttribute("data-id", id);
        c.addOpenTag(sup);
      }
      return STOP;
    }

    @Override
    public Traversal leaveElement(Element e, XmlContext c) {
      String id = e.getAttribute("id");
      if (StringUtils.isNotBlank(id)) {
        annotationIds.add(Integer.valueOf(id));
        c.addLiteral(notenum++);
        c.addCloseTag(TAG_SUP);
      }
      return NEXT;
    }
  }

  public List<Integer> getAnnotationIds() {
    return annotationIds;
  }

}
