package elaborate.editor.export.mvn;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2019 Huygens ING
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
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MVNConversionData {

  public static class EntryData {
    String id;
    String name;
    String body;
    String facs;
    String order;
  }

  public static class AnnotationData {
    String type;
    String body;
  }

  private final Map<Integer, AnnotationData> annotationIndex = Maps.newHashMap();
  private final List<EntryData> entryDataList = Lists.newArrayList();
  private final Set<String> deepestTextNums = Sets.newHashSet();

  public List<EntryData> getEntryDataList() {
    return entryDataList;
  }

  public Map<Integer, AnnotationData> getAnnotationIndex() {
    return annotationIndex;
  }

  public Set<String> getDeepestTextNums() {
    return deepestTextNums;
  }

}
