package spoon;

import spoon.reflect.CtModel;
import spoon.visitor.calculatorVisitor;
import spoon.visitor.callGraphVisitor;

public class spoonPart2 {
    public static void main(String[] args) {

        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        Launcher launcher = new Launcher();
        launcher.addInputResource(System.getProperty("user.dir"));
        launcher.getEnvironment().setNoClasspath(true);
        CtModel model = launcher.buildModel();
        callGraphVisitor callGraphVisitor = new callGraphVisitor();
        calculatorVisitor calculatorVisitor = new calculatorVisitor();
        model.getAllTypes().forEach(type->{
            type.accept(callGraphVisitor);
            type.accept(calculatorVisitor);
        });

        Calculator calculator = new Calculator(calculatorVisitor);
        calculator.setX(3);
        calculator.calculateAll();
        System.out.println(callGraphVisitor.getHashMapToString());
    }
}

