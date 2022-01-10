package elaborate.editor.backend;

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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

import org.apache.commons.lang.time.StopWatch;

import nl.knaw.huygens.Log;

import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.Transcription;
import elaborate.editor.model.orm.service.TranscriptionService;
import elaborate.util.HibernateUtil;

class AnnotationMarkerScrubber {
  @SuppressWarnings("boxing")
  public static void main(String[] args) {
    StopWatch sw = new StopWatch();
    sw.start();
    EntityManager entityManager = HibernateUtil.beginTransaction();
    TranscriptionService ts = TranscriptionService.instance();
    ts.setEntityManager(entityManager);
    try {
      List<Transcription> resultList =
          entityManager // .
              .createQuery("select t from Transcription t", Transcription.class)
              .getResultList();
      int size = resultList.size();
      int n = 1;
      for (Transcription t : resultList) {
        Log.info(
            "indexing transcription {} ({}/{} = {}%)", t.getId(), n, size, percentage(n, size));
        String bodyBefore = t.getBody();
        ts.cleanupAnnotations(t);
        String bodyAfter = t.getBody();
        if (!bodyAfter.equals(bodyBefore)) {
          ProjectEntry projectEntry = t.getProjectEntry();
          String projectname = projectEntry.getProject().getName();
          long entryId = projectEntry.getId();
          Log.info(
              "url: http://test.elaborate.huygens.knaw.nl/projects/{}/entries/{}/transcriptions/{}",
              projectname,
              entryId,
              t.getTextLayer());
          Log.info("body changed:\nbefore: {}\nafter:{}", bodyBefore, bodyAfter);
        }
        n++;
      }
    } finally {
      HibernateUtil.commitTransaction(entityManager);
    }
    sw.stop();
    Log.info("done in {}", convert(sw.getTime()));
  }

  private static String percentage(int part, int total) {
    return new DecimalFormat("0.00").format((double) (100 * part) / (double) total);
  }

  private static String convert(long ms) {
    Date date = new Date(ms);
    DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
    return formatter.format(date);
  }
}
