package acsse.csc3a.mst;

import acsse.csc31.graph.Edge;
import acsse.csc31.graph.Graph;
import acsse.csc31.graph.Vertex;
import acsse.csc3a.lists.LinkedPositionalList;
import acsse.csc3a.lists.PositionalList;
import acsse.csc3a.maps.ChainHashMap;
import acsse.csc3a.maps.Map;


public class Prims_MST<V> {
    
    public int CalcMST(Graph<V, Integer> graph) {
        PositionalList<Edge<Integer>> mst = new LinkedPositionalList<>();
        int Total_Weight = 0;
        // Check if graph is empty
        if (graph.vertices() == null || !graph.vertices().iterator().hasNext()) {
            return Total_Weight;
        }
        
        // Get first vertex as starting point
        Vertex<V> start = graph.vertices().iterator().next();
        
        // Initialize data structures
        PriorityQueue<Integer, Vertex<V>> pq = new HeapPriorityQueue<>();
        Map<Vertex<V>, Integer> distance = new ChainHashMap<>();
        Map<Vertex<V>, Edge<Integer>> connect = new ChainHashMap<>();
        Map<Vertex<V>, Boolean> inMST = new ChainHashMap<>();
        
        // Initialize all vertices

        for (Vertex<V> v : graph.vertices()) {
        	 int dist = v.equals(start) ? 0 : Integer.MAX_VALUE;
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
            Edge<Integer> connectingEdge = connect.get(u);
            if (connectingEdge != null) {
                mst.addLast(connectingEdge);
            }
            
            // Process all edges
            for (Edge<Integer> e : graph.allEdges(u)) {
                try {
                    Vertex<V> v = graph.opposite(u, e);
                    
                    // If v not in MST and edge weight is better
                    if (inMST.get(v) == null) {
                        int weight = e.getElement();
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
        
        
        System.out.println("MST contains " + mst.size() + " edges");
        return Total_Weight;
    }
}