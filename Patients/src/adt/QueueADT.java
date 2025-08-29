package adt;

import java.util.function.Consumer;

public class QueueADT<T> {
    private Node<T> front;
    private Node<T> rear;
    private int size;

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    public QueueADT() {
        front = null;
        rear = null;
        size = 0;
    }

    public boolean offer(T item) {
        Node<T> newNode = new Node<>(item);
        if (rear != null) rear.next = newNode;
        rear = newNode;
        if (front == null) front = rear;
        size++;
        return true;
    }

    public T poll() {
        if (front == null) return null;
        T data = front.data;
        front = front.next;
        if (front == null) rear = null;
        size--;
        return data;
    }

    public void forEach(Consumer<? super T> action) {
        Node<T> current = front;
        while (current != null) {
            action.accept(current.data);
            current = current.next;
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void clear() {
        front = rear = null;
        size = 0;
    }
    
    public boolean remove(T item) {
    if (isEmpty()) return false;

    // Special case: item is at the front
    if (front.data.equals(item)) {
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return true;
    }

    // Search for item in the middle
    Node<T> current = front;
    while (current.next != null) {
        if (current.next.data.equals(item)) {
            current.next = current.next.next;
            if (current.next == null) {
                rear = current; // removed last element
            }
            size--;
            return true;
        }
        current = current.next;
    }
    return false; // not found
}


  
}
