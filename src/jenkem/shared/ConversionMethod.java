package jenkem.shared;

public enum ConversionMethod {	
	SuperHybrid("Super-Hybrid"),
	FullHd("Full HD"),
	Pwntari("Pwntari"),
	Hybrid("Hybrid"),
	Plain("Plain");
	
	private String name;
	
	private ConversionMethod(final String name) {
		this.name = name;
	}
	
	public static ConversionMethod getValueByName(String name) {
		for (ConversionMethod method : ConversionMethod.values()) {
			if (method.getName().equalsIgnoreCase(name)) {
				return method;
			}
		}
		return SuperHybrid;
	}
	
	public String getName() {
		return name;
	}

	public String toString() {
		return name;
	}
}
