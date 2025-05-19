package acsse.csc3a.lists;

import java.io.Serializable;
import java.util.*;

public class ArrayList<T> implements List<T>, Iterable<T>, Serializable {

    private T[] data;
    private int size;
    private int arrayLength;
    private char strategy;

    public ArrayList() {
        this('A', 5);
    }

    public ArrayList(int arrayLength) {
        this('A', arrayLength);
    }

    public ArrayList(char strategy) {
        this(strategy, 5);
    }

    /**
     * Constructs an arraylist which uses a specified strategy an with an initial size
     * @param strategy the strategy the arraylist should use to grow when full
     * @param arrayLength the length of the initial array
     */
    @SuppressWarnings("unchecked")
    public ArrayList(char strategy, int arrayLength) {
        this.strategy = (strategy == 'A' || strategy == 'B') ? strategy : 'A';
        this.arrayLength = arrayLength;
        this.data = (T[]) new Object[arrayLength];
        this.size = 0;
    }

    /**
     * expands the array according to the provided strategy
     */
    private void expandArray() {
        int newSize;
        switch (strategy) {
            case 'A':
                newSize = this.arrayLength + 10;
                break;
            case 'B':
                newSize = this.arrayLength * 2;
                break;
            default:
                newSize = this.arrayLength + 10;
        }
        data = Arrays.copyOf(data, newSize);
        arrayLength = newSize;
    }

    /**
     * Checks if the index is within the array's bounds
     * @param i the index being queried
     * @param n the size being checked against
     */
    private void checkIndex(int i, int n) {
        if (i < 0 || i >= n) throw new IndexOutOfBoundsException("Invalid index " + i);
    }

    @Override
    public T get(int index) {
        checkIndex(index, size);
        return data[index];
    }

    @Override
    public T set(int index, T element) {
        checkIndex(index, size);
        T old = data[index];
        data[index] = element;
        return old;
    }

    @Override
    public void add(int index, T element) {
        checkIndex(index, size + 1);
        if (size >= arrayLength) expandArray();
        System.arraycopy(data, index, data, index + 1, size - index);
        data[index] = element;
        size++;
    }

    @Override
    public boolean add(T element) {
        if (size >= arrayLength) expandArray();
        data[size++] = element;
        return true;
    }

    @Override
    public T remove(int index) {
        checkIndex(index, size);
        T removed = data[index];
        int moveCount = size - index - 1;
        if (moveCount > 0) {
            System.arraycopy(data, index + 1, data, index, moveCount);
        }
        data[--size] = null;
        return removed;
    }

    @Override
    public boolean remove(Object o) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(data[i], o)) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public String toString() {
        return Arrays.toString(Arrays.copyOf(data, size));
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(data, size);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> U[] toArray(U[] a) {
        if (a.length < size)
            return (U[]) Arrays.copyOf(data, size, a.getClass());
        System.arraycopy(data, 0, a, 0, size);
        if (a.length > size) a[size] = null;
        return a;
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(o, data[i])) return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = size - 1; i >= 0; i--) {
            if (Objects.equals(o, data[i])) return i;
        }
        return -1;
    }

    @Override
    public void clear() {
        Arrays.fill(data, 0, size, null);
        size = 0;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        for (T item : c) add(item);
        return !c.isEmpty();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        checkIndex(index, size + 1);
        for (T item : c) {
            add(index++, item);
        }
        return !c.isEmpty();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object o : c) {
            while (remove(o)) changed = true;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        for (int i = 0; i < size; ) {
            if (!c.contains(data[i])) {
                remove(i);
                changed = true;
            } else {
                i++;
            }
        }
        return changed;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int cursor = 0;
            @Override public boolean hasNext() { return cursor < size; }
            @Override public T next() { return data[cursor++]; }
        };
    }

    @Override
    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        checkIndex(index, size + 1);
        return new ListIterator<T>() {
            int cursor = index;

            @Override public boolean hasNext() { return cursor < size; }
            @Override public T next() { return data[cursor++]; }
            @Override public boolean hasPrevious() { return cursor > 0; }
            @Override public T previous() { return data[--cursor]; }
            @Override public int nextIndex() { return cursor; }
            @Override public int previousIndex() { return cursor - 1; }
            @Override public void remove() { throw new UnsupportedOperationException(); }
            @Override public void set(T t) { data[cursor - 1] = t; }
            @Override public void add(T t) { throw new UnsupportedOperationException(); }
        };
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        if (fromIndex > toIndex || fromIndex < 0 || toIndex > size)
            throw new IndexOutOfBoundsException();
        ArrayList<T> sub = new ArrayList<>(toIndex - fromIndex);
        for (int i = fromIndex; i < toIndex; i++) {
            sub.add(data[i]);
        }
        return sub;
    }
}
