package jvm;

import jvm.classfile.ClassFile;
import jvm.classfile.MethodInfo;
import jvm.classfile.attribute.CodeAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The bytecode interpreter for the Mini JVM.
 * This class is responsible for executing Java bytecode instructions.
 */
public class Interpreter {
    private static final Logger logger = LoggerFactory.getLogger(Interpreter.class);
    
    private final RuntimeData runtimeData;
    
    public Interpreter(RuntimeData runtimeData) {
        this.runtimeData = runtimeData;
    }
    
    /**
     * Execute a method
     */
    public void execute(MethodInfo method, RuntimeData.Thread thread) {
        CodeAttribute codeAttr = method.getCodeAttribute();
        if (codeAttr == null) {
            throw new RuntimeException("No Code attribute found in method");
        }
        
        // Create a new frame for this method
        RuntimeData.Frame frame = new RuntimeData.Frame(method, codeAttr.getMaxLocals());
        thread.pushFrame(frame);
        
        byte[] code = codeAttr.getCode();
        int codeLength = code.length;
        
        logger.debug("Executing method: {}", method);
        
        // Main interpretation loop
        while (frame.getPc() < codeLength) {
            int pc = frame.getPc();
            int opcode = code[pc] & 0xFF; // Convert byte to unsigned int
            frame.incrementPc(1);
            
            try {
                switch (opcode) {
                    // Constants
                    case 0x01: // aconst_null
                        frame.pushOperand(null);
                        break;
                    case 0x03: // iconst_0
                        frame.pushOperand(0);
                        break;
                    case 0x04: // iconst_1
                        frame.pushOperand(1);
                        break;
                        
                    // Loads
                    case 0x1A: // iload_0
                        frame.pushOperand(frame.getLocalVariable(0));
                        break;
                    case 0x1B: // iload_1
                        frame.pushOperand(frame.getLocalVariable(1));
                        break;
                    case 0x1C: // iload_2
                        frame.pushOperand(frame.getLocalVariable(2));
                        break;
                    case 0x1D: // iload_3
                        frame.pushOperand(frame.getLocalVariable(3));
                        break;
                    case 0x15: // iload
                        int index = code[frame.getPc()] & 0xFF;
                        frame.incrementPc(1);
                        frame.pushOperand(frame.getLocalVariable(index));
                        break;
                        
                    // Stores
                    case 0x3B: // istore_0
                        frame.setLocalVariable(0, frame.popOperand());
                        break;
                    case 0x3C: // istore_1
                        frame.setLocalVariable(1, frame.popOperand());
                        break;
                    case 0x3D: // istore_2
                        frame.setLocalVariable(2, frame.popOperand());
                        break;
                    case 0x3E: // istore_3
                        frame.setLocalVariable(3, frame.popOperand());
                        break;
                    case 0x36: // istore
                        index = code[frame.getPc()] & 0xFF;
                        frame.incrementPc(1);
                        frame.setLocalVariable(index, frame.popOperand());
                        break;
                        
                    // Arithmetic
                    case 0x60: // iadd
                        int b = (Integer) frame.popOperand();
                        int a = (Integer) frame.popOperand();
                        frame.pushOperand(a + b);
                        break;
                    case 0x64: // isub
                        b = (Integer) frame.popOperand();
                        a = (Integer) frame.popOperand();
                        frame.pushOperand(a - b);
                        break;
                    case 0x68: // imul
                        b = (Integer) frame.popOperand();
                        a = (Integer) frame.popOperand();
                        frame.pushOperand(a * b);
                        break;
                    case 0x6C: // idiv
                        b = (Integer) frame.popOperand();
                        a = (Integer) frame.popOperand();
                        frame.pushOperand(a / b);
                        break;
                        
                    // Control
                    case 0x57: // pop
                        frame.popOperand();
                        break;
                    case 0xAC: // ireturn
                        Object returnValue = frame.popOperand();
                        thread.popFrame();
                        if (thread.hasFrames()) {
                            // Push return value onto the operand stack of the calling frame
                            thread.currentFrame().pushOperand(returnValue);
                        }
                        return;
                    case 0xB1: // return
                        thread.popFrame();
                        return;
                        
                    // Comparisons
                    case 0x9F: // if_icmpeq
                        int offset = (code[frame.getPc()] << 8) | (code[frame.getPc() + 1] & 0xFF);
                        frame.incrementPc(2);
                        b = (Integer) frame.popOperand();
                        a = (Integer) frame.popOperand();
                        if (a == b) {
                            frame.setPc(frame.getPc() + offset - 3); // -3 because we already incremented PC by 3
                        }
                        break;
                    case 0xA0: // if_icmpne
                        offset = (code[frame.getPc()] << 8) | (code[frame.getPc() + 1] & 0xFF);
                        frame.incrementPc(2);
                        b = (Integer) frame.popOperand();
                        a = (Integer) frame.popOperand();
                        if (a != b) {
                            frame.setPc(frame.getPc() + offset - 3);
                        }
                        break;
                    case 0xA1: // if_icmplt
                        offset = (code[frame.getPc()] << 8) | (code[frame.getPc() + 1] & 0xFF);
                        frame.incrementPc(2);
                        b = (Integer) frame.popOperand();
                        a = (Integer) frame.popOperand();
                        if (a < b) {
                            frame.setPc(frame.getPc() + offset - 3);
                        }
                        break;
                    case 0xA2: // if_icmpge
                        offset = (code[frame.getPc()] << 8) | (code[frame.getPc() + 1] & 0xFF);
                        frame.incrementPc(2);
                        b = (Integer) frame.popOperand();
                        a = (Integer) frame.popOperand();
                        if (a >= b) {
                            frame.setPc(frame.getPc() + offset - 3);
                        }
                        break;
                    case 0xA3: // if_icmpgt
                        offset = (code[frame.getPc()] << 8) | (code[frame.getPc() + 1] & 0xFF);
                        frame.incrementPc(2);
                        b = (Integer) frame.popOperand();
                        a = (Integer) frame.popOperand();
                        if (a > b) {
                            frame.setPc(frame.getPc() + offset - 3);
                        }
                        break;
                    case 0xA4: // if_icmple
                        offset = (code[frame.getPc()] << 8) | (code[frame.getPc() + 1] & 0xFF);
                        frame.incrementPc(2);
                        b = (Integer) frame.popOperand();
                        a = (Integer) frame.popOperand();
                        if (a <= b) {
                            frame.setPc(frame.getPc() + offset - 3);
                        }
                        break;
                    case 0x99: // ifeq
                        offset = (code[frame.getPc()] << 8) | (code[frame.getPc() + 1] & 0xFF);
                        frame.incrementPc(2);
                        if ((Integer) frame.popOperand() == 0) {
                            frame.setPc(frame.getPc() + offset - 3);
                        }
                        break;
                    case 0x9A: // ifne
                        offset = (code[frame.getPc()] << 8) | (code[frame.getPc() + 1] & 0xFF);
                        frame.incrementPc(2);
                        if ((Integer) frame.popOperand() != 0) {
                            frame.setPc(frame.getPc() + offset - 3);
                        }
                        break;
                    case 0x9B: // iflt
                        offset = (code[frame.getPc()] << 8) | (code[frame.getPc() + 1] & 0xFF);
                        frame.incrementPc(2);
                        if ((Integer) frame.popOperand() < 0) {
                            frame.setPc(frame.getPc() + offset - 3);
                        }
                        break;
                    case 0x9C: // ifge
                        offset = (code[frame.getPc()] << 8) | (code[frame.getPc() + 1] & 0xFF);
                        frame.incrementPc(2);
                        if ((Integer) frame.popOperand() >= 0) {
                            frame.setPc(frame.getPc() + offset - 3);
                        }
                        break;
                    case 0x9D: // ifgt
                        offset = (code[frame.getPc()] << 8) | (code[frame.getPc() + 1] & 0xFF);
                        frame.incrementPc(2);
                        if ((Integer) frame.popOperand() > 0) {
                            frame.setPc(frame.getPc() + offset - 3);
                        }
                        break;
                    case 0x9E: // ifle
                        offset = (code[frame.getPc()] << 8) | (code[frame.getPc() + 1] & 0xFF);
                        frame.incrementPc(2);
                        if ((Integer) frame.popOperand() <= 0) {
                            frame.setPc(frame.getPc() + offset - 3);
                        }
                        break;
                    case 0xA7: // goto
                        offset = (code[frame.getPc()] << 8) | (code[frame.getPc() + 1] & 0xFF);
                        frame.incrementPc(2);
                        frame.setPc(frame.getPc() + offset - 3);
                        break;
                        
                    // Invocations
                    case 0xB6: // invokevirtual
                        // Get the method reference index (2 bytes)
                        int methodRefIndex = (code[frame.getPc()] << 8) | (code[frame.getPc() + 1] & 0xFF);
                        frame.incrementPc(2);
                        
                        // In a real implementation, we would resolve the method and invoke it
                        logger.debug("Invoking method with reference index: {}", methodRefIndex);
                        
                        // For now, we'll just pop the arguments and push a dummy return value
                        // This is a placeholder - in a real implementation, we would actually invoke the method
                        int numArgs = 1; // Default to 1 for non-static methods (this reference)
                        
                        // Pop the arguments (in reverse order)
                        for (int i = 0; i < numArgs; i++) {
                            frame.popOperand();
                        }
                        
                        // Push a dummy return value (0)
                        frame.pushOperand(0);
                        break;
                        
                    // Stack manipulation
                    case 0x59: // dup
                        Object value = frame.popOperand();
                        frame.pushOperand(value);
                        frame.pushOperand(value);
                        break;
                    case 0x5A: // dup_x1
                        value = frame.popOperand();
                        Object value2 = frame.popOperand();
                        frame.pushOperand(value);
                        frame.pushOperand(value2);
                        frame.pushOperand(value);
                        break;
                    case 0x5B: // dup_x2
                        value = frame.popOperand();
                        value2 = frame.popOperand();
                        Object value3 = frame.popOperand();
                        frame.pushOperand(value);
                        frame.pushOperand(value3);
                        frame.pushOperand(value2);
                        frame.pushOperand(value);
                        break;
                    case 0x5C: // dup2
                        value = frame.popOperand();
                        value2 = frame.popOperand();
                        frame.pushOperand(value2);
                        frame.pushOperand(value);
                        frame.pushOperand(value2);
                        frame.pushOperand(value);
                        break;
                    case 0x5D: // dup2_x1
                        value = frame.popOperand();
                        value2 = frame.popOperand();
                        value3 = frame.popOperand();
                        frame.pushOperand(value2);
                        frame.pushOperand(value);
                        frame.pushOperand(value3);
                        frame.pushOperand(value2);
                        frame.pushOperand(value);
                        break;
                    case 0x5E: // dup2_x2
                        value = frame.popOperand();
                        value2 = frame.popOperand();
                        value3 = frame.popOperand();
                        Object value4 = frame.popOperand();
                        frame.pushOperand(value2);
                        frame.pushOperand(value);
                        frame.pushOperand(value4);
                        frame.pushOperand(value3);
                        frame.pushOperand(value2);
                        frame.pushOperand(value);
                        break;
                    case 0x5F: // swap
                        value = frame.popOperand();
                        value2 = frame.popOperand();
                        frame.pushOperand(value);
                        frame.pushOperand(value2);
                        break;
                        
                    // Other
                    case 0x00: // nop
                        break;
                        
                    default:
                        throw new UnsupportedOperationException(
                            String.format("Unsupported opcode: 0x%02X at PC=%d", opcode, pc));
                }
            } catch (Exception e) {
                throw new RuntimeException(
                    String.format("Error executing opcode 0x%02X at PC=%d: %s", 
                                 opcode, pc, e.getMessage()), e);
            }
        }
    }
}
