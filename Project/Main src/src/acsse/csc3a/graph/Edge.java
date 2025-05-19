package acsse.csc3a.graph;

import java.io.Serializable;

/**
 * An interface which species functionality which all edges must implement
 * @param <V> the type which the edge holds
 */
public interface Edge<E> extends Serializable {
	
	/**
	 * Gets the element which the vertex contains
	 * @return the element contained by the vertex
	 */
	E getElement();
}
