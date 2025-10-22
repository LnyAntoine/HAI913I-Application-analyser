package com.example.services;

import com.example.services.visitor.CouplingVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CouplingServices {
    private final CouplingVisitor visitor;
    private HashMap<String, HashMap<String, HashMap<String, Integer>>> couplingGraph;
    private HashMap<String,HashMap<String,Integer>> classCouplingCount;
    private HashMap<String,HashMap<String,Float>> classCouplingNote;

    private int totalcall;
    public CouplingServices(CouplingVisitor visitor) {
        this.visitor = visitor;
    }
    public void generateGraphFilter(ArrayList<String> filters) {
        if (filters == null || filters.isEmpty()) {
            generateGraph();
        } else {
            generateGraph();
        }
    }
    public void generateGraph(){
        totalcall = 0;
        couplingGraph = visitor.getCouplingGraph();
        classCouplingCount = new HashMap<>();
        classCouplingNote = new HashMap<>();
        ArrayList<String> classes = visitor.getClasses();
        //On récupère chaque classe de notre graphe de couplage
        for (String parentClass : couplingGraph.keySet()) {
            HashMap<String, HashMap<String, Integer>> targetClasses = couplingGraph.get(parentClass);
            if (!classCouplingCount.containsKey(parentClass)) {
                classCouplingCount.put(parentClass, new HashMap<String,Integer>());
            }
            //On regarde toutes les classes cibles de la classe parente
            for (String targetClass : targetClasses.keySet()){
                //Si la classe cible est déjà présente dans le graphe de couplage on ne l'analyse pas
                // car le couplage aura déjà été calculé entre ces deux classes
                //TODO probleme ici ?
                //!classCouplingCount.containsKey(targetClass)
                //                        &&
                if (
                        classes.contains(targetClass) &&
                        !Objects.equals(targetClass, parentClass)
                ){
                    if (!classCouplingCount.get(parentClass).containsKey(targetClass)) {
                        classCouplingCount.get(parentClass).put(targetClass, 0);
                    }
                    HashMap<String,Integer> parentMethodCall = targetClasses.get(targetClass)!=null
                            ?targetClasses.get(targetClass)
                            :new HashMap<>();
                    int couplingCall = 0;
                    for (String methodName : parentMethodCall.keySet()){
                        couplingCall += parentMethodCall.get(methodName);
                    }
                    HashMap<String,Integer> targetMethodCall = couplingGraph.get(targetClass)!=null
                            ?couplingGraph.get(targetClass).get(parentClass)!=null
                                ?couplingGraph.get(targetClass).get(parentClass)
                                :new HashMap<>()
                            :new HashMap<>();
                    for (String methodName : targetMethodCall.keySet()) {
                        couplingCall += targetMethodCall.get(methodName);
                    }

                    totalcall+=couplingCall;
                    classCouplingCount.get(parentClass).put(targetClass, couplingCall);
                }
            }
        }
        classCouplingCount.forEach((k,v)->{
            HashMap<String,Float> jsp = new HashMap<>();
            v.forEach((k2,v2)->{
                float coupling = totalcall>0
                        ? (float) v2 /totalcall
                        :0;
                jsp.put(k2,coupling);
            });
            System.out.println(k+"    :     "+v+"     :     "+jsp);
            classCouplingNote.put(k,jsp);
        });
    }

    public HashMap<String, HashMap<String, HashMap<String, Integer>>> getCouplingGraph() {
        return couplingGraph;
    }

    public int getTotalcall() {
        return totalcall;
    }

    public HashMap<String, HashMap<String, Float>> getClassCouplingNote() {
        return classCouplingNote;
    }

    public HashMap<String, HashMap<String, Integer>> getClassCouplingCount() {
        return classCouplingCount;
    }
    public String getGraphAsDot(){
        StringBuilder dot = new StringBuilder();
        dot.append("graph CouplingGraph {\n");
        dot.append("\" Total call count : "+ getTotalcall() +" \";\n");
        if (classCouplingNote == null || classCouplingNote.isEmpty()) {
            dot.append("}");
            return dot.toString();
        }
        // Ajout des noeuds
        for (String node : classCouplingNote.keySet()) {
            dot.append("  \"").append(node).append("\";\n");
        }
        // Ajout des arêtes avec cardinalité
        for (String from : classCouplingNote.keySet()) {
            HashMap<String, Float> targets = classCouplingNote.get(from);
            if (targets != null) {
                for (String to : targets.keySet()) {
                    float cardinality = targets.get(to);
                    if (cardinality > 0) {
                        dot.append("  \"").append(from).append("\" -- \"")
                           .append(to).append("\" [label=\"")
                           .append(cardinality).append(" : "+getClassCouplingCountAsString(from,to)+"\"];\n");
                    }
                }
            }
        }
        dot.append("}\n");
        return dot.toString();
    }

    public String getClassCouplingCountAsString(String name1, String name2){
        StringBuilder sb = new StringBuilder();
        sb.append(classCouplingCount.get(name1).get(name2));
        return sb.toString();
    }
}
