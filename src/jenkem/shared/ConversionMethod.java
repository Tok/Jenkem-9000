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
	
	public String toString() {
		return name;
	}
}
