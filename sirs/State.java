package sirs;

public enum State {
	RECOVERED,SUSCEPTIBLE,INFECTED, IMMUNE;
	
	public String toString() {
		if (this == RECOVERED) {
			return "R";
		} else if (this == SUSCEPTIBLE) {
			return "S";
		} else if (this == INFECTED) {
			return "I";
		} else if (this == IMMUNE) {
			return "-";
		}
		
		return null;
	}
}
