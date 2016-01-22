package elaborate.editor.export.mvn;

import java.util.List;

import nl.knaw.huygens.Log;
import nl.knaw.huygens.tei.Document;
import nl.knaw.huygens.tei.Element;
import nl.knaw.huygens.tei.Node;

public class TranscriptionHierarchyFixer {

  public String fix(String xml) {
    TextDecorationVisitor visitor = new TextDecorationVisitor();
    Document document = Document.createFromXml(xml, false);
    document.accept(visitor);
    String textDecorationFixed = visitor.getContext().getResult();
    Log.info("textDecorationFixed={}", textDecorationFixed);

    AnnotationHierarchyVisitor ahVisitor = new AnnotationHierarchyVisitor();
    document = Document.createFromXml("<body>" + textDecorationFixed + "</body>", false);
    traverse(document);
    document.accept(ahVisitor);
    ahVisitor.status();

    return textDecorationFixed;
  }

  private void traverse(Document document) {
    Element documentRoot = document.getRoot();
    Element resultRoot = Element.copyOf(documentRoot);
    Log.info("root={}", documentRoot);
    traverseNodes(documentRoot, resultRoot, 0);
  }

  private void traverseNodes(Element parentElement, Element resultElement, int level) {
    if (parentElement instanceof Element) {
      Element parent = parentElement;
      List<Node> nodes = parent.getNodes();
      for (int j = 0; j < nodes.size(); j++) {
        Node node = nodes.get(j);
        if (j > 1 && j < nodes.size()) {
          Node left = nodes.get(j - 1);
          if (left instanceof Element) {
            Element leftElement = (Element) left;
            if (leftElement.getName().equals("ab")) {}
          }
          Node right = nodes.get(j + 1);
        }
      }
      for (Node node : nodes) {
        Log.info("{} node='{}' ({})", level, node, node.getClass());
        if (node instanceof Element) {
          Element child = (Element) node;

        } else {

        }
      }
      level++;
      for (Node node : nodes) {
        if (node instanceof Element) {
          traverseNodes((Element) node, resultElement, level);
        }
      }
    }
  }

}
