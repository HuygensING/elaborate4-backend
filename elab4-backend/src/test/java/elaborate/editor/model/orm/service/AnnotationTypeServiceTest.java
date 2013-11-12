package elaborate.editor.model.orm.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import nl.knaw.huygens.jaxrstools.exceptions.UnauthorizedException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.NotFoundException;

import elaborate.AbstractTest;
import elaborate.editor.model.ElaborateRoles;
import elaborate.editor.model.orm.AnnotationType;
import elaborate.editor.model.orm.User;

@Ignore
public class AnnotationTypeServiceTest extends AbstractTest {
  static AnnotationTypeService service;
  private static User projectleader;
  private static User user;
  private static User root;
  private static User reader;
  private static User admin;
  static Logger LOG = getLOG(AnnotationTypeServiceTest.class);

  @BeforeClass
  public static void setupClass() {
    LOG.info("setupClass - start");
    service = new AnnotationTypeService();

    projectleader = new User().setRoleString(ElaborateRoles.PROJECTLEADER);
    user = new User().setRoleString(ElaborateRoles.USER);
    reader = new User().setRoleString(ElaborateRoles.READER);
    admin = new User().setRoleString(ElaborateRoles.ADMIN);
    root = new User().setRoot(true);

    //    testdb();

    service.beginTransaction();
    service.persist(root);
    service.persist(user);
    service.persist(reader);
    service.persist(admin);
    service.persist(projectleader);
    service.commitTransaction();

    ImmutableList<AnnotationType> all = service.getAll();
    assertEquals(true, all.isEmpty());
    LOG.info("setupClass - end");
  }

  private static void testdb() {
    try {
      Class.forName("org.hsqldb.jdbcDriver");
      Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
      Statement s = conn.createStatement();
      s.execute("SELECT * FROM *");
      ResultSet rs = s.getResultSet();
      ResultSetMetaData metaData = rs.getMetaData();
      int columnCount = metaData.getColumnCount();
      for (int i = 1; i < columnCount; i++) {
        System.out.print(metaData.getColumnName(i) + " | ");
      }
      LOG.info("");

      while (rs.next()) {
        for (int i = 1; i < columnCount; i++) {
          System.out.print(rs.getString(i) + " | ");
        }
        LOG.info("");
      }
      conn.close();
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  @AfterClass
  public static void teardownClass() {
    LOG.info("teardownClass - start");
    service = null;
    LOG.info("teardownClass - end");
  }

  @Test
  public void testCRUD_asProjectLeader() throws Exception {
    testAsAuthorizedUser("bladiebla", projectleader);
  }

  @Test
  public void testCRUD_asRoot() throws Exception {
    testAsAuthorizedUser("ofallevil", root);
  }

  @Test
  public void testCRUD_asAdmin() throws Exception {
    testAsAuthorizedUser("administer", admin);
  }

  private void testAsAuthorizedUser(String name, User user) {
    AnnotationType placeholder = new AnnotationType().setName(name).setDescription("description");
    assertEquals(0, placeholder.getId()); // default

    // Create
    AnnotationType created = service.create(placeholder, user);
    long id = created.getId();
    assertNotNull(id);

    ImmutableList<AnnotationType> all = service.getAll();
    assertEquals(1, all.size());

    // Read
    AnnotationType read = service.read(id, user);
    assertEquals(name, read.getName());
    assertEquals("description", read.getDescription());

    // Update
    read.setName("newName").setDescription("newDescription");
    service.update(read, user);
    AnnotationType updated = service.read(id, user);
    assertEquals("newName", updated.getName());
    assertEquals("newDescription", updated.getDescription());

    // Delete
    service.delete(id, user);
    try {
      service.read(id, user);
      fail("NotFoundException expected");
    } catch (NotFoundException e) {}
  }

  private void createAsUnauthorizedUser(User _user) {
    AnnotationType annotationType = new AnnotationType().setName("user").setDescription("description");
    service.create(annotationType, _user);
    fail("an UnauthorizedException should've been thrown");
  }

  @Test(expected = UnauthorizedException.class)
  public void testCreateAsUser() throws Exception {
    createAsUnauthorizedUser(user);
  }

  @Test(expected = UnauthorizedException.class)
  public void testCreateAsReader() throws Exception {
    createAsUnauthorizedUser(reader);
  }

  private void updateAsUnauthorizedUser(User _user) {
    AnnotationType annotationType = new AnnotationType().setName("user").setDescription("description");
    AnnotationType created = service.create(annotationType, root);

    created.setName("newName");
    boolean exceptionThrown = false;
    try {
      service.update(created, _user);
    } catch (UnauthorizedException e) {
      exceptionThrown = true;
    }
    service.delete(created.getId(), root);
    assertEquals(true, exceptionThrown);
  }

  @Test
  public void testUpdateAsUser() throws Exception {
    updateAsUnauthorizedUser(user);
  }

  @Test
  public void testUpdateAsReader() throws Exception {
    updateAsUnauthorizedUser(reader);
  }

  private void deleteAsUnauthorizedUser(User _user) {
    AnnotationType annotationType = new AnnotationType().setName("user").setDescription("description");
    AnnotationType created = service.create(annotationType, root);

    boolean exceptionThrown = false;
    try {
      service.delete(created.getId(), _user);
    } catch (UnauthorizedException e) {
      exceptionThrown = true;
    }
    service.delete(created.getId(), root);
    assertEquals(true, exceptionThrown);
  }

  @Test
  public void testDeleteAsUser() throws Exception {
    deleteAsUnauthorizedUser(user);
  }

  @Test
  public void testDeleteAsReader() throws Exception {
    deleteAsUnauthorizedUser(reader);
  }

}
