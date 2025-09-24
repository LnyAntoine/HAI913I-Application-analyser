package spoon.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;

public class nbClassProcessor extends AbstractProcessor<CtClass<?>> {
    static int counter = 0;
    @Override
    public void process(CtClass<?> ctClass) {
        counter++;
    }
    @Override
    public void processingDone() {
        System.out.println("=== Résumé de l'analyse ===");
        System.out.println("Nombre de classes : " + counter);
    }
}