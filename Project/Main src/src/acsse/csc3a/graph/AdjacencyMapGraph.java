package acsse.csc3a.graph;


import acsse.csc3a.lists.LinkedPositionalList;
import acsse.csc3a.lists.PositionalList;
import acsse.csc3a.map.AbstractMap;
import acsse.csc3a.map.AdjacencyMap;
import acsse.csc3a.lists.Position;

/**
 * A graph that is implemented using an adjacency map
 * 
 * @param <V> the type the vertex will hold
 * @param <E> the type the edge will hold
 */
public class AdjacencyMapGraph<V, E> implements Graph<V, E> {

	/**
	 * A class for the vertex representation of the graph
	 * @param <V>
	 */
	private class InnerVertex<V> implements Vertex<V> {
		private V element;
		transient private Position<Vertex<V>> position;
		transient private AbstractMap<Vertex<V>, Edge<E>> incidenceMap;

		/**
		 * Constructs a vertex containing the specified element
		 * @param element the element being stored
		 */
		public InnerVertex(V element) {
			this.element = element;
			/*
			 * undirected graph
			 */
			incidenceMap = new AdjacencyMap<>();
			incidenceMap.updateLoadFactor((float) 0.5);
		}

		@Override
		public V getElement() {
			return element;
		}

		/**
		 * Stores the position of this vertex within the graph's vertex list
		 * @param p the position within the graphs vertex list
		 */
		public void setPosition(Position<Vertex<V>> p) {
			position = p;
		}

		/**
		 * Gets the position of this vertex within the graph's vertex list
		 * @return the position within the graphs vertex list
		 */
		public Position<Vertex<V>> getPosition() {
			return position;
		}

		/** 
		 * Gets all edges incident to this vertex
		 * @return reference to the underlying map of all edges
		 */
		public AbstractMap<Vertex<V>, Edge<E>> getIncidenceMap() {
			return incidenceMap;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return String.format("Vertex<%s>", element);
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			return element.equals(((Vertex<V>) obj).getElement());
		}

		@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			return element.hashCode();
		}
	}

	/*
	 * class for the edge between two vertices
	 */
	private class InnerEdge<E> implements Edge<E> {
		private E element;
		private Position<Edge<E>> position;
		private Vertex<V>[] endpoints;

		/**
		 * Constructs an edge instance with its value and associated vertices from u to v
		 * @param u the first associated vertex
		 * @param v the second associated vertex
		 * @param element the value of the edge between the vertecies
		 */
		@SuppressWarnings("unchecked")
		public InnerEdge(Vertex<V> u, Vertex<V> v, E element) {
			this.element = element;
			endpoints = (Vertex<V>[]) new Vertex[] { u, v };
		}

		@Override
		public E getElement() {
			return element;
		}

		/*
		 * returns the refernce to the endpoint array
		 */
		public Vertex<V>[] getEndpoint() {
			return endpoints;
		}

		/*
		 * stores the position of the edge withini the vertex list
		 */
		public void setPosition(Position<Edge<E>> p) {
			position = p;
		}

		/*
		 * returns the position of the edge within the vertex list
		 */
		public Position<Edge<E>> getPosition() {
			return position;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			return element.equals(((Edge<E>) obj).getElement());
		}

		@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			return element.hashCode();
		}

	}

	private PositionalList<Vertex<V>> vertices = new LinkedPositionalList<>();
	private PositionalList<Edge<E>> edges = new LinkedPositionalList<>();

	@Override
	public int numVertices() {
		return vertices.size();
	}

	@Override
	public Iterable<Vertex<V>> vertices() {
		return vertices;
	}

	@Override
	public int degree(Vertex<V> v) {
		InnerVertex<V> vert = validate(v);
		return vert.getIncidenceMap().size();
	}

	@Override
	public Iterable<Edge<E>> allEdges(Vertex<V> v) {
		InnerVertex<V> vert = validate(v);
		return vert.getIncidenceMap().values();
	}

	@Override
	public Edge<E> getEdge(Vertex<V> u, Vertex<V> v) {
		// get the inner vertex by validating
		InnerVertex<V> origin = validate(u);
		// get the edge (value) from the map, using the vertex as the key
		return origin.getIncidenceMap().get(v);
	}

	@Override
	public Vertex<V>[] endVertices(Edge<E> e) {
		InnerEdge<E> edge = validate(e);
		return edge.getEndpoint();
	}

	@Override
	public Vertex<V> opposite(Vertex<V> v, Edge<E> e) throws IllegalArgumentException {
		InnerEdge<E> edge = validate(e);
		Vertex<V>[] endpoints = edge.getEndpoint();
		if (endpoints[0] == v) {
			return endpoints[1];
		} else if (endpoints[1] == v) {
			return endpoints[0];
		} else {
			throw new IllegalArgumentException("v is not incident to this edge");
		}
	}

	@Override
	public Vertex<V> insertVertex(V element) {
		InnerVertex<V> v = new InnerVertex<>(element);
		v.setPosition(vertices.addLast(v));
		return v;
	}

	@Override
	public Edge<E> insertEdge(Vertex<V> u, Vertex<V> v, E element) throws IllegalArgumentException {
		if (getEdge(u, v) == null) {
			InnerEdge<E> e = new InnerEdge<>(u, v, element);
			e.setPosition(edges.addLast(e));
			InnerVertex<V> origin = validate(u);
			InnerVertex<V> dest = validate(v);
			origin.getIncidenceMap().put(v, e);
			dest.getIncidenceMap().put(u, e);

			return e;
		} else {
			throw new IllegalArgumentException("Edge from u to v exists");
		}
	}

	@Override
	public void removeVertex(Vertex<V> v) {
		InnerVertex<V> vert = validate(v);
		for (Edge<E> e : vert.getIncidenceMap().values()) {
			removeEdge(e);
		}
		for (Edge<E> e : vert.getIncidenceMap().values()) {
			removeEdge(e);
		}
		vertices.remove(vert.getPosition());
	}

	@Override
	public void removeEdge(Edge<E> e) {
		InnerEdge<E> edge = validate(e); // type check and cast
		Vertex<V>[] endpoints = edge.getEndpoint(); // [u, v]
		InnerVertex<V> u = validate(endpoints[0]);
		InnerVertex<V> v = validate(endpoints[1]);

		// Remove the edge from both vertices' maps
		u.getIncidenceMap().remove(v);
		v.getIncidenceMap().remove(u);

		// Remove the edge from the graph's master edge list
		edges.remove(edge.getPosition());
	}

	/*
	 * Validates and casts a Vertex to InnerVertex
	 */
	private InnerVertex<V> validate(Vertex<V> v) throws IllegalArgumentException {
		if (!(v instanceof InnerVertex))
			throw new IllegalArgumentException("Invalid vertex");
		return (InnerVertex<V>) v;
	}

	/**
	 * Validates and casts an Edge to InnerEdge
	 * 
	 * @param e the edge being queried
	 * @return the InnerEdge instance of the edge
	 * @throws IllegalArgumentException if the edge is not an instance of InnerEdge
	 */
	private InnerEdge<E> validate(Edge<E> e) throws IllegalArgumentException {
		if (!(e instanceof InnerEdge))
			throw new IllegalArgumentException("Invalid edge");
		return (InnerEdge<E>) e;
	}

	@Override
	public int numEdges() {
		return edges.size();
	}

	@Override
	public Iterable<Edge<E>> edges() {
		return edges;
	}

}
