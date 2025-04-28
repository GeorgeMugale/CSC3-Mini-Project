package acsse.csc3a.mst;

import acsse.csc3a.maps.Entry;

public interface PriorityQueue<K,V> {
	int size();
	boolean isEmpty();
	Entry<K,V> insert(K key,V value);
	Entry<K,V> min();
	Entry<K,V> removeMin();
}
