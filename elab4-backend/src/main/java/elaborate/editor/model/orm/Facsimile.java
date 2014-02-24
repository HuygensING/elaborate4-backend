package elaborate.editor.model.orm;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2014 Huygens ING
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

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

import elaborate.editor.model.AbstractProjectEntryElement;

@Entity
@Table(name = "facsimiles")
@XmlRootElement
public class Facsimile extends AbstractProjectEntryElement<Facsimile> {
  private static final long serialVersionUID = 1L;
  public static final String SORT_PROPERTY_FILENAME = "filename";
  public static final String SORT_PROPERTY_NAME = "name";
  public static final String SORT_PROPERTY_TITLE = "title";
  public static final String SORT_PROPERTY_CREATOR = "creator_id";
  public static final String SORT_PROPERTY_CREATED_ON = "created_on";

  public static final String THUMBNAIL_PREFIX = "t100_";
  public static final String BROWSE_IMAGE_PREFIX = "t500_";

  String name;
  String filename;
  String zoomableUrl;

  String title;// not used
  String thumbnailUrl;// not used

  public String getName() {
    return name;
  }

  public Facsimile setName(String name) {
    this.name = name;
    return this;
  }

  public String getTitle() {
    return title;
  }

  @JsonIgnore
  public Facsimile setTitle(String title) {
    this.title = title;
    return this;
  }

  public String getZoomableUrl() {
    return zoomableUrl;
  }

  public Facsimile setZoomableUrl(String zoomableUrl) {
    this.zoomableUrl = zoomableUrl;
    return this;
  }

  @JsonIgnore
  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  public Facsimile setThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
    return this;
  }

  public String getFilename() {
    return filename;
  }

  public Facsimile setFilename(String filename) {
    this.filename = filename;
    return this;
  }

  //
  //  public String getThumbnailFilename() {
  //    return Facsimile.THUMBNAIL_PREFIX + filenameWithoutExtension() + JPG;
  //  }
  //
  //
  //  //  public String getJp2URL() {
  //  //    //    return "http://memory.loc.gov/gmd/gmd433/g4330/g4330/np000066.jp2";
  //  //    return Configuration.getSetting(Configuration.JP2SERVER) + facsimile.getId() + ".jp2";
  //  //  }
  //

}
