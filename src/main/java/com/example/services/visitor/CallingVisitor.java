package com.example.services.visitor;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.CtScanner;

import java.util.*;

public class CallingVisitor extends CtScanner {
    private final HashMap<String, ArrayList<String>> invocations = new HashMap<>();

    @Override
    public <T> void visitCtInvocation(CtInvocation<T> invocation) {
        super.visitCtInvocation(invocation);
        String parentMethodName = invocation.getParent(CtMethod.class)!=null?invocation.getParent(CtMethod.class).getSimpleName():"";
        if (parentMethodName.isEmpty()) {return;}
        String methodName = invocation.getExecutable().getSimpleName();
        if (!invocations.containsKey(parentMethodName)) {
            invocations.put(parentMethodName, new ArrayList<>());
        }
        ArrayList<String> list = invocations.get(parentMethodName);
        if(!invocations.get(parentMethodName).contains(methodName)) {
            list.add(methodName);
            invocations.put(parentMethodName, list);
        }
    }

    public HashMap<String, ArrayList<String>> getInvocations() {
        return invocations;
    }

    public String getHashMapToString(){
        StringBuilder sb = new StringBuilder();
        sb.append("@startuml\n");

        for (Map.Entry<String, ArrayList<String>> entry : invocations.entrySet()) {
            String source = entry.getKey();
            for (String target : entry.getValue()) {
                sb.append(source).append(" --> ").append(target).append("\n");
            }
        }
        sb.append("@enduml");
        return sb.toString();
    }
}
