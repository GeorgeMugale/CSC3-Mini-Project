package acsse.csc3a.maps;

import java.util.ArrayList;
import java.util.Random;

public abstract class AbstractHashMap<K, V> extends AbstractMap<K,V>{
	protected int n = 0;
	protected int capacity;
	private int prime;
	private long scale,shift;
	
	public AbstractHashMap(int cap,int p) {
		prime = p;
		capacity = cap;
		Random rand = new Random();
		scale = rand.nextInt(prime - 1) + 1;
		shift = rand.nextInt(prime);
		createTable();
	}
	
	/*
	 * default prime
	 */
	public AbstractHashMap(int cap) {
		this(cap,10935121);
	}
	
	/*
	 * default capacity
	 */
	public AbstractHashMap() {
		this(17);
	}
	
	public int size() {
		return n;
	}
	
	public V get(K key) {
		return bucketGet(hashValue(key),key);
	}
	
	public V remove(K key) {
		return bucketRemove(hashValue(key),key);
	}
	
	@Override
	public V put(K key,V value) {
		V answer = bucketPut(hashValue(key),key,value);
		if(n > capacity/2) {
			resize(2 * capacity - 1);
		}
		return answer;
	}
	
	private int hashValue(K key) {
		return (int)((Math.abs(key.hashCode()*scale + shift) % prime) % capacity);
	}
	
	private void resize(int newCap) {
		ArrayList<Entry<K,V>> buffer = new ArrayList<>(n);
		for(Entry<K,V> e: entrySet()) {
			buffer.add(e);
			capacity = newCap;
			
		}
		createTable();
		n = 0;
		for(Entry<K,V> e: buffer) {
			put(e.getKey(),e.getValue());	
		}
	}
	
	protected abstract void createTable();
	protected abstract V bucketGet(int h,K key);
	protected abstract V bucketPut(int h,K key,V v);
	protected abstract V bucketRemove(int h,K key);
	
}
