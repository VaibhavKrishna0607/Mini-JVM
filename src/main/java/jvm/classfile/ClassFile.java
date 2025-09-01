package jvm.classfile;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Java class file structure.
 * Follows the JVM Specification Chapter 4: The class File Format
 */
public class ClassFile {
    private int magic;
    private int minorVersion;
    private int majorVersion;
    private ConstantPool constantPool;
    private int accessFlags;
    private int thisClass;
    private int superClass;
    private int[] interfaces;
    private FieldInfo[] fields;
    private MethodInfo[] methods;
    private AttributeInfo[] attributes;

    // Access flags
    public static final int ACC_PUBLIC = 0x0001;
    public static final int ACC_FINAL = 0x0010;
    public static final int ACC_SUPER = 0x0020;
    public static final int ACC_INTERFACE = 0x0200;
    public static final int ACC_ABSTRACT = 0x0400;
    public static final int ACC_SYNTHETIC = 0x1000;
    public static final int ACC_ANNOTATION = 0x2000;
    public static final int ACC_ENUM = 0x4000;

    /**
     * Parse a class file from an input stream
     */
    public static ClassFile parse(DataInputStream in) throws IOException {
        ClassFile classFile = new ClassFile();
        
        // Read magic number and verify
        classFile.magic = Integer.reverseBytes(in.readInt());
        if (classFile.magic != 0xCAFEBABE) {
            throw new ClassFormatError("Invalid magic number: 0x" + Integer.toHexString(classFile.magic));
        }
        
        // Read version
        classFile.minorVersion = in.readUnsignedShort();
        classFile.majorVersion = in.readUnsignedShort();
        
        // Read constant pool
        classFile.constantPool = ConstantPool.parse(in);
        
        // Read access flags
        classFile.accessFlags = in.readUnsignedShort();
        
        // Read this class and super class
        classFile.thisClass = in.readUnsignedShort();
        classFile.superClass = in.readUnsignedShort();
        
        // Read interfaces
        int interfacesCount = in.readUnsignedShort();
        classFile.interfaces = new int[interfacesCount];
        for (int i = 0; i < interfacesCount; i++) {
            classFile.interfaces[i] = in.readUnsignedShort();
        }
        
        // Read fields
        int fieldsCount = in.readUnsignedShort();
        classFile.fields = new FieldInfo[fieldsCount];
        for (int i = 0; i < fieldsCount; i++) {
            classFile.fields[i] = FieldInfo.parse(in, classFile.constantPool);
        }
        
        // Read methods
        int methodsCount = in.readUnsignedShort();
        classFile.methods = new MethodInfo[methodsCount];
        for (int i = 0; i < methodsCount; i++) {
            classFile.methods[i] = MethodInfo.parse(in, classFile.constantPool);
        }
        
        // Read attributes
        int attributesCount = in.readUnsignedShort();
        classFile.attributes = new AttributeInfo[attributesCount];
        for (int i = 0; i < attributesCount; i++) {
            classFile.attributes[i] = AttributeInfo.parse(in, classFile.constantPool);
        }
        
        return classFile;
    }
    
    // Getters
    public int getMagic() { return magic; }
    public int getMinorVersion() { return minorVersion; }
    public int getMajorVersion() { return majorVersion; }
    public ConstantPool getConstantPool() { return constantPool; }
    public int getAccessFlags() { return accessFlags; }
    public int getThisClass() { return thisClass; }
    public int getSuperClass() { return superClass; }
    public int[] getInterfaces() { return interfaces; }
    public FieldInfo[] getFields() { return fields; }
    public MethodInfo[] getMethods() { return methods; }
    public AttributeInfo[] getAttributes() { return attributes; }
    
    /**
     * Represents a method in the class file
     */
    public static class MethodInfo {
        private int accessFlags;
        private int nameIndex;
        private int descriptorIndex;
        private AttributeInfo[] attributes;
        private CodeAttribute codeAttribute;
        
        public static MethodInfo parse(DataInputStream in, ConstantPool cp) throws IOException {
            MethodInfo method = new MethodInfo();
            method.accessFlags = in.readUnsignedShort();
            method.nameIndex = in.readUnsignedShort();
            method.descriptorIndex = in.readUnsignedShort();
            
            int attributesCount = in.readUnsignedShort();
            method.attributes = new AttributeInfo[attributesCount];
            for (int i = 0; i < attributesCount; i++) {
                AttributeInfo attr = AttributeInfo.parse(in, cp);
                method.attributes[i] = attr;
                if (attr instanceof CodeAttribute) {
                    method.codeAttribute = (CodeAttribute) attr;
                }
            }
            
            return method;
        }
        
        // Getters
        public int getAccessFlags() { return accessFlags; }
        public int getNameIndex() { return nameIndex; }
        public int getDescriptorIndex() { return descriptorIndex; }
        public AttributeInfo[] getAttributes() { return attributes; }
        public CodeAttribute getCodeAttribute() { return codeAttribute; }
    }
    
    /**
     * Represents a field in the class file
     */
    public static class FieldInfo {
        private int accessFlags;
        private int nameIndex;
        private int descriptorIndex;
        private AttributeInfo[] attributes;
        
        public static FieldInfo parse(DataInputStream in, ConstantPool cp) throws IOException {
            FieldInfo field = new FieldInfo();
            field.accessFlags = in.readUnsignedShort();
            field.nameIndex = in.readUnsignedShort();
            field.descriptorIndex = in.readUnsignedShort();
            
            int attributesCount = in.readUnsignedShort();
            field.attributes = new AttributeInfo[attributesCount];
            for (int i = 0; i < attributesCount; i++) {
                field.attributes[i] = AttributeInfo.parse(in, cp);
            }
            
            return field;
        }
    }
    
    /**
     * Base class for all attributes
     */
    public static abstract class AttributeInfo {
        protected int nameIndex;
        protected byte[] info;
        
        public static AttributeInfo parse(DataInputStream in, ConstantPool cp) throws IOException {
            int nameIndex = in.readUnsignedShort();
            int length = in.readInt();
            
            // Read the attribute data
            byte[] info = new byte[length];
            in.readFully(info);
            
            // Create appropriate attribute type based on name
            String name = cp.getUtf8String(nameIndex);
            
            switch (name) {
                case "Code":
                    return CodeAttribute.parse(nameIndex, info, cp);
                case "LineNumberTable":
                    return LineNumberTableAttribute.parse(nameIndex, info, cp);
                case "SourceFile":
                    return SourceFileAttribute.parse(nameIndex, info, cp);
                // Add more attribute types as needed
                default:
                    // Generic attribute for unsupported types
                    AttributeInfo attr = new AttributeInfo() {};
                    attr.nameIndex = nameIndex;
                    attr.info = info;
                    return attr;
            }
        }
        
        public int getNameIndex() { return nameIndex; }
        public byte[] getInfo() { return info; }
    }
    
    /**
     * Code attribute - contains the bytecode for a method
     */
    public static class CodeAttribute extends AttributeInfo {
        private int maxStack;
        private int maxLocals;
        private byte[] code;
        private ExceptionTableEntry[] exceptionTable;
        private AttributeInfo[] attributes;
        
        public static CodeAttribute parse(int nameIndex, byte[] info, ConstantPool cp) throws IOException {
            CodeAttribute attr = new CodeAttribute();
            attr.nameIndex = nameIndex;
            
            ByteBuffer buffer = ByteBuffer.wrap(info).order(ByteOrder.BIG_ENDIAN);
            attr.maxStack = buffer.getShort() & 0xFFFF;
            attr.maxLocals = buffer.getShort() & 0xFFFF;
            
            // Read code
            int codeLength = buffer.getInt();
            attr.code = new byte[codeLength];
            buffer.get(attr.code);
            
            // Read exception table
            int exceptionTableLength = buffer.getShort() & 0xFFFF;
            attr.exceptionTable = new ExceptionTableEntry[exceptionTableLength];
            for (int i = 0; i < exceptionTableLength; i++) {
                int startPc = buffer.getShort() & 0xFFFF;
                int endPc = buffer.getShort() & 0xFFFF;
                int handlerPc = buffer.getShort() & 0xFFFF;
                int catchType = buffer.getShort() & 0xFFFF;
                attr.exceptionTable[i] = new ExceptionTableEntry(startPc, endPc, handlerPc, catchType);
            }
            
            // Read attributes
            int attributesCount = buffer.getShort() & 0xFFFF;
            attr.attributes = new AttributeInfo[attributesCount];
            for (int i = 0; i < attributesCount; i++) {
                // For simplicity, we'll just skip attributes in the Code attribute
                int attrNameIndex = buffer.getShort() & 0xFFFF;
                int attrLength = buffer.getInt();
                byte[] attrInfo = new byte[attrLength];
                buffer.get(attrInfo);
                // In a full implementation, we would parse these attributes
            }
            
            return attr;
        }
        
        public int getMaxStack() { return maxStack; }
        public int getMaxLocals() { return maxLocals; }
        public byte[] getCode() { return code; }
        public ExceptionTableEntry[] getExceptionTable() { return exceptionTable; }
        public AttributeInfo[] getAttributes() { return attributes; }
    }
    
    /**
     * Represents an entry in the exception table of a Code attribute
     */
    public static class ExceptionTableEntry {
        private final int startPc;
        private final int endPc;
        private final int handlerPc;
        private final int catchType;
        
        public ExceptionTableEntry(int startPc, int endPc, int handlerPc, int catchType) {
            this.startPc = startPc;
            this.endPc = endPc;
            this.handlerPc = handlerPc;
            this.catchType = catchType;
        }
        
        // Getters
        public int getStartPc() { return startPc; }
        public int getEndPc() { return endPc; }
        public int getHandlerPc() { return handlerPc; }
        public int getCatchType() { return catchType; }
    }
    
    /**
     * LineNumberTable attribute - maps bytecode offsets to source line numbers
     */
    public static class LineNumberTableAttribute extends AttributeInfo {
        private LineNumberTableEntry[] lineNumberTable;
        
        public static LineNumberTableAttribute parse(int nameIndex, byte[] info, ConstantPool cp) {
            LineNumberTableAttribute attr = new LineNumberTableAttribute();
            attr.nameIndex = nameIndex;
            
            ByteBuffer buffer = ByteBuffer.wrap(info).order(ByteOrder.BIG_ENDIAN);
            int lineNumberTableLength = buffer.getShort() & 0xFFFF;
            attr.lineNumberTable = new LineNumberTableEntry[lineNumberTableLength];
            
            for (int i = 0; i < lineNumberTableLength; i++) {
                int startPc = buffer.getShort() & 0xFFFF;
                int lineNumber = buffer.getShort() & 0xFFFF;
                attr.lineNumberTable[i] = new LineNumberTableEntry(startPc, lineNumber);
            }
            
            return attr;
        }
    }
    
    /**
     * Represents an entry in the line number table
     */
    public static class LineNumberTableEntry {
        private final int startPc;
        private final int lineNumber;
        
        public LineNumberTableEntry(int startPc, int lineNumber) {
            this.startPc = startPc;
            this.lineNumber = lineNumber;
        }
        
        // Getters
        public int getStartPc() { return startPc; }
        public int getLineNumber() { return lineNumber; }
    }
    
    /**
     * SourceFile attribute - contains the name of the source file
     */
    public static class SourceFileAttribute extends AttributeInfo {
        private int sourceFileIndex;
        
        public static SourceFileAttribute parse(int nameIndex, byte[] info, ConstantPool cp) {
            SourceFileAttribute attr = new SourceFileAttribute();
            attr.nameIndex = nameIndex;
            
            ByteBuffer buffer = ByteBuffer.wrap(info).order(ByteOrder.BIG_ENDIAN);
            attr.sourceFileIndex = buffer.getShort() & 0xFFFF;
            
            return attr;
        }
        
        public int getSourceFileIndex() { return sourceFileIndex; }
    }
}
