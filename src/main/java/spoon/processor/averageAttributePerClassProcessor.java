package spoon.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.*;

public class averageAttributePerClassProcessor extends AbstractProcessor<CtClass<?>> {
    static int counter = 0;
    static int counter2 = 0;

    @Override
    public void process(CtClass<?> ctclass) {
        counter++;
        for (CtField<?> field : ctclass.getFields()) {
            counter2++;
        }
    }

    @Override
    public void processingDone() {
        System.out.println("=== Résumé de l'analyse averageAttributePerClassProcessor===");
        System.out.println("Nombre d'attribut par classe : " + (float)counter2/counter);
    }
}


