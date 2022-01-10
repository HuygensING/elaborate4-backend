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

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import elaborate.editor.model.ProjectMetadataFields;
import elaborate.editor.model.orm.Project;
import elaborate.editor.publish.Publication.Status;
import elaborate.freemarker.FreeMarker;

public class MVNConversionResult {
  private static final String TEMPLATE = "mvn.tei.ftl";
  private final String place;
  private final String institution;
  private final String idno;
  private final String subtitle;
  private final String sigle;
  private final String baseURL;
  private final Status logger;
  private String title;
  private String body = "";

  public MVNConversionResult(final Project project, final Status logger, final String baseURL) {
    this.logger = logger;
    this.baseURL = baseURL + "/projects/" + project.getName();

    Map<String, String> projectMetadata = project.getMetadataMap();
    this.title = projectMetadata.get(ProjectMetadataFields.PUBLICATION_TITLE);
    if (StringUtils.isEmpty(this.title)) {
      logger.addError("project has no publication title, using project title");
      this.title = project.getTitle();
    }

    this.sigle = project.getName().toUpperCase();
    if (StringUtils.isEmpty(this.sigle)) {
      logger.addError("project has no name");
    }

    this.place =
        StringUtils.defaultIfBlank(projectMetadata.get(ProjectMetadataFields.MVN_PLACENAME), "");
    this.institution =
        StringUtils.defaultIfBlank(projectMetadata.get(ProjectMetadataFields.MVN_INSTITUTION), "");
    this.idno = StringUtils.defaultIfBlank(projectMetadata.get(ProjectMetadataFields.MVN_IDNO), "");
    this.subtitle =
        StringUtils.defaultIfBlank(projectMetadata.get(ProjectMetadataFields.MVN_SUBTITLE), "");
  }

  public String getTitle() {
    return title;
  }

  public String getSubtitle() {
    return subtitle;
  }

  public String getPlace() {
    return place;
  }

  public String getInstitution() {
    return institution;
  }

  public String getIdno() {
    return idno;
  }

  public String getSigle() {
    return sigle;
  }

  public void setBody(final String teibody) {
    body = teibody;
  }

  public String getBody() {
    return body;
  }

  //  private static final Comparator<MVNFolium> PAGE_ORDER = new Comparator<MVNFolium>() {

  //    @Override
  //    public int compare(MVNFolium t1, MVNFolium t2) {
  //      return t1.getOrder().compareTo(t2.getOrder());
  //    }
  //  };

  //  public List<MVNFolium> getPages() {
  //    Collections.sort(pages, PAGE_ORDER);
  //    return pages;
  //  }

  //  public void addPages(MVNFolium page) {
  //    pages.add(page);
  //  }

  public void addError(final String entryId, final String error) {
    if (StringUtils.isNotBlank(entryId)) {
      logger.addError(url(entryId) + " : " + error);
    } else {
      logger.addError(error);
    }
  }

  public String getTEI() {
    return FreeMarker.templateToString(TEMPLATE, this, getClass());
  }

  public boolean isOK() {
    return logger.getErrors().isEmpty();
  }

  public Status getStatus() {
    return logger;
  }

  /* private methods */

  private String url(final String entryId) {
    if (StringUtils.isNotBlank(entryId)) {
      return baseURL + "/entries/" + entryId + "/transcriptions/diplomatic";
    }
    return baseURL;
  }
}
