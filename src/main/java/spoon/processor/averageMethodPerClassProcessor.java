package spoon.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

public class averageMethodPerClassProcessor extends AbstractProcessor<CtClass<?>> {
    static int counter = 0;
    static int counter2 = 0;

    @Override
    public void process(CtClass<?> ctClass) {
        counter++;
        for (CtMethod<?> method : ctClass.getMethods()) {
            counter2++;
        }
    }

    @Override
    public void processingDone() {
        System.out.println("=== Résumé de l'analyse averageMethodPerClassProcessor===");
        System.out.println("Nombre de methode par classe : " + (float)counter2/counter);
    }
}
