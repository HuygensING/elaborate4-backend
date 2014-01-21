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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import elaborate.editor.model.ModelFactory;

public class ProjectTest extends StoredEntityTest {

  //  @Test
  public void test1() {
    EntityManager entityManager = entityManagerFactory.createEntityManager();

    User user = ModelFactory.create(User.class)//
        .setUsername("root")//
        .setFirstName("firstName")//
        .setLastName("last");
    assertThat(user.getId()).isEqualTo(0);

    entityManager.getTransaction().begin();
    entityManager.persist(user);
    Project project1 = ModelFactory.create(Project.class).setName("project1").setCreatedOn(new Date()).setCreator(user);
    entityManager.persist(project1);
    ProjectEntry entry = project1.addEntry("entry", user);
    entityManager.persist(entry);
    Transcription transcription = entry.addTranscription(user);
    entityManager.persist(transcription);
    entityManager.persist(ModelFactory.create(Project.class).setName("project2").setCreatedOn(new Date()).setCreator(user));
    entityManager.getTransaction().commit();
    entityManager.close();
    assertThat(user.getId()).isEqualTo(1);

    entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();

    List<Project> result = entityManager.createQuery("from Project", Project.class).getResultList();
    for (Project project : result) {
      System.out.println("Project (" + project.getName() + ") : " + project.getId());
    }
    project1 = result.get(0);
    Project project2 = result.get(1);
    assertThat(project1.getName()).isEqualTo("project1");
    assertThat(project2.getName()).isEqualTo("project2");
    assertThat(project1.getCreator().getUsername()).isEqualTo("root");

    List<ProjectEntry> projectEntries = project1.getProjectEntries();
    assertThat(projectEntries.size()).isEqualTo(1);

    ProjectEntry projectEntry = projectEntries.get(0);
    assertThat(projectEntry.getName()).isEqualTo("entry");

    List<Transcription> transcriptions = projectEntry.getTranscriptions();
    assertThat(transcriptions.size()).isEqualTo(1);

    Transcription transcription2 = transcriptions.get(0);
    assertThat(transcription2.getBody()).isEqualTo("");

    List<Facsimile> facsimiles = projectEntry.getFacsimiles();
    assertThat(facsimiles.size()).isEqualTo(1);

    Facsimile facsimile = facsimiles.get(0);
    assertThat(facsimile.getFilename()).isEqualTo("");

    entityManager.getTransaction().commit();
    entityManager.close();
  }

}
