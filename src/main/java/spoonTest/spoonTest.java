package spoonTest;

import com.example.services.ClusteringServices;
import com.example.services.visitor.ClusteringVisitor;
import spoon.Launcher;
import spoon.reflect.CtModel;

public class spoonTest {
    public static void main(String[] args) {

        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        Launcher launcher = new Launcher();
        String dir = System.getProperty("user.dir") ;
        //dir = "C:\\Users\\launa\\IdeaProjects\\TPGRPC2\\exo2\\server2";
        launcher.addInputResource(dir);

        launcher.getEnvironment().setNoClasspath(true);
        CtModel model = launcher.buildModel();
        ClusteringVisitor clusteringVisitor = new ClusteringVisitor();
        model.getAllTypes().forEach(type->{
            type.accept(clusteringVisitor);
        });
        ClusteringServices clusteringServices = new ClusteringServices(clusteringVisitor,0f);
        clusteringServices.clusteringHierarchique();
        clusteringServices.generateModules();
        System.out.println(clusteringServices.getModulesDendogramDot());
        System.out.println("--------------------");
        //System.out.println(clusteringServices.getDendrogramDot());


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

