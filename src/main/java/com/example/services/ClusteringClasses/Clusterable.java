package com.example.services.ClusteringClasses;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public abstract class Clusterable {
    public abstract boolean isCluster();
    public abstract String getName();
    public abstract List<Clusterable> getClusterables();
    public abstract float getCouplingValue();
    public float getCouplingWith(Clusterable c,
                                 HashMap<String,HashMap<String,Float>> classCouplingNote,
                                 List<String> classList) {
        //System.out.println("GetCouplingWith");
        float totalCouplingValue = 0.0f;
        float couplingValue = 0.0f;

        //On récupère TOUS les clusterables de notre this et de c
        List<Clusterable> fromClusterables = getClusterables();
        List<Clusterable> toClusterables = c.getClusterables();
        //System.out.println("From clusterables : "+fromClusterables);
        //System.out.println("To clusterables : "+toClusterables);

        //Pour toutes les clusterabless contenu dans le cluster this
        for (Clusterable fromClusterable : fromClusterables) {
            //On regarde tous les clusterabless de C
            for  (Clusterable toClusterable : toClusterables) {
                //Si la couplingNote contient une relation de couplage
                if (classCouplingNote.containsKey(fromClusterable.getName())) {
                    if (classCouplingNote.get(fromClusterable.getName())
                            .containsKey(toClusterable.getName())) {
                        //System.out.println("Couplage : "+fromClusterable +" | "+ toClusterable);
                        /*System.out.println("CouplageValue : "+totalCouplingValue + " | " + classCouplingNote.get(fromClusterable.getName())
                                .get(toClusterable.getName()));

                         */
                        totalCouplingValue += classCouplingNote.get(fromClusterable.getName())
                                .get(toClusterable.getName());
                        //System.out.println(totalCouplingValue);

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
        //System.out.println(totalCouplingValue);
        return couplingValue;
    }
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
