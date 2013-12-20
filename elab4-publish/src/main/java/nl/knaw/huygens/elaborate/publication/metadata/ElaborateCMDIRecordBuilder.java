package nl.knaw.huygens.elaborate.publication.metadata;

public class ElaborateCMDIRecordBuilder extends CMDIRecord.Builder {
	private DublinCoreRecord dublinCoreRecord;

	public ElaborateCMDIRecordBuilder() {
		setMdCreator("Elaborate");
		setMdCollectionDisplayName("Elaborate Editions");
		setMdProfile("clarin.eu:cr1:p_1288172614023"); // DcmiTerms, see http://catalog.clarin.eu/ds/ComponentRegistry/rest/registry/profiles/clarin.eu:cr1:p_1288172614023
	}

}
