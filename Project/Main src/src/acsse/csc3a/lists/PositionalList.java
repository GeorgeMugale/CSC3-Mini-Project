// Package declaration indicating that this interface belongs to the 'acsse.csc3a.lists' package.
package acsse.csc3a.lists;

import java.io.Serializable;

/**
 * A PositionalList is a linear data structure similar to a linked list
 * that allows access and manipulation via 'positions' (abstract locations),
 * rather than just numeric indices.
 * 
 * @param <E> the type of elements stored in the list
 */
public interface PositionalList<E> extends Iterable<E>, Serializable {

    /**
     * Returns the number of elements in the list.
     * @return the size of the list
     */
    int size();

    /**
     * Checks if the list is empty.
     * @return true if the list has no elements, false otherwise
     */
    boolean isEmpty();

    /**
     * Returns the first position in the list.
     * @return the Position of the first element, or null if the list is empty
     */
    Position<E> first();

    /**
     * Returns the last position in the list.
     * @return the Position of the last element, or null if the list is empty
     */
    Position<E> last();

    /**
     * Returns the position immediately before the given position.
     * @param p the position to find the predecessor of
     * @return the position before p
     * @throws IllegalArgumentException if the position is invalid
     */
    Position<E> before(Position<E> p) throws IllegalArgumentException;

    /**
     * Returns the position immediately after the given position.
     * @param p the position to find the successor of
     * @return the position after p
     * @throws IllegalArgumentException if the position is invalid
     */
    Position<E> after(Position<E> p) throws IllegalArgumentException;

    /**
     * Inserts an element at the front of the list.
     * @param element the element to insert
     * @return the position of the newly inserted element
     */
    Position<E> addFirst(E element);

    /**
     * Inserts an element at the end of the list.
     * @param element the element to insert
     * @return the position of the newly inserted element
     */
    Position<E> addLast(E element);

    /**
     * Inserts an element before the given position.
     * @param p the position before which to insert
     * @param element the element to insert
     * @return the position of the newly inserted element
     * @throws IllegalArgumentException if the position is invalid
     */
    Position<E> addBefore(Position<E> p, E element) throws IllegalArgumentException;

    /**
     * Inserts an element after the given position.
     * @param p the position after which to insert
     * @param element the element to insert
     * @return the position of the newly inserted element
     * @throws IllegalArgumentException if the position is invalid
     */
    Position<E> addAfter(Position<E> p, E element) throws IllegalArgumentException;

    /**
     * Replaces the element at the given position with a new element.
     * @param p the position of the element to replace
     * @param element the new element
     * @return the element that was replaced
     * @throws IllegalArgumentException if the position is invalid
     */
    E set(Position<E> p, E element) throws IllegalArgumentException;

    /**
     * Removes the element at the given position from the list.
     * @param p the position of the element to remove
     * @return the element that was removed
     * @throws IllegalArgumentException if the position is invalid
     */
    E remove(Position<E> p) throws IllegalArgumentException;
}
