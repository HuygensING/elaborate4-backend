package elaborate.editor.export.mvn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class ParseResult {
  private final List<String> textSegments = new ArrayList<String>();
  private final List<XmlAnnotation> xmlAnnotations = new ArrayList<XmlAnnotation>();
  private final Multimap<Integer, XmlAnnotation> openingAnnotationIndex = ArrayListMultimap.create();
  private final Multimap<Integer, XmlAnnotation> closingAnnotationIndex = ArrayListMultimap.create();
  private boolean indexed = false;

  public List<String> getTextSegments() {
    return textSegments;
  }

  public List<XmlAnnotation> getXmlAnnotations() {
    return xmlAnnotations;
  }

  public void index() {
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      openingAnnotationIndex.put(xmlAnnotation.getFirstSegmentIndex(), xmlAnnotation);
      closingAnnotationIndex.put(xmlAnnotation.getLastSegmentIndex(), xmlAnnotation);
    }
    indexed = true;
  }

  public Iterator<AnnotatedTextSegment> getAnnotatedTextSegmentIterator() {
    Preconditions.checkArgument(indexed);
    return new Iterator<AnnotatedTextSegment>() {
      Integer index = 0;

      @Override
      public boolean hasNext() {
        return index < textSegments.size();
      }

      @Override
      public AnnotatedTextSegment next() {
        AnnotatedTextSegment ats = new AnnotatedTextSegment(textSegments.get(index))//
            .withOpeningAnnotations(openingAnnotationIndex.get(index))//
            .withClosingAnnotations(closingAnnotationIndex.get(index));
        index++;
        return ats;
      }

      @Override
      public void remove() {}
    };
  }

}
