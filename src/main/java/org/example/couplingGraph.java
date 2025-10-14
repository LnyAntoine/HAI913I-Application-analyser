package org.example;

import spoon.reflect.visitor.CtScanner;
import spoon.visitor.couplingGraphVisitor;

import java.util.HashMap;

public class couplingGraph {
    private final couplingGraphVisitor visitor;
    private HashMap<String, HashMap<String, HashMap<String, Integer>>> couplingGraph;
    private HashMap<String,HashMap<String,Integer>> classCouplingCount;



    private HashMap<String,HashMap<String,Integer>> classCouplingNote;

    private int totalcall;
    public couplingGraph(couplingGraphVisitor visitor) {

        this.visitor = visitor;

    }
    public void generateGraph(){
        totalcall = 0;
        couplingGraph = visitor.getCouplingGraph();
        classCouplingCount = new HashMap<>();
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
                if (!classCouplingCount.containsKey(targetClass)){
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
                    couplingGraph.get(targetClass);
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
            HashMap<String,Integer> jsp = new HashMap<>();
            v.forEach((k2,v2)->{
                int coupling = totalcall>0?v2/totalcall:0;
                jsp.put(k2,coupling);
            });
            classCouplingNote.put(k,jsp);
        });
    }

    public HashMap<String, HashMap<String, HashMap<String, Integer>>> getCouplingGraph() {
        return couplingGraph;
    }

    public int getTotalcall() {
        return totalcall;
    }

    public HashMap<String, HashMap<String, Integer>> getClassCouplingNote() {
        return classCouplingNote;
    }

    public HashMap<String, HashMap<String, Integer>> getClassCouplingCount() {
        return classCouplingCount;
    }
}
