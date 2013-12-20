package nl.knaw.huygens.elaborate.publication;

import java.util.Date;
import java.util.List;

import nl.knaw.huygens.LoggableObject;
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

public class PublicationFinalizer extends LoggableObject {
	//	private static final String OAIPMH_URL = "http://localhost:9998/";
	private static final String OAIPMH_URL = "http://oaipmh.huygens.knaw.nl/";
	private static final String PREFIX = "oai:oaipmh.huygens.knaw.nl:elaborate:";
	private static final String PM_PREFIX = "11240.1";

	private final PersistenceManager pm = getPersistenceManager();
	private static final String elab4editionSetSpec = "elaborate:edition";

	public static void main(String[] args) {
		new PublicationFinalizer().finalizePublication();
	}

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
		OaiPmhRestClient oai = new OaiPmhRestClient(OAIPMH_URL);
		OAISet oaiSet = oai.getSet(elab4editionSetSpec);
		if (oaiSet == null) {
			oaiSet = new OAISet().setSetSpec(elab4editionSetSpec).setDescription("published elaborate editions").setSetName("elaborate edition");
			oai.postSet(oaiSet);
		}

		List<String> setSpecs = ImmutableList.of(elab4editionSetSpec);
		String url = "http://clusius.huygens.knaw.nl/edition";
		String id = "clusius_correspondence";

		String pid = persistURL(url);

		DublinCoreRecord dcRecord = new DublinCoreRecord()//
				.setTitle("Clusius correspondence")//
				.setSubject("Letters from/to Carolus Clusius (1526-1609)")//
				.setCreator("Esther van Gelder")//
				.setCoverage("1548-1609")//
				.setDate("2013-12-20")//
				.setDescription("Website Edition of letters from/to Carolus Clusius (1526-1609)")//
				.setIdentifier(pid)//
				.setLanguage("en;nl;fr;it;es;la;de")//
				.setPublisher("Huygens ING")//
				.setType("website")//
		//				.setContributor("contributor")//
		//				.setFormat("format")//
		//				.setRelation("relation")//
		//				.setRights("rights")//
		//				.setSource("source")//
		;

		String identifier = PREFIX + id;
		String selfLink = "http://oaipmh.huygens.knaw.nl/oai?verb=GetRecord&metadataPrefix=cmdi&identifier=" + identifier;

		CMDIRecord cMDIRecord = new ElaborateCMDIRecordBuilder()//
				.setMdSelfLink(persistURL(selfLink))//
				.setDublinCoreRecord(dcRecord)//
				.build();

		String metadata = "<meta>" + dcRecord.asXML() + cMDIRecord.asXML() + "</meta>";
		LOG.info("metadata={}", metadata);

		Date datestamp = new Date();
		oai.deleteRecord(identifier);
		OAIRecord oaiRecord = new OAIRecord()//
				.setIdentifier(identifier)//
				.setSetSpecs(setSpecs)//
				.setMetadata(metadata)//
				.setDatestamp(datestamp);
		oai.postRecord(oaiRecord);

	}

	String persistURL(String url) {
		try {
			String pid = pm.persistURL(url);
			String persistentURL = pm.getPersistentURL(pid);
			//			pm.deletePersistentId(pid);
			return persistentURL;
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
		return null;
	}
}
