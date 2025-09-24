package spoon.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;

import java.util.HashMap;

public class moreThanXMethodProcessor extends AbstractProcessor<CtClass<?>> {
    HashMap<String,Integer> nbParamMap = new HashMap<>();
    int param = 0;
    public void setParam(int param) {
        this.param = param;
    }

    @Override
    public void process(CtClass<?> ctClass) {
        if (ctClass.getMethods().size()>param){
            nbParamMap.put(ctClass.getSimpleName(),ctClass.getMethods().size());
        }
    }
    @Override
    public void processingDone() {
        System.out.println("=== Résumé de l'analyse moreThanXMethodProcessor===");
        for (String k : nbParamMap.keySet()){
            System.out.println(k+" : "+nbParamMap.get(k));
        }
    }
}