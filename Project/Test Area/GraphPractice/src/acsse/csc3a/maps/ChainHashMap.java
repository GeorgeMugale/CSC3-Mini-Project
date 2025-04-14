package acsse.csc3a.maps;

import java.util.ArrayList;

public class ChainHashMap<K, V> extends AbstractHashMap<K,V> {
	//array of UnsortedTableMap serves as a bucket
	private UnsortedTableMap<K,V>[] table;
	
	public ChainHashMap() {
		super();
	}
	
	public ChainHashMap(int cap) {
		super(cap);
	}
	
	public ChainHashMap(int cap,int p) {
		super(cap,p);
	}
	
	//creates an empty table
	@Override
	protected void createTable() {
		table = (UnsortedTableMap<K,V>[]) new UnsortedTableMap[capacity];
	}

	@Override
	protected V bucketGet(int h, K key) {
		UnsortedTableMap<K, V> bucket = table[h];
		if(bucket == null) {
			return null;
		}
		return bucket.get(key);
	}

	@Override
	protected V bucketPut(int h, K key, V v) {
		UnsortedTableMap<K, V> bucket = table[h];
		if(bucket == null) {
			bucket = table[h] = new UnsortedTableMap<>();
			
		}
		int oldSize = bucket.size();
		V answer = bucket.put(key, v);
		n += (bucket.size() - oldSize);
		
		return answer;
	}

	@Override
	protected V bucketRemove(int h, K key) {
		UnsortedTableMap<K,V> bucket = table[h];
		if(bucket == null) {
			return null;
		}
		int oldSize = bucket.size();
		V answer = bucket.remove(key);
		n -= (oldSize - bucket.size());
		
		return answer;
	}
	
	public Iterable<Entry<K,V>> entrySet(){
		ArrayList<Entry<K,V>> buffer = new ArrayList<>();
		for(int h = 0; h < capacity;h++) {
			if(table[h] != null) {
				for(Entry<K,V> entry: table[h].entrySet()) {
					buffer.add(entry);
				}
			}
		}
		return buffer;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

}
