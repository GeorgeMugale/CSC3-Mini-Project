package acsse.csc3a.graph;

public interface Graph<V, E> {

	/**
	 * Returns the number of vertices of the graph.
	 * @return return the number of vertices of the graph
	 */
	int numVertices();

	/**
	 * Returns the number of edges of the graph.
	 * @return number of edges
	 */
	int numEdges();
	
	/**
	 * Returns an iteration of all the vertices of the graph
	 * @return an iterable of vertices
	 */
	Iterable<Vertex<V>> vertices();

	/**
	 * Returns an iteration of all the edges of the graph
	 * @return an edges of vertices
	 */
	Iterable<Edge<E>> edges ();

	/**
	 * Returns the number of edges from vertex v
	 * @param v the vertex being queried
	 * @return returns the number of edges for a specific for a specific vertex
	 */
	int degree(Vertex<V> v);

	/**
	 * Returns the all edges from vertex v
	 * @param v the vertex being queried
	 * @return returns an iterable collection of edges for a specific vertex
	 */
	Iterable<Edge<E>> allEdges(Vertex<V> v);

	/**
	 * Returns the edge from vertex u to vertex v, if one exists;
	 * otherwise return null. For an undirected graph, there is no
	 * difference between getEdge(u, v) and getEdge(v, u).
	 * @param u the first vertex at one end of the edge
	 * @param v the vertex at the other end of the edge
	 * @return returns the edge from u to v if they are adjacent
	 */
	Edge<E> getEdge(Vertex<V> u, Vertex<V> v);

	/**
	 * Returns an array containing the two end point vertices of
	 * edge e. If the graph is directed, the first vertex is the origin
	 * and the second is the destination.
	 * @param e the edge being queried
	 * @return return the vertices of edge e as an array of length two
	 */
	Vertex<V>[] endVertices(Edge<E> e);

	/**
	 * For edge e incident to vertex v, returns the other vertex of
	 * the edge; an error occurs if e is not incident to v.
	 * @param v the vertex being queried
	 * @param e the edge being queried
	 * @return returns the vertex that is opposite vertex v on edge
	 */
	Vertex<V> opposite(Vertex<V> v, Edge<E> e);

	/**
	 * Creates and returns a new Vertex storing element x.
	 * @param element the element being added
	 * @return the resulting vertex created to hold the element 
	 */
	Vertex<V> insertVertex(V element);

	/**
	 * Creates and returns a new Edge from vertex u to vertex v,
	 * storing element x; an error occurs if there already exists an 
	 * edge from u to v.
	 * @param u the first vertex
	 * @param v the second vertex
	 * @param element the element being added within the edge
	 * @return the resulting edge created to hold the element
	 * @throws IllegalArgumentException
	 */
	Edge<E> insertEdge(Vertex<V> u, Vertex<V> v, E element) throws IllegalArgumentException;

	/**
	 * Removes vertex v and all its incident edges from the graph.
	 * @param v the vertex being removed
	 */
	void removeVertex(Vertex<V> v);

	/**
	 * Removes edge e from the graph.
	 * @param e the edge being removed
	 */
	void removeEdge(Edge<E> e);
}
