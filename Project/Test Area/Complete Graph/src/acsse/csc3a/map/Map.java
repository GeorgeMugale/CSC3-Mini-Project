package acsse.csc3a.map;

public interface Map<K, V> {
	
	/**
	 * Returns the number of entries in the map
	 * @return the size
	 */
	int size();
	
	/**
	 * Determines if the Map is empty
	 */
	boolean isEmpty();
	
	
	/**
	 * Gets an element from the tabel using the key
	 * @param key the object being hashed to determine the appropriate value
	 * @return the value
	 */
	V get(K key);
	
	/**
	 * Puts an entry containing the key and value in the table
	 * @param key the key that will be used for look up later
	 * @param value the value corresponding to the key
	 * @return the previous value associated with the key
	 */
	V put(K key, V value);
	
	/**
	 * Removes the entry with that specific key
	 * @param key the key that belongs to the entry intended to be removed
	 * @return the Value of the entry being removed
	 */
	V remove(K key) throws UnsupportedOperationException;
	
	/**
	 * This standard map method iterates through all keys of the map
	 * @return an Iterable of all keys in the map
	 */
	Iterable<K> keySet();
	
	/**
	 * Provides a view of all mapped values as an Iterable collection
	 * @return iterable view of the map's values
	 */
	Iterable<V> values();
	
	/**
	 * This standard map method iterates through all entries of the map
	 * @return an Iterable of all entries in the map
	 */
	Iterable<AbstractMap<K, V>.MapEntry<K, V>> entrySet();
	
}
