package com.example.services.ClusteringClasses;

import java.util.ArrayList;
import java.util.List;

public class Cluster extends Clusterable {
    private final Clusterable clusterable1;
    private final Clusterable clusterable2;
    private float clusterValue;

    public Cluster(Clusterable clusterable1, Clusterable clusterable2,float clusterValue) {
        this.clusterable1 = clusterable1;
        this.clusterable2 = clusterable2;
        this.clusterValue = clusterValue;
    }

    @Override
    public boolean isCluster() {
        return true;
    }

    @Override
    public String getName() {
        return clusterable1.getName() + "-" + clusterable2.getName();
    }

    @Override
    public List<Clusterable> getClusterables() {
        ArrayList<Clusterable> clusterableList = new ArrayList<>();
        clusterableList.add(this);
        clusterableList.addAll(clusterable1.getClusterables());
        clusterableList.addAll(clusterable2.getClusterables());
        return clusterableList;
    }

    @Override
    public float getCouplingValue() {
        return clusterValue;
    }


    @Override
    public String toString(){
        String text = "";
        text += "[ ["+ clusterable1+"] | ["+clusterable2+"] ]";
        return text;
    }
}


