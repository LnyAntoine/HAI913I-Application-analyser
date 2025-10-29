package com.example.services;

import com.example.services.ClusteringClasses.Cluster;
import com.example.services.ClusteringClasses.Clusterable;
import com.example.services.visitor.ClusteringVisitor;

import java.util.ArrayList;
import java.util.List;

public class ModuleService {
    private final ClusteringVisitor clusteringVisitor;
    private final float cp;

    public ModuleService(ClusteringVisitor clusteringVisitor, float cp) {
        this.clusteringVisitor = clusteringVisitor;
        this.cp = cp;
    }

    public String generateModulesDot() {
        ClusteringServices clusteringServices = new ClusteringServices(clusteringVisitor, cp);
        clusteringServices.clusteringHierarchique();
        Clusterable finalCluster = clusteringServices.getFinalCluster();
        if (finalCluster == null) return "";

        List<Clusterable> modules = getModules(finalCluster);
        if (modules == null || modules.isEmpty()) return "";

        DendrogramDotGenerator generator = new DendrogramDotGenerator();
        return generator.generateDotFromList(modules);
    }

    // Récupère tous les modules valides et les classes esseulées d'un clusterable
    private List<Clusterable> getModules(Clusterable c){
        if (!c.isCluster()) return c.getDirectClusterables();
        if (isValidModule(c)){
            List<Clusterable> sons = new ArrayList<>(c.getAllClasses());
            Clusterable module = new Cluster(sons, getAverageSonCoupling(c));
            return List.of(module);
        }
        List<Clusterable> finalList = new ArrayList<>();
        for (Clusterable cl : c.getDirectClusterables()){
            finalList.addAll(getModules(cl));
        }
        return finalList;
    }

    // Un module est valide si la moyenne du couplage de ses fils est supérieur au cp
    // Vérifie aussi que le cluster soit meilleur que ses sous-clusters
    private boolean isValidModule(Clusterable c1){
        if (!c1.isCluster()){
            return false;
        }
        float averageSonCoupling = getAverageSonCoupling(c1);
        return (c1.getCouplingValue()) >= cp
                && c1.getCouplingValue() >= (averageSonCoupling);
    }

    private float getAverageSonCoupling(Clusterable c1){
        List<Clusterable> clusterableList = c1.getDirectClusterables();
        float totalSonCoupling = 0;
        int numberOfSons = 0;
        for (Clusterable son : clusterableList){
            numberOfSons++;
            totalSonCoupling += son.getCouplingValue();
        }
        if (numberOfSons==0){
            return 0f;
        }
        return totalSonCoupling/numberOfSons;
    }
}
