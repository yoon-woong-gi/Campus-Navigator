// == CS400 Fall 2024 File Header Information ==
// Name: Woonggi Yoon
// Email: wyoon8@wisc.edu
// Group: P2.3905
// Lecturer: Florian Heimerl
// Notes to Grader: N/A

import java.util.LinkedList;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HashtableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType>{

    /**
     * Inner class to represent a key-value pair.
     */
    protected class Pair {
        public KeyType key; // Key of the pair
        public ValueType value; // Value of the pair

        /**
         * Constructs a new Pair with the specified key and value.
         * @param key the key of the pair
         * @param value the value of the pair
         */
        public Pair(KeyType key, ValueType value) {
            this.key = key;
            this.value = value;
        }
    }

    protected LinkedList<Pair>[] table = null;

    /**
     * Constructor to initialize the hashtable with a given capacity.
     * @param capacity the initial size of the hashtable
     */
    @SuppressWarnings("unchecked")
    public HashtableMap(int capacity) {
        table = (LinkedList<Pair>[]) new LinkedList[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new LinkedList<>();
        }
    }

    /**
     * Default constructor to initialize the hashtable with a default capacity of 64.
     */
    public HashtableMap() {
        this(64);
    }

    /**
     * Adds a key-value pair to the hashtable.
     * @param key the key of the pair
     * @param value the value of the pair
     * @throws IllegalArgumentException if the key already exists
     * @throws NullPointerException if the key is null
     */
    public void put(KeyType key, ValueType value) throws IllegalArgumentException {
        if (key == null) throw new NullPointerException("The key is null");
        if (containsKey(key)) throw new IllegalArgumentException("The key already mapped");

        Pair newPair = new Pair(key, value);
        int index = Math.abs(key.hashCode()) % getCapacity();
        table[index].add(newPair);

        double load_factor = (double) getSize() / getCapacity();
        if (load_factor >= 0.8) resize();
    }

    /**
     * Resizes the hashtable by doubling its capacity and rehashing all key-value pairs.
     */
    private void resize() {
        LinkedList<Pair>[] oldTable = table;
        table = (LinkedList<Pair>[]) new LinkedList[oldTable.length * 2];
        for (int i = 0; i < table.length; ++i) {
            table[i] = new LinkedList<>();
        }
        for (LinkedList<Pair> chaining : oldTable) {
            for (Pair pair : chaining) {
                int newIndex = Math.abs(pair.key.hashCode()) % table.length;
                table[newIndex].add(pair);
            }
        }
    }

    /**
     * Checks if the hashtable contains a given key.
     * @param key the key to check
     * @return true if the key exists, false otherwise
     */
    public boolean containsKey(KeyType key) {
        if (key == null) return false;

        int index = Math.abs(key.hashCode()) % getCapacity();
        for (Pair pairs : table[index]) {
            if (pairs.key.equals(key)) return true;
        }

        return false;
    }

    /**
     * Retrieves the value associated with a given key.
     * @param key the key to retrieve
     * @return the value associated with the key
     * @throws NoSuchElementException if the key does not exist
     */
    public ValueType get(KeyType key) throws NoSuchElementException {
        if (!containsKey(key)) throw new NoSuchElementException("The key is not stored");

        int index = Math.abs(key.hashCode()) % getCapacity();
        for (Pair pairs : table[index]) {
            if (pairs.key.equals(key))
                return pairs.value;
        }

        throw new NoSuchElementException("The key is not stored");
    }

    /**
     * Removes a key-value pair from the hashtable.
     * @param key the key to remove
     * @return the value of the removed key
     * @throws NoSuchElementException if the key does not exist
     */
    public ValueType remove(KeyType key) throws NoSuchElementException {
        if (!containsKey(key)) throw new NoSuchElementException("The key is not stored");

        int index = Math.abs(key.hashCode()) % getCapacity();
        for (int i = 0; i < table[index].size(); ++i) {
            if (table[index].get(i).key.equals(key)) {
                ValueType removedValue = table[index].get(i).value;
                table[index].remove(i);
                return removedValue;
            }
        }
        throw new NoSuchElementException("The key is not stored");
    }

    /**
     * Clears all key-value pairs from the hashtable.
     */
    public void clear() {
        for (LinkedList<Pair> pairs : table) {
            pairs.clear();
        }
    }

    /**
     * Retrieves the number of key-value pairs in the hashtable.
     * @return the number of pairs
     */
    public int getSize() {
        int size = 0;
        for (LinkedList<Pair> chaining : table) {
            size += chaining.size();
        }
        return size;
    }

    /**
     * Retrieves the capacity of the hashtable.
     * @return the capacity
     */
    public int getCapacity() {
        return table.length;
    }

    /**
     * Retrieves all keys stored in the hashtable.
     * @return a LinkedList containing all keys in the hashtable
     */
    public LinkedList<KeyType> getKeys() {
        LinkedList<KeyType> keys = new LinkedList<>();
        for (LinkedList<Pair> chaining : table) {
            for (Pair pair : chaining)
                keys.add(pair.key);
        }
        return keys;
    }


    /**
     * Basic test for adding and retrieving a key-value pair.
     */
    @Test
    public void testBasicPutAndGet() {
        HashtableMap<String, Integer> map = new HashtableMap<>();
        map.put("test", 1);
        assertEquals(1, map.get("test"));
    }

    /**
     * Tests removing a key-value pair from the hashtable.
     */
    @Test
    public void testRemove() {
        HashtableMap<String, String> map = new HashtableMap<>();
        map.put("key", "value");
        assertEquals("value", map.remove("key"));
        assertFalse(map.containsKey("key"));
    }

    /**
     * Tests that the hashtable correctly handles null keys.
     */
    @Test
    public void testNullKey() {
        HashtableMap<String, Integer> map = new HashtableMap<>();
        assertThrows(NullPointerException.class, () -> map.put(null, 1));
    }

    /**
     * Tests that the hashtable correctly handles duplicate keys.
     */
    @Test
    public void testDuplicateKey() {
        HashtableMap<Integer, String> map = new HashtableMap<>();
        map.put(1, "first");
        assertThrows(IllegalArgumentException.class, () -> map.put(1, "second"));
    }

    /**
     * Tests that the hashtable correctly tracks its size.
     */
    @Test
    public void testSize() {
        HashtableMap<String, Double> map = new HashtableMap<>();
        assertEquals(0, map.getSize());
        map.put("A", 1.0);
        assertEquals(1, map.getSize());
        map.put("B", 2.0);
        assertEquals(2, map.getSize());
        map.remove("A");
        assertEquals(1, map.getSize());
    }
}
