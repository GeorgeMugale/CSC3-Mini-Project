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

/**
 * A class that encapsulates attributes and behavior required for image graph
 * handling
 */
public class ImageGraph implements Serializable, GraphFeatureSource {

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

	@Override
	public MSTFeatures getFeatures() {
		return features;
	}

	/**
	 * Sets the MSTFeatures of then graph
	 * 
	 * @param features the computed features being set
	 */
	public void setFeatures(MSTFeatures features) {
		this.features = features;
	}

	/**
	 * Category which the image was classified under
	 * 
	 * @return the type of the category
	 */
	public CATEGORY_TYPE getWaterImageType() {
		return waterImageType;
	}

	/**
	 * Sets the category which the image was classified under
	 * 
	 * @param waterImageType the type of the category
	 */
	public void setWaterImageType(CATEGORY_TYPE waterImageType) {
		this.waterImageType = waterImageType;
	}

	/**
	 * Sets the similarity which the image most similar with
	 * 
	 * @param label the type of match
	 */
	public void setLabel(MATCH_TYPE label) {
		this.label = label;
	}

	/**
	 * Gets the type of water image is most similar with
	 * 
	 * @return the type of match
	 */
	public MATCH_TYPE getLabel() {
		return label;
	}

	/**
	 * Constructs a the ImageGraph class using the buffered image provided
	 * 
	 * @param image the image reference
	 */
	public ImageGraph(BufferedImage image) {

		// maintain reference of image
		this.image = image;
		// get the size of the image in pixels
		int size = image.getWidth() * image.getHeight();

		/*
		 * if the size is above the size factor create pixel regions which will reduce
		 * the size and memory overhead
		 */
		if (size >= (sizeFactor - 100000)) {
			pixelRegionSize = Math.round(size / sizeScaler);
		} else {
			// else the region size stays the same
			pixelRegionSize = 1;
		}

		// create our graph will is a representation of the given image
		this.graph = new AdjacencyMapGraph<>();

		/*
		 * create a Point to Pixel Map which will be used to get corresponding vertices
		 * from point
		 */
		this.pixelVertices = new AdjacencyMap<>(size / pixelRegionSize);

		// build the graph from the image
		buildGraphFromImage();
	}

	/**
	 * This method efficiently builds the graph from an image
	 */
	private void buildGraphFromImage() {

		final int width = image.getWidth();
		final int height = image.getHeight();
		final int gridWidth = width / pixelRegionSize + 1;
		final int gridHeight = height / pixelRegionSize + 1;

		// create a grid that will temporarily store pixels next to each other
		@SuppressWarnings("unchecked")
		Vertex<Point>[][] vertexGrid = new Vertex[gridHeight][gridWidth];

		// Only two directions (left and up) to prevent duplicate edges
		final int[][] neighborDirections = { { -1, 0 }, { 0, -1 } };

		for (int y = 0; y < height; y += pixelRegionSize) {
			for (int x = 0; x < width; x += pixelRegionSize) {
				// Calculate relative grid coordinates
				final int gridX = x / pixelRegionSize;
				final int gridY = y / pixelRegionSize;

				// Compute average color for the current block
				Color averageColor = calculateBlockAverageColor(x, y);
				// creates a point representation of the points in the region
				Point regionPoint = createRegionPoint(x, y, averageColor);

				// if the point does not already exist
				if (pixelVertices.get(regionPoint) == null) {
					// Create and store vertex
					Vertex<Point> vertex = graph.insertVertex(regionPoint);
					// add the vertex to the adjacency map
					vertexGrid[gridY][gridX] = vertex;
					pixelVertices.put(regionPoint, vertex);

					// Connect to neighbors with smart edge pruning
					connectToNeighbors(vertexGrid, vertex, gridX, gridY, averageColor, neighborDirections);
				}
			}
		}

	}

	/**
	 * This method calculates the average block color of a region
	 * 
	 * @param startX the start X location of the pixel
	 * @param startY the start Y location of the pixel
	 * @return the average color
	 */
	private Color calculateBlockAverageColor(int startX, int startY) {
		int sumRed = 0, sumGreen = 0, sumBlue = 0, sumAlpha = 0;
		int count = 0;

		// Calculate the end X and Y location of the region
		final int endY = Math.min(startY + pixelRegionSize, image.getHeight());
		final int endX = Math.min(startX + pixelRegionSize, image.getWidth());

		// add up total of RGB values of the region
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

		// calculate the average by dividng by the sum
		return new Color(Math.min(255, sumRed / count), Math.min(255, sumGreen / count), Math.min(255, sumBlue / count),
				Math.min(255, sumAlpha / count));
	}

	/**
	 * This method creates a point from region values
	 * 
	 * @param x     region coordinate of the point
	 * @param y     region coordinate of the point
	 * @param color region color
	 * @return a Pint instance
	 */
	private Point createRegionPoint(int x, int y, Color color) {
		int centerX = x + (pixelRegionSize / 2);
		int centerY = y + (pixelRegionSize / 2);
		return new Point(centerX, centerY, color);
	}

	/**
	 * Connect the current vertex to relevant neighbors
	 * 
	 * @param vertexGrid the grid which stores the vertices in order
	 * @param vertex     the vertex being queried
	 * @param gridX      the current column index of the vertex on the grid
	 * @param gridY      the current row index of the vertex on the grid
	 * @param color      the average color of the region point
	 * @param directions the possible offset direction used to reach neighboring
	 *                   region points
	 */
	private void connectToNeighbors(Vertex<Point>[][] vertexGrid, Vertex<Point> vertex, int gridX, int gridY,
			Color color, int[][] directions) {
		// keep track of the sum and count of discarded edge weights
		float discardedWeightSum = 0;
		int discardedCount = 0;

		// for each offset direction
		for (int[] direction : directions) {
			// calculate possible neighbor positions with direction
			int neighborX = gridX + direction[0];
			int neighborY = gridY + direction[1];

			// if it is a valid position within the grid
			if (isValidGridPosition(vertexGrid, neighborX, neighborY)) {
				// possible neighbor
				Vertex<Point> neighbor = vertexGrid[neighborY][neighborX];

				// if there is a neighbor
				if (neighbor != null && !neighbor.equals(vertex)) {
					// calculate the weight
					float weight = Point.colorDifference(color, neighbor.getElement().getColour());

					// if the weight is above the threshold
					if (shouldCreateEdge(weight)) {
						/*
						 * Include average of previously discarded edges When an edge weight is above
						 * the threshold, the average of all previously discarded weights within the
						 * possible neighbor distance is added to the current edge weight before
						 * insertion
						 */
						if (discardedCount > 0) {
							// calculate new weight
							weight += (discardedWeightSum / discardedCount);
							// accumulator reset after being applied to an edge
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

	/**
	 * Checks if the given x and y position is within the bounds of the grid
	 * 
	 * @param grid the grid being queried
	 * @param x    the x location being queried
	 * @param y    the y location being queried
	 * @return true if the x and y location is valid
	 */
	private boolean isValidGridPosition(Vertex<Point>[][] grid, int x, int y) {
		return x >= 0 && y >= 0 && y < grid.length && x < grid[0].length;
	}

	/**
	 * checks if the weight is above the edge threshold
	 * @param weight the weight being queried
	 * @return true if the weight is above the threshold, false otherwise
	 */
	private boolean shouldCreateEdge(float weight) {
		return weight > EDGE_THRESHOLD;
	}

	/**
	 * Gets the underlying graph
	 * @return the actual graph ADT
	 */
	public Graph<Point, Float> getGraph() {
		return graph;
	}

	
	/**
	 * Gets a {@link #Vertex<Point>} from a given {@link #Point}
	 * @param p the point being queried
	 * @return the associated vertex
	 */
	public Vertex<Point> getVertex(Point p) {
		return pixelVertices.get(p);
	}

	/**
	 * Returns a {@link #Point} to {@link #Vertex<Point>} mapping
	 * @return a map to retrieve a vertex from a point efficiently
	 */
	public Map<Point, Vertex<Point>> getVerticies() {
		return pixelVertices;
	}

	/**
	 * Prints the graph, for debugging purposes
	 */
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

	/**
	 * Custom serialization logic
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject(); // If you have other non-transient fields
		writeGraph(out);
	}

	/**
	 * Custom deserialization logic
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject(); // If you have other non-transient fields
		this.graph = readGraph(in);
	}

	/**
	 * Efficiently writes a graph to a file
	 * @param out
	 * @throws IOException
	 */
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

	/**
	 * Efficiently read a graph object from a file
	 * @param in
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
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
