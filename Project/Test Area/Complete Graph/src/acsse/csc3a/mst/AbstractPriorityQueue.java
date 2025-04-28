package acsse.csc3a.mst;

import java.util.Comparator;

import acsse.csc3a.maps.Entry;

public abstract class AbstractPriorityQueue<K,V> implements PriorityQueue<K,V> {

	//have a nested priority queue entry class
	protected static class PQEntry<K,V> implements Entry<K,V>{
		K key;
		V value;
		
		public PQEntry(K key,V value) {
			 this.key = key;
			 this.value = value;
		}
		
		//setters and getters
		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}
		
		public void setValue(V value) {
			this.value = value;
		}
		
		public void setKey(K key) {
			this.key = key;
		}
		
	}
	
	//defines the order of the keys in the priority queue
	private Comparator<K> comp;
	
	//create an empty priority queue using the given comparator to order key
	protected AbstractPriorityQueue(Comparator<K> c) {
		comp = c;
	}
	
	protected AbstractPriorityQueue() {
		this.comp = null;
	}
	
	//compares two entries using their keys
	@SuppressWarnings("unchecked")
	protected int compare(Entry<K, V> a, Entry<K, V> b) {
		//check if comp is null first
	    if (comp != null) {
	        return comp.compare(a.getKey(), b.getKey());
	    } else {
	        return ((Comparable<K>) a.getKey()).compareTo(b.getKey());
	    }
	}

	
	//checks whether the key is valid
	@SuppressWarnings("unchecked")
	protected boolean checkKey(K key) throws IllegalArgumentException {
	    try {
	        if (comp != null) {
	            return (comp.compare(key, key) == 0);
	        } else {
	            return (((Comparable<K>) key).compareTo(key) == 0);
	        }
	    } catch (ClassCastException e) {
	        throw new IllegalArgumentException("Incompatible key");
	    }
	}
	
	
	@Override
	public abstract int size();

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public abstract Entry<K, V> insert(K key, V value);

	@Override
	public abstract Entry<K, V> min();

	@Override
	public abstract Entry<K, V> removeMin();
	

}
