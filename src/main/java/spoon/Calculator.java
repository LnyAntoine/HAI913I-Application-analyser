package spoon;

import spoon.visitor.calculatorVisitor;

import java.util.*;
import java.util.stream.Collectors;

public class Calculator {
    private final calculatorVisitor visitor;
    private final Map<String, String> methodMessages = new HashMap<>();

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
        String msg = "Nombre de classes : " + visitor.getTotalClassesCounter();
        System.out.println(msg);
        methodMessages.put("calculateNbClass", msg);
    }

    public void calculateNbLine(){
        String msg = "Nombre de lignes : " + visitor.getTotalLineCounter();
        System.out.println(msg);
        methodMessages.put("calculateNbLine", msg);
    }

    public void calculateNbMethod(){
        String msg = "Nombre de méthodes : " + visitor.getTotalMethodsCounter();
        System.out.println(msg);
        methodMessages.put("calculateNbMethod", msg);
    }

    public void calculateNbPackage(){
        String msg = "Nombre de packages : " + visitor.getTotalPackageCounter();
        System.out.println(msg);
        methodMessages.put("calculateNbPackage", msg);
    }

    public void calculateAverageMethodPerClass(){
        String msg;
        if (visitor.getTotalClassesCounter() == 0) {
            msg = "Moyenne de méthodes par classe : 0";
            System.out.println(msg);
            methodMessages.put("calculateAverageMethodPerClass", msg);
            return;
        }
        double average = (double) visitor.getTotalMethodsCounter() / visitor.getTotalClassesCounter();
        msg = "Moyenne de méthodes par classe : " + average;
        System.out.println(msg);
        methodMessages.put("calculateAverageMethodPerClass", msg);
    }
    public void calculateAverageLinePerMethod(){
        String msg;
        if (visitor.getTotalMethodsCounter() == 0) {
            msg = "Moyenne de lignes par méthode : 0";
            System.out.println(msg);
            methodMessages.put("calculateAverageLinePerMethod", msg);
            return;
        }
        double average = (double) visitor.getTotalMethodLineCounter() / visitor.getTotalMethodsCounter();
        msg = "Moyenne de lignes par méthode : " + average;
        System.out.println(msg);
        methodMessages.put("calculateAverageLinePerMethod", msg);
    }
    public void calculateAverageAttributePerClass(){
        String msg;
        if (visitor.getTotalClassesCounter() == 0) {
            msg = "Moyenne d'attributs par classe : 0";
            System.out.println(msg);
            methodMessages.put("calculateAverageAttributePerClass", msg);
            return;
        }
        double average = (double) visitor.getTotalFieldsCounter() / visitor.getTotalClassesCounter();
        msg = "Moyenne d'attributs par classe : " + average;
        System.out.println(msg);
        methodMessages.put("calculateAverageAttributePerClass", msg);
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
        StringBuilder sb = new StringBuilder();
        sb.append("Top 10% des classes avec le plus de méthodes :\n");
        for (String s : result) {
            sb.append(s).append(" ").append(map.get(s)).append("\n");
        }
        String msg = sb.toString();
        System.out.print(msg);
        methodMessages.put("calculateTop10PercentNbMethodPerClass", msg);
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
        StringBuilder sb = new StringBuilder();
        sb.append("Top 10% des classes avec le plus d'attributs :\n");
        for (String s : result) {
            sb.append(s).append(" ").append(map.get(s)).append("\n");
        }
        String msg = sb.toString();
        System.out.print(msg);
        methodMessages.put("calculateTop10PercentNbAttributePerClass", msg);
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

        StringBuilder sb = new StringBuilder();
        sb.append("Top 10% des classes avec le plus d'attributs et de méthodes :\n");
        for (String s : resultMethod) {
            if (resultAttribute.contains(s)) {
                sb.append(s).append(" - Nb d'attributs : ").append(mapAttribute.get(s)).append(" - Nb de méthodes :").append(mapMethod.get(s)).append("\n");
            }
        }
        String msg = sb.toString();
        System.out.print(msg);
        methodMessages.put("calculateTop10PercentNbAttributeMethodePerClass", msg);
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
        StringBuilder sb = new StringBuilder();
        sb.append("Classes avec plus de ").append(x).append(" méthodes :\n");
        for (String s : result) {
            sb.append(s).append(" ").append(map.get(s)).append("\n");
        }
        String msg = sb.toString();
        System.out.print(msg);
        methodMessages.put("moreThanXMethods", msg);
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

        StringBuilder sb = new StringBuilder();
        sb.append("Les méthodes pour chaque classes avec le plus de lignes (top 10%) :\n");
        for (Map.Entry<String, ArrayList<String>> entry : result.entrySet()) {
            sb.append(entry.getKey()).append(": ").append("\n");
            for (String methodName : entry.getValue()) {
                sb.append("\t").append(methodName).append(" : ").append(map.get(entry.getKey()).get(methodName)).append("\n");
            }
        }
        String msg = sb.toString();
        System.out.print(msg);
        methodMessages.put("calculateTop10PercentMethodPerLinPerClass", msg);
    }

    public void maxParameterMethod(){
        String msg = "Le nombre maximum de paramètres dans une méthode : " + visitor.getMaxParameterCounter();
        System.out.println(msg);
        methodMessages.put("maxParameterMethod", msg);
    }

    public String getMessageForMethod(String methodName) {
        return methodMessages.getOrDefault(methodName, "");
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
