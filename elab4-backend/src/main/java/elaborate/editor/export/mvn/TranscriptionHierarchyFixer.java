package elaborate.editor.export.mvn;

import java.util.List;

import com.google.common.collect.Lists;

import elaborate.editor.model.orm.Transcription.BodyTags;
import nl.knaw.huygens.Log;
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
    Log.info("textDecorationFixed={}", textDecorationFixed);

    AnnotationHierarchyVisitor ahVisitor = new AnnotationHierarchyVisitor();
    document = Document.createFromXml("<body>" + textDecorationFixed + "</body>", false);
    textDecorationFixed = traverse(document);
    //    document.accept(ahVisitor);
    //    ahVisitor.status();

    return textDecorationFixed;
  }

  private String traverse(Document document) {
    Element documentRoot = document.getRoot();
    Element resultRoot = Element.copyOf(documentRoot);
    Log.info("root={}", documentRoot);
    traverseNodes((Element) documentRoot.getNodes().get(0), resultRoot, 0);
    return dom2xml(resultRoot);
  }

  private String dom2xml(Element resultRoot) {
    ExportVisitor serializer = new ExportVisitor();
    resultRoot.accept(serializer);
    return serializer.getContext().getResult();
  }

  private void traverseNodes(Element parentElement, Element resultParentElement, int level) {
    List<Grouping> groupings = Lists.newArrayList();
    if (parentElement instanceof Element) {
      for (Node node : parentElement.getNodes()) {
        Log.info("[{}] node={}", level, node);
        if (node instanceof Element) {
          Element element = (Element) node;
          if (isAnnotationMarker(element)) {
            if (BodyTags.ANNOTATION_BEGIN.equals(element.getName())) {
              AnnotationOpenGrouping grouping = currentGrouping(groupings, AnnotationOpenGrouping.class);
              grouping.addId(element.getAttribute("id"));
            } else if (BodyTags.ANNOTATION_END.equals(element.getName())) {
              AnnotationCloseGrouping grouping = currentGrouping(groupings, AnnotationCloseGrouping.class);
              grouping.addId(element.getAttribute("id"));
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
          for (String id : ((AnnotationOpenGrouping) grouping).getIds()) {
            Element element = new Element(BodyTags.ANNOTATION_BEGIN).withAttribute("id", id);
            resultParentElement.addNode(element);
          }
        } else if (grouping instanceof AnnotationCloseGrouping) {
          for (String id : ((AnnotationCloseGrouping) grouping).getIds()) {
            Element element = new Element(BodyTags.ANNOTATION_END).withAttribute("id", id);
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
