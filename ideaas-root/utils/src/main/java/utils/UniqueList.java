package utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class UniqueList<O> implements List<O>, Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<O> internal;
	
	public UniqueList() {
		this.internal = new ArrayList<>();
	}
	
	public UniqueList(Collection<O> c) {
		this();
		this.internal.addAll(c);
	}
	
	@Override
	public String toString() {
		return Arrays.toString(this.toArray());
	}
	
	@Override
	public int size() {
		return this.internal.size();
	}

	@Override
	public boolean isEmpty() {
		return this.internal.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return this.internal.contains(o);
	}

	@Override
	public Iterator<O> iterator() {
		return this.internal.iterator();
	}

	@Override
	public Object[] toArray() {
		return this.internal.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.internal.toArray(a);
	}

	@Override
	public boolean add(O e) {
		if (this.internal.contains(e)) {
			return false;
		}
		return this.internal.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return this.internal.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.internal.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends O> c) {
		boolean retVal = false;
		for (O el : c) {
			if (this.add(el)) {
				retVal = true;
			}
		}
		return retVal;
	}

	@Override
	public boolean addAll(int index, Collection<? extends O> c) {
		boolean retVal = false;
		for (O el : c) {
			if (this.contains(el)) {
				continue;
			}
			retVal = true;
			this.add(index, el);
		}
		return retVal;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.internal.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.internal.retainAll(c);
	}

	@Override
	public void clear() {
		this.internal.clear();
	}

	@Override
	public O get(int index) {
		return this.internal.get(index);
	}

	@Override
	public O set(int index, O element) {
		return this.internal.set(index, element);
	}

	@Override
	public void add(int index, O element) {
		if (this.contains(element)) {
			return;
		}
		this.internal.add(index, element);
	}

	@Override
	public O remove(int index) {
		return this.internal.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return this.internal.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return this.internal.lastIndexOf(o);
	}

	@Override
	public ListIterator<O> listIterator() {
		return this.internal.listIterator();
	}

	@Override
	public ListIterator<O> listIterator(int index) {
		return this.internal.listIterator(index);
	}

	@Override
	public List<O> subList(int fromIndex, int toIndex) {
		return this.internal.subList(fromIndex, toIndex);
	}

}
