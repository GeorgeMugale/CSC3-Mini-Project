package acsse.csc3a.graph;

import java.io.Serializable;

/**
 * An interface which species functionality which all vertices must implement
 * @param <V> the type which the vertex holds
 */
public interface Vertex<V> extends Serializable {
	
	/**
	 * Gets the element which the edge contains
	 * @return the element contained by the edge
	 */
	V getElement();
}
