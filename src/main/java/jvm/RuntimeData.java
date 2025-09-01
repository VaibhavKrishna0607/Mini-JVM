package jvm;

import jvm.classfile.ClassFile;
import jvm.classfile.MethodInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Represents the runtime data areas of the JVM.
 * This includes the method area, heap, and per-thread data.
 */
public class RuntimeData {
    private static final Logger logger = LoggerFactory.getLogger(RuntimeData.class);
    
    // Method Area - stores class metadata and method code
    private final Map<String, ClassFile> methodArea = new HashMap<>();
    
    // Heap - stores objects and arrays
    private final Heap heap = new Heap();
    
    // Thread management
    private final Map<Long, Thread> threads = new HashMap<>();
    private long nextThreadId = 1;
    
    /**
     * Load a class into the method area
     */
    public void loadClass(ClassFile classFile) {
        String className = classFile.getConstantPool().getClassName(classFile.getThisClass());
        methodArea.put(className, classFile);
        logger.debug("Loaded class: {}", className);
    }
    
    /**
     * Get a loaded class by name
     */
    public ClassFile getClass(String className) {
        return methodArea.get(className);
    }
    
    /**
     * Create a new thread
     */
    public Thread createThread() {
        Thread thread = new Thread(this, nextThreadId++);
        threads.put(thread.getId(), thread);
        return thread;
    }
    
    /**
     * Get a thread by ID
     */
    public Thread getThread(long threadId) {
        return threads.get(threadId);
    }
    
    /**
     * Represents the JVM heap for object allocation
     */
    public static class Heap {
        // Simple implementation using a map for object storage
        private final Map<Long, Object> objects = new HashMap<>();
        private long nextObjectId = 1;
        
        /**
         * Allocate a new object
         */
        public long allocateObject(ClassFile classFile) {
            long objectId = nextObjectId++;
            // In a real implementation, we would allocate memory for the object's fields
            objects.put(objectId, new ObjectData(classFile));
            return objectId;
        }
        
        /**
         * Get an object by ID
         */
        public ObjectData getObject(long objectId) {
            return (ObjectData) objects.get(objectId);
        }
        
        /**
         * Represents an object in the heap
         */
        public static class ObjectData {
            private final ClassFile classFile;
            private final Map<String, Object> fields = new HashMap<>();
            
            public ObjectData(ClassFile classFile) {
                this.classFile = classFile;
            }
            
            public ClassFile getClassFile() {
                return classFile;
            }
            
            public Object getField(String name) {
                return fields.get(name);
            }
            
            public void setField(String name, Object value) {
                fields.put(name, value);
            }
        }
    }
    
    /**
     * Represents a thread of execution in the JVM
     */
    public static class Thread {
        private final RuntimeData runtimeData;
        private final long id;
        private final Stack<Frame> stack = new Stack<>();
        
        public Thread(RuntimeData runtimeData, long id) {
            this.runtimeData = runtimeData;
            this.id = id;
        }
        
        public long getId() {
            return id;
        }
        
        /**
         * Push a new frame onto the stack
         */
        public void pushFrame(Frame frame) {
            stack.push(frame);
        }
        
        /**
         * Pop the current frame from the stack
         */
        public Frame popFrame() {
            return stack.pop();
        }
        
        /**
         * Get the current frame
         */
        public Frame currentFrame() {
            return stack.peek();
        }
        
        /**
         * Check if the thread has any frames on the stack
         */
        public boolean hasFrames() {
            return !stack.isEmpty();
        }
    }
    
    /**
     * Represents a stack frame in the JVM
     */
    public static class Frame {
        private final MethodInfo method;
        private final Object[] localVariables;
        private final Stack<Object> operandStack = new Stack<>();
        private int pc = 0; // Program counter
        
        public Frame(MethodInfo method, int maxLocals) {
            this.method = method;
            this.localVariables = new Object[maxLocals];
        }
        
        public MethodInfo getMethod() {
            return method;
        }
        
        public Object getLocalVariable(int index) {
            if (index < 0 || index >= localVariables.length) {
                throw new IndexOutOfBoundsException("Invalid local variable index: " + index);
            }
            return localVariables[index];
        }
        
        public void setLocalVariable(int index, Object value) {
            if (index < 0 || index >= localVariables.length) {
                throw new IndexOutOfBoundsException("Invalid local variable index: " + index);
            }
            localVariables[index] = value;
        }
        
        public void pushOperand(Object value) {
            operandStack.push(value);
        }
        
        public Object popOperand() {
            return operandStack.pop();
        }
        
        public int getPc() {
            return pc;
        }
        
        public void setPc(int pc) {
            this.pc = pc;
        }
        
        public void incrementPc(int offset) {
            this.pc += offset;
        }
    }
}
