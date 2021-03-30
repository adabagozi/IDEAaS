package utils.lists;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.directory.api.util.exception.NotImplementedException;

public class ReadOnlyArrayList<T> implements List<T> {

		private final T[] array;
		
		public ReadOnlyArrayList() {
			this.array = (T[]) new ArrayList<>().toArray();
		}
		
		public ReadOnlyArrayList(T[] array) {
			this.array = array;
		}
		
		@Override
		public int size() {
			return this.array.length;
		}

		@Override
		public boolean isEmpty() {
			return this.array.length == 0;
		}

		@Override
		public boolean contains(Object o) {
			throw new NotImplementedException();
		}

		@Override
		public Iterator<T> iterator() {
			return Arrays.stream(this.array).iterator();
		}

		@Override
		public Object[] toArray() {
			return this.array;
		}

		@Override
		public <T> T[] toArray(T[] a) {
			throw new NotImplementedException();
		}

		@Override
		public boolean add(T e) {
			throw new NotImplementedException();
		}

		@Override
		public boolean remove(Object o) {
			throw new NotImplementedException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			throw new NotImplementedException();
		}

		@Override
		public boolean addAll(Collection<? extends T> c) {
			throw new NotImplementedException();
		}

		@Override
		public boolean addAll(int index, Collection<? extends T> c) {
			throw new NotImplementedException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new NotImplementedException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new NotImplementedException();
		}

		@Override
		public void clear() {
			throw new NotImplementedException();
		}

		@Override
		public T get(int index) {
			return this.array[index];
		}

		@Override
		public T set(int index, T element) {
			throw new NotImplementedException();
		}

		@Override
		public void add(int index, T element) {
			throw new NotImplementedException();
		}

		@Override
		public T remove(int index) {
			throw new NotImplementedException();
		}

		@Override
		public int indexOf(Object o) {
			throw new NotImplementedException();
		}

		@Override
		public int lastIndexOf(Object o) {
			throw new NotImplementedException();
		}

		@Override
		public ListIterator<T> listIterator() {
			throw new NotImplementedException();
		}

		@Override
		public ListIterator<T> listIterator(int index) {
			throw new NotImplementedException();
		}

		@Override
		public List<T> subList(int fromIndex, int toIndex) {
			throw new NotImplementedException();
		}
		
	}
	