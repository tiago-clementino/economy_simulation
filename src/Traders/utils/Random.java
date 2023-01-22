package Traders.utils;


public class Random {

	public static int fraction(int value) {
		return Math.round((new java.util.Random()).nextFloat()*value);
	}
}
