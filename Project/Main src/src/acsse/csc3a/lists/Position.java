package acsse.csc3a.lists;

import java.io.Serializable;

/**
 * An interface that defines required functionality for a position
 * @param <V>
 */
public interface Position<V> extends Serializable {
	/**
	 * Return the element at that position
	 * @return the element being stored
	 * @throws IllegalStateException
	 */
	V getElement() throws IllegalStateException;
}
