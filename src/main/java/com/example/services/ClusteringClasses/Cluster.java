package com.example.services.ClusteringClasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cluster extends Clusterable {
    private final ArrayList<Clusterable> clusterables;
    private float clusterValue;

    public Cluster(Clusterable clusterable1, Clusterable clusterable2,float clusterValue) {
        this.clusterables = new ArrayList<>(Arrays.asList(clusterable1, clusterable2));
        this.clusterValue = clusterValue;
    }
    public Cluster(List<Clusterable> clusterables, float clusterValue) {
        this.clusterables = new ArrayList<>(clusterables);
        this.clusterValue = clusterValue;
    }

    @Override
    public boolean isCluster() {
        return true;
    }

    @Override
    public String getName() {
        StringBuilder s = new StringBuilder();
        for (Clusterable c : clusterables) {
            s.append(c.getName()).append("-");
        }
        return s.substring(0, s.length() - 1);
    }

    @Override
    public List<Clusterable> getClusterables() {
        ArrayList<Clusterable> clusterableList = new ArrayList<>();
        clusterableList.add(this);
        for (Clusterable c : clusterables) {
            clusterableList.addAll(c.getClusterables());
        }
        return clusterableList;
    }

    @Override
    public float getCouplingValue() {
        return clusterValue;
    }


    @Override
    public String toString(){
        StringBuilder text = new StringBuilder("[ ");
        for (Clusterable c : clusterables) {
            text.append(" [").append(c.toString()).append("] |");
        }
        String s = text.substring(0, text.length() - 1);
        s+=" ]";
        return s;
    }
}


