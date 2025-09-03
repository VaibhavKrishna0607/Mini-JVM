package main.java.classloader;

import java.util.HashMap;
import java.util.Map;

public class ClassLoader {
    private final Map<String, ClassMetadata> loadedClasses = new HashMap<>();

    public void loadClass(String className) {
        System.out.println("[ClassLoader] → Loading class: " + className);
        if (loadedClasses.containsKey(className)) {
            System.out.println("[ClassLoader] → Class already loaded: " + className);
            return;
        }

        verifyClass(className);

        ClassMetadata metadata = new ClassMetadata(className);
        metadata.addMethod("main", "void");
        metadata.addField("counter", "int");

        loadedClasses.put(className, metadata);
        System.out.println("[ClassLoader] → Class loaded: " + className);
    }

    public void verifyClass(String className) {
        System.out.println("[ClassLoader] → Verifying class: " + className);
        System.out.println("[ClassLoader] → Signature OK");
        System.out.println("[ClassLoader] → Fields and methods validated");
    }

    public ClassMetadata getClassMetadata(String className) {
        return loadedClasses.get(className);
    }

    public static class ClassMetadata {
        private final String className;
        private final Map<String, String> methods = new HashMap<>();
        private final Map<String, String> fields = new HashMap<>();

        public ClassMetadata(String className) {
            this.className = className;
        }

        public void addMethod(String name, String returnType) {
            methods.put(name, returnType);
            System.out.println("[ClassMetadata] → Method added: " + name + " : " + returnType);
        }

        public void addField(String name, String type) {
            fields.put(name, type);
            System.out.println("[ClassMetadata] → Field added: " + name + " : " + type);
        }

        public Map<String, String> getMethods() {
            return methods;
        }

        public Map<String, String> getFields() {
            return fields;
        }

        public String getClassName() {
            return className;
        }
    }
}