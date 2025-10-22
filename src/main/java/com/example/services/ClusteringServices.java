package com.example.services;

import com.example.services.ClusteringClasses.Classes;
import com.example.services.ClusteringClasses.Cluster;
import com.example.services.ClusteringClasses.Clusterable;
import com.example.services.visitor.ClusteringVisitor;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

public class ClusteringServices {
    private final ClusteringVisitor clusteringVisitor;
    private HashMap<String,HashMap<String,Float>> classCouplingNote;
    private ArrayList<String> classes;
    private Clusterable finalCluster;
    private final DendrogramDotGenerator dotGenerator;

    public ClusteringServices(ClusteringVisitor clusteringVisitor){
        this.clusteringVisitor = clusteringVisitor;
        this.dotGenerator = new DendrogramDotGenerator();
    }

    public void clusteringHierarchique(){
        this.calculateInitialCoupling();

        ArrayList<Clusterable> clusters = new ArrayList<>();

        //On crée des Classes (Clusterable) pour chaque classe et on les stock dans une liste
        for (String className : classes) {
            clusters.add(new Classes(className));
        }

        Clusterable bestCluster = null;
        Clusterable tempCluster;

        //Tant que l'on a plus d'un cluster
        while (clusters.size()>1) {
                bestCluster = null;
                //Pour chaque cluster dans la liste des clusters
                for (Clusterable cluster : clusters) {
                    tempCluster = getBestCoupling(cluster,clusters);
                    if (bestCluster==null) {
                        bestCluster = tempCluster;
                    }
                    else {
                        //On regarde si son meilleur cluster possible est le meilleur de toute l'architecture
                        if (tempCluster.getCouplingValue()>=bestCluster.getCouplingValue()) {
                            bestCluster = tempCluster;
                        }
                    }
                }
                assert bestCluster!=null;
                //On retire les sous-clusters du meilleur cluster de la liste

                for (Clusterable cluster : bestCluster.getClusterables()){
                    int index = getIndex(cluster,clusters);
                    if (index>=0) {
                        clusters.remove(index);
                    }
                }
                if (!clusters.contains(bestCluster)) {
                    //On ajoute le meilleure cluster à la liste
                    clusters.add(bestCluster);
                }

        }

        //On renvoie le premier cluster, le cluster racine
        finalCluster = clusters.getFirst();
    }

    private int getIndex(Clusterable cluster,ArrayList<Clusterable> clusters) {
        for (int i = 0; i < clusters.size(); i++) {
            Clusterable cl = clusters.get(i);
            if (cluster.equals(cl)) {
                return i;
            }
        }
        return -1;
    }

    private Clusterable getBestCoupling(Clusterable c1, ArrayList<Clusterable> clusters) {
        Clusterable best = null;
        float bestCoupling = -1;
        Clusterable returnedCluster = null;
        //Pour chaque cluster dans notre liste
        for (Clusterable c2 : clusters) {
            if (c1 != c2) {
                //On récupère le coupling avec c2
                float coupling = c1.getCouplingWith(c2,
                        classCouplingNote,
                        classes
                );
                //On regarde si c'est le meilleur coupling possible
                if (coupling > bestCoupling) {
                    bestCoupling = coupling;
                    best = c2;
                }
            }
        }

        //System.out.println("c1 : "+ c1 +" Best : "+best + " coupling : "+bestCoupling);
        if (best!=null && bestCoupling>=0) {
            returnedCluster = new Cluster(c1, best, bestCoupling);
        } else { // Si on a pas de meilleur cluster possible alors on considère
            // que le seul cluster restant est la classe elle même
            returnedCluster = c1;
        }
        //System.out.println("returned Cluster : "+returnedCluster);
        return returnedCluster;
    }

    private void calculateInitialCoupling(){
        classes = clusteringVisitor.getClassesList();
        classCouplingNote = new HashMap<>();
        for (String class1 : classes) {
            HashMap<String,Float> couplingMap = new HashMap<>();
            for (String class2 : classes) {
                if (!classCouplingNote.containsKey(class2)) {
                    float coupling = getCouplingBetween(new Classes(class1), new Classes(class2));
                    couplingMap.put(class2, coupling);
                }
            }
            classCouplingNote.put(class1,couplingMap);
        }
        System.out.println(" classcouplingnote : "+classCouplingNote);

    }

    private float getCouplingBetween(@NonNull Clusterable c1, Clusterable c2) {

        float CM = 0;
        float CA = 0;
        float CP = 0;
        HashMap<String,
                HashMap<String,
                                HashMap<String,
                                        HashMap<String,
                                                Integer>>>> clusteringInvocationMap = clusteringVisitor.getClusteringInvocationMap();

        HashMap<String,HashMap<String,Integer>> fieldMap=clusteringVisitor.getClusteringFieldMap();
        HashMap<String,HashMap<String,Integer>> paramsMap=clusteringVisitor.getClusteringParamsMap();
        for (Clusterable bbC1 : c1.getClusterables()){
            if (clusteringInvocationMap.containsKey(bbC1.getName())) {
                HashMap<String, HashMap<String, HashMap<String, Integer>>> relationsMap = clusteringInvocationMap.get(bbC1.getName());
                for (Clusterable bbC2 : c2.getClusterables()) {
                         //On calcule CM
                        if (relationsMap.containsKey(bbC2.getName())) {
                            HashMap<String, HashMap<String, Integer>> methodsMap = relationsMap.get(bbC2.getName());
                            for (String methodName : methodsMap.keySet()) {
                                HashMap<String, Integer> optionsMap = methodsMap.get(methodName);
                                if (optionsMap.containsKey("call") && optionsMap.containsKey("params")) {
                                    CM += optionsMap.get("call") * (optionsMap.get("params")+1);
                                }
                            }
                        }
                        //On calcule CA
                        if (fieldMap.containsKey(bbC1.getName())) {
                            HashMap<String, Integer> fieldsOfC1 = fieldMap.get(bbC1.getName());
                            if (fieldsOfC1.containsKey(bbC2.getName())) {
                                CA += fieldsOfC1.get(bbC2.getName());
                            }
                        }
                        //On calcule CP
                        if (paramsMap.containsKey(bbC1.getName())) {
                            HashMap<String, Integer> paramsOfC1 = paramsMap.get(bbC1.getName());
                            if (paramsOfC1.containsKey(bbC2.getName())) {
                                CP += paramsOfC1.get(bbC2.getName());
                            }
                        }
                }
            }
        }
        for (Clusterable bbC2 : c2.getClusterables()){
            if (clusteringInvocationMap.containsKey(bbC2.getName())) {
                HashMap<String, HashMap<String, HashMap<String, Integer>>> relationsMap = clusteringInvocationMap.get(bbC2.getName());
                for (Clusterable bbC1 : c1.getClusterables()) {

                    //On calcule CM

                        if (relationsMap.containsKey(bbC1.getName())) {
                            HashMap<String, HashMap<String, Integer>> methodsMap = relationsMap.get(bbC1.getName());
                            for (String methodName : methodsMap.keySet()) {
                                HashMap<String, Integer> optionsMap = methodsMap.get(methodName);
                                if (optionsMap.containsKey("call") && optionsMap.containsKey("params")) {
                                    CM += optionsMap.get("call") * (optionsMap.get("params")+1);
                                }
                            }
                        }

                        if (fieldMap.containsKey(bbC2.getName())) {
                            HashMap<String, Integer> fieldsOfC1 = fieldMap.get(bbC2.getName());
                            if (fieldsOfC1.containsKey(bbC1.getName())) {
                                CA += fieldsOfC1.get(bbC1.getName());
                            }
                        }
                        //On calcule CP
                        if (paramsMap.containsKey(bbC2.getName())) {
                            HashMap<String, Integer> paramsOfC1 = paramsMap.get(bbC2.getName());
                            if (paramsOfC1.containsKey(bbC1.getName())) {
                                CP += paramsOfC1.get(bbC1.getName());
                            }
                        }

                    //On calcule CA
                    //On calcule CP
                }
            }
        }

        System.out.println(CM+" | "+CA+" | "+CP);
        return (CM+CA+CP)/3;
    }

    private int getTotalCall(Clusterable c1){
        int totalCall = 0;
        HashMap<String,
                HashMap<String,
                                HashMap<String,
                                        HashMap<String,
                                                Integer>>>> clusteringInvocationMap = clusteringVisitor.getClusteringInvocationMap();
        for (Clusterable bbC1 : c1.getClusterables()){
            if (bbC1.isCluster()){
                //On ignore les clusters qui sont mis dans la liste
                // des clusterable (leur classe est déjà présente)
                continue;
            }
            if (clusteringInvocationMap.containsKey(bbC1.getName())) {
                HashMap<String, HashMap<String, HashMap<String, Integer>>> relationsMap = clusteringInvocationMap.get(bbC1.getName());
                for (HashMap<String, HashMap<String, Integer>> methodsMap : relationsMap.values()) {
                    for (HashMap<String, Integer> optionsMap : methodsMap.values()) {
                        if (optionsMap.containsKey("call")) {
                            totalCall += optionsMap.get("call");
                        }
                    }
                }
            }
        }
        return totalCall;
    }

    private int getInternalCall(Clusterable c1){
        int internalCall = 0;
        HashMap<String,
                HashMap<String,
                                HashMap<String,
                                        HashMap<String,
                                                Integer>>>> clusteringInvocationMap = clusteringVisitor.getClusteringInvocationMap();
        for (Clusterable bbC1 : c1.getClusterables()){
            if (bbC1.isCluster()){
                //On ignore les clusters qui sont mis dans la liste
                // des clusterable (leur classe est déjà présente)
                continue;
            }
            if (clusteringInvocationMap.containsKey(bbC1.getName())) {
                HashMap<String, HashMap<String, HashMap<String, Integer>>> relationsMap = clusteringInvocationMap.get(bbC1.getName());
                for (Clusterable bbC2 : c1.getClusterables()) {
                    if (bbC2.isCluster()){
                        //On ignore les clusters qui sont mis dans la liste
                        // des clusterable (leur classe est déjà présente)
                        continue;
                    }
                    if (relationsMap.containsKey(bbC2.getName())) {
                        HashMap<String, HashMap<String, Integer>> methodsMap = relationsMap.get(bbC2.getName());
                        for (String methodName : methodsMap.keySet()) {
                            HashMap<String, Integer> optionsMap = methodsMap.get(methodName);
                            if (optionsMap.containsKey("call")) {
                                internalCall += optionsMap.get("call");
                            }
                        }
                    }
                }
            }
        }
        return internalCall;
    }

    public String getDendrogramDot() {
        return dotGenerator.generateDot(finalCluster);
    }

}
