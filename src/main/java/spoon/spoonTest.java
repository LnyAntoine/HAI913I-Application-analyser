package spoon;

import spoon.reflect.CtModel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class spoonTest {
    public static void main(String[] args) {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        Launcher launcher = new Launcher();
        launcher.addInputResource("exo2/server2/src/main/java");
        //launcher.addInputResource("exo2/common2/src/main/java");
        launcher.buildModel();

        CtModel model = launcher.getModel();

        for(CtPackage p : model.getAllPackages()) {
            System.out.println("package: " + p.getQualifiedName());
        }
        for(CtType<?> s : model.getAllTypes()) {
            System.out.println("class: " + s.getQualifiedName());
            if(s.getSuperclass()!=null) System.out.println("superclasses: " + s.getSuperclass());
            List<CtField<?>> fields = s.getFields();
            System.out.println("Attributs : ");
            for(CtField<?> f : fields) {
                System.out.println("\t" + f.getModifiers() + " : " + f.getSimpleName());
            }
            Set<CtMethod<?>> m = s.getMethods();
            System.out.println("Methodes : ");
            for(CtMethod<?> m1 : m) {
                System.out.println("\t" + m1.getModifiers() + " : " + m1.getSimpleName());
                CtBlock<?> b = m1.getBody();
                if (b == null) continue;
                List<CtInvocation<?>> invok = b.getElements(Objects::nonNull);
                if (!invok.isEmpty()) System.out.println("\t Appels");
                for (CtInvocation<?> i : invok) {
                    System.out.println("\t"+"\t" + i.getType() + " " + i.getExecutable().getSimpleName());
                }
            }
        }
    }
}