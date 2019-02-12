package fwd.busim.module;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1
 */
public class Util {
	
	
	public static double decimalPlace(double value, int places) {
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public static double decimal(double value) {
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(2, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

}
