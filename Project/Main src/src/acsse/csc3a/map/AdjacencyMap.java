package acsse.csc3a.map;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

import acsse.csc3a.graph.Vertex;
import acsse.csc3a.imagegraph.Point;

/**
 * Specialized AdjacencyMap with Robin Hood hashing for water contamination
 * analysis
 * 
 * @param <K> Vertex type (typically coordinates or pixel IDs)
 * @param <V> Edge type representing connections between vertices
 */
public class AdjacencyMap<K, V> extends AbstractMap<K, V> {
	private MapEntry<K, V>[] table;
	private final MapEntry<K, V> DEFUNCT_SENTINEL = new MapEntry<>(null, null, -1);
	private static final int MAX_PROBE_LIMIT = 32; // Prevent infinite loops

	public AdjacencyMap() {
		super();
	}

	public AdjacencyMap(int capacity) {
		super(capacity);
	}

	public AdjacencyMap(int capacity, int prime) {
		super(capacity, prime);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void createTable() {
		table = (MapEntry<K, V>[]) new MapEntry[capacity];
	}

	/**
	 * Robin Hood get operation with precomputed hash
	 * 
	 * @param h   Precomputed hash value
	 * @param key Vertex key to lookup
	 * @return Edge value if found, null otherwise
	 */
	@Override
	protected V bucketGet(int h, K key) {
		int hash = h;
		int probe = 0;
	

		while (table[hash] != null && probe <= MAX_PROBE_LIMIT) {
			MapEntry<K, V> entry = table[hash];
			// Check for match first to optimize successful lookups
			if (entry != DEFUNCT_SENTINEL && entry.getKey().equals(key)) {
				return entry.getValue();
			}

			// Early exit if we hit an empty slot (for unsuccessful lookups)
			if (entry == null) {
				break;
			}

			hash = (hash + 1) % capacity;
			probe++;
		}

		return null;
	}

	/**
	 * Inserts a key-value pair using Robin Hood hashing and returns the previous
	 * value
	 * 
	 * @param h     Precomputed hash index (from hashFunction(key))
	 * @param key   Key to insert
	 * @param value Value to associate with key
	 * @return Previous value associated with key, or null if no previous mapping
	 * @throws IllegalStateException when the probe limit has exceeded
	 */
	@Override

	protected V bucketPut(int h, K key, V value) throws IllegalStateException {
		int hash = h;
		int probe = 0;
		MapEntry<K, V> newEntry = new MapEntry<>(key, value, probe);
		V oldValue = null;

		while (probe <= MAX_PROBE_LIMIT) {
			MapEntry<K, V> existing = table[hash];

			// Case 1: Empty or tombstone slot
			if (existing == null || existing == DEFUNCT_SENTINEL) {
				table[hash] = newEntry;
				n++;
				return oldValue;
			}

			// Case 2: Key exists - update value
			if (existing.getKey().equals(key)) {
				oldValue = existing.getValue();
				existing.setValue(value);
				return oldValue;
			}

			// Case 3: Robin Hood swap
			if (probe > existing.dib) {
				// Swap entries and continue with displaced entry
				table[hash] = newEntry;
				newEntry = existing;
				probe = existing.dib;
			}

			// Linear probing
			hash = (hash + 1) % capacity;
			probe++;
		}

		// Probing limit exceeded - resize and retry
		resize(findNewCapacity());
		return bucketPut(hashValue(key), key, value);
	}

	private int findNewCapacity() {
		// Find next prime number for better distribution
		int newCapacity = capacity * 2;
		while (!isPrime(newCapacity)) {
			newCapacity++;
		}
		return newCapacity;
	}

	private boolean isPrime(int num) {
		if (num <= 1)
			return false;
		if (num == 2)
			return true;
		if (num % 2 == 0)
			return false;
		for (int i = 3; i * i <= num; i += 2) {
			if (num % i == 0)
				return false;
		}
		return true;
	}

	/**
	 * Remove operation with backward shifting
	 * 
	 * @param h   Precomputed hash value
	 * @param key Vertex key to remove
	 * @return Removed edge value
	 */
	@Override
	protected V bucketRemove(int h, K key) {
		int currentPos = findKeyIndex(h, key);
		if (currentPos == -1)
			return null;

		V removedValue = table[currentPos].getValue();
		table[currentPos] = DEFUNCT_SENTINEL;
		n--;

		// Backward shift
		int nextPos = (currentPos + 1) % capacity;
		while (table[nextPos] != null && table[nextPos].dib > 0) {
			table[currentPos] = table[nextPos];
			table[currentPos].dib--;
			table[nextPos] = DEFUNCT_SENTINEL;
			currentPos = nextPos;
			nextPos = (nextPos + 1) % capacity;
		}

		return removedValue;
	}

	/**
	 * Private utility that finds the index of the key by probing
	 * 
	 * @param h   the precomuted hash
	 * @param key the key of the element
	 * @return the index in the array
	 */
	private int findKeyIndex(int h, K key) {
		int hash = h;
		int probe = 0;

		while (table[hash] != null && probe <= MAX_PROBE_LIMIT) {
			if (table[hash] != DEFUNCT_SENTINEL && table[hash].getKey().equals(key)) {
				return hash;
			}
			hash = (hash + 1) % capacity;
			probe++;
		}
		return -1;
	}

	@Override
	public boolean containsValue(Object value) {
		for (MapEntry<K, V> entry : table) {
			if (entry != null && entry != DEFUNCT_SENTINEL && Objects.equals(value, entry.getValue())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterable<MapEntry<K, V>> entrySet() {
		ArrayList<MapEntry<K, V>> entries = new ArrayList<>(n);
		for (MapEntry<K, V> entry : table) {
			if (entry != null && entry != DEFUNCT_SENTINEL) {
				entries.add(entry);
			}
		}
		return entries;
	}

	@Override
	public Iterable<K> keySet() {
		// TODO Auto-generated method stub
		ArrayList<K> keys = new ArrayList<>(n);
		for (MapEntry<K, V> entry : table) {
			if (entry != null && entry != DEFUNCT_SENTINEL) {
				keys.add(entry.getKey());
			}
		}
		return keys;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public void putAll(AbstractMap<? extends K, ? extends V> m) {
		// TODO Auto-generated method stub

		for (AbstractMap<? extends K, ? extends V>.MapEntry<? extends K, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}

	}

}