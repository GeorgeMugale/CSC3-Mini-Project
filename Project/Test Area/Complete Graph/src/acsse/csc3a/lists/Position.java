package acsse.csc3a.lists;

public interface Position<V> {
	/**
	 * Return the element at that position
	 * @return the element being stored
	 * @throws IllegalStateException
	 */
	V getElement() throws IllegalStateException;
}
