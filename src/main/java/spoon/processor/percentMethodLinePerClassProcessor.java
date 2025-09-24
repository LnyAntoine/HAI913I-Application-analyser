package spoon.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.*;

import java.util.*;
import java.util.stream.Collectors;

public class percentMethodLinePerClassProcessor extends AbstractProcessor<CtClass<?>> {
    static int counterMethod = 0;
    static int counterAttribute = 0;
    static Map<String, Map<String, Integer>> mapMethodClass = new HashMap<>();
    static Map<String, ArrayList<String>> mapArrayClass = new HashMap<>();


    @Override
    public void process(CtClass<?> ctclass) {
        String className = ctclass.getSimpleName();
        mapMethodClass.put(className,new HashMap<>());
        for (CtMethod<?> method : ctclass.getMethods()) {
            String methodName = method.getSimpleName();
            if (method.getPosition().isValidPosition()) {
                int start = method.getPosition().getLine();
                int end = method.getPosition().getEndLine();
                mapMethodClass.get(className).put(
                        methodName,
                        (end - start + 1));
            }
        }
    }

    @Override
    public void processingDone() {
        mapArrayClass = transformer(mapMethodClass);

        Map<String, ArrayList<String>> result = new HashMap<>();

        for (Map.Entry<String, ArrayList<String>> entry : mapArrayClass.entrySet()) {
            String className = entry.getKey();
            ArrayList<String> arrayList = entry.getValue();
            result.put(className,new ArrayList<>());
            int n = arrayList.size();
            int nbTop = Math.max(1, (int) Math.ceil(n * 0.1));
            if (arrayList.isEmpty()) {
                // Rien à ajouter, passer à la classe suivante
                continue;
            }
            for (int i = 0; i < nbTop; i++) {
                result.get(className).add(arrayList.get(i));
            }
        }
        System.out.println("=== Résumé de l'analyse percentMethodLinePerClassProcessor===");
        for (Map.Entry<String, ArrayList<String>> entry : result.entrySet()) {
            System.out.println(entry.getKey() + ": ");
            for (String methodName : entry.getValue()) {
                System.out.println("\t"+methodName + " : "+mapMethodClass.get(entry.getKey()).get(methodName));
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


    public static Map<String, ArrayList<String>> transformer(
            Map<String, Map<String, Integer>> classesEtMethodes) {

        Map<String, ArrayList<String>> resultat = new HashMap<>();

        for (Map.Entry<String, Map<String, Integer>> entreeClasse : classesEtMethodes.entrySet()) {
            String nomClasse = entreeClasse.getKey();
            Map<String, Integer> methodes = entreeClasse.getValue();

            // Convertir les entrées en liste
            List<Map.Entry<String, Integer>> listeMethodes = new ArrayList<>(methodes.entrySet());

            // Tri manuel sans Comparator (valeur décroissante, puis nom de méthode)
            Collections.sort(listeMethodes, (e1, e2) -> {
                if (e1.getValue() > e2.getValue()) return -1;
                if (e1.getValue() < e2.getValue()) return 1;
                return e1.getKey().compareTo(e2.getKey());
            });

            // Extraire uniquement les noms des méthodes dans l’ordre
            ArrayList<String> nomsOrdonnes = new ArrayList<>();
            for (Map.Entry<String, Integer> e : listeMethodes) {
                nomsOrdonnes.add(e.getKey());
            }

            // Mettre dans le résultat
            resultat.put(nomClasse, nomsOrdonnes);
        }

        return resultat;
    }
}


