package elaborate.editor.export.pdf;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.Transcription;
import elaborate.editor.model.orm.service.ProjectService;

public class PdfMaker {

  private PDDocument doc;
  private final Project project;
  private final EntityManager entityManager;

  public PdfMaker(Project _project, EntityManager _entityManager) {
    this.project = _project;
    this.entityManager = _entityManager;
    if (_project == null) {
      doc = null;

    } else {
      try {
        doc = new PDDocument();

        ProjectService projectService = new ProjectService();
        projectService.setEntityManager(entityManager);
        List<ProjectEntry> projectEntriesInOrder = projectService.getProjectEntriesInOrder(project.getId());

        for (ProjectEntry projectEntry : projectEntriesInOrder) {
          PDPage page = new PDPage();
          doc.addPage(page);

          PDFont font = PDType1Font.HELVETICA_BOLD;

          PDPageContentStream content = new PDPageContentStream(doc, page);
          content.beginText();
          content.setFont(font, 12);
          content.drawString(projectEntry.getName());
          for (Transcription transcription : projectEntry.getTranscriptions()) {
            content.drawString(transcription.getTextLayer());
            content.drawString(transcription.getBody());
          }

          content.endText();
          content.close();
        }

      } catch (Exception e) {
        System.out.println("Exception");
      }
    }
  }

  public void saveToFile(String filename) {
    if (doc != null) {
      try {
        doc.save(filename);
      } catch (COSVisitorException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
