package spoon.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.*;

import java.util.*;
import java.util.stream.Collectors;

public class percentClassMethodProcessor extends AbstractProcessor<CtClass<?>> {
    static int counter2 = 0;
    static HashMap<String,Integer> map = new HashMap<String,Integer>();
    static ArrayList<String> array = new ArrayList<>();

    @Override
    public void process(CtClass<?> ctclass) {
        for (CtMethod<?> method : ctclass.getMethods()) {
            counter2++;
        }
        map.put(ctclass.getSimpleName(),counter2);
        counter2 = 0;
    }

    @Override
    public void processingDone() {
        array = trierParValeurCroissant(map);
        int n = map.size();
        int nbTop = Math.max(1, (int) Math.ceil(n * 0.9));

        List<String> result = new ArrayList<>();
        for (int i = array.size()-1; i >= nbTop; i--) {
            result.add(array.get(i));
        }

        System.out.println("=== Résumé de l'analyse percentClassMethodProcessor===");
        for (String s : result) {
            System.out.println(s + " " + map.get(s));
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


