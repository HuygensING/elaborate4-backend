package elaborate.editor.export.mvn;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import elaborate.editor.model.orm.Facsimile;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.Transcription;
import elaborate.editor.model.orm.service.AnnotationService;
import nl.knaw.huygens.Log;
import nl.knaw.huygens.tei.Document;
import nl.knaw.huygens.tei.XmlContext;

public class MVNConverter {
  private final Project project;
  private final AnnotationService annotationService;

  public MVNConverter(Project project, AnnotationService annotationService) {
    this.project = project;
    this.annotationService = annotationService;
  }

  // fase 1: collect MVNFolium with raw transcription 
  // order by 'order' or entryname
  // fail when 1, but not all entries have order
  // fail when entrynames are not unique
  // select id, name from project_entries where project_id=$projectId
  // select 

  public MVNConversionResult validate() {
    MVNConversionResult result = new MVNConversionResult(project);
    List<ProjectEntry> projectEntries = project.getProjectEntries();
    for (ProjectEntry entry : projectEntries) {
      MVNFolium page = new MVNFolium();
      String n = entry.getName();
      page.setN(n);
      page.setId(result.getSigle() + "-pb-" + n);
      setFacs(entry, page, result);
      setOrder(page, entry);
      setBody(page, entry, result);
      result.addPages(page);
    }
    return result;
  }

  private void setFacs(ProjectEntry entry, MVNFolium page, MVNConversionResult result) {
    List<Facsimile> facsimiles = entry.getFacsimiles();
    if (facsimiles.isEmpty()) {
      result.addError(entry, "no facsimile");
    } else {
      if (facsimiles.size() > 1) {
        result.addError(entry, "multiple facsimiles, using first");
      }
      page.setFacs(facsimiles.get(0).getName());
    }
  }

  private void setBody(MVNFolium page, ProjectEntry entry, MVNConversionResult result) {
    String body = null;
    for (Transcription transcription : entry.getTranscriptions()) {
      if ("Diplomatic".equals(transcription.getTextLayer())) {
        body = transcription.getBody().replace("&nbsp;", " ");
      } else {
        result.addError(entry, "incorrect textlayer found: " + transcription.getTextLayer());
      }
    }
    Log.info("body=[\n{}\n]", body);
    if (body == null) {
      result.addError(entry, "no Diplomatic textlayer");
    } else {
      page.setBody(toTei(body, entry, result));
    }
    Log.info("body=[\n{}\n]", page.getBody());
  }

  String toTei(String xml, ProjectEntry entry, MVNConversionResult result) {
    MVNTranscriptionVisitor visitor = new MVNTranscriptionVisitor(result.getSigle(), annotationService);

    final Document document = Document.createFromXml(xml, false);
    document.accept(visitor);

    final XmlContext c = visitor.getContext();
    String rawResult = c.getResult();
    for (String error : visitor.getErrors()) {
      result.addError(entry, error);
    }

    return rawResult;
  }

  private void setOrder(MVNFolium text, ProjectEntry projectEntry) {
    String order = projectEntry.getMetadataValue("order");
    if (StringUtils.isEmpty(order)) {
      text.setOrder("9999" + projectEntry.getName());
    } else {
      Integer orderValue = Integer.valueOf(order);
      text.setOrder(String.valueOf(1000 + orderValue));
    }
  }

}
