package nl.knaw.huygens.elaborate.persistence;

import nl.knaw.huygens.persistence.PersistenceException;
import nl.knaw.huygens.persistence.PersistenceManager;

public class PersistenceWrapper {

  private final PersistenceManager persistenceManager;
  private final String baseUrl;

  public PersistenceWrapper(String baseUrl, PersistenceManager persistenceManager) {
    this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    this.persistenceManager = persistenceManager;
  }

  public String persistUrl(String url) throws PersistenceException {
    return persistenceManager.persistURL(url);
  }

  public String getPersistentUrl(String persistentId) throws PersistenceException {
    return persistenceManager.getPersistentURL(persistentId);
  }

}
