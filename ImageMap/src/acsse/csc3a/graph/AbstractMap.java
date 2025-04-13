package acsse.csc3a.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Specialized abstract Map
 * @param <K>
 * @param <V>
 */
public abstract class AbstractMap<K, V> {

	/**
	 * capacity - Length of the table
	 * n - Number of entries in the map
	 */
	protected int capacity, n = 0;
	
	
	/**
	 * Prime factor used in hash functions for tables with prime-number capacities
	 * Helps with poor hash functions (e.g., x + y for pixel coordinates).
	 */
	private int prime;
	
	
	/**
	 * scale - Controls how the table grows/shrinks during resizing.
	 * shift - Shift factor used in bit manipulation for hash code adjustment and index calculation and helps distribute hash codes more evenly by incorporating higher-order bits.
	 */
	private long scale, shift;

	/**
	 * Constructs the map with a default capacity
	 */
	public AbstractMap() {
		this(17);
	}

	/**
	 * Constructs the map with a capacity and default prime factor
	 * @param cap capacity of the table
	 */
	public AbstractMap(int capacity) {
		this(capacity, 109345121);
	}

	/**
	 * Constructs the map with the provided capacity and prime factor
	 * @param capacity The initial size of the table (how many buckets available)
	 * @param prime used in the hash function and it reduce modular bias when the hash function and capacity interact.
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
	
	/**
	 * Returns the number of entries in the map
	 * @return the size
	 */
	public int size() {
		return n;
	}

	/**
	 * Determines if the Map is empty
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Gets an element from the tabel using the key
	 * @param key the object being hashed to determine the appropriate value
	 * @return the value
	 */
	public V get(K key) {
		// use the hash algorithm to determine the bucket to access
		return bucketGet(hashValue(key), key);
	}

	/**
	 * Puts an entry containing the key and value in the table
	 * @param key the key that will be used for look up later
	 * @param value the value corresponding to the key
	 * @return the previous value associated with the key
	 */
	public V put(K key, V value) {
		// if the number of entries in the table more than half the capacity of the table 
	    if (n >= capacity / 2)  // Check before insertion to be more optimal
	        resize(2 * capacity - 1);  // Resize if needed
	    
	    return bucketPut(hashValue(key), key, value);  // Insert into resized table
	}

	/**
	 * Resizes the table
	 * @param newCapacity the size of the new table
	 */
	private void resize(int newCapacity) {
		// create a new array list of the size
		ArrayList<MapEntry<K, V>> buffer = new ArrayList<>(n);
		
		// add everything in the table to the array list
		for (MapEntry<K, V> e : entrySet())
			buffer.add(e);
		
		// update the capacity
		capacity = newCapacity;
		
		// create the new table based on updated capacity
		createTable();
		// set size of the new table to 0, this will be incremented while we put everything back
		n = 0;
		// put everything in the array list back in the table 
		for (MapEntry<K, V> e : buffer)
			put(e.getKey(), e.getValue());
	}

	/**
	 * Removes the entry with that specific key
	 * @param key the key that belongs to the entry intended to be removed
	 * @return the Value of the entry being removed
	 */
	public V remove(K key) {
		return bucketRemove(hashValue(key), key);
	}

	/**
	 * Creates a hash value to determine the appropriate bucket based on the key
	 * @param key the key that is being hashed
	 * @return the appropriate bucket position in the tabel
	 */
	private int hashValue(K key) {
		return (int) ((Math.abs(key.hashCode() * scale + shift) % prime) /*% capacity*/);
	}
	
	/**
	 * Specialized hash function designed to convert 2D vertex coordinates (x, y) into a well-distributed integer hash value. 
	 * @param x the x coordinate of the vertex
	 * @param y the y coordinate of the vertex
	 * @return
	 */
	public int hashValue(int x, int y) {
		// Combines coordinates with different weights effectively voiding simple patterns
		long hash = x * 0x4F1BBCDC + y * 0x31415927; // Two large primes
		// XORs the original hash with its right-shifted 32-bit version
		return (int) (hash ^ (hash >>> 32));
	}
	
	/**
	 * This method should create an initially empty table having size equal to a designated capacity instance variable.
	 */
	protected abstract void createTable();

	/**
	 * This method should mimic the semantics of the public get 
	 * method, but for a key k that is known to hash to bucket h.
	 * @param h the position of the bucket in the table
	 * @param k the key of the entry at the bucket location
	 * @return the value that was in the bucket
	 */
	protected abstract V bucketGet(int h, K k);

	/*
	 * This method should mimic the semantics of the public put
	 * method, but for a key k that is known to hash to bucket h.
	 * @param h the position of the bucket in the table
	 * @param k the key of the entry
	 * @param v the value of the entry
	 * @return the previous value associated with the key
	 */
	protected abstract V bucketPut(int h, K k, V v);

	/**
	 * This method should mimic the semantics of the public 
	 * remove method, but for a key k known to hash to bucket h.
	 * @param h the position of the bucket in the table
	 * @param k the key of the element being removed
	 * @return the value of the entry being removed
	 */
	protected abstract V bucketRemove(int h, K k);

	/**
	 * This standard map method iterates through all entries of the map
	 * @return an Iterable of all entries in the map
	 */
	public abstract Iterable<MapEntry<K, V>> entrySet();

	/**
	 * This standard map method iterates through all keys of the map
	 * @return an Iterable of all keys in the map
	 */
	public abstract Set<K> keySet();

	/**
	 * 
	 */
	public abstract void clear();

	/**
	 * copies all key-value mappings from one map into another.
	 * @param m
	 */
	public abstract void putAll(AbstractMap<? extends K, ? extends V> m);

	/**
	 * 
	 * Checks if the map contains at least one entry with the specified value.
	 * @param value the value being checked
	 * @return true if the value is in the map, false otherwise
	 */
	public abstract boolean containsValue(Object value);

	/**
	 * Checks if the map contains a specific key.
	 * @param key the key being checked
	 * @return true if the key is within the map, false otherwise
	 */
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return bucketGet(hashValue((K)key), (K)key) != null;
	}
	
	/**
	 * A private iterator implementation that provides key iteration 
	 * capabilities for the map by delegating to the entry set iterator.
	 * This avoids duplicate key storage while maintaining consistent iteration.
	 */
	private class KeyIterator implements Iterator<K> {
		
	    /**
	     * Uses the entry set iterator as the backing implementation which is memory-efficient as we don't need to store keys separately
	     */
	    private Iterator<MapEntry<K, V>> entries = entrySet().iterator();

	    /**
	     * Checks if there are more keys to iterate over
	     * @return true if the iteration has more elements, false otherwise
	     */
	    public boolean hasNext() {
	        // Delegates directly to the entry set iterator
	        return entries.hasNext();
	    }

	    /**
	     * Returns the next key in the iteration
	     * @return the next key in the iteration
	     * @throws NoSuchElementException if the iteration has no more elements
	     */
	    public K next() {
	        // Gets the next entry and extracts just its key component
	        return entries.next().getKey();
	    }

	    /**
	     * Removal is not supported through this iterator
	     * @throws UnsupportedOperationException always because we are not allowed to remove
	     */
	    public void remove() {
	        // To maintain consistency with the underlying map structure we prevent modification
	        throw new UnsupportedOperationException("Remove not supported via KeyIterator");
	    }
	}

	/**
	 * A private iterable implementation that provides key iteration capability
	 * for the map by instantiating new KeyIterator objects on demand.
	 * This creates a lightweight view of the map's keys without storing them separately we can just use this.
	 */
	private class KeyIterable implements Iterable<K> {
	    /**
	     * Creates a new iterator instance for traversing all keys in the map
	     * @return a new KeyIterator instance
	     */
	    public Iterator<K> iterator() {
	        // Returns a fresh KeyIterator each time, allowing multiple independent iterations to exist at once
	        return new KeyIterator();
	    }
	}

	/**
	 * Provides an alternative key iteration view (alias for keySet())
	 * @return an iterable view of the map's keys (alternative accessor)
	 */
	public Iterable<K> keyIterable() {
	    // A method that offers the same functionality as keySet() but with iterable nature rather than returning a set
	    return new KeyIterable();
	}
	
	
	/**
	 * Private iterator implementation that provides value-only iteration
	 * by delegating to the underlying entry set iterator.
	 * This is memory-efficient because we traverse the values without storing them separately.
	 */
	private class ValueIterator implements Iterator<V> {
		/**
	     * Uses the entry set iterator as the backing implementation which is memory-efficient efficient than maintaining separate value storage
	     */
	    private Iterator<MapEntry<K, V>> entries = entrySet().iterator();

	    /**
	     * Checks if there are more values to iterate over
	     * @return true if iteration has more elements, false otherwise
	     */
	    public boolean hasNext() {
	        // Directly delegates to the entry set iterator's hasNext()
	        return entries.hasNext();
	    }

	    /**
	     * Returns the next value in the iteration
	     * @return the next mapped value
	     * @throws NoSuchElementException if iteration has no more elements
	     */
	    public V next() {
	        // Extracts just the value component from the next entry
	        return entries.next().getValue();
	    }

	    /**
	     * Removal through this iterator is not allowed
	     * @throws UnsupportedOperationException always
	     */
	    public void remove() {
	        // Prevents structural modification through the value iterator to maintain consistency in the maps structure
	        throw new UnsupportedOperationException("Remove not supported via ValueIterator");
	    }
	}

	/**
	 * Private iterable wrapper that provides on-demand value iteration
	 * capability through ValueIterator instances.
	 */
	private class ValueIterable implements Iterable<V> {
	    /**
	     * Creates a fresh iterator instance for traversing all values
	     * @return new ValueIterator instance
	     */
	    public Iterator<V> iterator() {
	        // New iterator each time allows multiple independent iterations
	        return new ValueIterator();
	    }
	}

	/**
	 * Provides a view of all mapped values as an Iterable collection
	 * @return iterable view of the map's values
	 */
	public Iterable<V> valuesIterable() {
	    // Returns a new ValueIterable instance that will create fresh iterators
	    return new ValueIterable();
	}


	/*
	 * composition design pattern used to keep track of both an element and its key
	 */
	protected class MapEntry<K, V> {
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
		protected V setValue(V value) {
			V old = v;
			v = value;
			return old;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return String.format("{Key: %s Value: %s}", k, v);
		}
	}


}
