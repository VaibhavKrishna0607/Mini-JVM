package jvm;

import java.util.Stack;

public class MiniJVM {
    private Stack<Integer> operandStack = new Stack<>();

    public void execute(String[] bytecode){
        System.out.println("[MiniJVM] Starting execution");
        for(String instruction: bytecode){
            System.out.println("[MiniJVM] -> Instruction: "+instruction);
            String[] parts = instruction.split(" ");
            String opcpde = parts[0];

            switch(opcode){
            case "LOAD":
                int value = Integer.parseInt(parts[1]);
                operandStack.push(value);
                System.out.println("[Stack] -> Pushed "+value+", Stack: "+operandStack);
                break;
            case "ADD":
                int b = operandStack.pop();
                int a = operandStack.pop();
                int result = a+b;
                operandStack.push(result);
                System.out.println("[Stack] -> Popped "+a+" and "+ b+", Pushed result: "+result);
                break;
            case "PRINT": 
                System.out.println("[Output]-> "+operandStack.peek());
                break;
            default:
                System.out.println("[MiniJVM] Unknown instruction: "+opcode);
            }
        }
        System.out.println("[MiniJVM] Execution complete.");
    }
}

    