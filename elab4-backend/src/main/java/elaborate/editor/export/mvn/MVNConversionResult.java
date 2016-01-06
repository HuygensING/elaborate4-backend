package elaborate.editor.export.mvn;

import org.apache.commons.lang.StringUtils;

import elaborate.editor.model.ProjectMetadataFields;
import elaborate.editor.model.orm.Project;
import elaborate.editor.publish.Publication.Status;
import elaborate.freemarker.FreeMarker;

public class MVNConversionResult {
  private static final String TEMPLATE = "mvn.tei.ftl";
  private final String title;
  private final String idno;
  private final String sigle;
  private String body = "";
  private final String baseURL;
  private final Status logger;

  public MVNConversionResult(final Project project, final Status logger) {
    this.logger = logger;
    this.baseURL = "https://www.elaborate.huygens.knaw.nl/projects/" + project.getName();
    this.title = project.getMetadataMap().get(ProjectMetadataFields.PUBLICATION_TITLE);
    if (StringUtils.isEmpty(this.title)) {
      logger.addError("project has no publication title");
    }
    this.idno = project.getTitle();
    if (StringUtils.isEmpty(this.idno)) {
      logger.addError("project has no title");
    }
    this.sigle = project.getName().toUpperCase();
    if (StringUtils.isEmpty(this.sigle)) {
      logger.addError("project has no name");
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
    logger.addError(url(entryId) + " : " + error);
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
