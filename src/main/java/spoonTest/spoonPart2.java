package spoonTest;

import com.example.services.ClusteringClasses.Classes;
import com.example.services.ClusteringClasses.Cluster;
import com.example.services.ClusteringClasses.Clusterable;
import com.example.services.ClusteringServices;
import com.example.services.CouplingServices;
import com.example.services.StatisticCalculatorServices;
import com.example.services.visitor.ClusteringVisitor;
import spoon.Launcher;
import spoon.reflect.CtModel;
import com.example.services.visitor.StatisticsVisitor;
import com.example.services.visitor.CallingVisitor;
import com.example.services.visitor.CouplingVisitor;

import java.util.ArrayList;

public class spoonPart2 {
    public static void main(String[] args) {

        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        Launcher launcher = new Launcher();
        String dir = System.getProperty("user.dir") ;
        dir = "C:\\Users\\launa\\IdeaProjects\\Spoon-TP2-HAI913I";
        launcher.addInputResource(dir);

        launcher.getEnvironment().setNoClasspath(true);
        CtModel model = launcher.buildModel();
        CouplingVisitor couplingGraphVisitor = new CouplingVisitor();
        ClusteringVisitor clusteringVisitor = new ClusteringVisitor();
        model.getAllTypes().forEach(type->{
            type.accept(couplingGraphVisitor);
            type.accept(clusteringVisitor);
        });
        CouplingServices couplingServices = new CouplingServices(couplingGraphVisitor);
        couplingServices.generateGraphFilter(new ArrayList<>());
        ClusteringServices clusteringServices = new ClusteringServices(couplingGraphVisitor,clusteringVisitor,couplingServices);
        clusteringServices.clusteringHierarchique();
        System.out.println(clusteringServices.getDendrogramDot());

        /*
        Clusterable classe1Test = new Classes("test1");
        Clusterable classe2Test = new Classes("test2");
        Clusterable cluster1Test = new Cluster(classe1Test,classe2Test,1);
        Clusterable cluster2Test = new Cluster(classe1Test,classe2Test,1);
        System.out.println(cluster1Test.equals(cluster2Test));
        ArrayList<Clusterable> clusters = new ArrayList<>();
        clusters.add(cluster1Test);
        System.out.println("contains : "+clusters.contains(cluster2Test));
        */
    }
}

