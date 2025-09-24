package spoon.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtPackage;

public class nbPackageProcessor extends AbstractProcessor<CtPackage> {
    static int counter = 0;

    @Override
    public void process(CtPackage ctPackage) {
        counter++;
    }

    @Override
    public void processingDone() {
        System.out.println("=== Résumé de l'analyse ===");
        System.out.println("Nombre de packages : " + counter);
    }
}
