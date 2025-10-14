package spoon.visitor;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.CtScanner;

import java.util.HashMap;

public class couplingGraphVisitor extends CtScanner {
    private final HashMap<String,HashMap<String,HashMap<String,Integer>>> couplingGraph = new HashMap<>();
    @Override
    public <T> void visitCtInvocation(CtInvocation<T> ctInvocation) {
        super.visitCtInvocation(ctInvocation);
        String parentClass = ctInvocation.getParent(CtClass.class).getQualifiedName();
        String targetClass = ctInvocation.getExecutable().getDeclaringType().getQualifiedName();
        String targetMethodName = ctInvocation.getExecutable().getSimpleName();
        if (parentClass == null || targetClass == null || targetMethodName == null) {
            return;
        }
        if(!couplingGraph.containsKey(parentClass)) {
            couplingGraph.put(parentClass, new HashMap<>());
        }
        if(!couplingGraph.get(parentClass).containsKey(targetClass)) {
            couplingGraph.get(parentClass).put(targetClass, new HashMap<>());
        }
        HashMap<String, Integer> methodMap = couplingGraph.get(parentClass).get(targetClass);
        if (!methodMap.containsKey(targetMethodName)) {
            methodMap.put(targetMethodName, 1);
        } else {
            methodMap.put(targetMethodName, methodMap.get(targetMethodName) + 1);
        }
        couplingGraph.get(parentClass).put(targetClass, methodMap);
    }
    public HashMap<String, HashMap<String, HashMap<String, Integer>>> getCouplingGraph() {
        return couplingGraph;
    }
}
