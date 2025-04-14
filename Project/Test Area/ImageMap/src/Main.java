
import javax.imageio.ImageIO;

import acsse.csc3a.graph.ImageGraph;
import acsse.csc3a.graph.ImageGraph.PixelNode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class Main {
	public static void main(String[] args) {
	    try {
	        // Load actual image file
	        BufferedImage image = ImageIO.read(new File("C:\\Users\\sinqo\\Downloads\\ImageMap\\IMG_6145.jpg"));
	        ImageGraph imageGraph = new ImageGraph();
	        // Process with similarity threshold (0.1 = strict, 0.5 = more connections)
	        Map<PixelNode, Set<PixelNode>> graph = imageGraph.createImageGraph(image, 1);
	        
	        // Print the graph structure
	        imageGraph.printGraph(graph);
	        
	        // Or analyze the graph programmatically:
	        System.out.println("\nGraph Analysis:");
	        System.out.println("Total nodes: " + graph.size());
	        long totalEdges = graph.values().stream().mapToInt(Set::size).sum() / 2;
	        System.out.println("Total edges: " + totalEdges);
	        
	    } catch (IOException e) {
	        System.err.println("Error loading image: " + e.getMessage());
	    }
	}

}
