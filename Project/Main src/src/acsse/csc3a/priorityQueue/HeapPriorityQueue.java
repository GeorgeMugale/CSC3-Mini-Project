package acsse.csc3a.priorityQueue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map.Entry;

public class HeapPriorityQueue<K, V> extends AbstractPriorityQueue<K, V>{
	protected ArrayList<Entry<K,V>> heap = new ArrayList<>();
	
	//creates an empty priority queue usig the natural ordering of the keys
	public HeapPriorityQueue() {
		super();
	}
	
	public HeapPriorityQueue(Comparator<K> comp) {
		super(comp);
	}
	
	protected int parent(int j) {
		return (j-1)/2;
	}
	
	protected int left(int j) {
		return 2 * j + 1;
	}
	
	protected int right(int j) {
		return 2 * j + 2;
	}
	
	protected boolean hasLeft(int j) {
		return left(j) < heap.size();
	}
	
	protected boolean hasRight(int j) {
		return right(j) < heap.size();
	}
	
	//exchanges entries at i amd j of the array list
	protected void swap(int i,int j) {
		Entry<K,V> temp = heap.get(i);
		heap.set(i, heap.get(j));
		heap.set(j, temp);
	}
	
	//perfmes an uphead to restore the heap property
	protected void uphead(int j) {
		while(j > 0) {
			//loop until u find the root
			int p = parent(j);
			//checks if the key of the parent is lesser than that of j(child)
			if(compare(heap.get(j),heap.get(p)) < 0){
				swap(j,p);
				j = p;
			}else {
				break;
			}
		}
	}
	
	//moves index j lower(downheap) to restore heap store
	protected void downheap(int j) {
	    while (hasLeft(j)) {
	        int leftIndex = left(j);
	        int smallChildIndex = leftIndex;

	        if (hasRight(j)) {
	            int rightIndex = right(j);
	            if (compare(heap.get(leftIndex), heap.get(rightIndex)) > 0) {
	                smallChildIndex = rightIndex;
	            }
	        }

	        if (compare(heap.get(smallChildIndex), heap.get(j)) >= 0) {
	            break;
	        }

	        swap(j, smallChildIndex);
	        j = smallChildIndex;
	    }
	}

	@Override
	public int size() {
		return heap.size();
	}

	@Override
	public Entry<K, V> insert(K key, V value) {
		checkKey(key);
		Entry<K,V> newEntry = new PQEntry<>(key,value);
		heap.add(newEntry);
		uphead(heap.size() - 1);
		return newEntry;
	}

	@Override
	public Entry<K, V> min() {
		if(heap.isEmpty()) {
			return null;
		}
		return heap.get(0);
	}

	@Override
	public Entry<K, V> removeMin() {
		if(heap.isEmpty()) {
			return null;
		}
		Entry<K,V> removeEntry = heap.get(0);
		swap(0,heap.size() - 1);
		heap.remove(heap.size() - 1);
		downheap(0);
		return removeEntry;
	}
}
