package acsse.csc3a.graph.algorithms;

public enum MATCH_TYPE {
	
	/**
	 * Undrinkable
	 * Water is severely contaminated; not safe for any human consumption or contact.
	 */
	RED, 
	/**
	 * Poor Water Quality
	 * Water shows clear signs of pollution.
	 */
	ORANGE, 
	/**
	 * Moderate Water Quality
	 * Water appears some what clean but may still pose health risks if untreated.
	 */
	YELLOW, 
	/**
	 * Good Water Quality 
	 * Water is clean, clear, and safe for drinking and general use.
	 */
	GREEN, 
	/**
	 * Unknown
	 * Water quality cannot be determined.
	 */
	BLACK
}
