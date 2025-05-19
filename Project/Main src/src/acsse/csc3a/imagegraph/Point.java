package acsse.csc3a.imagegraph;

import java.awt.Color;
import java.io.Serializable;

/**
 * A point object which stores point properties and has behaviour for a point
 */
public class Point implements Serializable {
	int x, y;
	Color color;
	float[] hsb;

	/**
	 * Constructs a point with the given position and colour
	 * @param x x location of point
	 * @param y y location of point
	 * @param color color of point
	 */
	public Point(int x, int y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;

		this.hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
	}

	/**
	 * Gets the color of the point
	 * @return the color
	 */
	public Color getColour() {
		return color;
	}

	/**
	 * Calculates the difference in color between two colors
	 * @param color1 the first color
	 * @param color2 the second color
	 * @return the total of rgb differences
	 */
	public static float colorDifference(Color color1, Color color2) {
		int dr = color1.getRed() - color2.getRed();
		int dg = color1.getGreen() - color2.getGreen();
		int db = color1.getBlue() - color2.getBlue();
		int da = color1.getAlpha() - color2.getAlpha();

		return (float) Math.sqrt(dr * dr + dg * dg + db * db + da * da);
	}

	/**
	 * Calculates the opacity of the color of the point
	 * @return the calculated opacity
	 */
	public int calculateWaterOpacity() {

		float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

		// Calculate "water opacity score" (0-255)
		return (int) (255 * (0.6 * (1 - hsb[1]) + // Low saturation → more "transparent"
				0.4 * hsb[2] // High brightness → more "washed out"
		));
	}

	/**
	 * Likelihood of weather this is a water pixel or not
	 * @return true if it a water pixel, false otherwise
	 */
	public boolean isLikelyWater() {
		return (hsb[0] >= 0.45 && hsb[0] <= 0.65); // Clean blue water
	}

	/**
	 * Returns the quality of the pixel
	 * @return a quality metric
	 */
	public double getQuality() {

		float saturation = hsb[1];
		float brightness = hsb[2];
		// Clean water: Low saturation + medium brightness
		if (saturation < 0.3 && brightness > 0.4 && brightness < 0.8) {
			return 1.0; // Best quality
		}
		// Polluted water: High saturation or extreme brightness
		else if (saturation > 0.6 || brightness > 0.9) {
			return 0.2; // Worst quality
		}
		// Moderate quality
		else {
			return 0.5;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Point))
			return false;

		Point point = (Point) o;
		return x == point.x && y == point.y;
	}

	@Override
	public int hashCode() {
		// Combines coordinates with different weights effectively voiding simple
		// patterns
		long key = x * 0x4F1BBCDC + y * 0x31415927; // Two large primes
		// XORs the original hash with its right-shifted 32-bit version
		return (int) (key ^ (key >>> 32));
	}

	/**
	 * Gets the x location of the pixel
	 * @return
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Sets the x location of the pixel
	 * @return
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Gets the Y location of the pixel
	 * @return
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the Y location of the pixel
	 * @return
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Specialized function designed to convert 2D vertex coordinates (x, y) into a
	 * well-distributed integer key so it may be used in the hash function.
	 * @return
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("x: %s y: %s", x, y);
	}

}
