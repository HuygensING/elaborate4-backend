package nl.knaw.huygens.facetedsearch;

public class RangeField {
	public String name;
	public String lowerField;
	public String upperField;

	public RangeField() {};

	public RangeField(String name, String lower, String upper) {
		this.name = name;
		lowerField = lower;
		upperField = upper;
	}

	public String getName() {
		return name;
	}

	public RangeField setName(String name) {
		this.name = name;
		return this;
	}

	public String getLowerField() {
		return lowerField;
	}

	public RangeField setLowerField(String lowerField) {
		this.lowerField = lowerField;
		return this;
	}

	public String getUpperField() {
		return upperField;
	}

	public RangeField setUpperField(String upperField) {
		this.upperField = upperField;
		return this;
	}

}