package elaborate.editor.export.mvn;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import elaborate.util.XmlUtil;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.tei.Document;
import nl.knaw.huygens.tei.Element;

public class AnnotatedTextRange {
  public Multimap<Integer, Element> elementStartIndex = LinkedHashMultimap.create();
  public Multimap<Integer, Element> elementEndIndex = LinkedHashMultimap.create();
  private Set<RangeAnnotation> rangeAnnotations = Sets.newLinkedHashSet();
  private String text;

  public AnnotatedTextRange(String xml) {
    disassemble(xml);
  }

  void disassemble(String xml) {
    AnnotatedTextRangeVisitor v = new AnnotatedTextRangeVisitor();
    Document document = Document.createFromXml(xml, true);
    document.accept(v);
    text = v.getText();
    rangeAnnotations = v.getRangeAnnotations();
    index();
    Log.info("text=[{}]", text);
    Log.info("rangeAnnotations=\n{}\n", rangeAnnotations);
  }

  public String getText() {
    return text;
  }

  public Set<RangeAnnotation> getRangeAnnotations() {
    return rangeAnnotations;
  }

  public Iterator<TextPositionInfo> iterator() {
    return new Iterator<AnnotatedTextRange.TextPositionInfo>() {
      int i = 0;

      @Override
      public boolean hasNext() {
        return i <= text.length();
      }

      @Override
      public TextPositionInfo next() {
        List<Element> elementsToOpen = elementsStartingAt(i);
        List<Element> elementsToClose = elementsEndingAt(i);
        TextPositionInfo tpi;
        if (i < text.length()) {
          char character = text.charAt(i);
          tpi = new TextPositionInfo(character, elementsToClose, elementsToOpen);
        } else {
          tpi = new TextPositionInfo(elementsToClose);
        }
        i++;
        return tpi;
      }
    };
  }

  public String reassemble() {
    StringBuilder builder = new StringBuilder();
    Iterator<TextPositionInfo> iterator = iterator();
    while (iterator.hasNext()) {
      TextPositionInfo tpi = iterator.next();
      for (Element element : tpi.getClosingElements()) {
        builder.append(XmlUtil.closingTag(element));
      }
      for (Element element : tpi.getOpeningElements()) {
        builder.append(XmlUtil.openingTag(element));
      }
      if (tpi.hasCharacter()) {
        builder.append(tpi.getCharacter());
      }
    }
    return builder.toString();
  }

  public void joinAdjacentAnnotations() {
    Iterator<TextPositionInfo> iterator = iterator();
    while (iterator.hasNext()) {
      TextPositionInfo tpi = iterator.next();
      List<Element> closingElements = tpi.getClosingElements();
      List<Element> openingElements = tpi.getOpeningElements();
      if (closingElements.size() > 0 && openingElements.size() > 0) {
        // TODO: implement!
      }
    }
  }

  void addOpenElements(StringBuilder b, int i) {
    List<Element> elementsToOpen = elementsStartingAt(i);
    for (Element element : elementsToOpen) {
      b.append(XmlUtil.openingTag(element));
    }
  }

  void addCloseElements(StringBuilder b, int length) {
    List<Element> elementsToClose = elementsEndingAt(length);
    for (Element element : elementsToClose) {
      b.append(XmlUtil.closingTag(element));
    }
  }

  void index() {
    for (RangeAnnotation rangeAnnotation : rangeAnnotations) {
      elementStartIndex.put(rangeAnnotation.getStartOffset(), rangeAnnotation.getElement());
      elementEndIndex.put(rangeAnnotation.getEndOffset(), rangeAnnotation.getElement());
    }
  }

  private List<Element> elementsStartingAt(int i) {
    if (elementStartIndex.containsKey(i)) {
      return Lists.reverse(Lists.newArrayList(elementStartIndex.get(i)));
    } else {
      return Collections.emptyList();
    }
  }

  private List<Element> elementsEndingAt(int i) {
    if (elementEndIndex.containsKey(i)) {
      return Lists.newArrayList(elementEndIndex.get(i));
    } else {
      return Collections.emptyList();
    }
  }

  public static class TextPositionInfo {
    private final char character;
    private final List<Element> openingElements;
    private final List<Element> closingElements;
    private final boolean hasCharacter;

    public TextPositionInfo(char character, List<Element> closingElements, List<Element> openingElements) {
      this.character = character;
      this.closingElements = closingElements;
      this.openingElements = openingElements;
      this.hasCharacter = true;
    }

    public TextPositionInfo(List<Element> closingElements) {
      this.closingElements = closingElements;
      this.hasCharacter = false;
      this.character = '\0';
      this.openingElements = Collections.emptyList();
    }

    public char getCharacter() {
      return character;
    }

    public boolean hasCharacter() {
      return hasCharacter;
    }

    public List<Element> getOpeningElements() {
      return openingElements;
    }

    public List<Element> getClosingElements() {
      return closingElements;
    }

  }

}