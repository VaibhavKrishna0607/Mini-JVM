import jvm.MiniJVM;

public class Main {
    public static void main(String[] args) {
        String[] bytecode = {
                "LOAD 5",
                "LOAD 10",
                "ADD",
                "PRINT"
        };

        MiniJVM jvm = new MiniJVM();
        jvm.execute(bytecode);
    }
}