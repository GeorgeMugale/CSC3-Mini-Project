package acsse.csc3a.graph;

import acsse.csc3a.lists.LinkedPositionalList;
import acsse.csc3a.lists.PositionalList;
import acsse.csc3a.map.AbstractMap;
import acsse.csc3a.map.AdjacencyMap;
import acsse.csc3a.lists.Position;

/**
 * A graph that is implemented using an adjacency map
 * @param <V> the vertex
 * @param <E> the edge
 */
public class AdjacencyMapGraph<V, E> implements Graph<V, E> {

	/*
	 * class for the vertex of the graph
	 */
	private class InnerVertex<V> implements Vertex<V> {
		private V element;
		private Position<Vertex<V>> position;
		private AbstractMap<Vertex<V>, Edge<E>> incidenceMap;

		public InnerVertex(V element) {
			this.element = element;
			/*
			 * undirected graph
			 */
			incidenceMap = new AdjacencyMap<>();
		}

		@Override
		public V getElement() {
			return element;
		}

		public void setPosition(Position<Vertex<V>> p) {
			position = p;
		}

		public Position<Vertex<V>> getPosition() {
			return position;
		}

		public AbstractMap<Vertex<V>, Edge<E>> getIncidenceMap() {
			return incidenceMap;
		}
	}

	/*
	 * class for the edge between two vertices
	 */
	private class InnerEdge<E> implements Edge<E> {
		private E element;
		private Position<Edge<E>> position;
		private Vertex<V>[] endpoints;

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
			throw new IllegalArgumentException("visnotincidenttothisedge");
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

	public static void main(String[] args) {
	}
}
