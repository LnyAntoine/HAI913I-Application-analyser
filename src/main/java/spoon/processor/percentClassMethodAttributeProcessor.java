package spoon.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.*;

import java.util.*;
import java.util.stream.Collectors;

public class percentClassMethodAttributeProcessor extends AbstractProcessor<CtClass<?>> {
    static int counterMethod = 0;
    static int counterAttribute = 0;
    static HashMap<String,Integer> mapMethod = new HashMap<String,Integer>();
    static HashMap<String,Integer> mapAttribute = new HashMap<String,Integer>();
    static ArrayList<String> arrayMethod = new ArrayList<>();
    static ArrayList<String> arrayAttribute = new ArrayList<>();

    @Override
    public void process(CtClass<?> ctclass) {
        for (CtMethod<?> method : ctclass.getMethods()) {
            counterMethod++;
        }
        mapMethod.put(ctclass.getSimpleName(), counterMethod);
        counterMethod = 0;
        for (CtField<?> method : ctclass.getFields()) {
            counterAttribute++;
        }
        mapAttribute.put(ctclass.getSimpleName(), counterAttribute);
        counterAttribute = 0;
    }

    @Override
    public void processingDone() {
        arrayMethod = trierParValeurCroissant(mapMethod);
        int n = mapMethod.size();
        int nbTop = Math.max(1, (int) Math.ceil(n * 0.9));

        List<String> result = new ArrayList<>();
        for (int i = arrayMethod.size()-1; i >= nbTop; i--) {
            result.add(arrayMethod.get(i));
        }
        arrayAttribute = trierParValeurCroissant(mapAttribute);
        int nAttribute = mapAttribute.size();
        int nbTopAttribute = Math.max(1, (int) Math.ceil(nAttribute * 0.9));

        List<String> resultAttribute = new ArrayList<>();
        for (int i = arrayAttribute.size()-1; i >= nbTopAttribute; i--) {
            resultAttribute.add(arrayAttribute.get(i));
        }

        System.out.println("=== Résumé de l'analyse percentClassMethodAttributeProcessor===");
        for (String s : result) {
            if (resultAttribute.contains(s)) {
                System.out.println(s + " " + mapAttribute.get(s) + " " + mapMethod.get(s));
            }
        }
    }

    public static ArrayList<String> trierParValeurCroissant(Map<String, Integer> map) {
        return map.entrySet().stream()
                .sorted(
                        Comparator.comparing(Map.Entry<String, Integer>::getValue)
                                .thenComparing(Map.Entry::getKey)
                )
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}


