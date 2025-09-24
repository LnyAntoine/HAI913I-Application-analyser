package spoon.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;

public class averageLinePerMethodProcessor extends AbstractProcessor<CtMethod<?>> {
    static int counter = 0;
    static int counter2 = 0;

    @Override
    public void process(CtMethod<?> method) {
        counter++;
        if (method.getPosition().isValidPosition()) {
            int start = method.getPosition().getLine();
            int end = method.getPosition().getEndLine();
            counter2 += (end - start + 1);
        }
    }

    @Override
    public void processingDone() {
        System.out.println("=== Résumé de l'analyse averageLinePerMethodProcessor===");
        System.out.println("Nombre de ligne par methode : " + (float)counter2/counter);
    }
}

