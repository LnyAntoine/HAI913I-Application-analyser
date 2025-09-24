package spoon;

import spoon.processor.*;

public class spoonPart2 {
    public static void main(String[] args) {

        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        Launcher launcher = new Launcher();
        launcher.addInputResource("exo2/server2/src/main/java/");
        launcher.addInputResource("exo2/common2/src/main/java");
        launcher.addProcessor(new nbClassProcessor());
        launcher.addProcessor(new nbLineProcessor());
        launcher.addProcessor(new nbMethodProcessor());
        launcher.addProcessor(new nbPackageProcessor());
        launcher.addProcessor(new averageMethodPerClassProcessor());
        launcher.addProcessor(new averageLinePerMethodProcessor());
        launcher.addProcessor(new averageAttributePerClassProcessor());
        launcher.addProcessor(new percentClassMethodProcessor());
        launcher.addProcessor(new percentClassAttributeProcessor());
        launcher.addProcessor(new percentClassMethodAttributeProcessor());
        launcher.addProcessor(new percentMethodLinePerClassProcessor());
        launcher.addProcessor(new maxParametersProcessor());
        moreThanXMethodProcessor processor = new moreThanXMethodProcessor();
        processor.setParam(3);
        launcher.addProcessor(processor);
        launcher.run();
    }

}
