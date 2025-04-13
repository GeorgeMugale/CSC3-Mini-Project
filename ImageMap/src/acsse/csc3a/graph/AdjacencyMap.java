package acsse.csc3a.graph;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

/**
 * Specialized AdjacencyMap with Robin Hood hashing for water contamination analysis
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
     * @param h Precomputed hash value
     * @param key Vertex key to lookup
     * @return Edge value if found, null otherwise
     */
    @Override
	protected V bucketGet(int h, K key) {
        int hash = h % capacity;
        int probe = 0;
        
        while (table[hash] != null && probe <= MAX_PROBE_LIMIT) {
            if (table[hash] != DEFUNCT_SENTINEL && 
                table[hash].getKey().equals(key)) {
                return table[hash].getValue();
            }
            hash = (hash + 1) % capacity;
            probe++;
        }
        return null;
    }

    /**
     * Inserts a key-value pair using Robin Hood hashing and returns the previous value
     * 
     * @param h     Precomputed hash index (from hashFunction(key))
     * @param key   Key to insert
     * @param value Value to associate with key
     * @return Previous value associated with key, or null if no previous mapping
     */
    @Override
    protected V bucketPut(int h, K key, V value) {
        int hash = h % capacity;
        int probe = 0;
        MapEntry<K, V> newEntry = new MapEntry<>(key, value, 0);
        V oldValue = null;

        while (probe <= MAX_PROBE_LIMIT) {
            if (table[hash] == null || table[hash] == DEFUNCT_SENTINEL) {
                newEntry.dib = probe;
                table[hash] = newEntry;
                n++;
                return oldValue;  // Return null if new insertion
            }

            // Check for existing key match
            if (table[hash].getKey().equals(key)) {
                oldValue = table[hash].getValue();  // Capture old value
                table[hash].setValue(value);          // Update value
                return oldValue;                    // Return previous value
            }

            // Robin Hood swap if needed
            if (probe > table[hash].dib) {
                MapEntry<K, V> temp = table[hash];
                table[hash] = newEntry;
                newEntry = temp;
                probe = temp.dib;
            }

            hash = (hash + 1) % capacity;
            probe++;
        }
        throw new IllegalStateException("Probe limit exceeded");
    }

    /**
     * Remove operation with backward shifting
     * @param h Precomputed hash value
     * @param key Vertex key to remove
     * @return Removed edge value
     */
    @Override
    protected V bucketRemove(int h, K key) {
        int currentPos = findKeyIndex(h, key);
        if (currentPos == -1) return null;

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

    private int findKeyIndex(int h, K key) {
        int hash = h;
        int probe = 0;
        
        while (table[hash] != null && probe <= MAX_PROBE_LIMIT) {
            if (table[hash] != DEFUNCT_SENTINEL && 
                table[hash].getKey().equals(key)) {
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
            if (entry != null && entry != DEFUNCT_SENTINEL && 
                Objects.equals(value, entry.getValue())) {
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
	public Set<K> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void putAll(AbstractMap<? extends K, ? extends V> m) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		
		AdjacencyMap<Integer, String> map = new AdjacencyMap<>(5, 10);
		
		map.put(0, "First");
		map.put(1, "Second");
		map.put(2, "third");
		map.put(3, "fourth");
		map.put(4, "fith");
		
		
		
		Iterable<Integer> iter = map.keyIterable();
		
		
		for (Integer key : iter) {
			System.out.println(map.get(key));
		}
		
		System.out.println("size: " + map.size());
		
	}

    // Other required methods...
}