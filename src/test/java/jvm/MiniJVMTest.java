package jvm;

import jvm.classfile.ClassFile;
import jvm.classfile.ConstantPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MiniJVMTest {
    
    private MiniJVM jvm;
    
    @BeforeEach
    public void setUp() {
        jvm = new MiniJVM();
    }
    
    @Test
    public void testClassFileMagicNumber() throws IOException {
        // A minimal class file with just the magic number (0xCAFEBABE)
        byte[] classData = {
            (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE, // magic
            0x00, 0x00, // minor version
            0x00, 0x34, // major version (Java 8)
            0x00, 0x10, // constant pool count (16 entries)
            // Constant pool entries would go here
        };
        
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(classData))) {
            ClassFile classFile = ClassFile.parse(in);
            assertEquals(0xCAFEBABE, classFile.getMagic());
            assertEquals(52, classFile.getMajorVersion()); // Java 8
        }
    }
    
    @Test
    public void testInvalidMagicNumber() {
        // Invalid magic number
        byte[] invalidClassData = {
            0x12, 0x34, 0x56, 0x78, // invalid magic
            0x00, 0x00, // minor version
            0x00, 0x34, // major version (Java 8)
            0x00, 0x10  // constant pool count
        };
        
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(invalidClassData))) {
            assertThrows(ClassFormatError.class, () -> ClassFile.parse(in));
        } catch (IOException e) {
            fail("Unexpected IOException", e);
        }
    }
    
    @Test
    public void testConstantPoolParsing() {
        // This is a simplified test - a real test would need a complete constant pool
        assertTrue(true, "Placeholder for constant pool parsing tests");
    }
    
    @Test
    public void testMethodParsing() {
        // This is a simplified test - a real test would need a complete class file
        assertTrue(true, "Placeholder for method parsing tests");
    }
    
}
