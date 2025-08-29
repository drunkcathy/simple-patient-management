package adt;

import java.util.Comparator;

public class PriorityQueueADT<T> implements QueueInterface<T> {
    private ArrayListADT<T> heap;
    private Comparator<T> comparator;

    public PriorityQueueADT(Comparator<T> comparator) {
        this.heap = new ArrayListADT<>();
        this.comparator = comparator;
    }

    @Override
    public void enqueue(T item) {
        heap.add(item);
        heapifyUp(heap.size() - 1);
    }

    @Override
    public T dequeue() {
        if (heap.isEmpty()) return null;
        T top = heap.get(0);
        T last = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, last);
            heapifyDown(0);
            
        }
        return top;
    }

    @Override
    public T peek() {
        return heap.isEmpty() ? null : heap.get(0);
    }

    @Override
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    @Override
    public int size() {
        return heap.size();
    }

    @Override
    public void clear() {
        heap = new ArrayListADT<>();
    }

    // --- Heap operations ---
    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (comparator.compare(heap.get(index), heap.get(parent)) >= 0) break;
            swap(index, parent);
            index = parent;
        }
    }

    private void heapifyDown(int index) {
        int size = heap.size();
        while (true) {
            int left = 2 * index + 1;
            int right = left + 1;
            int smallest = index;

            if (left < size && comparator.compare(heap.get(left), heap.get(smallest)) < 0) {
                smallest = left;
            }
            if (right < size && comparator.compare(heap.get(right), heap.get(smallest)) < 0) {
                smallest = right;
            }
            if (smallest == index) break;

            swap(index, smallest);
            index = smallest;
        }
    }
    
    public ArrayListADT<T> getAllItems() {
    ArrayListADT<T> copy = new ArrayListADT<>();
    for (int i = 0; i < heap.size(); i++) {
        copy.add(heap.get(i));
    }
    return copy;
}


    private void swap(int i, int j) {
        T temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
    
   public void offer(T element){
       heap.add(element);
       heap.sort(comparator);
   }
    
   public T poll() {
    if (heap.isEmpty()) return null; // nothing to remove
    T top = heap.get(0);
    T last = heap.remove(heap.size() - 1);
    if (!heap.isEmpty()) {
        heap.set(0, last);
        heapifyDown(0);
    }
    return top;
}

 
}
