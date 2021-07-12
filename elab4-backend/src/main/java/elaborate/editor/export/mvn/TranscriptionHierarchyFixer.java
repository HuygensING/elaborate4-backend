package elaborate.editor.export.mvn;

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


import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

import nl.knaw.huygens.tei.Document;
import nl.knaw.huygens.tei.Element;
import nl.knaw.huygens.tei.Node;
import nl.knaw.huygens.tei.Text;
import nl.knaw.huygens.tei.export.ExportVisitor;

import elaborate.editor.model.orm.Transcription.BodyTags;

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
      for (Node node : parentElement.getNodes()) {
        //        Log.info("[{}] node={}", level, node);
        if (node instanceof Element && isAnnotationMarker((Element) node)) {
          Element element = (Element) node;
          String id = element.getAttribute("id");
          String name = element.getName();
          if (BodyTags.ANNOTATION_BEGIN.equals(name)) {
            processAnnotationElement(groupings, id, AnnotationOpenGrouping.class, annotationOpenIndex);

          } else if (BodyTags.ANNOTATION_END.equals(name)) {
            processAnnotationElement(groupings, id, AnnotationCloseGrouping.class, annotationCloseIndex);
          }

        } else {
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
          addAnnotationMilestone(resultParentElement, grouping, annotationOpenOrdering, BodyTags.ANNOTATION_BEGIN);

        } else if (grouping instanceof AnnotationCloseGrouping) {
          addAnnotationMilestone(resultParentElement, grouping, annotationCloseOrdering, BodyTags.ANNOTATION_END);
        }
      }
    }
  }

  private void processAnnotationElement(List<Grouping> groupings, String id, Class<? extends AnnotationGrouping> groupingClass, Map<String, Integer> index) {
    AnnotationGrouping grouping = currentGrouping(groupings, groupingClass);
    grouping.addId(id);
    index.put(id, groupings.size());
  }

  private void addAnnotationMilestone(Element resultParentElement, Grouping grouping, Ordering<String> ordering, String tagName) {
    List<String> ids = ((AnnotationGrouping) grouping).getIds();
    for (String id : ordering.sortedCopy(ids)) {
      Element element = new Element(tagName).withAttribute("id", id);
      resultParentElement.addNode(element);
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
  interface Grouping {}

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

  static final class AnnotationOpenGrouping extends AnnotationGrouping {}

  static final class AnnotationCloseGrouping extends AnnotationGrouping {}

}
