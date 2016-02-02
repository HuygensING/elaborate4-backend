package elaborate.editor.export.mvn;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

import elaborate.editor.model.orm.Transcription.BodyTags;
import nl.knaw.huygens.tei.Document;
import nl.knaw.huygens.tei.Element;
import nl.knaw.huygens.tei.Node;
import nl.knaw.huygens.tei.Text;
import nl.knaw.huygens.tei.export.ExportVisitor;

public class TranscriptionHierarchyFixer {

  public String fix(String xml) {
    TextDecorationVisitor visitor = new TextDecorationVisitor();
    Document document = Document.createFromXml(xml, false);
    document.accept(visitor);
    String textDecorationFixed = visitor.getContext().getResult();
    //    Log.info("textDecorationFixed={}", textDecorationFixed);

    document = Document.createFromXml(textDecorationFixed, false);
    return traverse(document);//.replaceAll("<body>", "").replaceAll("</body>", "");
  }

  private String traverse(Document document) {
    Element documentRoot = document.getRoot();
    Element resultRoot = Element.copyOf(documentRoot);
    //    Log.info("root={}", documentRoot);
    //    Element parentElement = (Element) documentRoot.getNodes().get(0);
    traverseNodes(documentRoot, resultRoot, 0);
    return dom2xml(resultRoot);
  }

  private String dom2xml(Element resultRoot) {
    ExportVisitor serializer = new ExportVisitor();
    resultRoot.accept(serializer);
    return serializer.getContext().getResult();
  }

  private void traverseNodes(Element parentElement, Element resultParentElement, int level) {
    List<Grouping> groupings = Lists.newArrayList();
    final Map<String, Integer> annotationOpenIndex = Maps.newHashMap();
    final Map<String, Integer> annotationCloseIndex = Maps.newHashMap();

    Ordering<String> annotationOpenOrdering = new Ordering<String>() {
      @Override
      public int compare(String id1, String id2) {
        return Ints.compare(annotationCloseIndex.get(id2), annotationCloseIndex.get(id1));
      }
    }.compound(new Ordering<String>() {
      @Override
      public int compare(String id1, String id2) {
        return Ints.compare(annotationOpenIndex.get(id1), annotationOpenIndex.get(id2));
      }
    }).compound(Ordering.natural());
    Ordering<String> annotationCloseOrdering = annotationOpenOrdering.reverse();

    if (parentElement instanceof Element) {
      String annotationBegin = BodyTags.ANNOTATION_BEGIN;
      String annotationEnd = BodyTags.ANNOTATION_END;
      for (Node node : parentElement.getNodes()) {
        //        Log.info("[{}] node={}", level, node);
        if (node instanceof Element) {
          Element element = (Element) node;
          if (isAnnotationMarker(element)) {
            String id = element.getAttribute("id");
            if (annotationBegin.equals(element.getName())) {
              AnnotationOpenGrouping grouping = currentGrouping(groupings, AnnotationOpenGrouping.class);
              grouping.addId(id);
              annotationOpenIndex.put(id, groupings.size());
            } else if (annotationEnd.equals(element.getName())) {
              AnnotationCloseGrouping grouping = currentGrouping(groupings, AnnotationCloseGrouping.class);
              grouping.addId(id);
              annotationCloseIndex.put(id, groupings.size());
            }
          } else {
            NodeGrouping grouping = currentGrouping(groupings, NodeGrouping.class);
            grouping.addNode(element);
          }
        } else if (node instanceof Text) {
          NodeGrouping grouping = currentGrouping(groupings, NodeGrouping.class);
          grouping.addNode(node);
        }
      }
      for (Grouping grouping : groupings) {
        if (grouping instanceof NodeGrouping) {
          NodeGrouping nodeGrouping = (NodeGrouping) grouping;
          List<Node> nodes = nodeGrouping.getNodes();
          for (Node node : nodes) {
            if (node instanceof Element) {
              Element element = (Element) node;
              Element returnElement = Element.copyOf(element);
              resultParentElement.addNode(returnElement);
              if (element.hasChildren()) {
                traverseNodes(element, returnElement, level + 1);
              }
            } else {
              resultParentElement.addNode(new Text(((Text) node).getText()));
            }
          }
        } else if (grouping instanceof AnnotationOpenGrouping) {
          List<String> ids = ((AnnotationOpenGrouping) grouping).getIds();
          for (String id : annotationOpenOrdering.sortedCopy(ids)) {
            Element element = new Element(annotationBegin).withAttribute("id", id);
            resultParentElement.addNode(element);
          }
        } else if (grouping instanceof AnnotationCloseGrouping) {
          List<String> ids = ((AnnotationCloseGrouping) grouping).getIds();
          for (String id : annotationCloseOrdering.sortedCopy(ids)) {
            Element element = new Element(annotationEnd).withAttribute("id", id);
            resultParentElement.addNode(element);
          }
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends Grouping> T currentGrouping(List<Grouping> groupings, Class<T> groupingClass) {
    T grouping;
    if (groupings.isEmpty()) {
      grouping = newGrouping(groupings, groupingClass);
    } else {
      Grouping currentGrouping = groupings.get(groupings.size() - 1);
      if (groupingClass.isInstance(currentGrouping)) {
        grouping = (T) currentGrouping;
      } else {
        grouping = newGrouping(groupings, groupingClass);
      }
    }
    return grouping;
  }

  private <T extends Grouping> T newGrouping(List<Grouping> groupings, Class<T> groupingClass) {
    T grouping;
    try {
      grouping = groupingClass.newInstance();
      groupings.add(grouping);
    } catch (InstantiationException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return grouping;
  }

  private boolean isAnnotationMarker(Element element) {
    return BodyTags.ANNOTATION_MARKERS.contains(element.getName());
  }

  // Grouping classes
  static interface Grouping {}

  static final class NodeGrouping implements Grouping {
    private final List<Node> nodes = Lists.newArrayList();

    public void addNode(Node node) {
      getNodes().add(node);
    }

    public List<Node> getNodes() {
      return nodes;
    }
  }

  static abstract class AnnotationGrouping implements Grouping {
    private final List<String> ids = Lists.newArrayList();

    public void addId(String id) {
      getIds().add(id);
    }

    public List<String> getIds() {
      return ids;
    }
  }

  static final class AnnotationOpenGrouping extends AnnotationGrouping {};

  static final class AnnotationCloseGrouping extends AnnotationGrouping {};

}
