package adt;

// Top-level Node class for generics
public class Node<E> {
    public E data;
    public Node<E> next;

    public Node(E data) {
        this.data = data;
        this.next = null;
    }
}
