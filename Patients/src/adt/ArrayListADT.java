package adt;

import java.util.*;

public class ArrayListADT<E> implements List<E> {
    private Object[] elements;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    // Constructor
    public ArrayListADT() {
        elements = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    // Ensure array has enough capacity
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = elements.length * 2;
            if (newCapacity < minCapacity) newCapacity = minCapacity;
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }

    // Basic operations
    @Override
    public int size() { return size; }

    @Override
    public boolean isEmpty() { return size == 0; }

    @Override
    public boolean contains(Object o) { return indexOf(o) >= 0; }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            int cursor = 0;
            @Override
            public boolean hasNext() { return cursor < size; }
            @SuppressWarnings("unchecked")
            @Override
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                return (E) elements[cursor++];
            }
        };
    }

    // Zero-arg list iterator delegates to indexed version
    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int startIndex) {
        checkIndexForAdd(startIndex); // allow startIndex == size
        return new ListIterator<E>() {
            private int index = startIndex;
            private int lastReturned = -1;

            @Override
            public boolean hasNext() { return index < size; }
            @Override
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                lastReturned = index;
                return get(index++);
            }
            @Override
            public boolean hasPrevious() { return index > 0; }
            @Override
            public E previous() {
                if (!hasPrevious()) throw new NoSuchElementException();
                lastReturned = --index;
                return get(index);
            }
            @Override
            public int nextIndex() { return index; }
            @Override
            public int previousIndex() { return index - 1; }
            @Override
            public void remove() {
                if (lastReturned < 0) throw new IllegalStateException();
                ArrayListADT.this.remove(lastReturned);
                if (lastReturned < index) index--;
                lastReturned = -1;
            }
            @Override
            public void set(E e) {
                if (lastReturned < 0) throw new IllegalStateException();
                ArrayListADT.this.set(lastReturned, e);
            }
            @Override
            public void add(E e) {
                ArrayListADT.this.add(index++, e);
                lastReturned = -1;
            }
        };
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(elements, size);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            return (T[]) Arrays.copyOf(elements, size, a.getClass());
        }
        System.arraycopy(elements, 0, a, 0, size);
        if (a.length > size) a[size] = null;
        return a;
    }

    @Override
    public boolean add(E e) {
        ensureCapacity(size + 1);
        elements[size++] = e;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index >= 0) {
            remove(index);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E get(int index) {
        checkIndex(index);
        return (E) elements[index];
    }

    @SuppressWarnings("unchecked")
    @Override
    public E set(int index, E element) {
        checkIndex(index);
        E old = (E) elements[index];
        elements[index] = element;
        return old;
    }

    @Override
    public void add(int index, E element) {
        checkIndexForAdd(index);
        ensureCapacity(size + 1);
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = element;
        size++;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E remove(int index) {
        checkIndex(index);
        E removed = (E) elements[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elements, index + 1, elements, index, numMoved);
        }
        elements[--size] = null;
        return removed;
    }

    @Override
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++) if (elements[i] == null) return i;
        } else {
            for (int i = 0; i < size; i++) if (o.equals(elements[i])) return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = size - 1; i >= 0; i--) if (elements[i] == null) return i;
        } else {
            for (int i = size - 1; i >= 0; i--) if (o.equals(elements[i])) return i;
        }
        return -1;
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) if (!contains(e)) return false;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (E e : c) add(e);
        return !c.isEmpty();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        checkIndexForAdd(index);
        int numNew = c.size();
        ensureCapacity(size + numNew);
        System.arraycopy(elements, index, elements, index + numNew, size - index);
        int i = index;
        for (E e : c) elements[i++] = e;
        size += numNew;
        return numNew > 0;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object e : c) {
            while (remove(e)) modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        for (int i = 0; i < size; i++) {
            if (!c.contains(elements[i])) {
                remove(i);
                i--;
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        Arrays.fill(elements, 0, size, null);
        size = 0;
    }

    // Helpers
    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private void checkIndexForAdd(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }
}
