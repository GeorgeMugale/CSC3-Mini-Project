package acsse.csc3a.imagegraph;

import java.awt.Color;
import java.util.Objects;

public class Point {
    int x, y;
    Color color;
    double brightness;

    public Point(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.brightness = computeBrightness(color);
    }
    
    private double computeBrightness(Color c) {
        return 0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;
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
   	 * Specialized function designed to convert 2D vertex coordinates (x, y)
   	 * into a well-distributed integer key so it may be used in the hash function.
   	 * 
   	 * @return
   	 */
    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	return String.format("x: %s y: %s", x, y);
    }
	
}
