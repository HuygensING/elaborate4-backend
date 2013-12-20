package nl.knaw.huygens.elaborate.publication;

import static org.assertj.core.api.Assertions.assertThat;
import net.handle.hdllib.HandleException;
import nl.knaw.huygens.LoggableObject;
import nl.knaw.huygens.persistence.PersistenceException;
import nl.knaw.huygens.persistence.PersistenceManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PublicationFinalizerTest extends LoggableObject {

	private PublicationFinalizer pf;

	@Before
	public void setUp() throws Exception {
		pf = new PublicationFinalizer();
	}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void testGetPersistenceManager() throws Exception {
		PersistenceManager persistenceManager = pf.getPersistenceManager();
		assertThat(persistenceManager).isNotNull();

		String url = "http://elaborate.huygens.knaw.nl/test/test/test";
		String pid = persistenceManager.persistURL(url);
		LOG.info("pid={}", pid);
		assertThat(pid).isNotNull();

		String persistedURL = persistenceManager.getPersistedURL(pid);
		assertThat(persistedURL).isEqualTo(url);

		String persistentURL = persistenceManager.getPersistentURL(pid);
		assertThat(persistentURL).isEqualTo("http://hdl.handle.net/11240.1/" + pid);

		String newURL = "http://example.org/bla";
		persistenceManager.modifyURLForPersistentId(pid, newURL);
		persistedURL = persistenceManager.getPersistedURL(pid);
		assertThat(persistedURL).isEqualTo(newURL);

		persistenceManager.deletePersistentId(pid);
		assertDeleted(persistenceManager, pid);
	}

	private void assertDeleted(PersistenceManager persistenceManager, String pid) {
		try {
			persistenceManager.getPersistedURL(pid);
		} catch (PersistenceException pe) {
			Throwable cause = pe.getCause();
			assertThat(cause).isInstanceOf(HandleException.class);
			HandleException he = (HandleException) cause;
			assertThat(he.getCode()).isEqualTo(HandleException.HANDLE_DOES_NOT_EXIST);
		}
	}

	//  @Test
	//  public void testPersistURL() throws Exception {
	//    String persistURL = pf.persistURL("http://example.com/made-up-url.html");
	//  }

	//  @Test
	//  public void deleteTest() throws PersistenceException {
	//    PublicationFinalizer pf = new PublicationFinalizer();
	//    PersistenceManager persistenceManager = pf.getPersistenceManager();
	//    List<String> pids = ImmutableList.of(
	//        "336D0FF5-4E96-4424-BD10-020324A6294E", "AA4A71FA-712A-47BF-9E45-1C4748E97FE3", "B0A557DD-AB8D-4F0E-A3FE-A513CB4EB369", "F8AC3A64-C2CD-4DC2-9276-42E5040FFC9B", "FD2DAE41-83ED-4865-8D55-30863D60E4A4", "HUYGENS", "HUYGENS1", "HUYGENS2", "SPEEDTEST");
	//    for (String pid : pids) {
	//      persistenceManager.deletePersistentId(pid);
	//    }
	//  }
}
