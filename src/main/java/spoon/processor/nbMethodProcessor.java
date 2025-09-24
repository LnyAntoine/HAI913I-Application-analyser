package spoon.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;

public class nbMethodProcessor extends AbstractProcessor<CtMethod<?>> {
    static int counter = 0;
    @Override
    public void process(CtMethod<?> ctMethod) {
        counter++;
    }
    @Override
    public void processingDone() {
        System.out.println("=== Résumé de l'analyse ===");
        System.out.println("Nombre de methode : " + counter);
    }
}