package economy_simulation.utils;


public class Random {

	public static int fraction(int value) {
		return fraction(value*1.0F);// Math.round((new java.util.Random()).nextFloat()*value);
	}

	public static int fraction(float value) {
		return Math.round((new java.util.Random()).nextFloat()*value);
	}
} 
