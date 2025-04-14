import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import acsse.csc31.graph.Edge;
import acsse.csc31.graph.Graph;
import acsse.csc31.graph.Vertex;
import acsse.csc3a.imagegraph.ImageGraph;
import acsse.csc3a.imagegraph.Point;

public class Main {

	public static void main(String[] args) {
		BufferedImage img;
		try {
			img = ImageIO.read(new File("C:\\Users\\sinqo\\Desktop\\Practice01_Maps\\IMG_6145.jpg"));
			ImageGraph imageGraph = new ImageGraph(img);

			Graph<Point, Double> g = imageGraph.getGraph();

			// Now you can do graph operations like:
			Vertex<Point> a = imageGraph.getVertex(new Point(0, 0));
			Vertex<Point> b = imageGraph.getVertex(new Point(10, 10));

			Edge<Double> edge = g.getEdge(a, b);
			if (edge != null) {
			    System.out.println("Edge weight: " + edge.getElement());
			}
			
			imageGraph.printGraph();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}

}
