package adt;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListADT<E> implements Iterable<E> {

    private Node<E> head;
    private int size;

    // Constructor
    public LinkedListADT() {
        head = null;
        size = 0;
    }

    // Add to end
    public void add(E element) {
        Node<E> newNode = new Node<>(element);
        if (head == null) {
            head = newNode;
        } else {
            Node<E> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    // Add at specific index
    public void add(int index, E element) {
        checkPositionIndex(index);

        Node<E> newNode = new Node<>(element);
        if (index == 0) {
            newNode.next = head;
            head = newNode;
        } else {
            Node<E> prev = getNode(index - 1);
            newNode.next = prev.next;
            prev.next = newNode;
        }
        size++;
    }

    // Get element at index
    public E get(int index) {
        checkElementIndex(index);
        return getNode(index).data;
    }

    // Set element at index
    public E set(int index, E element) {
        checkElementIndex(index);
        Node<E> node = getNode(index);
        E oldData = node.data;
        node.data = element;
        return oldData;
    }

    // Remove element at index
    public E remove(int index) {
        checkElementIndex(index);

        Node<E> removedNode;
        if (index == 0) {
            removedNode = head;
            head = head.next;
        } else {
            Node<E> prev = getNode(index - 1);
            removedNode = prev.next;
            prev.next = removedNode.next;
        }
        size--;
        return removedNode.data;
    }

    // Remove by value
    public boolean remove(E element) {
        if (head == null) return false;

        if (head.data.equals(element)) {
            head = head.next;
            size--;
            return true;
        }

        Node<E> current = head;
        while (current.next != null) {
            if (current.next.data.equals(element)) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // Check if contains element
    public boolean contains(E element) {
        Node<E> current = head;
        while (current != null) {
            if (current.data.equals(element)) return true;
            current = current.next;
        }
        return false;
    }

    // Size
    public int size() {
        return size;
    }

    // Empty check
    public boolean isEmpty() {
        return size == 0;
    }

    // Clear list
    public void clear() {
        head = null;
        size = 0;
    }

    // Helper: get node by index
    private Node<E> getNode(int index) {
        Node<E> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    // Index validation
    private void checkElementIndex(int index) {
        if (!(index >= 0 && index < size)) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private void checkPositionIndex(int index) {
        if (!(index >= 0 && index <= size)) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    // Iterator for foreach loops
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node<E> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                E data = current.data;
                current = current.next;
                return data;
            }
        };
    }
}
