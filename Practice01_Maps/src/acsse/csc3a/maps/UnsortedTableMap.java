package acsse.csc3a.maps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class UnsortedTableMap<K, V> extends AbstractMap<K,V>{
	//storage for map entries
	private ArrayList<MapEntry<K,V>> table = new ArrayList<>();
	
	//constructor
	public UnsortedTableMap() {}
	
	//returns a index of an entry 
	private int findIndex(K key) {
		int n = table.size();
		for(int j = 0;j < n;j++) {
			if(table.get(j).getKey().equals(key)) {
				return j;
			}
		}
		return -1;
	}
	
	public int size() {
		return table.size();
	}
	
	public V get(K key) {
		int j = findIndex(key);
		if(j == -1) {
			return null;
		}
		return table.get(j).getValue();
	}
	
	//inserting a value or replacing old value with a new one
	public V put(K key,V value) {
		int j = findIndex(key);
		if(j == -1) {
			table.add(new MapEntry<>(key,value));
			return null;
		}else {
			return table.get(j).setValue(value);
		}
	}
	
	public V remove(K key) {
		int j = findIndex(key);
		int n = size();
		if(j == -1) {
			return null;
		}
		V answer = table.get(j).getValue();
		if(j != n - 1) {
			table.set(j, table.get(n - 1));
		}
		table.remove(n - 1);
		return answer;
	}
	
	private class EntryIterator implements Iterator<Entry<K,V>>{
		private int j = 0;
		public boolean hasNext() {
			return j < table.size();
		}
		@Override
		public Entry<K, V> next() {
			if(j == table.size()) {
				throw new NoSuchElementException();
			}
			return table.get(j++);
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	private class EntryIterable implements Iterable<Entry<K,V>>{

		@Override
		public Iterator<Entry<K, V>> iterator() {
			return new EntryIterator();
		}
		
	}
	public Iterable<Entry<K,V>> entrySet(){
		return new EntryIterable();
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
