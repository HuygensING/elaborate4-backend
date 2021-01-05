package elaborate.editor.model;

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

public class ProjectMetadataFields {
  public static final String TYPE = "projectType";
  public static final String TEXT_FONT = "text.font";
  public static final String PUBLICATION_TITLE = "publication.title";

  public static final String ENTRYTERM_SINGULAR = "entry.term_singular";
  public static final String ENTRYTERM_PLURAL = "entry.term_plural";

  public static final String PUBLISHABLE_ANNOTATION_TYPE_IDS = "publishableAnnotationTypeIds";
  public static final String PUBLISHABLE_PROJECT_ENTRY_METADATA_FIELDS = "publishableProjectEntryMetadataFields";
  public static final String FACETABLE_PROJECT_ENTRY_METADATA_FIELDS = "facetableProjectEntryMetadataFields";
  public static final String PUBLISHABLE_TEXT_LAYERS = "publishableTextLayers";
  public static final String MULTIVALUED_METADATA_FIELDS = "multivaluedProjectEntryMetadataFields";

  public static final String ANNOTATIONTYPE_BOLD_DESCRIPTION = "annotationtype.b.description";
  public static final String ANNOTATIONTYPE_BOLD_NAME = "annotationtype.b.name";
  public static final String ANNOTATIONTYPE_ITALIC_DESCRIPTION = "annotationtype.i.description";
  public static final String ANNOTATIONTYPE_ITALIC_NAME = "annotationtype.i.name";
  public static final String ANNOTATIONTYPE_STRIKE_DESCRIPTION = "annotationtype.strike.description";
  public static final String ANNOTATIONTYPE_STRIKE_NAME = "annotationtype.strike.name";
  public static final String ANNOTATIONTYPE_UNDERLINE_DESCRIPTION = "annotationtype.u.description";
  public static final String ANNOTATIONTYPE_UNDERLINE_NAME = "annotationtype.u.name";
  public static final String[] ANNOTATIONTYPE_FIELDS = { //
      ANNOTATIONTYPE_BOLD_DESCRIPTION, ANNOTATIONTYPE_BOLD_NAME, //
      ANNOTATIONTYPE_ITALIC_DESCRIPTION, ANNOTATIONTYPE_ITALIC_NAME, //
      ANNOTATIONTYPE_UNDERLINE_DESCRIPTION, ANNOTATIONTYPE_UNDERLINE_NAME, //
      ANNOTATIONTYPE_STRIKE_DESCRIPTION, ANNOTATIONTYPE_STRIKE_NAME //
  };
  public static final String MVN_PLACENAME = "mvn.placeName";
  public static final String MVN_INSTITUTION = "mvn.institution";
  public static final String MVN_IDNO = "mvn.idno";
  public static final String MVN_SUBTITLE = "mvn.subtitle";

}
