# Mini JVM

## Overview
The Mini JVM project is a simplified Java Virtual Machine implementation designed for educational purposes. It demonstrates the core concepts of memory management, garbage collection, and bytecode execution. The project includes essential components such as a garbage collector, memory manager, and the main execution engine.

## Project Structure
```
mini-jvm
├── src
│   ├── gc
│   │   └── GarbageCollector.java
│   ├── jvm
│   │   └── MiniJVM.java
│   ├── memory
│   │   └── MemoryManager.java
│   └── Main.java
├── .gitignore
├── pom.xml
└── README.md
```

## Components
- **GarbageCollector.java**: Implements memory management and garbage collection with methods for collecting garbage, marking objects, and sweeping memory.
- **MiniJVM.java**: The main entry point for the Mini JVM, responsible for executing Java bytecode.
- **MemoryManager.java**: Manages memory allocation and deallocation within the Mini JVM.
- **Main.java**: Initializes the Mini JVM and starts the execution process.



## Usage Guidelines
To run the Mini JVM, execute the `Main` class. You can provide Java bytecode files as input for execution. The Mini JVM will handle memory management and garbage collection automatically.

## Contributing
Contributions are welcome! Please submit a pull request or open an issue for any enhancements or bug fixes.

