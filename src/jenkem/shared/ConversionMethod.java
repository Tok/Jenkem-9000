package jenkem.shared;

public enum ConversionMethod {
	//TODO add more methods
	
	SuperHybrid("Super-Hybrid"),
	FullHd("Full HD"),
	Pwntari("Pwntari"),
	Hybrid("Hybrid");
	
	private String name;
	
	private ConversionMethod(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
}
