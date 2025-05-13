package acsse.csc3a.graph.algorithms;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.KeyStore.Entry;
import java.util.List;

import javax.imageio.ImageIO;

import acsse.csc3a.graph.Edge;
import acsse.csc3a.graph.Graph;
import acsse.csc3a.graph.Vertex;
import acsse.csc3a.imagegraph.ImageGraph;
import acsse.csc3a.imagegraph.Point;
import acsse.csc3a.lists.ArrayList;
import acsse.csc3a.lists.LinkedPositionalList;
import acsse.csc3a.lists.PositionalList;
import acsse.csc3a.map.AdjacencyMap;
import acsse.csc3a.map.Map;
import acsse.csc3a.priorityQueue.HeapPriorityQueue;
import acsse.csc3a.priorityQueue.PriorityQueue;

public class Prims_MST<V> {

	// MSTFeatures class to store the extracted features

	public MSTFeatures CalcMST(Graph<V, Float> graph) {

		float Total_Weight = 0;

		MSTFeatures features = new MSTFeatures();
		features.degreeMap = new AdjacencyMap<>();

		// Check if graph is empty
		if (graph.vertices() == null || !graph.vertices().iterator().hasNext()) {
			features.totalWeight = 0f;
			features.averageWeight = 0f;
			features.variance = 0f;
			features.edgeCount = 0;
			return features;
		}

		PositionalList<Edge<Float>> mst = new LinkedPositionalList<>();

		// Get first vertex as starting point
		Vertex<V> start = graph.vertices().iterator().next();

		// Initialize data structures
		PriorityQueue<Float, Vertex<V>> pq = new HeapPriorityQueue<>();
		Map<Vertex<V>, Float> distance = new AdjacencyMap<>();
		Map<Vertex<V>, Edge<Float>> connect = new AdjacencyMap<>();
		Map<Vertex<V>, Boolean> inMST = new AdjacencyMap<>();
		List<Float> edgeWeights = new ArrayList<>();

		// Initialize all vertices
		for (Vertex<V> v : graph.vertices()) {
			float dist = v.equals(start) ? 0 : Integer.MAX_VALUE;
			distance.put(v, dist);
			pq.insert(dist, v);
		}

		while (!pq.isEmpty()) {
			// Get vertex with minimum distance
			Vertex<V> u = pq.removeMin().getValue();
			// Skip if already in MST
			if (inMST.get(u) != null) {
				continue;
			}

			// Add to MST
			inMST.put(u, true);

			// Add connecting edge if not the first vertex
			Edge<Float> connectingEdge = connect.get(u);
			if (connectingEdge != null) {
				mst.addLast(connectingEdge);

				float weight = connectingEdge.getElement();
				Total_Weight += weight;
				edgeWeights.add(weight);

				// Update degree map
				Vertex<V>[] endpoints = graph.endVertices(connectingEdge);
				for (Vertex<V> endpoint : endpoints) {
					int deg = features.degreeMap.get(endpoint) != null ? features.degreeMap.get(endpoint) : 0;
					features.degreeMap.put(endpoint, deg + 1);
				}
			}

			// Process all edges
			for (Edge<Float> e : graph.allEdges(u)) {
				try {
					Vertex<V> v = graph.opposite(u, e);

					// If v not in MST and edge weight is better
					if (inMST.get(v) == null && distance.get(v) != null) {
						float weight = e.getElement();
						if (weight < distance.get(v)) {
							distance.put(v, weight);
							connect.put(v, e);
							pq.insert(weight, v);
							Total_Weight += weight;
						}
					}
				} catch (IllegalArgumentException ex) {
					System.err.println("Invalid edge endpoint: " + ex.getMessage());
				}
			}
		}

		features.totalWeight = Total_Weight;
		features.edgeCount = edgeWeights.size();
		features.averageWeight = features.edgeCount > 0 ? Total_Weight / features.edgeCount : 0f;

		// Variance
		float variance = 0f;
		for (Float w : edgeWeights) {
			float diff = w - features.averageWeight;
			variance += diff * diff;
		}
		features.variance = features.edgeCount > 0 ? variance / features.edgeCount : 0f;

		System.out.println("MST contains " + mst.size() + " edges");
		return features;
	}

	public static void main(String[] args) {

		File imageDir = new File(
				"C:\\Users\\GEORGE MUGALE\\Desktop\\CS3 Mini Project\\Project\\Main src\\data\\reference-data\\WATER_IN_TRANSPARENT");
		Map<String, MSTFeatures> mstMap = new AdjacencyMap<>();
		Prims_MST<Point> mstcalc = new Prims_MST<>();
		if (!imageDir.exists()) {
			System.out.println("Directory does not exist: " + imageDir.getAbsolutePath());
			return;
		}

		if (!imageDir.isDirectory()) {
			System.out.println("Path is not a directory: " + imageDir.getAbsolutePath());
			return;
		}

		if (!imageDir.canRead()) {
			System.out.println("Cannot read directory: " + imageDir.getAbsolutePath());
			return;
		}

		// 2. Get filtered files with null check
		File[] imageFiles = imageDir.listFiles((dir, name) -> {
			String lowerName = name.toLowerCase();
			return lowerName.endsWith(".jpg") || lowerName.endsWith(".png") || lowerName.endsWith(".jpeg");
		});

		if (imageFiles == null) {
			System.out.println("Error accessing directory contents");
			return;
		}

		if (imageFiles.length == 0) {
			System.out.println("No image files found in directory");
			return;
		}

		try {

			int count = 0;

			for (File file : imageFiles) {

				String fname = file.getName();
				System.out.println(file.getAbsolutePath());
				BufferedImage image = ImageIO.read(file);
				ImageGraph graph = new ImageGraph(image);

				if (fname.contains("dirty") && !fname.contains("undrink")) {
					graph.setLabel(MATCH_TYPE.ORANGE);
				} else if (fname.contains("undrink")) {
					graph.setLabel(MATCH_TYPE.RED);
				} else if (fname.contains("moderate")) {
					graph.setLabel(MATCH_TYPE.YELLOW);
				} else {
					graph.setLabel(MATCH_TYPE.GREEN);
				}

				graph.setWaterImageType(CATEGORY_TYPE.WATER_IN_TRANSPARENT);
				
				MSTFeatures features = mstcalc.CalcMST(graph.getGraph());

				features.category_TYPE = graph.getWaterImageType();
				features.match_TYPE = graph.getLabel();
				features.degreeMap = null;
				
				graph = null;
				System.gc();

				mstMap.put(new File(file.getParent()).getName() + file.getName(), features);	

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

	
		File outFile = new File(
				"C:\\Users\\GEORGE MUGALE\\Desktop\\CS3 Mini Project\\Project\\Main src\\data\\reference-data\\WATER_IN_TRANSPARENT\\precomputed-mst-features.dat");

		try (FileOutputStream fos = new FileOutputStream(outFile);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				ObjectOutputStream oos = new ObjectOutputStream(bos);) {

			oos.writeObject(mstMap);

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		

	}


}