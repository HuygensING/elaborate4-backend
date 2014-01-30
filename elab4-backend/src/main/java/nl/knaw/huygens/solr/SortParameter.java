package nl.knaw.huygens.solr;

public class SortParameter {
	private String fieldname;
	private String direction = "asc";

	public String getFieldname() {
		return fieldname;
	}

	public SortParameter setFieldname(String fieldname) {
		this.fieldname = fieldname;
		return this;
	}

	public String getDirection() {
		return direction;
	}

	public SortParameter setDirection(String direction) {
		this.direction = direction;
		return this;
	}

}
