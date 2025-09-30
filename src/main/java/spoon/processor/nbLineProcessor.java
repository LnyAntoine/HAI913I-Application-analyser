package spoon.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtType;

public class nbLineProcessor extends AbstractProcessor<CtType<?>> {
    static int counter = 0;
    @Override
    public void process(CtType<?> CtType) {
        if (CtType.getPosition().isValidPosition()) {
            int start = CtType.getPosition().getLine();
            int end = CtType.getPosition().getEndLine();
            counter += (end - start + 1);
        }
    }
    @Override
    public void processingDone() {
        System.out.println("=== Résumé de l'analyse ===");
        System.out.println("Nombre de lignes : " + counter);
    }

}