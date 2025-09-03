package memory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MemoryManager {
    private Map<Integer, int[]> heap = new HashMap<>();
    private int nextObjectId = 1000;

    public int allocateArray(int size) {
        int objectId = nextObjectId++;
        heap.put(objectId, new int[size]);
        System.out.println("[MemoryManager] → Allocated array of size " + size + " with ObjectID: " + objectId);
        System.out.println("[Heap] → ObjectID " + objectId + " → " + Arrays.toString(heap.get(objectId)));
        return objectId;
    }

    public void storeValue(int objectId, int index, int value) {
        int[] array = heap.get(objectId);
        if (array == null) {
            System.out.println("[MemoryManager] ⚠ Invalid ObjectID: " + objectId);
            return;
        }
        if (index < 0 || index >= array.length) {
            System.out.println("[MemoryManager] ⚠ Index out of bounds: " + index);
            return;
        }
        array[index] = value;
        System.out.println("[MemoryManager] → Stored value " + value + " at index " + index + " of ObjectID " + objectId);
        System.out.println("[Heap] → ObjectID " + objectId + " → " + Arrays.toString(array));
    }

    public int[] getArray(int objectId) {
        return heap.get(objectId);
    }

    public boolean exists(int objectId) {
        return heap.containsKey(objectId);
    }

    public Map<Integer, int[]> getHeapSnapshot() {
        return new HashMap<>(heap);
    }

    public void deallocate(int objectId) {
        heap.remove(objectId);
        System.out.println("[MemoryManager] → Deallocated ObjectID: " + objectId);
    }
}