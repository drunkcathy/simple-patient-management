package adt;

public interface QueueInterface<T> {
    void enqueue(T item);
    T dequeue();
    T peek();
    boolean isEmpty();
    int size();
    void clear();
}
