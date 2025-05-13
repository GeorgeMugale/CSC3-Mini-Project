package acsse.csc3a.priorityQueue;

import java.util.Map.Entry;

public interface PriorityQueue<K,V> {
	int size();
	boolean isEmpty();
	Entry<K,V> insert(K key,V value);
	Entry<K,V> min();
	Entry<K,V> removeMin();
}
