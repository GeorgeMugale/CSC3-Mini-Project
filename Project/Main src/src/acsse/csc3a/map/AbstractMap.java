package acsse.csc3a.map;

import acsse.csc3a.lists.ArrayList;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Specialized abstract Hash Map, specialized to b tailor point hashing (where
 * the index of the point is used in the hashing)
 * 
 * @param <K> A key that must be a point
 * @param <V>
 */
public abstract class AbstractMap<K, V> implements Map<K, V> {

	/**
	 * capacity - Length of the table n - Number of entries in the map
	 */
	protected int capacity, n = 0;

	/**
	 * Prime factor used in hash functions for tables with prime-number capacities
	 * Helps with poor hash functions (e.g., x + y for pixel coordinates).
	 */
	private int prime;

	/**
	 * scale - Controls how the table grows/shrinks during resizing. shift - Shift
	 * factor used in bit manipulation for hash code adjustment and index
	 * calculation and helps distribute hash codes more evenly by incorporating
	 * higher-order bits.
	 */
	private long scale, shift;

	/**
	 * The load factor threshold that determines when the hash table should be
	 * resized. When the ratio of entries (n) to capacity exceeds this value, the
	 * table will be expanded to maintain optimal performance.
	 * 
	 * A value of 0.5 means the table will resize when it becomes 50% full. This
	 * provides a balance between memory usage and performance by: - Reducing hash
	 * collisions before they become problematic - Maintaining reasonable memory
	 * efficiency - Keeping the average time complexity near O(1) for operations
	 */
	private float loadFactor = 0.5f;

	/**
	 * Constructs the map with a default capacity
	 */
	public AbstractMap() {
		this(17);
	}

	/**
	 * Constructs the map with a capacity and default prime factor
	 * 
	 * @param cap capacity of the table
	 */
	public AbstractMap(int capacity) {
		this(capacity, 109345121);
	}

	/**
	 * This method changes the load factor, which affectes how large the table grows
	 * 
	 * @param loadFactor
	 */
	public void updateLoadFactor(float loadFactor) {
		if (loadFactor <= 0 || Float.isNaN(loadFactor))
			throw new IllegalArgumentException("Illegal load factor: " + loadFactor);

		this.loadFactor = loadFactor;

		// Optional: resize if current load exceeds new factor
		if (n > capacity * loadFactor) {
			resize((int) (n / loadFactor) + 1);
		}
	}

	/**
	 * Constructs the map with the provided capacity and prime factor
	 * 
	 * @param capacity The initial size of the table (how many buckets available)
	 * @param prime    used in the hash function and it reduce modular bias when the
	 *                 hash function and capacity interact.
	 */
	public AbstractMap(int capacity, int prime) {
		this.prime = prime;
		this.capacity = capacity;
		// to make the hash function unpredictable (collision attacks)
		Random rand = new Random();
		// make the scale factor be a random integer between 1 and prime-1, inclusive
		scale = rand.nextInt(prime - 1) + 1;
		// make a random number between 0 and prime-1, helps distribute keys more evenly
		shift = rand.nextInt(prime);
		// create the table
		createTable();
	}

	@Override
	public int size() {
		return n;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public V get(K key) {
		// use the hash algorithm to determine the bucket to access
		return bucketGet(hashValue(key), key);
	}

	@Override
	public V remove(K key) {
		return bucketRemove(hashValue(key), key);
	}

	@Override
	public V put(K key, V value) {
		/*
		 * if the number of entries in the table more than half the capacity of the
		 * table
		 */
		if (n >= capacity * loadFactor) // Check before insertion to be more optimal
			resize(2 * capacity - 1); // Resize if needed

		return bucketPut(hashValue(key), key, value); // Insert into resized table
	}

	/**
	 * Resizes the table
	 * 
	 * @param newCapacity the size of the new table
	 */
	protected void resize(int newCapacity) {
		// Ensure new capacity is at least double and a prime number
		newCapacity = nextPrime(newCapacity);

		ArrayList<MapEntry<K, V>> buffer = new ArrayList<>(n);
		for (MapEntry<K, V> e : entrySet()) {
			buffer.add(e);
		}

		capacity = newCapacity;
		createTable();
		n = 0;

		for (MapEntry<K, V> e : buffer) {
			put(e.getKey(), e.getValue());
		}
	}

	/**
	 * Returns the next prime number after a given input number
	 * 
	 * @param num the input number being queried
	 * @return a prime number
	 */
	private int nextPrime(int num) {
		if (num <= 1)
			return 2;
		int prime = num;
		boolean found = false;
		while (!found) {
			prime++;
			if (isPrime(prime))
				found = true;
		}
		return prime;
	}

	/**
	 * Checks if the provided number is a prime number or not
	 * 
	 * @param num the number being queried
	 * @return true if num is a prime number, false otherwise
	 */
	private boolean isPrime(int num) {
		if (num <= 1)
			return false;
		if (num == 2)
			return true;
		if (num % 2 == 0)
			return false;
		// loop until half of the number
		for (int i = 3; i * i <= num; i += 2) {
			if (num % i == 0)
				return false;
		}
		return true;
	}

	/**
	 * Creates a hash value to determine the appropriate bucket based on the key
	 * 
	 * @param key the key that is being hashed
	 * @return the appropriate bucket position in the tabel
	 */
	protected int hashValue(K key) {
		int h = key.hashCode();
		// Spread bits to avoid clustering
		h ^= (h >>> 20) ^ (h >>> 12);
		h ^= (h >>> 7) ^ (h >>> 4);
		return (int) ((Math.abs(h * scale + shift) % prime) % capacity);
	}

	/**
	 * This method should create an initially empty table having size equal to a
	 * designated capacity instance variable.
	 */
	protected abstract void createTable();

	/**
	 * This method should mimic the semantics of the public get method, but for a
	 * key k that is known to hash to bucket h.
	 * 
	 * @param h the position of the bucket in the table
	 * @param k the key of the entry at the bucket location
	 * @return the value that was in the bucket
	 */
	protected abstract V bucketGet(int h, K k);

	/*
	 * This method should mimic the semantics of the public put method, but for a
	 * key k that is known to hash to bucket h.
	 * 
	 * @param h the position of the bucket in the table
	 * 
	 * @param k the key of the entry
	 * 
	 * @param v the value of the entry
	 * 
	 * @return the previous value associated with the key
	 */
	protected abstract V bucketPut(int h, K k, V v);

	/**
	 * This method should mimic the semantics of the public remove method, but for a
	 * key k known to hash to bucket h.
	 * 
	 * @param h the position of the bucket in the table
	 * @param k the key of the element being removed
	 * @return the value of the entry being removed
	 */
	protected abstract V bucketRemove(int h, K k);

	public abstract void clear();

	/**
	 * copies all key-value mappings from one map into another.
	 * 
	 * @param m
	 */
	public abstract void putAll(AbstractMap<? extends K, ? extends V> m);

	/**
	 * 
	 * Checks if the map contains at least one entry with the specified value.
	 * 
	 * @param value the value being checked
	 * @return true if the value is in the map, false otherwise
	 */
	public abstract boolean containsValue(Object value);

	/**
	 * Checks if the map contains a specific key.
	 * 
	 * @param key the key being checked
	 * @return true if the key is within the map, false otherwise
	 */
	@SuppressWarnings("unchecked")
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return bucketGet(hashValue((K) key), (K) key) != null;
	}

	/**
	 * A private iterator implementation that provides key iteration capabilities
	 * for the map by delegating to the entry set iterator. This avoids duplicate
	 * key storage while maintaining consistent iteration.
	 */
	private class KeyIterator implements Iterator<K> {

		/**
		 * Uses the entry set iterator as the backing implementation which is
		 * memory-efficient as we don't need to store keys separately
		 */
		private Iterator<MapEntry<K, V>> entries = entrySet().iterator();

		/**
		 * Checks if there are more keys to iterate over
		 * 
		 * @return true if the iteration has more elements, false otherwise
		 */
		public boolean hasNext() {
			// Delegates directly to the entry set iterator
			return entries.hasNext();
		}

		/**
		 * Returns the next key in the iteration
		 * 
		 * @return the next key in the iteration
		 * @throws NoSuchElementException if the iteration has no more elements
		 */
		public K next() throws NoSuchElementException {
			// Gets the next entry and extracts just its key component
			return entries.next().getKey();
		}

		/**
		 * Removal is not supported through this iterator
		 * 
		 * @F UnsupportedOperationException always because we are not allowed to remove
		 */
		public void remove() throws UnsupportedOperationException {
			/*
			 * To maintain consistency with the underlying map structure we prevent
			 * modification
			 */
			throw new UnsupportedOperationException("Remove not supported via KeyIterator");
		}
	}

	/**
	 * A private iterable implementation that provides key iteration capability for
	 * the map by instantiating new KeyIterator objects on demand. This creates a
	 * lightweight view of the map's keys without storing them separately we can
	 * just use this.
	 */
	private class KeyIterable implements Iterable<K> {
		/**
		 * Creates a new iterator instance for traversing all keys in the map
		 * 
		 * @return a new KeyIterator instance
		 */
		public Iterator<K> iterator() {
			// Returns a fresh KeyIterator each time, allowing multiple independent
			// iterations to exist at once
			return new KeyIterator();
		}
	}

	@Override
	public Iterable<K> keySet() {
		return new KeyIterable();
	}

	/**
	 * Private iterator implementation that provides value-only iteration by
	 * delegating to the underlying entry set iterator. This is memory-efficient
	 * because we traverse the values without storing them separately.
	 */
	private class ValueIterator implements Iterator<V> {
		/**
		 * Uses the entry set iterator as the backing implementation which is
		 * memory-efficient efficient than maintaining separate value storage
		 */
		private Iterator<MapEntry<K, V>> entries = entrySet().iterator();

		/**
		 * Checks if there are more values to iterate over
		 * 
		 * @return true if iteration has more elements, false otherwise
		 */
		public boolean hasNext() {
			// Directly delegates to the entry set iterator's hasNext()
			return entries.hasNext();
		}

		/**
		 * Returns the next value in the iteration
		 * 
		 * @return the next mapped value
		 * @throws NoSuchElementException if iteration has no more elements
		 */
		public V next() throws NoSuchElementException {
			// Extracts just the value component from the next entry
			return entries.next().getValue();
		}

		/**
		 * Removal through this iterator is not allowed
		 * 
		 * @throws UnsupportedOperationException always
		 */
		public void remove() throws UnsupportedOperationException {
			// Prevents structural modification through the value iterator to maintain
			// consistency in the maps structure
			throw new UnsupportedOperationException("Remove not supported via ValueIterator");
		}
	}

	/**
	 * Private iterable wrapper that provides on-demand value iteration capability
	 * through ValueIterator instances.
	 */
	private class ValueIterable implements Iterable<V> {
		/**
		 * Creates a fresh iterator instance for traversing all values
		 * 
		 * @return new ValueIterator instance
		 */
		public Iterator<V> iterator() {
			// New iterator each time allows multiple independent iterations
			return new ValueIterator();
		}
	}

	@Override
	public Iterable<V> values() {
		// Returns a new ValueIterable instance that will create fresh iterators
		return new ValueIterable();
	}

	
	/**
	 * composition design pattern used to keep track of both an element and its key
	 * @param <K> the type of the key which maps to an element
	 * @param <V> the type of value mapping from the key
	 */
	protected class MapEntry<K, V> implements Serializable, Entry<K, V> {
		/**
		 * key used to retrieve the value
		 */
		private K k;

		/**
		 * value that the entry stores
		 */
		private V v;

		/**
		 * the distance from the entry's initial bucket
		 */
		protected int dib;

		/**
		 * Constructs an entry that has a key value pair
		 * 
		 * @param key   key used to retrieve the value
		 * @param value value that the entry stores
		 */
		public MapEntry(K key, V value, int dib) {
			k = key;
			v = value;
			this.dib = dib;
		}

		/**
		 * Returns the key of the entry
		 * 
		 * @return Key
		 */
		public K getKey() {
			return k;
		}

		/**
		 * Returns the value of the entry
		 * 
		 * @return Value
		 */
		public V getValue() {
			return v;
		}

		/**
		 * Sets the key of the entry
		 * 
		 * @param key key used to retrieve the value
		 */
		protected void setKey(K key) {
			k = key;
		}

		/**
		 * Sets the value of the entry and returns the old one
		 * 
		 * @param value value that the entry stores
		 * @return the old value before it was set
		 */
		public V setValue(V value) {
			V old = v;
			v = value;
			return old;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return String.format("{Key: %s Value: %s}", k, v);
		}

		@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			return k.hashCode();
		}

	}

}
