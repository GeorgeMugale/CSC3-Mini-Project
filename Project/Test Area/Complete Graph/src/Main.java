import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import acsse.csc3a.graph.Graph;
import acsse.csc3a.graph.GraphPrinter;
import acsse.csc3a.graph.algorithms.ALGORITHM;
import acsse.csc3a.graph.algorithms.GraphEditDistance;
import acsse.csc3a.graph.algorithms.WATER_IMAGE_TYPE;
import acsse.csc3a.imagegraph.ImageGraph;
import acsse.csc3a.imagegraph.Point;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		BufferedImage img;
		BufferedImage img2;
		try {
			img = ImageIO.read(new File("water green.jpg"));
			ImageGraph graph = new ImageGraph(img);
			System.out.println("done");
			// 6m - 3
			// 4m - 2
			
			// dont let graph exceed 2m
			
//			img2 = ImageIO.read(new File("clean water 2.jpg"));
//			ImageGraph graph2 = new ImageGraph(img2, 1);
	
//			System.out.println(WaterQualityAnalyzer.analyzeQuality(img));
	
//			GraphEditDistance ged = new GraphEditDistance(ALGORITHM.MATCH);

//			System.out.println(ged.calculateGraphEditDistance(graph, graph2));
			
		
//			Graph<Point, Double> myGraph = graph.getGraph();
//			GraphPrinter.showGraph(myGraph);
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static class WaterQualityAnalyzer {
	    // Alpha thresholds (adjust based on calibration)
	    private static final int PURE_WATER_ALPHA = 50;   // Nearly transparent
	    private static final int DIRTY_WATER_ALPHA = 200; // Semi-opaque
	    
	    public static double analyzeQuality(BufferedImage waterImage) {
	        int waterPixels = 0;
	        int totalAlpha = 0;
	        
	        for (int y = 0; y < waterImage.getHeight(); y++) {
	            for (int x = 0; x < waterImage.getWidth(); x++) {
	                int pixel = waterImage.getRGB(x, y);
	                int alpha = new Color(pixel).getTransparency();
	                System.out.println(alpha);
	                
	                if (alpha < 255) {  // Ignore fully opaque (non-water?)
	                    waterPixels++;
	                    System.out.println("dxc");
	                    totalAlpha += alpha;
	                }
	            }
	        }
	        
	        if (waterPixels == 0) return 0.0; // No water detected
	        
	        double meanAlpha = (double) totalAlpha / waterPixels;
	        return normalizeQualityScore(meanAlpha);
	    }
	    
	    private static double normalizeQualityScore(double meanAlpha) {
	        // Linear scaling: 1.0 (purest) → 0.0 (dirtiest)
	        return 1.0 - Math.min(1.0, Math.max(0.0, 
	               (meanAlpha - PURE_WATER_ALPHA) / 
	               (DIRTY_WATER_ALPHA - PURE_WATER_ALPHA)));
	    }
	}

}
