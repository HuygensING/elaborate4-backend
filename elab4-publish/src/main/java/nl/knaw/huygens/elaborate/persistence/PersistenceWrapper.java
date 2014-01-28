package nl.knaw.huygens.elaborate.persistence;

import nl.knaw.huygens.persistence.PersistenceException;
import nl.knaw.huygens.persistence.PersistenceManager;

public class PersistenceWrapper {

	private final PersistenceManager persistenceManager;

	public PersistenceWrapper(PersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}

	public String persistUrl(String url) throws PersistenceException {
		return persistenceManager.persistURL(url);
	}

	public String getPersistedUrl(String persistentId) throws PersistenceException {
		return persistenceManager.getPersistedURL(persistentId);
	}

	public String getPersistentUrl(String persistentId) {
		return persistenceManager.getPersistentURL(persistentId);
	}

}
