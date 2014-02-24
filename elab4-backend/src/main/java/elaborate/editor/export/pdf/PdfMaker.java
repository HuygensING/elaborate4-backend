package elaborate.editor.export.pdf;

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

        ProjectService projectService = ProjectService.instance();
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
