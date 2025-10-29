package com.example.services;

import com.example.services.ClusteringClasses.Cluster;
import com.example.services.ClusteringClasses.Clusterable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;

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

    /**
     * Génère un DOT pour une liste de Clusterable. Chaque Clusterable est placé dans
     * un subgraph séparé pour éviter les chevauchements visuels et bien séparer les arbres.
     */
    public String generateDotFromList(List<Clusterable> roots) {
        if (roots == null || roots.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        sb.append("digraph D {\n");
        sb.append("rankdir=TB;\n");
        sb.append("node [shape=box, fontsize=10];\n");

        IdentityHashMap<Clusterable, Integer> idMap = new IdentityHashMap<>();
        AtomicInteger counter = new AtomicInteger(0);
        AtomicInteger subgraphCounter = new AtomicInteger(0);

        for (Clusterable root : roots) {
            if (root == null) continue;
            // Si la racine est un cluster, on la place dans un subgraph pour la séparer visuellement
            if (root.isCluster()) {
                int sg = subgraphCounter.getAndIncrement();
                sb.append(String.format("subgraph cluster_%d {\n", sg));
                sb.append("color=lightgrey;\n");
                String rootLabel = safeLabel(root.getName());
                sb.append(String.format("label=\"%s\";\n", rootLabel));

                // construire récursivement les nœuds/edges pour cette racine (dans le subgraph)
                buildDotRecursive(root, sb, idMap, counter);

                sb.append("}\n");
            } else {
                // Si ce n'est pas un cluster, l'ajouter directement au graphe principal
                buildDotRecursive(root, sb, idMap, counter);
            }
        }

        sb.append("}\n");
        return sb.toString();
    }

    private int getId(Clusterable node, IdentityHashMap<Clusterable, Integer> idMap, AtomicInteger counter) {
        if (node == null) return -1;
        // retourner directement la valeur créée ou existante
        return idMap.computeIfAbsent(node, k -> counter.getAndIncrement());
    }

    private void buildDotRecursive(Clusterable node,
                                   StringBuilder sb,
                                   IdentityHashMap<Clusterable, Integer> idMap,
                                   AtomicInteger counter) {
        if (node == null) return;

        int id = getId(node, idMap, counter);
        if (id < 0) return;
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
                    // safer conversion: accept any List<?> and copy verified Clusterable elements
                    if (obj instanceof List) {
                        children = new ArrayList<>();
                        for (Object o : (List<?>) obj) {
                            if (o instanceof Clusterable) {
                                children.add((Clusterable) o);
                            }
                        }
                        if (children.isEmpty()) children = null; // no valid children
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
                        if (childId >= 0) {
                            sb.append(String.format("n%d -> n%d;\n", id, childId));
                            buildDotRecursive(child, sb, idMap, counter);
                        }
                    }
                }
            } else {
                // sinon, attacher le cluster aux feuilles directes (fallback)
                if (node.getClusterables() != null) {
                    for (Clusterable leaf : node.getClusterables()) {
                        if (leaf != null && leaf != node) { // éviter auto-référence
                            int leafId = getId(leaf, idMap, counter);
                            if (leafId >= 0) {
                                sb.append(String.format("n%d [label=\"%s\", shape=ellipse];\n", leafId, safeLabel(leaf.getName())));
                                sb.append(String.format("n%d -> n%d;\n", id, leafId));
                            }
                        }
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
