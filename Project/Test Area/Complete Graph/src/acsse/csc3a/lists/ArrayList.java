package acsse.csc3a.lists;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ArrayList<T> implements List<T>, Iterable<T> {

	private T[] data;
	private Integer size;
	private Integer arrayLength;
	private char strategy;

	/*
	 * The default constructor
	 */
	public ArrayList() {
		this('A', 5);
	}

	/**
	 * @param n
	 */
	public ArrayList(int arrayLength) {
		// TODO Auto-generated constructor stub
		this('A', arrayLength);
	}

	/**
	 * @param n
	 */
	public ArrayList(char strategy) {
		// TODO Auto-generated constructor stub
		this(strategy, 5);
	}

	/*
	 * The overloaded constructor for creating an ArrayList
	 */
	public ArrayList(char strategy, int arrayLength) {

		this.strategy = (strategy != 'A' && strategy != 'B') ? strategy : 'A';
		this.arrayLength = arrayLength;
		this.data = createArray(this.arrayLength);
		this.size = 0;
	}

	/*
	 * A helper method for creating the underlying array
	 */
	@SuppressWarnings("unchecked")
	private T[] createArray(int size) {
		// safe casting to create a generic object
		return (T[]) new Object[size];

	}

	/*
	 * The method for retrieving the element from the ArrayList
	 * 
	 * @param the index to retrieve from
	 */
	@Override
	public T get(int index) {
		try {
			checkIndex(index, size);
		} catch (ArrayListException e) {
			e.printStackTrace();
		}
		return data[index];
	}

	/*
	 * The method for replacing an element in the ArrayList
	 * 
	 * @param The index and he element
	 */
	@Override
	public T set(int i, T e) {
		try {
			checkIndex(i, size);
		} catch (ArrayListException e1) {
			e1.printStackTrace();
		}
		T temp = data[i];
		data[i] = e;
		
		return temp;
	}

	/*
	 * The method for adding an element to the ArrayList
	 * 
	 * @param the index for where the new element needs to be added and the element
	 */
	@Override
	public void add(int i, T e) {
		try {
			checkIndex(i, size + 1);
		} catch (ArrayListException e1) {
			e1.printStackTrace();
		}
		if (size == arrayLength) {
			expandArray();
		}
		shiftElementsRight(i);
		data[i] = e;
		size++;
	}

	@Override
	public boolean add(T e) {
		if (size == arrayLength) {
			expandArray();
		}
		data[size++] = e;
		return true;
	}

	/*
	 * The method for removing an element from the arrayList
	 * 
	 * @param the index of the element for removal
	 * 
	 */
	@Override
	public T remove(int i) {
		try {
			checkIndex(i, size);
		} catch (ArrayListException e) {
			e.printStackTrace();
		}
		System.arraycopy(data, i + 1, data, i, size - i - 1);
		shiftElementsLeft(i);
		T toShow = data[i];
		data[i] = null;
		size--;
		return toShow;
	}
	
	@Override
    public boolean remove(Object o) {
        for (int i = 0; i < size; i++) {
            if (o == null ? data[i] == null : o.equals(data[i])) {
                remove(i);
                return true;
            }
        }
        return false;
    }

	/*
	 * The auxiliary method to determine the size of the ArrayList
	 */
	@Override
	public int size() {
		return size;
	}

	/*
	 * The auxiliary method to check if the list is empty
	 */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/*
	 * The overridden toString method
	 */
	@Override
	public String toString() {
		String str = "[";
		for (int i = 0; i < size - 1; i++) {
			str += data[i].toString() + ",";
		}
		if (size > 0) {
			str += data[size - 1];
		}
		str += "]";
		return str;
	}

	/*
	 * The expand array function that creates a new array that depends on the
	 * strategy (A for incremental and B for doubling) and copies the elements to
	 * the new array
	 * 
	 */
	private void expandArray() {

		int newSize = 0;
		switch (strategy) {
		case 'A':
			newSize = this.arrayLength + 10; // increase the size of the array constantly by 10
		case 'B':
			newSize = this.arrayLength * 2; // double the size of the array
		}
		T[] newData = createArray(newSize);
		System.arraycopy(data, 0, newData, 0, this.size);
	}
	
	@Override
    public List<T> subList(int fromIndex, int toIndex) {
        try {
            checkIndex(fromIndex, size);
            checkIndex(toIndex, size + 1);
        } catch (ArrayListException e) {
            e.printStackTrace();
        }
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex > toIndex");
        }
        
        ArrayList<T> sublist = new ArrayList<>(toIndex - fromIndex);
        System.arraycopy(data, fromIndex, sublist.data, 0, toIndex - fromIndex);
        sublist.size = toIndex - fromIndex;
        return sublist;
    }

	/*
	 * A method for shifting all the elements up by one to the right
	 * 
	 * @param the index from where to shift
	 */
	private void shiftElementsRight(Integer pos) {
		for (int i = this.size; i > pos; i--) {
			this.data[i + 1] = this.data[i];
		}
	}
	
	@Override
    public int lastIndexOf(Object o) {
        for (int i = size - 1; i >= 0; i--) {
            if (o == null ? data[i] == null : o.equals(data[i])) {
                return i;
            }
        }
        return -1;
    }

	/*
	 * A method for shifting all the elements up by one to the left
	 * 
	 * @param the index from where to shift
	 */
	private void shiftElementsLeft(Integer pos) {
		for (int i = pos; i < size; i++) {
			this.data[i] = this.data[i + 1];
		}
	}

	/*
	 * The overridden iterator method
	 *
	 */
	@Override
	public Iterator<T> iterator() {
		return new ArrayListIterator();
	}

	protected void checkIndex(int i, int n) throws ArrayListException {
		if (i >= n) {
			throw new ArrayListException("Index greater than size");
		}
		if (i < 0) {
			throw new ArrayListException("Index out of range");
		}
	}

	private class ArrayListIterator implements Iterator<T> {
		private int current = 0; // current index

		@Override
		public boolean hasNext() {
			return (current < size);
		}

		@Override
		public T next() {
			return data[current++];
		}

	}

	@Override
	public boolean contains(Object o) {
		for (int i = 0; i < size; i++) {
			if (o == null ? data[i] == null : o.equals(data[i])) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object[] toArray() {
		return Arrays.copyOf(data, size);
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<T> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

}
