package acsse.csc3a.imagegraph;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.security.PublicKey;

import acsse.csc3a.map.Map;
import acsse.csc3a.graph.*;
import acsse.csc3a.graph.algorithms.WATER_IMAGE_TYPE;
import acsse.csc3a.map.AdjacencyMap;

public class ImageGraph implements AbstractVisitable {

	private BufferedImage image;
	private Graph<Point, Double> graph;
	private Map<Point, Vertex<Point>> pixelVertices;
	private String label;
	private WATER_IMAGE_TYPE waterImageType;
	private int pixelRegionSize;

	public WATER_IMAGE_TYPE getWaterImageType() {
		return waterImageType;
	}

	public void setWaterImageType(WATER_IMAGE_TYPE waterImageType) {
		this.waterImageType = waterImageType;
	}
	
	public ImageGraph(BufferedImage image) {

		this.image = image;
		int size = image.getWidth() * image.getHeight();
		
		if (size > 2000000) {
			this.pixelRegionSize = Math.round(size / 2000000);
		}else {
			this.pixelRegionSize = 1;
		}
		
		this.graph = new AdjacencyMapGraph<>();
		this.pixelVertices = (Map<Point, Vertex<Point>>) new AdjacencyMap<Point, Vertex<Point>>();
		buildGraphFromImage();
		

		System.out.println(this.pixelVertices.size());
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

				// Create and store vertex
				Vertex<Point> vertex = graph.insertVertex(regionPoint);
				vertexGrid[gridY][gridX] = vertex;
				pixelVertices.put(regionPoint, vertex);

				// Connect to neighbors with smart edge pruning
				connectToNeighbors(vertexGrid, vertex, gridX, gridY, averageColor, neighborDirections);

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
		double discardedWeightSum = 0;
		int discardedCount = 0;

		for (int[] direction : directions) {
			int neighborX = gridX + direction[0];
			int neighborY = gridY + direction[1];

			if (isValidGridPosition(vertexGrid, neighborX, neighborY)) {
				Vertex<Point> neighbor = vertexGrid[neighborY][neighborX];

				if (neighbor != null) {
					double weight = Point.colorDifference(color, neighbor.getElement().getColour());

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

	private boolean shouldCreateEdge(double weight) {
		final double EDGE_THRESHOLD = 30.0;
		return weight > EDGE_THRESHOLD;
	}

	public Graph<Point, Double> getGraph() {
		return graph;
	}

	public String getLabel() {
		return label;
	}

	/*
	 * to give a point and get a vertex is trivial caz for the main graph a point is
	 * a vertex either way, and we can use that point in the real graph composit to
	 * hold edges (double wight value)
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

			for (Edge<Double> e : graph.allEdges(v)) {
				Vertex<Point> u = graph.opposite(v, e);
				Point neighbor = u.getElement();
				System.out.print("(" + neighbor.x + "," + neighbor.y + ")");
			}

			System.out.println();
		}
	}

	@Override
	public void accept(AbstractVisitor visitor) {
		// TODO Auto-generated method stub
		visitor.visit(this);
	}
}
