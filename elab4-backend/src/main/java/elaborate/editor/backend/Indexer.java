package elaborate.editor.backend;

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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

import org.apache.commons.lang.time.StopWatch;

import nl.knaw.huygens.Log;

import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.service.ProjectEntryService;
import elaborate.editor.solr.ElaborateSolrIndexer;
import elaborate.util.HibernateUtil;

public class Indexer {
  private static final int COMMIT_EVERY_N_RECORDS = 100;

  public static void main(String[] args) {
    boolean wipeIndexFirst = args.length != 0 && "-w".equals(args[0]);
    new Indexer().index(wipeIndexFirst);
  }

  public void index(boolean wipeIndexFirst) {
    StopWatch sw = new StopWatch();
    sw.start();
    ElaborateSolrIndexer solr = new ElaborateSolrIndexer();
    if (wipeIndexFirst) {
      Log.info("clearing index");
      solr.clear();
    }
    EntityManager entityManager = HibernateUtil.getEntityManager();
    try {
      ProjectEntryService projectEntryService = ProjectEntryService.instance();
      projectEntryService.setEntityManager(entityManager);
      List<ProjectEntry> projectentries = projectEntryService.getAll();
      int size = projectentries.size();
      Log.info("indexing {} projectEntries", size);
      int n = 1;
      for (ProjectEntry projectEntry : projectentries) {
        Log.info(
            "indexing projectEntry {} ({}/{} = {}%) (est. time remaining: {})",
            projectEntry.getId(),
            n,
            size,
            percentage(n, size),
            time_remaining(n, size, sw.getTime()));
        solr.index(projectEntry, autoCommit(n));
        n++;
      }
    } finally {
      entityManager.close();
    }
    solr.commit();
    sw.stop();
    Log.info("done in {}", convert(sw.getTime()));
  }

  private static String time_remaining(int n, long total, long timeelapsed) {
    long timeRemaining = (timeelapsed / n) * (total - n);
    return convert(timeRemaining);
  }

  private static String percentage(int part, int total) {
    return new DecimalFormat("0.00").format((double) (100 * part) / (double) total);
  }

  private static boolean autoCommit(int n) {
    return (n % COMMIT_EVERY_N_RECORDS) == 0;
  }

  private static String convert(long ms) {
    Date date = new Date(ms - (1000 * 60 * 60));
    DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
    return formatter.format(date);
  }
}
