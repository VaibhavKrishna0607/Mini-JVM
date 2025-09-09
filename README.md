# Mini JVM

## Overview
The Mini JVM project is a simplified Java Virtual Machine implementation. It demonstrates the core concepts of memory management, garbage collection, and bytecode execution. The project includes essential components such as a garbage collector, memory manager, and the main execution engine and can be integrated with backend projects such as GenAI and Java Springboot. Mini JVM is built with a focus on low-level execution modeling, modular runtime orchestration, and explicit memory lifecycle control. Each subsystem—from stack frames to garbage collection—is designed to mirror real-world runtime environments, making the architecture adaptable to plugin-based systems and cross-platform backends.

## Project Structure
```
mini-jvm/
├── src/
│   └── main/
│       └── java/
│           ├── jvm/
│           │   └── MiniJVM.java              # Bytecode interpreter
│           ├── memory/
│           │   └── MemoryManager.java        # Heap simulation
│           ├── gc/
│           │   └── GarbageCollector.java     # Mark-and-sweep GC
│           ├── classloader/
│           │   └── ClassLoader.java          # Simulated class loading
│           ├── runtime/
│           │   ├── StackFrame.java           # Method call context
│           │   ├── ConstantPool.java         # Symbol table
│           │   ├── InstructionSet.java       # Opcode registry
│           │   ├── MethodArea.java           # Class metadata (optional)
│           │   └── PCRegister.java           # Instruction pointer (optional)
│           ├── util/
│           │   └── Logger.java               # Centralized trace logging
│           ├── native/
│           │   └── NativeInterface.java      # JNI simulation (optional)
│           ├── debug/
│           │   └── Debugger.java             # Step-through execution (optional)
│           └── Main.java                     # Entry point
├── pom.xml                                   # Maven build config
├── .gitignore                                # Ignore build artifacts
└── README.md                                 # Project overview

## Components
- **MiniJVM.java**: Executes bytecode instructions and manages control flow
- **MemoryManager.java**: Allocates and tracks heap objects
- **GarbageCollector.java**: Implements mark-and-sweep collection
- **Debugger.java**: Enables step-through execution and runtime inspection
- **Logger.java**: Centralized logging for traceability and diagnostics



## Skills Demonstrated
- Object-oriented design and modular architecture
- Manual memory management and garbage collection
- Bytecode interpretation and runtime simulation
- Logging and debugging infrastructure
- Maven-based build and dependency management


## Setup  
To build and run the Mini JVM:  
```bash
git clone <repository-url>
cd mini-jvm
mvn clean install

## Usage Guidelines
To run the Mini JVM, execute the `Main` class. You can provide Java bytecode files as input for execution. The Mini JVM will handle memory management and garbage collection automatically.



