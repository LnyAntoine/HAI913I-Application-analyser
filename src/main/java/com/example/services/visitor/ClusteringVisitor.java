package com.example.services.visitor;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.CtScanner;

import java.util.ArrayList;
import java.util.HashMap;

public class ClusteringVisitor extends CtScanner {
    private HashMap<String, //Classe mere
            HashMap<String, //Type de relation invoc, attribut, heritage ?
                    HashMap<String, //Classe fille
                            HashMap<String,//Nom de la méthode
                                    HashMap<String, //Option de la methode
                                            Integer //Valeur
                                            >>>>> clusteringMap;



    private ArrayList<String> classesList;
    public <T> void visitCtInvocation(CtInvocation<T> ctInvocation) {
        super.visitCtInvocation(ctInvocation);
        String parentClass = ctInvocation.getParent(CtClass.class).getQualifiedName();

        String targetClass = ctInvocation.getExecutable() != null && ctInvocation.getExecutable().getDeclaringType() != null
                ? ctInvocation.getExecutable().getDeclaringType().getQualifiedName()
                : null;

        CtMethod ctMethod = (CtMethod) ctInvocation.getExecutable();
        String targetMethodName = ctInvocation.getExecutable() != null
                ? ctInvocation.getExecutable().getSimpleName()
                : null;

        if (parentClass == null || targetClass == null || targetMethodName == null) {
            return;
        }
        if(clusteringMap==null){
            clusteringMap = new HashMap<>();
        }
        if(!clusteringMap.containsKey(parentClass)) {
            clusteringMap.put(parentClass, new HashMap<>());
        }
        if(!clusteringMap.get(parentClass).containsKey("invocation")) {
            clusteringMap.get(parentClass).put("invocation", new HashMap<>());
        }
        if(!clusteringMap.get(parentClass).get("invocation").containsKey(targetClass)) {
            clusteringMap.get(parentClass).get("invocation").put(targetClass, new HashMap<>());
        }
        HashMap<String, HashMap<String, Integer>> methodMap = clusteringMap.get(parentClass).get("invocation").get(targetClass);
        if (!methodMap.containsKey(targetMethodName)) {
            HashMap<String,Integer> tempMap = new HashMap<>();
            tempMap.put("call", 1);
            tempMap.put("params", ctMethod.getParameters().size());
            methodMap.put(targetMethodName, tempMap);
        }
        else {
            HashMap<String,Integer> tempMap = methodMap.get(targetMethodName);
            tempMap.put("call", tempMap.get("call") + 1);
            methodMap.put(targetMethodName, tempMap);
        }

    }

    public <T> void visitCtClass(CtClass<T> ctClass) {
        if(classesList==null){
            classesList = new ArrayList<>();
        }
        classesList.add(ctClass.getQualifiedName());
        super.visitCtClass(ctClass);
    }
    public ArrayList<String> getClassesList() {
        return classesList;
    }
    public HashMap<String, HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>>> getClusteringMap() {
        return clusteringMap;
    }
}
