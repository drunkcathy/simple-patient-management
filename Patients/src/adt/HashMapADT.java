package adt;

public class HashMapADT<K, V> {

    
    private static class Entry<K, V> {
        K key;
        V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private LinkedListADT<Entry<K, V>>[] buckets;
    private int capacity;
    private int size;

    @SuppressWarnings("unchecked")
    public HashMapADT(int capacity) {
        this.capacity = capacity;
        this.buckets = new LinkedListADT[capacity];
        for (int i = 0; i < capacity; i++) {
            buckets[i] = new LinkedListADT<>();
        }
        this.size = 0;
    }

    private int hash(K key) {
        return (key == null) ? 0 : Math.abs(key.hashCode()) % capacity;
    }

    public void put(K key, V value) {
        int index = hash(key);
        LinkedListADT<Entry<K, V>> bucket = buckets[index];

        for (Entry<K, V> entry : bucket) {
            if ((key == null && entry.key == null) || (key != null && key.equals(entry.key))) {
                entry.value = value; // update
                return;
            }
        }

        bucket.add(new Entry<>(key, value));
        size++;
    }

    public V get(K key) {
        int index = hash(key);
        LinkedListADT<Entry<K, V>> bucket = buckets[index];

        for (Entry<K, V> entry : bucket) {
            if ((key == null && entry.key == null) || (key != null && key.equals(entry.key))) {
                return entry.value;
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // Return all values (standard HashMap "values()" behavior)
    public LinkedListADT<V> values() {
        LinkedListADT<V> allValues = new LinkedListADT<>();
        for (LinkedListADT<Entry<K, V>> bucket : buckets) {
            for (Entry<K, V> entry : bucket) {
                allValues.add(entry.value);
            }
        }
        return allValues;
    }
    
   public ArrayListADT<K> getKeys() {
    ArrayListADT<K> keys = new ArrayListADT<>();
    for (LinkedListADT<Entry<K, V>> bucket : buckets) {
        for (Entry<K, V> entry : bucket) {
            keys.add(entry.key);
        }
    }
    return keys;
}


}
