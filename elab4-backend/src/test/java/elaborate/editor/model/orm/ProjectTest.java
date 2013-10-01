package elaborate.editor.model.orm;

import static org.junit.Assert.*;

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
    assertEquals(0, user.getId());

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
    assertEquals(1, user.getId());

    entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();

    List<Project> result = entityManager.createQuery("from Project", Project.class).getResultList();
    for (Project project : result) {
      System.out.println("Project (" + project.getName() + ") : " + project.getId());
    }
    project1 = result.get(0);
    Project project2 = result.get(1);
    assertEquals("project1", project1.getName());
    assertEquals("project2", project2.getName());
    assertEquals("root", project1.getCreator().getUsername());

    List<ProjectEntry> projectEntries = project1.getProjectEntries();
    assertEquals(1, projectEntries.size());

    ProjectEntry projectEntry = projectEntries.get(0);
    assertEquals("entry", projectEntry.getName());

    List<Transcription> transcriptions = projectEntry.getTranscriptions();
    assertEquals(1, transcriptions.size());

    Transcription transcription2 = transcriptions.get(0);
    assertEquals("", transcription2.getBody());

    List<Facsimile> facsimiles = projectEntry.getFacsimiles();
    assertEquals(1, facsimiles.size());

    Facsimile facsimile = facsimiles.get(0);
    assertEquals("", facsimile.getFilename());

    entityManager.getTransaction().commit();
    entityManager.close();
  }

}
