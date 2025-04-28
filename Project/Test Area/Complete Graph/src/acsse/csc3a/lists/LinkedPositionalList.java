package acsse.csc3a.lists;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedPositionalList<E> implements PositionalList<E>{

	//node class
	private static class Node<E> implements Position<E>{
		private E element;
		private Node<E> prev;
		private Node<E> next;
		
		public Node(E element,Node<E> p,Node<E> n) {
			this.element = element;
			prev = p;
			next = n;
		}
		@Override
		public E getElement() throws IllegalStateException {
			if(next == null) {
				throw new IllegalStateException("Position no longer available");
			}
			return element;
		}
		
		public Node<E> getPrev(){
			return prev;
		}
		
		public Node<E> getNext(){
			return next;
		}
		
		public void setElement(E element) {
			this.element = element;
		}
		
		public void setPrev(Node<E> prev) {
			this.prev = prev;
		}
		
		public void setNext(Node<E> next) {
			this.next = next;
		}
	}
	
	//implementation of the LinkedPositionalList class
	private Node<E> header;
	private Node<E> trailer;
	private int size = 0;
	
	public LinkedPositionalList() {
		header = new Node<>(null,null,null);
		trailer = new Node<>(null,header,null);
		header.setNext(trailer);
	}
	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public Position<E> first() {
		return position(header.getNext());
	}

	@Override
	public Position<E> last() {
		return position(trailer.getPrev());
	}

	@Override
	public Position<E> before(Position<E> p) throws IllegalArgumentException {
		Node<E> node = validate(p);
		return position(node.getPrev());
	}

	@Override
	public Position<E> after(Position<E> p) throws IllegalArgumentException {
		Node<E> node = validate(p);
		return position(node.getNext());
	}

	@Override
	public Position<E> addFirst(E element) {
		return addBetween(element,header,header.getNext());
	}

	@Override
	public Position<E> addLast(E element) {
		return addBetween(element,trailer.getPrev(),trailer);
	}

	@Override
	public Position<E> addBefore(Position<E> p, E element) throws IllegalArgumentException {
		Node<E> node = validate(p);
		return addBetween(element,node.getPrev(),node);
	}

	@Override
	public Position<E> addAfter(Position<E> p, E element) throws IllegalArgumentException {
		Node<E> node = validate(p);
		return addBetween(element,node,node.getNext());
	}

	@Override
	public E set(Position<E> p, E element) throws IllegalArgumentException {
		Node<E> node = validate(p);
		E temp = node.getElement();
		node.setElement(element);
		return temp;
	}

	@Override
	public E remove(Position<E> p) throws IllegalArgumentException {
		Node<E> node = validate(p);
		Node<E> prev = node.getPrev();
		Node<E> next = node.getNext();
		prev.setNext(next);
		next.setPrev(prev);
		size--;
		E temp = node.getElement();
		return temp;
	}
	
	private Node<E> validate(Position<E> p)throws IllegalArgumentException{
		if(!(p instanceof Node)) {
			throw new IllegalArgumentException("Invalid position");
		}
		Node<E>node=(Node<E>)p;
		if(node.getNext() == null) {
			throw new IllegalArgumentException("Position is no longer in the list");
		}
		return node;
	}
	
	private Position<E> position(Node<E> node){
		if(node == header || node == trailer) {
			return null;
		}
		return node;
	}
	
	private Position<E> addBetween(E element,Node<E> pred,Node<E> succ){
		Node<E> newest = new Node<>(element,pred,succ);
		pred.setNext(newest);
		succ.setPrev(newest);
		size++;
		return newest;
	}
	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
	        private Position<E> cursor = first(); 

	        @Override
	        public boolean hasNext() {
	            return cursor != null;
	        }

	        @Override
	        public E next() {
	            if (cursor == null) throw new NoSuchElementException();
	            E value = cursor.getElement();
	            cursor = after(cursor);
	            return value;
	        }
	    };
	}

}
