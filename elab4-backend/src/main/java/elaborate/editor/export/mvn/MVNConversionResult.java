package elaborate.editor.export.mvn;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2016 Huygens ING
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

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

import elaborate.editor.model.ProjectMetadataFields;
import elaborate.editor.model.orm.Project;
import elaborate.freemarker.FreeMarker;

public class MVNConversionResult {
  private static final String TEMPLATE = "mvn.tei.ftl";
  private final String title;
  private final String idno;
  private final String sigle;
  private String body = "";
  private final String baseURL;
  private final List<String> errors = Lists.newArrayList();
  //  private final List<MVNFolium> pages = Lists.newArrayList();

  public MVNConversionResult(final Project project) {
    this.baseURL = "https://www.elaborate.huygens.knaw.nl/projects/" + project.getName();
    this.title = project.getMetadataMap().get(ProjectMetadataFields.PUBLICATION_TITLE);
    if (StringUtils.isEmpty(this.title)) {
      errors.add("project has no title");
    }
    this.idno = project.getTitle();
    if (StringUtils.isEmpty(this.idno)) {
      errors.add("project has no publication title");
    }
    this.sigle = project.getName();
    if (StringUtils.isEmpty(this.sigle)) {
      errors.add("project has no publication name");
    }
  }

  public String getTitle() {
    return title;
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

  public List<String> getErrors() {
    return errors;
  }

  //  private static final Comparator<MVNFolium> PAGE_ORDER = new Comparator<MVNFolium>() {
  //
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
    errors.add(url(entryId) + " : " + error);
  }

  public String getTEI() {
    return FreeMarker.templateToString(TEMPLATE, this, getClass());
  }

  public boolean isOK() {
    return errors.isEmpty();
  }

  /* private methods */

  private String url(final String entryId) {
    return baseURL + "/entries/" + entryId + "/transcriptions/diplomatic";
  }

}
