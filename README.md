# Mini JVM

## Overview
The Mini JVM project is a simplified Java Virtual Machine implementation designed for educational purposes. It demonstrates the core concepts of memory management, garbage collection, and bytecode execution. The project includes essential components such as a garbage collector, memory manager, and the main execution engine.

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
- **GarbageCollector.java**: Implements memory management and garbage collection with methods for collecting garbage, marking objects, and sweeping memory.
- **MiniJVM.java**: The main entry point for the Mini JVM, responsible for executing Java bytecode.
- **MemoryManager.java**: Manages memory allocation and deallocation within the Mini JVM.
- **Main.java**: Initializes the Mini JVM and starts the execution process.

## Setup Instructions
1. Clone the repository:
   ```
   git clone <repository-url>
   ```
2. Navigate to the project directory:
   ```
   cd mini-jvm
   ```
3. Build the project using Maven:
   ```
   mvn clean install
   ```

## Usage Guidelines
To run the Mini JVM, execute the `Main` class. You can provide Java bytecode files as input for execution. The Mini JVM will handle memory management and garbage collection automatically.

## Contributing
Contributions are welcome! Please submit a pull request or open an issue for any enhancements or bug fixes.

## License
This project is licensed under the MIT License. See the LICENSE file for more details.#
