package com.example.services.ClusteringClasses;

import java.util.List;

public class Classes extends Clusterable {
    private final String name;
    private Float internalCouplingValue;

    public Classes(String name) {
        this.name = name;
        this.internalCouplingValue = 0f;
    }

    public Classes(String name, Float value) {
        this.name = name;
        this.internalCouplingValue = value;
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
        return internalCouplingValue;
    }

    @Override
    public List<Clusterable> getDirectClusterables() {
        return List.of(this);
    }

    public void setCouplingValue(Float internalCouplingValue) {
        this.internalCouplingValue = internalCouplingValue;
    }

    @Override
    public String toString(){
        return name;
    }

}
