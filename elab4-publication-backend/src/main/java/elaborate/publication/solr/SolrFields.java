package elaborate.publication.solr;

/*
 * #%L
 * elab4-publication-backend
 * =======
 * Copyright (C) 2013 - 2014 Huygens ING
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


public class SolrFields {
  public static final String SORT_PREFIX = "sort_";

  public static final String UNKNOWN_VALUE = "unknown";
  public static final String DOC_ID = "id";

  /** Field used by Solr for refering to relevance of search results. */
  public static final String SCORE = "score";

  //  /** Field for storing letter texts. */
  //  public static final String TEXT = "text";

  public static final String PROJECT_ID = "project_id";
  static final String ID = "id";
  static final String NAME = "name";
  static final String PUBLISHABLE = "publishable";

  public static final String METADATAFIELD_PREFIX = "metadata_";
  static final String TEXTLAYER_PREFIX = "textlayer_";
  static final String TEXTLAYERCS_PREFIX = "textlayercs_";
  static final String ANNOTATION_PREFIX = "annotations_";
  static final String ANNOTATIONCS_PREFIX = "annotationscs_";

  private SolrFields() {
    throw new AssertionError("Non-instantiable class");
  }

}
