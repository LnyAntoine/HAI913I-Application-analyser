package spoon.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;

public class maxParametersProcessor extends AbstractProcessor<CtMethod<?>> {
    static int MaxParamCounter = 0;
    static String methodName = "";
    @Override
    public void process(CtMethod<?> ctMethod) {
        if (ctMethod.getParameters().size() > MaxParamCounter) {
            MaxParamCounter = ctMethod.getParameters().size();
            methodName = ctMethod.getSimpleName();
        }
    }
    @Override
    public void processingDone() {
        System.out.println("=== Résumé de l'analyse ===");
        System.out.println("Nombre de paramètres max : " + MaxParamCounter +" : " + methodName);
    }
}