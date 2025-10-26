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
    //Méthode qui calcule le couplage entre this et un autre clusterable c
    /*public float getCouplingWith(Clusterable c,
                                 HashMap<String,HashMap<String,Float>> classCouplingNote,
                                 List<String> classList) {
        float totalCouplingValue = 0.0f;
        float couplingValue = 0.0f;

        //On récupère TOUS les clusterables de notre this et de c
        List<Clusterable> fromClusterables = getClusterables();
        List<Clusterable> toClusterables = c.getClusterables();

        //Pour toutes les clusterabless contenu dans le cluster this
        for (Clusterable fromClusterable : fromClusterables) {
            //On regarde tous les clusterabless de C
            for  (Clusterable toClusterable : toClusterables) {
                //Si la couplingNote contient une relation de couplage
                if (classCouplingNote.containsKey(fromClusterable.getName())) {
                    if (classCouplingNote.get(fromClusterable.getName())
                            .containsKey(toClusterable.getName())) {
                        totalCouplingValue += classCouplingNote.get(fromClusterable.getName())
                                .get(toClusterable.getName());
                    }
                }
                else {
                    if (classCouplingNote.containsKey(toClusterable.getName())){
                        if (classCouplingNote.get(toClusterable.getName())
                                .containsKey(fromClusterable.getName())) {
                            totalCouplingValue += classCouplingNote.get(fromClusterable.getName())
                                    .get(toClusterable.getName());
                        }
                    }
                }
            }
        }

        couplingValue = !classList.isEmpty()
                ? totalCouplingValue/classList.size()
                : couplingValue;


        couplingValue = totalCouplingValue;
        //System.out.println(totalCouplingValue);
        return couplingValue;
    }
    */
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
