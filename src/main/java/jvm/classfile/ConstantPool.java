package jvm.classfile;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Represents the constant pool of a Java class file.
 * The constant pool is a table of structures representing various string constants,
 * class and interface names, field names, and other constants.
 */
public class ConstantPool {
    private final ConstantInfo[] constantPool;
    private final int size;

    private ConstantPool(int size) {
        this.size = size;
        this.constantPool = new ConstantInfo[size];
    }

    /**
     * Parse the constant pool from a DataInputStream
     */
    public static ConstantPool parse(DataInputStream in) throws IOException {
        int constantPoolCount = in.readUnsignedShort();
        ConstantPool constantPool = new ConstantPool(constantPoolCount);
        
        // The constant pool is indexed from 1 to constant_pool_count-1
        for (int i = 1; i < constantPoolCount; i++) {
            int tag = in.readUnsignedByte();
            ConstantInfo info = ConstantInfo.create(tag, in);
            constantPool.constantPool[i] = info;
            
            // Long and Double constants take up two slots in the constant pool
            if (tag == ConstantInfo.CONSTANT_Long || tag == ConstantInfo.CONSTANT_Double) {
                i++;
            }
        }
        
        return constantPool;
    }
    
    /**
     * Get a constant pool entry by index
     */
    public ConstantInfo get(int index) {
        if (index <= 0 || index >= size) {
            throw new IllegalArgumentException("Invalid constant pool index: " + index);
        }
        return constantPool[index];
    }
    
    /**
     * Get a UTF-8 string from the constant pool
     */
    public String getUtf8String(int index) {
        ConstantInfo info = get(index);
        if (info.getTag() != ConstantInfo.CONSTANT_Utf8) {
            throw new ClassFormatError("Expected UTF-8 constant at index " + index);
        }
        return ((ConstantUtf8Info) info).getValue();
    }
    
    /**
     * Get a class name from the constant pool
     */
    public String getClassName(int classIndex) {
        ConstantInfo info = get(classIndex);
        if (info.getTag() != ConstantInfo.CONSTANT_Class) {
            throw new ClassFormatError("Expected class constant at index " + classIndex);
        }
        int nameIndex = ((ConstantClassInfo) info).getNameIndex();
        return getUtf8String(nameIndex);
    }
    
    /**
     * Get the size of the constant pool
     */
    public int size() {
        return size;
    }
    
    /**
     * Base class for all constant pool entries
     */
    public abstract static class ConstantInfo {
        // Constant pool tags
        public static final int CONSTANT_Class = 7;
        public static final int CONSTANT_Fieldref = 9;
        public static final int CONSTANT_Methodref = 10;
        public static final int CONSTANT_InterfaceMethodref = 11;
        public static final int CONSTANT_String = 8;
        public static final int CONSTANT_Integer = 3;
        public static final int CONSTANT_Float = 4;
        public static final int CONSTANT_Long = 5;
        public static final int CONSTANT_Double = 6;
        public static final int CONSTANT_NameAndType = 12;
        public static final int CONSTANT_Utf8 = 1;
        public static final int CONSTANT_MethodHandle = 15;
        public static final int CONSTANT_MethodType = 16;
        public static final int CONSTANT_InvokeDynamic = 18;
        
        protected final int tag;
        
        protected ConstantInfo(int tag) {
            this.tag = tag;
        }
        
        public int getTag() {
            return tag;
        }
        
        /**
         * Factory method to create the appropriate constant pool entry
         */
        public static ConstantInfo create(int tag, DataInputStream in) throws IOException {
            return switch (tag) {
                case CONSTANT_Class -> new ConstantClassInfo(in);
                case CONSTANT_Fieldref -> new ConstantFieldrefInfo(in);
                case CONSTANT_Methodref -> new ConstantMethodrefInfo(in);
                case CONSTANT_InterfaceMethodref -> new ConstantInterfaceMethodrefInfo(in);
                case CONSTANT_String -> new ConstantStringInfo(in);
                case CONSTANT_Integer -> new ConstantIntegerInfo(in);
                case CONSTANT_Float -> new ConstantFloatInfo(in);
                case CONSTANT_Long -> new ConstantLongInfo(in);
                case CONSTANT_Double -> new ConstantDoubleInfo(in);
                case CONSTANT_NameAndType -> new ConstantNameAndTypeInfo(in);
                case CONSTANT_Utf8 -> new ConstantUtf8Info(in);
                case CONSTANT_MethodHandle -> new ConstantMethodHandleInfo(in);
                case CONSTANT_MethodType -> new ConstantMethodTypeInfo(in);
                case CONSTANT_InvokeDynamic -> new ConstantInvokeDynamicInfo(in);
                default -> throw new ClassFormatError("Unknown constant pool tag: " + tag);
            };
        }
    }
    
    // Constant pool entry implementations
    
    public static class ConstantClassInfo extends ConstantInfo {
        private final int nameIndex;
        
        public ConstantClassInfo(DataInputStream in) throws IOException {
            super(CONSTANT_Class);
            this.nameIndex = in.readUnsignedShort();
        }
        
        public int getNameIndex() {
            return nameIndex;
        }
    }
    
    public static class ConstantFieldrefInfo extends ConstantInfo {
        private final int classIndex;
        private final int nameAndTypeIndex;
        
        public ConstantFieldrefInfo(DataInputStream in) throws IOException {
            super(CONSTANT_Fieldref);
            this.classIndex = in.readUnsignedShort();
            this.nameAndTypeIndex = in.readUnsignedShort();
        }
        
        public int getClassIndex() {
            return classIndex;
        }
        
        public int getNameAndTypeIndex() {
            return nameAndTypeIndex;
        }
    }
    
    public static class ConstantMethodrefInfo extends ConstantInfo {
        private final int classIndex;
        private final int nameAndTypeIndex;
        
        public ConstantMethodrefInfo(DataInputStream in) throws IOException {
            super(CONSTANT_Methodref);
            this.classIndex = in.readUnsignedShort();
            this.nameAndTypeIndex = in.readUnsignedShort();
        }
        
        public int getClassIndex() {
            return classIndex;
        }
        
        public int getNameAndTypeIndex() {
            return nameAndTypeIndex;
        }
    }
    
    public static class ConstantInterfaceMethodrefInfo extends ConstantInfo {
        private final int classIndex;
        private final int nameAndTypeIndex;
        
        public ConstantInterfaceMethodrefInfo(DataInputStream in) throws IOException {
            super(CONSTANT_InterfaceMethodref);
            this.classIndex = in.readUnsignedShort();
            this.nameAndTypeIndex = in.readUnsignedShort();
        }
        
        public int getClassIndex() {
            return classIndex;
        }
        
        public int getNameAndTypeIndex() {
            return nameAndTypeIndex;
        }
    }
    
    public static class ConstantStringInfo extends ConstantInfo {
        private final int stringIndex;
        
        public ConstantStringInfo(DataInputStream in) throws IOException {
            super(CONSTANT_String);
            this.stringIndex = in.readUnsignedShort();
        }
        
        public int getStringIndex() {
            return stringIndex;
        }
    }
    
    public static class ConstantIntegerInfo extends ConstantInfo {
        private final int bytes;
        
        public ConstantIntegerInfo(DataInputStream in) throws IOException {
            super(CONSTANT_Integer);
            this.bytes = in.readInt();
        }
        
        public int getValue() {
            return bytes;
        }
    }
    
    public static class ConstantFloatInfo extends ConstantInfo {
        private final float value;
        
        public ConstantFloatInfo(DataInputStream in) throws IOException {
            super(CONSTANT_Float);
            this.value = Float.intBitsToFloat(in.readInt());
        }
        
        public float getValue() {
            return value;
        }
    }
    
    public static class ConstantLongInfo extends ConstantInfo {
        private final long value;
        
        public ConstantLongInfo(DataInputStream in) throws IOException {
            super(CONSTANT_Long);
            this.value = in.readLong();
        }
        
        public long getValue() {
            return value;
        }
    }
    
    public static class ConstantDoubleInfo extends ConstantInfo {
        private final double value;
        
        public ConstantDoubleInfo(DataInputStream in) throws IOException {
            super(CONSTANT_Double);
            this.value = Double.longBitsToDouble(in.readLong());
        }
        
        public double getValue() {
            return value;
        }
    }
    
    public static class ConstantNameAndTypeInfo extends ConstantInfo {
        private final int nameIndex;
        private final int descriptorIndex;
        
        public ConstantNameAndTypeInfo(DataInputStream in) throws IOException {
            super(CONSTANT_NameAndType);
            this.nameIndex = in.readUnsignedShort();
            this.descriptorIndex = in.readUnsignedShort();
        }
        
        public int getNameIndex() {
            return nameIndex;
        }
        
        public int getDescriptorIndex() {
            return descriptorIndex;
        }
    }
    
    public static class ConstantUtf8Info extends ConstantInfo {
        private final String value;
        
        public ConstantUtf8Info(DataInputStream in) throws IOException {
            super(CONSTANT_Utf8);
            int length = in.readUnsignedShort();
            byte[] bytes = new byte[length];
            in.readFully(bytes);
            this.value = new String(bytes, StandardCharsets.UTF_8);
        }
        
        public String getValue() {
            return value;
        }
    }
    
    public static class ConstantMethodHandleInfo extends ConstantInfo {
        private final int referenceKind;
        private final int referenceIndex;
        
        public ConstantMethodHandleInfo(DataInputStream in) throws IOException {
            super(CONSTANT_MethodHandle);
            this.referenceKind = in.readUnsignedByte();
            this.referenceIndex = in.readUnsignedShort();
        }
        
        public int getReferenceKind() {
            return referenceKind;
        }
        
        public int getReferenceIndex() {
            return referenceIndex;
        }
    }
    
    public static class ConstantMethodTypeInfo extends ConstantInfo {
        private final int descriptorIndex;
        
        public ConstantMethodTypeInfo(DataInputStream in) throws IOException {
            super(CONSTANT_MethodType);
            this.descriptorIndex = in.readUnsignedShort();
        }
        
        public int getDescriptorIndex() {
            return descriptorIndex;
        }
    }
    
    public static class ConstantInvokeDynamicInfo extends ConstantInfo {
        private final int bootstrapMethodAttrIndex;
        private final int nameAndTypeIndex;
        
        public ConstantInvokeDynamicInfo(DataInputStream in) throws IOException {
            super(CONSTANT_InvokeDynamic);
            this.bootstrapMethodAttrIndex = in.readUnsignedShort();
            this.nameAndTypeIndex = in.readUnsignedShort();
        }
        
        public int getBootstrapMethodAttrIndex() {
            return bootstrapMethodAttrIndex;
        }
        
        public int getNameAndTypeIndex() {
            return nameAndTypeIndex;
        }
    }
}
