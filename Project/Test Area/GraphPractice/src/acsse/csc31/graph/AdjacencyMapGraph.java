package acsse.csc31.graph;

import acsse.csc3a.lists.LinkedPositionalList;
import acsse.csc3a.lists.PositionalList;
import acsse.csc3a.maps.ChainHashMap;
import acsse.csc3a.maps.Map;

public class AdjacencyMapGraph<V,E> implements Graph<V,E>{

	/*
	 * class for the vertex of the graph
	 */
	private class InnerVertex<V> implements Vertex<V>{
		private V element;
		private Position<Vertex<V>> position;
		private Map<Vertex<V>,Edge<E>> outgoing,incoming;
		
		public InnerVertex(V element) {
			this.element = element;
			/*
			 * directed graph
			 */
			outgoing = new ChainHashMap<>();
			incoming = new ChainHashMap<>();
		}
		
		@Override
		public V getElement() {
			return element;
		}
		
		public void setPosition(Position<Vertex<V>> p) {
			position = p;
		}
		
		public Position<Vertex<V>> getPosition(){
			return position;
		}
		
		public Map<Vertex<V>, Edge<E>> getOutgoing(){
			return outgoing;
		}

		public Map<Vertex<V>, Edge<E>> getIncoming(){
			return incoming;
		}
	}
		
		/*
		 * class for the edge between two vertices
		 */
	private class InnerEdge<E> implements Edge<E>{
		private E element;
		private Position<Edge<E>> position;
		private Vertex<V>[] endpoints;
			
		public InnerEdge(Vertex<V> u,Vertex<V> v,E element) {
			this.element = element;
			endpoints = (Vertex<V>[]) new Vertex[]{u,v};
		}

		@Override
		public E getElement() {
			return element;
		}
			
		/*
		 * returns the refernce to the endpoint array
		*/
		public Vertex<V>[] getEndpoint(){
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
		public Position<Edge<E>> getPosition(){
			return position;
		}
			
	}
		
	private PositionalList<Vertex<V>> vertices = new LinkedPositionalList<>();
	private PositionalList<Edge<E>> edges = new LinkedPositionalList<>();
	
	/*
	 * return the number of vertices of the graph
	 */
	public int numVertices() {
		return vertices.size();
	}
	
	public Iterable<Vertex<V>> vertices(){
		return vertices;
	}
	
	/*
	 * returns the number of edges for a specific for a specific vertex
	 */
	public int outDegree(Vertex<V> v) {
		InnerVertex<V> vert= validate(v);
		return vert.getOutgoing().size();
	}
	
	/*
	 * returns an iterable collection of edges for a specifif vertex
	 */
	public Iterable<Edge<E>> outgoingEdges(Vertex<V> v){
		InnerVertex<V> vert = validate(v);
		return vert.getOutgoing().values();
		
	}
	
	/*
	 * returns the number of edges that have vertex as their destination
	 */
	public int inDegree(Vertex<V> v) {
		InnerVertex<V> vert= validate(v);
		return vert.getIncoming().size();
	}
	
	/*
	 * returns an iterable collection of edges for a specifif vertex
	 */
	@Override
	public Iterable<Edge<E>> incomingEdges(Vertex<V> v){
		InnerVertex<V> vert = validate(v);
		return vert.getIncoming().values();
		
	}

	/*
	 * returns the edge from u to v if they are adjacent
	 */
	public Edge<E> getEdge(Vertex<V> u,Vertex<V> v){
		InnerVertex<V> origin = validate(u);
		return origin.getOutgoing().get(v);
	}
	
	/*
	 * return the vertices of edge e as an array of length two
	 */
	public Vertex<V>[] endVertices(Edge<E> e){
		InnerEdge<E>edge= validate(e);
		return edge.getEndpoint();
	}
	
	/*
	 * returns the vertex that is opposite vertx v on edge
	 */
	public Vertex<V> opposite(Vertex<V> v,Edge<E> e) throws IllegalArgumentException{
		InnerEdge<E> edge = validate(e);
	    Vertex<V>[] endpoints = edge.getEndpoint();
	    if(endpoints[0]==v) {
	    	return endpoints[1];
	    }else if(endpoints[1]== v) {
	    	return endpoints[0];
	    }else {
	    	throw new IllegalArgumentException("visnotincidenttothisedge");
	    }
	}
	
	public Vertex<V> insertVertex(V element){
		InnerVertex<V> v = new InnerVertex<> (element);
		v.setPosition(vertices.addLast(v));
		return v;
	}
	 
	
	public Edge<E>insertEdge(Vertex<V> u,Vertex<V> v,E element) throws IllegalArgumentException{
		if(getEdge(u,v)==null){
			InnerEdge<E> e = new InnerEdge<>(u,v,element);
	    	e.setPosition(edges.addLast(e));
	    	InnerVertex<V> origin = validate(u);
	    	InnerVertex<V> dest = validate(v);
	    	origin.getOutgoing().put(v,e);
	    	dest.getIncoming().put(u,e);
	    return e;
		}else {
			throw new IllegalArgumentException("Edge from u to v exists");
		}
	}
	
	public void removeVertex(Vertex<V> v){
		 InnerVertex<V> vert = validate(v);
		 for(Edge<E> e : vert.getOutgoing().values()) {
		    removeEdge(e);
		 }
		 for(Edge<E>e:vert.getIncoming().values()) {
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
	    u.getOutgoing().remove(v);
	    v.getIncoming().remove(u);

	    // Remove the edge from the graph's master edge list
	    edges.remove(edge.getPosition());
	}


	/*
	 *  Validates and casts a Vertex to InnerVertex
	 */
	private InnerVertex<V> validate(Vertex<V> v) throws IllegalArgumentException {
	    if (!(v instanceof InnerVertex))
	        throw new IllegalArgumentException("Invalid vertex");
	    return (InnerVertex<V>) v;
	}

	/*
	 *  Validates and casts an Edge to InnerEdge
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

