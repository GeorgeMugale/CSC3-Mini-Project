package acsse.csc3a.lists;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedPositionalList<E> implements PositionalList<E>, Serializable {

	/**
	 * A node class that holds an element
	 * 
	 * @param <E> the type which th node holds
	 */
	private static class Node<E> implements Position<E> {
		private E element;
		private Node<E> prev;
		private Node<E> next;

		public Node(E element, Node<E> p, Node<E> n) {
			this.element = element;
			prev = p;
			next = n;
		}

		/**
		 * Gets the reference to the element stored at this node
		 */
		@Override
		public E getElement() throws IllegalStateException {
			if (next == null) {
				throw new IllegalStateException("Position no longer available");
			}
			return element;
		}

		/**
		 * Gets a reference to the previous node in the list
		 * 
		 * @return
		 */
		public Node<E> getPrev() {
			return prev;
		}

		/**
		 * Gets a reference to the subsequent node in the list
		 * 
		 * @return
		 */
		public Node<E> getNext() {
			return next;
		}

		/**
		 * Sets the element contained in this node
		 * 
		 * @param element
		 */
		public void setElement(E element) {
			this.element = element;
		}

		/**
		 * Sets the previous node in the list
		 * 
		 * @param prev the predecessor node
		 */
		public void setPrev(Node<E> prev) {
			this.prev = prev;
		}

		/**
		 * Sets the subsequent node in the list
		 * 
		 * @param next the successive node
		 */
		public void setNext(Node<E> next) {
			this.next = next;
		}
	}

	private Node<E> header;
	private Node<E> trailer;
	private int size = 0;

	/**
	 * Constructs an empty linked positional list
	 */
	public LinkedPositionalList() {
		header = new Node<>(null, null, null);
		trailer = new Node<>(null, header, null);
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
		return addBetween(element, header, header.getNext());
	}

	@Override
	public Position<E> addLast(E element) {
		return addBetween(element, trailer.getPrev(), trailer);
	}

	@Override
	public Position<E> addBefore(Position<E> p, E element) throws IllegalArgumentException {
		Node<E> node = validate(p);
		return addBetween(element, node.getPrev(), node);
	}

	@Override
	public Position<E> addAfter(Position<E> p, E element) throws IllegalArgumentException {
		Node<E> node = validate(p);
		return addBetween(element, node, node.getNext());
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

	/**
	 * Validates the queried position
	 * 
	 * @param p the position being queried
	 * @return true if the position is valid, false otherwise
	 * @throws IllegalArgumentException if the queried position is not an instance
	 *                                  of a node
	 */
	private Node<E> validate(Position<E> p) throws IllegalArgumentException {
		if (!(p instanceof Node)) {
			throw new IllegalArgumentException("Invalid position");
		}
		Node<E> node = (Node<E>) p;
		if (node.getNext() == null) {
			throw new IllegalArgumentException("Position is no longer in the list");
		}
		return node;
	}

	/**
	 * Return the position of a h=given node
	 * @param node the node being queried
	 * @return the position associated with the queried node
	 */
	private Position<E> position(Node<E> node) {
		if (node == header || node == trailer) {
			return null;
		}
		return node;
	}

	/**
	 * Adds an element between two nodes
	 * @param element the element being added
	 * @param pred the predecessor 
	 * @param succ the successor
	 * @return the position of the element that was added
	 */
	private Position<E> addBetween(E element, Node<E> pred, Node<E> succ) {
		Node<E> newest = new Node<>(element, pred, succ);
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
				if (cursor == null)
					throw new NoSuchElementException();
				E value = cursor.getElement();
				cursor = after(cursor);
				return value;
			}
		};
	}

}
