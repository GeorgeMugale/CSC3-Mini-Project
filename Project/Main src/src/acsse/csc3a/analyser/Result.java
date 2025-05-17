package acsse.csc3a.analyser;

import acsse.csc3a.graph.algorithms.CATEGORY_TYPE;
import acsse.csc3a.graph.algorithms.MATCH_TYPE;
import javafx.scene.paint.Color;

public class Result {
	public CATEGORY_TYPE category_TYPE = CATEGORY_TYPE.ONLY_WATER_TOP_VIEW;
	public MATCH_TYPE match_TYPE = MATCH_TYPE.BLACK;

	public Result() {

	}

	public String getCategory() {
		// TODO Auto-generated method stub

		String result;

		switch (category_TYPE) {
		case ONLY_WATER_SIDE_VIEW: {
			result = "water from side view";
			break;
		}
		case ONLY_WATER_TOP_VIEW: {
			result = "water from top view";
			break;
		}
		case WATER_IN_OPAQUE: {
			result = "water in opaque container";
			break;
		}
		case WATER_IN_TRANSPARENT: {
			result = "water in transaparent container";
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + category_TYPE);
		}

		return result;
	}

	public String getQuality() {
		// TODO Auto-generated method stub

		String result;

		switch (match_TYPE) {
		case GREEN: {
			result = "Good quality; clear, and safe for drinking and general use.";
			break;
		}
		case ORANGE: {
			result = "Poor quality; it shows clear signs of pollution.";
			break;
		}
		case BLACK: {
			result = "Water quality cannot be determined.";
			break;
		}
		case RED: {
			result = "Water quality undrinkable; not safe for any human consumption or contact.";
			break;
		}
		case YELLOW: {
			result = "Water qality moderate; appears some what clean but may still pose health risks if untreated.";
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + match_TYPE);
		}

		return result;
	}

	public Color textColour() {
		// TODO Auto-generated method stub
		
		Color textFill;
		
		switch (match_TYPE) {
		case GREEN: {
			textFill = Color.GREEN;
			break;
		}
		case ORANGE: {
			textFill = Color.ORANGE;
			break;
		}
		case BLACK: {
			textFill = Color.BLACK;
			break;
		}
		case RED: {
			textFill = Color.RED;
			break;
		}
		case YELLOW: {
			textFill = Color.YELLOW;
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + match_TYPE);
		}
		
		return textFill;
	}
}
