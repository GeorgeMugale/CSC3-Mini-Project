package acsse.csc3a.graph.algorithms;


import java.util.List;
import acsse.csc3a.graph.Edge;
import acsse.csc3a.graph.Graph;
import acsse.csc3a.graph.Vertex;
import acsse.csc3a.lists.ArrayList;
import acsse.csc3a.lists.LinkedPositionalList;
import acsse.csc3a.lists.PositionalList;
import acsse.csc3a.map.AdjacencyMap;
import acsse.csc3a.map.Map;
import acsse.csc3a.priorityQueue.HeapPriorityQueue;
import acsse.csc3a.priorityQueue.PriorityQueue;

/**
 * A class that calculates the MST of a graph as assigns the respective features to it
 * @param <V> the Vertex type of the given graph
 */
public class Prims_MST<V> {

	/**
	 * This method creates a MSTFeatures class object to store the extracted features
	 * @param graph the graph being queried
	 * @return the MTSFeature vector
	 */
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
		
		features.degreeMap = null;
		return features;
	}

}