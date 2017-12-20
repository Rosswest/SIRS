package sirs;

/**
 * Calling a function with no arguments assumes:
 * 		Minimum = 0
 * 		Maximum = 10
 * @author Ross
 *
 */
public class Randoms {
	public static int randomInt(int min, int max) {
		int diff = (max - min) + 1;
		return (int) ((Math.random() * diff) + min);
	}
	
	public static int randomInt() {
		return randomInt(0,10);
	}
	
	public static double randomDouble(double min, double max) {
		double diff = (max - min) + 1;
		return (Math.random() * diff) + min;
	}
	
	public static double randomDouble() {
		return randomDouble(0,10.0);
	}

}
