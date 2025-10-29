package com.example.services.ClusteringClasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public abstract class Clusterable {
    public abstract boolean isCluster();
    public abstract String getName();
    public abstract List<Clusterable> getClusterables();
    public abstract float getCouplingValue();
    public abstract List<Clusterable> getDirectClusterables();
    public List<Classes> getAllClasses() {
        ArrayList<Classes> classesArrayList = new ArrayList<>();
        for (Clusterable c1 : this.getClusterables()) {
            if (!c1.isCluster()) classesArrayList.add((Classes) c1);
        }
        return classesArrayList;
    }
    //Redéfinition de equals et hashcode pour comparer les clusterables par leur nom
    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null) return false;
        boolean value;
        if(obj instanceof Clusterable clusterable){
            value = clusterable.getName().equals(this.getName());
        }
        else value = false;
        return value;
    }
    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
