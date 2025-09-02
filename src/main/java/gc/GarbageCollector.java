package main.java.gc;

import memory.MemoryManager;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GarbageCollector {
    private final MemoryManager memoryManager;
    private final Set<Integer> marked = new HashSet<>();

    public GarbageCollector(MemoryManager memoryManager){
        this.memoryManager = memoryManager;
    }

    public void mark(Set<Integer> roots){
        System.out.println("[GC]->Starting mark phase");;
        marked.clear();
         for (int rootId : roots) {
            if (memoryManager.exists(rootId)) {
                marked.add(rootId);
                System.out.println("[GC] → Marked reachable ObjectID: " + rootId);
            } else {
                System.out.println("[GC] ⚠ Root ObjectID not found: " + rootId);
            }

    }
}
 public void sweep() {
        System.out.println("[GC] → Starting sweep phase");
        Map<Integer, int[]> heap = memoryManager.getHeapSnapshot();
        int freedCount = 0;

        for (int objectId : new HashSet<>(heap.keySet())) {
            if (!marked.contains(objectId)) {
                memoryManager.deallocate(objectId);
                System.out.println("[GC] → Swept unreachable ObjectID: " + objectId);
                freedCount++;
            }
        }

        System.out.println("[GC] → Sweep complete. Freed " + freedCount + " objects");
    }

    
}
