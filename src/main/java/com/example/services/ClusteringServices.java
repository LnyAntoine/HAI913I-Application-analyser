package com.example.services;

import com.example.services.ClusteringClasses.Classes;
import com.example.services.ClusteringClasses.Cluster;
import com.example.services.ClusteringClasses.Clusterable;
import com.example.services.visitor.ClusteringVisitor;
import com.example.services.visitor.CouplingVisitor;
import jakarta.websocket.OnClose;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClusteringServices {
    private final ClusteringVisitor clusteringVisitor;
    private HashMap<String,HashMap<String,Float>> classCouplingNote;
    private ArrayList<String> classes;
    private Clusterable finalCluster;
    public ClusteringServices(ClusteringVisitor clusteringVisitor){
        this.clusteringVisitor = clusteringVisitor;
    }
    public void clusteringHierarchique(){
        this.calculateInitialCoupling();

        ArrayList<Clusterable> clusters = new ArrayList<>();
        ArrayList<Clusterable> clustersTemp;

        //On crée des Classes (Clusterable) pour chaque classe et on les stock dans une liste
        for (String className : classes) {
            clusters.add(new Classes(className));
        }

        Clusterable bestCluster = null;
        Clusterable tempCluster;
        Clusterable toprintCluster = null;

        //Tant que l'on a plus d'un cluster
        while (clusters.size()>1) {
            clustersTemp = new ArrayList<>(clusters);
            clusters = new ArrayList<>();

            while (!clustersTemp.isEmpty()) {
                System.out.println(clustersTemp);
                bestCluster = null;
                //Pour chaque cluster dans la liste des clusters
                for (Clusterable cluster : clustersTemp) {
                    tempCluster = getBestCoupling(cluster,clustersTemp);
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
                        int index = getIndex(cluster,clustersTemp);
                        if (index>=0) {
                            clustersTemp.remove(index);
                        }
                    }
                if (!clusters.contains(bestCluster)) {
                    //On ajoute le meilleure cluster à la liste
                    clusters.add(bestCluster);
                }
            }
        }

        //On renvoie le premier cluster, le cluster racine
        finalCluster = clusters.getFirst();
    }

    public int getIndex(Clusterable cluster,ArrayList<Clusterable> clusters) {
        for (int i = 0; i < clusters.size(); i++) {
            Clusterable cl = clusters.get(i);
            if (cluster.equals(cl)) {
                return i;
            }
        }
        return -1;
    }

    public Clusterable getBestCoupling(Clusterable c1, ArrayList<Clusterable> clusters) {
        //System.out.println("getBestCoupling");
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
                //System.out.println("c1 : "+ c1 +" c2 : "+c2 + "coupling : "+coupling);
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

    public void calculateInitialCoupling(){
        classes = clusteringVisitor.getClassesList();
        classCouplingNote = new HashMap<>();
        for (String class1 : classes) {
            HashMap<String,Float> couplingMap = new HashMap<>();
            for (String class2 : classes) {
                if (!classCouplingNote.containsKey(class2)) {
                    if (!class1.equals(class2)) {
                        float coupling = getCouplingBetween(new Classes(class1), new Classes(class2));
                        couplingMap.put(class2, coupling);
                    }
                }
            }
            classCouplingNote.put(class1,couplingMap);
        }
        System.out.println(" classcoiplingnote : "+classCouplingNote);

    }

    public float getCouplingBetween(Clusterable c1, Clusterable c2) {

        float CM = 0;
        float CA = 0;
        float CP = 0;
        HashMap<String,
                HashMap<String,
                        HashMap<String,
                                HashMap<String,
                                        HashMap<String,
                                                Integer>>>>> clusteringMap = clusteringVisitor.getClusteringMap();

        for (Clusterable bbC1 : c1.getClusterables()){
            if (clusteringMap.containsKey(bbC1.getName())) {
                HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> relationsMap = clusteringMap.get(bbC1.getName());
                for (Clusterable bbC2 : c2.getClusterables()) {

                    if (relationsMap.containsKey("invocation")) { //On calcule CM
                        HashMap<String, HashMap<String, HashMap<String, Integer>>> toClassesMap = relationsMap.get("invocation");
                        if (toClassesMap.containsKey(bbC2.getName())) {
                            HashMap<String, HashMap<String, Integer>> methodsMap = toClassesMap.get(bbC2.getName());
                            for (String methodName : methodsMap.keySet()) {
                                HashMap<String, Integer> optionsMap = methodsMap.get(methodName);
                                if (optionsMap.containsKey("call") && optionsMap.containsKey("params")) {
                                    CM += optionsMap.get("call") * (optionsMap.get("params")+1);
                                }
                            }
                        }
                    }
                    //On calcule CA
                    if (relationsMap.containsKey("attribute")) {}
                    //On calcule CP
                    if (relationsMap.containsKey("params")) {}
                }
            }
        }
        for (Clusterable bbC2 : c2.getClusterables()){
            if (clusteringMap.containsKey(bbC2.getName())) {
                HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> relationsMap = clusteringMap.get(bbC2.getName());
                for (Clusterable bbC1 : c1.getClusterables()) {

                    //On calcule CM
                    if (relationsMap.containsKey("invocation")) {
                        HashMap<String, HashMap<String, HashMap<String, Integer>>> toClassesMap = relationsMap.get("invocation");
                        if (toClassesMap.containsKey(bbC1.getName())) {
                            HashMap<String, HashMap<String, Integer>> methodsMap = toClassesMap.get(bbC1.getName());
                            for (String methodName : methodsMap.keySet()) {
                                HashMap<String, Integer> optionsMap = methodsMap.get(methodName);
                                if (optionsMap.containsKey("call") && optionsMap.containsKey("params")) {
                                    CM += optionsMap.get("call") * (optionsMap.get("params")+1);
                                }
                            }
                        }
                    }
                    //On calcule CA
                    if (relationsMap.containsKey("attribute")) {}
                    //On calcule CP
                    if (relationsMap.containsKey("params")) {}
                }
            }
        }

        return (CM+CA+CP)/3;
    }





    // Génère un dendrogramme DOT à partir d'un Clusterable racine.
    // N'utilise pas de modification des classes model : descend l'arbre en testant "instanceof Cluster"
    // et en récupérant les deux sous-clusters par réflexion (champs privés).
    public String getDendrogramDot() {
        Clusterable root = finalCluster;
        if (root == null) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("digraph D {\n");
        sb.append("rankdir=TB;\n");
        sb.append("node [shape=box, fontsize=10];\n");

        IdentityHashMap<Clusterable, Integer> idMap = new IdentityHashMap<>();
        AtomicInteger counter = new AtomicInteger(0);

        buildDotRecursive(root, sb, idMap, counter);

        sb.append("}\n");
        return sb.toString();
    }

    private int getId(Clusterable node, IdentityHashMap<Clusterable, Integer> idMap, AtomicInteger counter) {
        if (!idMap.containsKey(node)) {
            idMap.put(node, counter.getAndIncrement());
        }
        return idMap.get(node);
    }

    private void buildDotRecursive(Clusterable node,
                                   StringBuilder sb,
                                   IdentityHashMap<Clusterable, Integer> idMap,
                                   AtomicInteger counter) {
        if (node == null) return;

        int id = getId(node, idMap, counter);
        String label = safeLabel(node.getName());

        if (node.isCluster()) {
            // label with name and coupling value
            float cv = node.getCouplingValue();
            sb.append(String.format("n%d [label=\"%s\\n(%.2f)\"];\n", id, label, cv));

            Clusterable left = null;
            Clusterable right = null;

            // try to extract children using reflection if it's an instance of Cluster
            if (node instanceof Cluster) {
                try {
                    java.lang.reflect.Field f1 = node.getClass().getDeclaredField("clusterable1");
                    java.lang.reflect.Field f2 = node.getClass().getDeclaredField("clusterable2");
                    f1.setAccessible(true);
                    f2.setAccessible(true);
                    Object o1 = f1.get(node);
                    Object o2 = f2.get(node);
                    if (o1 instanceof Clusterable) left = (Clusterable) o1;
                    if (o2 instanceof Clusterable) right = (Clusterable) o2;
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    // Si réflexion échoue, on ignore — on peut tout de même tenter de connecter
                    // le noeud aux feuilles connues via getClusterables()
                    left = null;
                    right = null;
                }
            }

            // si on n'a pas réussi à obtenir les enfants via réflexion, attacher le cluster aux feuilles directes
            if (left == null && right == null) {
                // récupère les feuilles (sous-clusterables) et crée des noeuds pour chacune
                for (Clusterable leaf : node.getClusterables()) {
                    int leafId = getId(leaf, idMap, counter);
                    sb.append(String.format("n%d [label=\"%s\", shape=ellipse];\n", leafId, safeLabel(leaf.getName())));
                    sb.append(String.format("n%d -> n%d;\n", id, leafId));
                }
            } else {
                if (left != null) {
                    int lid = getId(left, idMap, counter);
                    sb.append(String.format("n%d -> n%d;\n", id, lid));
                    buildDotRecursive(left, sb, idMap, counter);
                }
                if (right != null) {
                    int rid = getId(right, idMap, counter);
                    sb.append(String.format("n%d -> n%d;\n", id, rid));
                    buildDotRecursive(right, sb, idMap, counter);
                }
            }

        } else {
            // feuille
            sb.append(String.format("n%d [label=\"%s\", shape=ellipse];\n", id, label));
        }
    }

    private String safeLabel(String in) {
        if (in == null) return "";
        return in.replace("\\", "\\\\").replace("\"", "\\\"");
    }

}
