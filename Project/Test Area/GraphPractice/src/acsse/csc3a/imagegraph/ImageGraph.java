package acsse.csc3a.imagegraph;
import java.awt.Color;
import java.awt.image.BufferedImage;

import acsse.csc31.graph.AdjacencyMapGraph;
import acsse.csc31.graph.Edge;
import acsse.csc31.graph.Graph;
import acsse.csc31.graph.Vertex;
import acsse.csc3a.maps.ChainHashMap;
import acsse.csc3a.maps.Map;

public class ImageGraph {
	
    private BufferedImage image;
    private Graph<Point, Double> graph;
    private Map<Point, Vertex<Point>> pixelVertices;

    public ImageGraph(BufferedImage image) {
        this.image = image;
        this.graph = new AdjacencyMapGraph<>();
        this.pixelVertices = new ChainHashMap<>();
        buildGraphFromImage();
    }

    private void buildGraphFromImage() {
        int width = image.getWidth();
        int height = image.getHeight();

        /*
         *  create vertices
         */
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Point p = new Point(x, y);
                Vertex<Point> v = graph.insertVertex(p);
                pixelVertices.put(p, v);
            }
        }

        /*
         * Create edges
         */
        int[][] directions = { {0,1}, {1,0}, {-1,0}, {0,-1} }; 
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Point p1 = new Point(x, y);
                Vertex<Point> v1 = pixelVertices.get(p1);
                int rgb1 = image.getRGB(x, y);

                for (int[] d : directions) {
                    int nx = x + d[0];
                    int ny = y + d[1];
                    if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                        Point p2 = new Point(nx, ny);
                        Vertex<Point> v2 = pixelVertices.get(p2);
                        int rgb2 = image.getRGB(nx, ny);
                        double weight = colorDifference(rgb1, rgb2);

                        if (graph.getEdge(v1, v2) == null) {
                            graph.insertEdge(v1, v2, weight);
                        }
                    }
                }
            }
        }
        
    }

    private double colorDifference(int rgb1, int rgb2) {
        Color c1 = new Color(rgb1);
        Color c2 = new Color(rgb2);
        int dr = c1.getRed() - c2.getRed();
        int dg = c1.getGreen() - c2.getGreen();
        int db = c1.getBlue() - c2.getBlue();
        return Math.sqrt(dr * dr + dg * dg + db * db);
    }

    public Graph<Point, Double> getGraph() {
        return graph;
    }

    public Vertex<Point> getVertex(Point p) {
        return pixelVertices.get(p);
    }
    
    public void printGraph() {
        for (Vertex<Point> v : graph.vertices()) {
            Point p = v.getElement();
            System.out.print("(" + p.x + "," + p.y + ") ->");
            
            for (Edge<Double> e : graph.outgoingEdges(v)) {
                Vertex<Point> u = graph.opposite(v, e);
                Point neighbor = u.getElement();
                System.out.print("(" + neighbor.x + "," + neighbor.y + ")");
            }
            
            System.out.println();
        }
    }
}
