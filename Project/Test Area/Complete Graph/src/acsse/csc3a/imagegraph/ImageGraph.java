package acsse.csc3a.imagegraph;
import java.awt.Color;
import java.awt.image.BufferedImage;
import acsse.csc3a.map.Map;
import acsse.csc3a.graph.*;
import acsse.csc3a.map.AdjacencyMap;

public class ImageGraph {
	
    private BufferedImage image;
    private Graph<Point, Double> graph;
    private Map<Point, Vertex<Point>> pixelVertices;

    public ImageGraph(BufferedImage image) {
        this.image = image;
        this.graph = new AdjacencyMapGraph<>();
        this.pixelVertices = (Map<Point, Vertex<Point>>)new AdjacencyMap<Point, Vertex<Point>>();
        buildGraphFromImage();
    }

    private void buildGraphFromImage() {
        int width = image.getWidth();
        int height = image.getHeight();

        @SuppressWarnings("unchecked")
		Vertex<Point>[][] vertexGrid = new Vertex[height][width];
        int[][] rgbGrid = new int[height][width];

        /*
         * Only two directions (right and down) are used to prevent inserting the same edge twice.
         * Each pixel connects to its neighbors,
         * and as long as each pixel is connected to its right and bottom neighbors, 
         * the graph will still be fully connected for 4-directional adjacency
         */
        int[][] directions = { {-1, 0}, {0, -1} /*,{-1,0}, {0,-1}*/}; // left, top

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
            	int rgb = image.getRGB(x, y);
            	Color color = new Color(rgb);
                Point p = new Point(x, y, color);
                
                Vertex<Point> v = graph.insertVertex(p);

                vertexGrid[y][x] = v;
                rgbGrid[y][x] = rgb;
                pixelVertices.put(p, v);

                // Check only previously visited neighbors to avoid duplication
                for (int[] d : directions) {
                    int nx = x + d[0];
                    int ny = y + d[1];

                    if (nx >= 0 && ny >= 0) {
                        Vertex<Point> neighbor = vertexGrid[ny][nx];
                        Color neighborColor = new Color(rgbGrid[ny][nx]);
                        
                        // Calculate color difference
                        double weight = colorDifference(color, neighborColor);
                        
                        // Only add edge if color difference exceeds threshold (edge pruning)
                        if (weight > 30.0) {
                            graph.insertEdge(v, neighbor, weight);
                        }
                   
                    }
                }
            }
        }
    }

    private double colorDifference(Color color1, Color color2) {
        int dr = color1.getRed() - color2.getRed();
        int dg = color1.getGreen() - color2.getGreen();
        int db = color1.getBlue() - color2.getBlue();
        return Math.sqrt(dr * dr + dg * dg + db * db);
    }

    public Graph<Point, Double> getGraph() {
        return graph;
    }

    /*
     * to give a point and get a vertex is trivial caz for the main graph a point is a vertex either way,
     * and we can use that point in the real graph composit to hold edges (double wight value)
     */
    // the vertex is the point (which we use to get edges) we do not need this method caz with the point, we can use that as a key for the main graph
    public Vertex<Point> getVertex(Point p) {
        return pixelVertices.get(p);
//    	return graph.;
    }
    
    public void printGraph() {
        for (Vertex<Point> v : graph.vertices()) {
            Point p = v.getElement();
            System.out.print("(" + p.x + "," + p.y + ") ->");
            
            for (Edge<Double> e : graph.allEdges(v)) {
                Vertex<Point> u = graph.opposite(v, e);
                Point neighbor = u.getElement();
                System.out.print("(" + neighbor.x + "," + neighbor.y + ")");
            }
            
            System.out.println();
        }
    }
    
    public static void main(String[] args) {
		System.out.println("sdfhg");
	}
}
