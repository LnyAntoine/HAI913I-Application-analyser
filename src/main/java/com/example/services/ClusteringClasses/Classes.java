package com.example.services.ClusteringClasses;

import java.util.List;

public class Classes extends Clusterable {
    private final String name;

    public Classes(String name) {
        this.name = name;
    }

    @Override
    public boolean isCluster() {
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Clusterable> getClusterables() {
        return List.of(this);
    }

    @Override
    public float getCouplingValue() {
        return -1;
    }

    @Override
    public String toString(){
        return name;
    }
}
