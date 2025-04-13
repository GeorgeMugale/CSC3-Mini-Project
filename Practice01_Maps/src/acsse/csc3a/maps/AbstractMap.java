package acsse.csc3a.maps;

import java.util.Iterator;

public abstract class AbstractMap<K, V> implements Map<K,V>{
	//have a nested class
	protected static class MapEntry<K,V> implements Entry<K, V>{
		private K key;
		private V value;
		
		public MapEntry(K key,V value) {
			this.key = key;
			this.value = value;
		}
		
		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}
		
		protected void setKey(K Key) {
			key = Key;
		}
		protected V setValue(V value) {
			V old = this.value;
			this.value = value;
			return old;
		}
		
	}
	
	private K key;
	private V value;
	
	MapEntry<K, V> entry = new MapEntry(key, value);
	
	//key iterator
	private class KeyIterator implements Iterator<K>{
		private Iterator<Entry<K,V>> entries = entrySet().iterator();
		@Override
		public boolean hasNext() {
			return entries.hasNext();
		}

		@Override
		public K next() {
			return entries.next().getKey();
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	private class KeyIterable implements Iterable<K>{

		@Override
		public Iterator<K> iterator() {
			return new KeyIterator();
		}
		
	}
	
	public Iterable<K> keySet(){
		return new KeyIterable();
	}
	
	private class ValueIterator implements Iterator<V>{
		private Iterator<Entry<K,V>> entries = entrySet().iterator();
		@Override
		public boolean hasNext() {
			return entries.hasNext();
		}

		@Override
		public V next() {
			return entries.next().getValue();
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	private class ValueIterable implements Iterable<V>{

		@Override
		public Iterator<V> iterator() {
			return new ValueIterator();
		}
		
	}
	
	@Override
	public Iterable<V> values() {
		return new ValueIterable();
	}

	/*
	 * entry iterator
	 */
	
	@Override
	public abstract int size();

	@Override
	public abstract boolean isEmpty();

	@Override
	public abstract V get(K key);

	@Override
	public  abstract V put(K key, V value);

	@Override
	public abstract V remove(K key);
	
	@Override
	public abstract Iterable<Entry<K, V>> entrySet();

}
