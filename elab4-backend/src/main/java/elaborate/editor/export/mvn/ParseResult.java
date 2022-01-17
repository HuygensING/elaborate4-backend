package elaborate.editor.export.mvn;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import nl.knaw.huygens.Log;

public class ParseResult {
  private final List<String> textSegments = new ArrayList<>();
  private final List<XmlAnnotation> xmlAnnotations = new ArrayList<>();
  private final Multimap<Integer, XmlAnnotation> openingAnnotationIndex =
      ArrayListMultimap.create();
  private final Multimap<Integer, XmlAnnotation> closingAnnotationIndex =
      ArrayListMultimap.create();
  private boolean indexed = false;
  private final List<String> deepestTekstNs = Lists.newArrayList();

  public List<String> getTextSegments() {
    return textSegments;
  }

  public List<XmlAnnotation> getXmlAnnotations() {
    return xmlAnnotations;
  }

  public void index() {
    Collections.sort(xmlAnnotations);
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      if ("tekst".equals(xmlAnnotation.getName())) {
        Log.info("textAnnotation={}", xmlAnnotation);
        String n = xmlAnnotation.getAttributes().get("n");
        if (!deepestTekstNs.isEmpty()) {
          int index = deepestTekstNs.size() - 1;
          String lastN = deepestTekstNs.get(index);
          if (n.startsWith(lastN + ".")) {
            deepestTekstNs.remove(index);
          }
        }
        deepestTekstNs.add(n);
      }
    }
    Set<String> annotationNames = Sets.newTreeSet();
    for (XmlAnnotation xmlAnnotation : xmlAnnotations) {
      openingAnnotationIndex.put(xmlAnnotation.getFirstSegmentIndex(), xmlAnnotation);
      closingAnnotationIndex.put(xmlAnnotation.getLastSegmentIndex(), xmlAnnotation);
      annotationNames.add(xmlAnnotation.getName());
    }
    Log.info("annotationNames = {}", annotationNames);
    indexed = true;
  }

  public boolean isTextGroup(String tekstN) {
    return !deepestTekstNs.contains(tekstN);
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
        AnnotatedTextSegment ats =
            new AnnotatedTextSegment(textSegments.get(index))
                .withOpeningAnnotations(openingAnnotationIndex.get(index))
                .withClosingAnnotations(closingAnnotationIndex.get(index));
        index++;
        return ats;
      }

      @Override
      public void remove() {}
    };
  }
}
