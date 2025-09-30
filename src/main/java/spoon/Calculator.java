package spoon;

import spoon.visitor.calculatorVisitor;

import java.util.*;
import java.util.stream.Collectors;

public class Calculator {
    private final calculatorVisitor visitor;

    public void setX(int x) {
        this.x = x;
    }

    private int x = 0;
    public Calculator(calculatorVisitor visitor) {
        this.visitor = visitor;
    }
    public void calculateAll() {
        calculateNbClass();
        calculateNbLine();
        calculateNbMethod();
        calculateNbPackage();
        calculateAverageMethodPerClass();
        calculateAverageLinePerMethod();
        calculateAverageAttributePerClass();
        calculateTop10PercentNbMethodPerClass();
        calculateTop10PercentNbAttributePerClass();
        calculateTop10PercentNbAttributeMethodePerClass();
        moreThanXMethods();
        calculateTop10PercentMethodPerLinPerClass();
        maxParameterMethod();
    }
    public void calculateNbClass(){
        System.out.println("Nombre de classes : " + visitor.getTotalClassesCounter());
    }

    public void calculateNbLine(){
        System.out.println("Nombre de lignes : " + visitor.getTotalLineCounter());
    }

    public void calculateNbMethod(){
        System.out.println("Nombre de méthodes : " + visitor.getTotalMethodsCounter());
    }

    public void calculateNbPackage(){
        System.out.println("Nombre de packages : " + visitor.getTotalPackageCounter());
    }

    public void calculateAverageMethodPerClass(){
        if (visitor.getTotalClassesCounter() == 0) {
            System.out.println("Moyenne de méthodes par classe : 0");
            return;
        }
        double average = (double) visitor.getTotalMethodsCounter() / visitor.getTotalClassesCounter();
        System.out.println("Moyenne de méthodes par classe : " + average);
    }
    public void calculateAverageLinePerMethod(){
        if (visitor.getTotalMethodsCounter() == 0) {
            System.out.println("Moyenne de lignes par méthode : 0");
            return;
        }
        double average = (double) visitor.getTotalMethodLineCounter() / visitor.getTotalMethodsCounter();
        System.out.println("Moyenne de lignes par méthode : " + average);
    }
    public void calculateAverageAttributePerClass(){
        if (visitor.getTotalClassesCounter() == 0) {
            System.out.println("Moyenne d'attributs par classe : 0");
            return;
        }
        double average = (double) visitor.getTotalFieldsCounter() / visitor.getTotalClassesCounter();
        System.out.println("Moyenne d'attributs par classe : " + average);
    }
    public void calculateTop10PercentNbMethodPerClass(){
        HashMap<String, Integer> map = visitor.getClassMethodCountMap();
        ArrayList<String> array = trierParValeurCroissant(map);
        int n = map.size();
        int nbTop = Math.max(1, (int) Math.ceil(n * 0.9));

        List<String> result = new ArrayList<>();
        for (int i = array.size()-1; i >= nbTop; i--) {
            result.add(array.get(i));
        }
        System.out.println("Top 10% des classes avec le plus de méthodes :");
        for (String s : result) {
            System.out.println(s + " " + map.get(s));
        }
    }

    public void calculateTop10PercentNbAttributePerClass(){
        HashMap<String, Integer> map = visitor.getClassAttributeCountMap();
        ArrayList<String> array = trierParValeurCroissant(map);
        int n = map.size();
        int nbTop = Math.max(1, (int) Math.ceil(n * 0.9));
        List<String> result = new ArrayList<>();
        for (int i = array.size()-1; i >= nbTop; i--) {
            result.add(array.get(i));
        }
        System.out.println("Top 10% des classes avec le plus d'attributs :");
        for (String s : result) {
            System.out.println(s + " " + map.get(s));
        }
    }

    public void calculateTop10PercentNbAttributeMethodePerClass(){
        HashMap<String, Integer> mapMethod = visitor.getClassMethodCountMap();
        HashMap<String, Integer> mapAttribute = visitor.getClassAttributeCountMap();
        ArrayList<String> arrayMethod = trierParValeurCroissant(mapMethod);
        int n = mapMethod.size();
        int nbTop = Math.max(1, (int) Math.ceil(n * 0.9));

        List<String> resultMethod = new ArrayList<>();
        for (int i = arrayMethod.size()-1; i >= nbTop; i--) {
            resultMethod.add(arrayMethod.get(i));
        }
        ArrayList<String> arrayAttribute = trierParValeurCroissant(mapAttribute);
        int nAttribute = mapAttribute.size();
        int nbTopAttribute = Math.max(1, (int) Math.ceil(nAttribute * 0.9));

        List<String> resultAttribute = new ArrayList<>();
        for (int i = arrayAttribute.size()-1; i >= nbTopAttribute; i--) {
            resultAttribute.add(arrayAttribute.get(i));
        }

        System.out.println("Top 10% des classes avec le plus d'attributs et de méthodes :");
        for (String s : resultMethod) {
            if (resultAttribute.contains(s)) {
                System.out.println(s + " - Nb d'attributs : " + mapAttribute.get(s) + " - Nb de méthodes :" + mapMethod.get(s));
            }
        }
    }

    public void moreThanXMethods(){
        HashMap<String, Integer> map = visitor.getClassMethodCountMap();
        ArrayList<String> array = trierParValeurCroissant(map);
        List<String> result = new ArrayList<>();
        for (String s : array) {
            if (map.get(s) > x) {
                result.add(s);
            }
        }
        System.out.println("Classes avec plus de " + x + " méthodes :");
        for (String s : result) {
            System.out.println(s + " " + map.get(s));
        }
    }

    public void calculateTop10PercentMethodPerLinPerClass(){
        HashMap<String, HashMap<String, Integer>> map = visitor.getLinePerMethodPerClass();

        HashMap<String, ArrayList<String>> mapArrayClass = transformer(map);

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

        System.out.println("Les méthodes pour chaque classes avec le plus de lignes (top 10%) :");
        for (Map.Entry<String, ArrayList<String>> entry : result.entrySet()) {
            System.out.println(entry.getKey() + ": ");
            for (String methodName : entry.getValue()) {
                System.out.println("\t"+methodName + " : "+map.get(entry.getKey()).get(methodName));
            }
        }
    }

    public void maxParameterMethod(){
        System.out.println("Le nombre maximum de paramètres dans une méthode : " + visitor.getMaxParameterCounter());
    }

    public ArrayList<String> trierParValeurCroissant(Map<String, Integer> map) {
        return map.entrySet().stream()
                .sorted(
                        Comparator.comparing(Map.Entry<String, Integer>::getValue)
                                .thenComparing(Map.Entry::getKey)
                )
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public HashMap<String, ArrayList<String>> transformer(
            HashMap<String, HashMap<String, Integer>> classesEtMethodes) {

        HashMap<String, ArrayList<String>> resultat = new HashMap<>();

        for (HashMap.Entry<String, HashMap<String, Integer>> entreeClasse : classesEtMethodes.entrySet()) {
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
