package com.example.services;

import com.example.services.ClusteringClasses.Cluster;
import com.example.services.ClusteringClasses.Clusterable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DendrogramDotGenerator {

    /**
     * Génère un dendrogramme DOT à partir d'un Clusterable racine.
     * Descend l'arbre en testant "instanceof Cluster" et en récupérant
     * les sous-clusters par réflexion (champ privé clusterables).
     */
    public String generateDot(Clusterable root) {
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

            ArrayList<Clusterable> children = null;

            // try to extract children using reflection if it's an instance of Cluster
            if (node instanceof Cluster) {
                try {
                    java.lang.reflect.Field field = node.getClass().getDeclaredField("clusterables");
                    field.setAccessible(true);
                    Object obj = field.get(node);
                    if (obj instanceof ArrayList) {
                        children = (ArrayList<Clusterable>) obj;
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    // Si réflexion échoue, on ignore
                    children = null;
                }
            }

            // si on a réussi à obtenir les enfants directs via réflexion
            if (children != null && !children.isEmpty()) {
                for (Clusterable child : children) {
                    if (child != null) {
                        int childId = getId(child, idMap, counter);
                        sb.append(String.format("n%d -> n%d;\n", id, childId));
                        buildDotRecursive(child, sb, idMap, counter);
                    }
                }
            } else {
                // sinon, attacher le cluster aux feuilles directes (fallback)
                for (Clusterable leaf : node.getClusterables()) {
                    if (leaf != node) { // éviter auto-référence
                        int leafId = getId(leaf, idMap, counter);
                        sb.append(String.format("n%d [label=\"%s\", shape=ellipse];\n", leafId, safeLabel(leaf.getName())));
                        sb.append(String.format("n%d -> n%d;\n", id, leafId));
                    }
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

