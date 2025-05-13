package acsse.csc3a.lists;

import java.io.Serializable;

public interface PositionalList<E> extends Iterable<E>, Serializable{
	int size();
	boolean isEmpty();
	Position<E> first(); 
	Position<E> last();
	Position<E> before(Position<E> p) throws IllegalArgumentException;
	Position<E> after(Position<E> p) throws IllegalArgumentException; 
	Position<E> addFirst(E element);
	Position<E> addLast(E element);
	Position<E> addBefore(Position<E> p,E element) throws IllegalArgumentException;
	Position<E> addAfter(Position<E> p ,E element) throws IllegalArgumentException;
	E set(Position<E> p,E element) throws IllegalArgumentException;
	E remove(Position<E> p) throws IllegalArgumentException;
	
}
