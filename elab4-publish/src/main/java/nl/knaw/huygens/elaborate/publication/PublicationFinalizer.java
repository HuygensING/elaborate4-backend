package nl.knaw.huygens.elaborate.publication;

import java.util.Date;
import java.util.List;

import nl.knaw.huygens.elaborate.publication.metadata.CMDIRecord;
import nl.knaw.huygens.elaborate.publication.metadata.DublinCoreRecord;
import nl.knaw.huygens.elaborate.publication.metadata.ElaborateCMDIRecordBuilder;
import nl.knaw.huygens.oaipmh.OAIRecord;
import nl.knaw.huygens.oaipmh.OAISet;
import nl.knaw.huygens.oaipmh.OaiPmhRestClient;
import nl.knaw.huygens.persistence.PersistenceException;
import nl.knaw.huygens.persistence.PersistenceManager;
import nl.knaw.huygens.persistence.PersistenceManagerFactory;

import com.google.common.collect.ImmutableList;

public class PublicationFinalizer {
	//	private static final String HANDLE_URL = "http://hdl.handle.net/";
	private static final String PREFIX = "oai:oaipmh.huygens.knaw.nl:elaborate:";
	private static final String PM_PREFIX = "11240.1";

	private final PersistenceManager pm = getPersistenceManager();

	public void finalizePublication() {
		// steps:
		// - move war to production
		// register persistent identifiers
		// generate metadata in oai & cmdi
		// send metadata to oaipmh server
		doOAI();
	}

	PersistenceManager getPersistenceManager() {
		String cipher = "Gewacu6u";
		String namingAuthority = "0.NA";
		String pathToPrivateKey = getClass().getClassLoader().getResource("admpriv.bin").getPath();
		PersistenceManager pm = PersistenceManagerFactory.newPersistenceManager(true, cipher, namingAuthority, PM_PREFIX, pathToPrivateKey);
		return pm;
	}

	private void doOAI() {
		OaiPmhRestClient oai = new OaiPmhRestClient("http://127.0.0.1:9998/");
		String elab4editionSetSpec = "elaborate:edition";
		OAISet oaiSet = oai.getSet(elab4editionSetSpec);
		if (oaiSet == null) {
			oaiSet = new OAISet().setSetSpec(elab4editionSetSpec).setDescription("published elaborate editions").setSetName("elaborate edition");
			oai.postSet(oaiSet);
		}

		List<String> setSpecs = ImmutableList.of(elab4editionSetSpec);
		DublinCoreRecord dcRecord = new DublinCoreRecord();
		String url = "url";
		String pid = persistURL(url);
		CMDIRecord cMDIRecord = new ElaborateCMDIRecordBuilder().setMdSelfLink(pid).build();
		String metadata = dcRecord.asXML() + cMDIRecord.asXML();
		String id = "";
		Date datestamp = new Date();
		OAIRecord oaiRecord = new OAIRecord().setIdentifier(PREFIX + id).setSetSpecs(setSpecs).setMetadata(metadata).setDatestamp(datestamp);
		oai.postRecord(oaiRecord);

	}

	String persistURL(String url) {
		try {
			String pid = pm.persistURL(url);
			return pm.getPersistentURL(pid);
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
		return null;
	}
}
