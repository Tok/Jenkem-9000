package jenkem.shared;

public enum Kick {	
	Off, X, Y, XY;
	
	private Kick() {
	}
	
	public String toString() {
		return this.name();
	}
}
