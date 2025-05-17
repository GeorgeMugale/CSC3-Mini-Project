package acsse.csc3a.imagegraph;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import acsse.csc3a.map.Map;
import acsse.csc3a.graph.*;
import acsse.csc3a.graph.algorithms.CATEGORY_TYPE;
import acsse.csc3a.graph.algorithms.MATCH_TYPE;
import acsse.csc3a.graph.algorithms.MSTFeatures;
import acsse.csc3a.map.AbstractMap;
import acsse.csc3a.map.AdjacencyMap;


public class ImageGraph implements Serializable {

	transient private BufferedImage image;
	transient private Graph<Point, Float> graph;
	transient private Map<Point, Vertex<Point>> pixelVertices;
	private MSTFeatures features;
	private MATCH_TYPE label;
	private CATEGORY_TYPE waterImageType;
	private int pixelRegionSize;
	private static float EDGE_THRESHOLD = 30.0f;
	private static int sizeFactor = 2000000;
	private static int sizeScaler = sizeFactor / 2;

	public MSTFeatures getFeatures() {
		return features;
	}

	public void setFeatures(MSTFeatures features) {
		this.features = features;
	}

	public CATEGORY_TYPE getWaterImageType() {
		return waterImageType;
	}

	public void setWaterImageType(CATEGORY_TYPE waterImageType) {
		this.waterImageType = waterImageType;
	}

	public void setLabel(MATCH_TYPE label) {
		this.label = label;
	}

	public ImageGraph(BufferedImage image) {

		this.image = image;
		int size = image.getWidth() * image.getHeight();

		if (size >= (sizeFactor - 100000)) {
			this.pixelRegionSize = Math.round(size / sizeScaler);
		} else {
			this.pixelRegionSize = 1;
		}

		this.graph = new AdjacencyMapGraph<>();

		this.pixelVertices = new AdjacencyMap<>();

		buildGraphFromImage();
	}

	private void buildGraphFromImage() {

		final int width = image.getWidth();
		final int height = image.getHeight();
		final int gridWidth = width / pixelRegionSize + 1;
		final int gridHeight = height / pixelRegionSize + 1;

		@SuppressWarnings("unchecked")
		Vertex<Point>[][] vertexGrid = new Vertex[gridHeight][gridWidth];

		// Only two directions (left and up) to prevent duplicate edges
		final int[][] neighborDirections = { { -1, 0 }, { 0, -1 } };

		for (int y = 0; y < height; y += pixelRegionSize) {
			for (int x = 0; x < width; x += pixelRegionSize) {
				// Calculate grid coordinates
				final int gridX = x / pixelRegionSize;
				final int gridY = y / pixelRegionSize;

				// Compute average color for the current block
				Color averageColor = calculateBlockAverageColor(x, y);
				Point regionPoint = createRegionPoint(x, y, averageColor);

				if (pixelVertices.get(regionPoint) == null) {
					// Create and store vertex
					Vertex<Point> vertex = graph.insertVertex(regionPoint);
					vertexGrid[gridY][gridX] = vertex;
					pixelVertices.put(regionPoint, vertex);

					// Connect to neighbors with smart edge pruning
					connectToNeighbors(vertexGrid, vertex, gridX, gridY, averageColor, neighborDirections);
				}
			}
		}

	}

	private Color calculateBlockAverageColor(int startX, int startY) {
		int sumRed = 0, sumGreen = 0, sumBlue = 0, sumAlpha = 0;
		int count = 0;
		final int endY = Math.min(startY + pixelRegionSize, image.getHeight());
		final int endX = Math.min(startX + pixelRegionSize, image.getWidth());

		for (int y = startY; y < endY; y++) {
			for (int x = startX; x < endX; x++) {
				Color currentColor = new Color(image.getRGB(x, y));
				sumRed += currentColor.getRed();
				sumGreen += currentColor.getGreen();
				sumBlue += currentColor.getBlue();
				sumAlpha += currentColor.getAlpha();
				count++;
			}
		}

		return new Color(Math.min(255, sumRed / count), Math.min(255, sumGreen / count), Math.min(255, sumBlue / count),
				Math.min(255, sumAlpha / count));
	}

	private Point createRegionPoint(int x, int y, Color color) {
		int centerX = x + (pixelRegionSize / 2);
		int centerY = y + (pixelRegionSize / 2);
		return new Point(centerX, centerY, color);
	}

	private void connectToNeighbors(Vertex<Point>[][] vertexGrid, Vertex<Point> vertex, int gridX, int gridY,
			Color color, int[][] directions) {
		// keep track of the sum and count of discarded edge weights
		float discardedWeightSum = 0;
		int discardedCount = 0;

		for (int[] direction : directions) {
			int neighborX = gridX + direction[0];
			int neighborY = gridY + direction[1];

			if (isValidGridPosition(vertexGrid, neighborX, neighborY)) {
				Vertex<Point> neighbor = vertexGrid[neighborY][neighborX];

				if (neighbor != null && !neighbor.equals(vertex)) {
					float weight = Point.colorDifference(color, neighbor.getElement().getColour());

					if (shouldCreateEdge(weight)) {
						/*
						 * Include average of previously discarded edges When an edge weight is above
						 * the threshold, the average of all previously discarded weights is added to
						 * the current edge weight before insertion
						 */
						if (discardedCount > 0) {
							weight += (discardedWeightSum / discardedCount);
							// accumulator is reset after being applied to an edge
							discardedWeightSum = 0;
							discardedCount = 0;
						}
						graph.insertEdge(vertex, neighbor, weight);
					} else {
						/*
						 * Accumulate discarded edge weights. When an edge weight is below the threshold
						 * (30.0), its weight is added to the sum and the count is incremented
						 */
						discardedWeightSum += weight;
						discardedCount++;
					}
				}
			}
		}
	}

	private boolean isValidGridPosition(Vertex<Point>[][] grid, int x, int y) {
		return x >= 0 && y >= 0 && y < grid.length && x < grid[0].length;
	}

	private boolean shouldCreateEdge(float weight) {
		return weight > EDGE_THRESHOLD;
	}

	public Graph<Point, Float> getGraph() {
		return graph;
	}

	public MATCH_TYPE getLabel() {
		return label;
	}

	/*
	 * to give a point and get a vertex is trivial caz for the main graph a point is
	 * a vertex either way, and we can use that point in the real graph composit to
	 * hold edges (float wight value)
	 */
	public Vertex<Point> getVertex(Point p) {
		return pixelVertices.get(p);
	}

	public Map<Point, Vertex<Point>> getVerticies() {
		return pixelVertices;
	}

	public void printGraph() {
		for (Vertex<Point> v : graph.vertices()) {
			Point p = v.getElement();
			System.out.print("(" + p.x + "," + p.y + ") ->");

			for (Edge<Float> e : graph.allEdges(v)) {
				Vertex<Point> u = graph.opposite(v, e);
				Point neighbor = u.getElement();
				System.out.print("(" + neighbor.x + "," + neighbor.y + ")");
			}

			System.out.println();
		}
	}

	// Custom serialization logic
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject(); // If you have other non-transient fields
		writeGraph(out);
	}

	// Custom deserialization logic
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject(); // If you have other non-transient fields
		this.graph = readGraph(in);
	}

	private void writeGraph(ObjectOutputStream out) throws IOException {
		// Assign temporary IDs to vertices
		AbstractMap<Vertex<Point>, Integer> vertexIds = new AdjacencyMap<>(graph.numVertices());

		int id = 0;
		for (Vertex<Point> v : graph.vertices()) {
			vertexIds.put(v, id++);

		}

		// Write vertices
		out.writeInt(graph.numVertices());
		for (Vertex<Point> v : graph.vertices()) {

			int i = vertexIds.get(v);

			out.writeInt(i);
			out.writeObject(v.getElement()); // Use writeObject for generic types
		}

		// Write edges
		out.writeInt(graph.numEdges());
		for (Edge<Float> e : graph.edges()) {

			Vertex<Point>[] points = graph.endVertices(e);

			out.writeInt(vertexIds.get(points[0]));
			out.writeInt(vertexIds.get(points[1]));
			out.writeObject(e.getElement()); // Use writeObject for generic types
		}
	}

	private Graph<Point, Float> readGraph(ObjectInputStream in) throws IOException, ClassNotFoundException {
		Graph<Point, Float> graph = new AdjacencyMapGraph<>();

		// Read vertices
		int vertexCount = in.readInt();
		Map<Integer, Vertex<Point>> idToVertex = new AdjacencyMap<>(vertexCount);

		for (int i = 0; i < vertexCount; i++) {
			int id = in.readInt();
			Point element = (Point) in.readObject(); // Cast generic type
			idToVertex.put(id, graph.insertVertex(element));
		}

		// Read edges
		int edgeCount = in.readInt();
		for (int i = 0; i < edgeCount; i++) {
			int startId = in.readInt();
			int endId = in.readInt();
			Float element = (Float) in.readObject(); // Cast generic type

			Vertex<Point> start = idToVertex.get(startId);
			Vertex<Point> end = idToVertex.get(endId);
			graph.insertEdge(start, end, element);
		}

		return graph;
	}

}
