package com.example.services.visitor;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.CtScanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClusteringVisitor extends CtScanner {
    private HashMap<String, //Classe mere
                    HashMap<String, //Classe fille
                            HashMap<String,//Nom de la méthode
                                    HashMap<String, //type de la valeur
                                            Integer //Valeur
                                            >>>> clusteringInvocationMap;
    private HashMap<String,//Classe mere (contenant l'attribut)
            HashMap<String,//classe fille (type de l'attribut)
                            Integer>> clusteringFieldMap;
    private HashMap<String, //Classe mere
            HashMap<String, //Classe fille (type du paramètre)
                    Integer>> clusteringParamsMap;
    private ArrayList<String> classesList;
    public <T> void visitCtInvocation(CtInvocation<T> ctInvocation) {
        super.visitCtInvocation(ctInvocation);
        String parentClass = ctInvocation.getParent(CtClass.class).getQualifiedName();

        String targetClass = ctInvocation.getExecutable() != null && ctInvocation.getExecutable().getDeclaringType() != null
                ? ctInvocation.getExecutable().getDeclaringType().getQualifiedName()
                : null;

        CtExecutableReference<?> ctMethod = ctInvocation.getExecutable();

        String targetMethodName = ctInvocation.getExecutable() != null
                ? ctInvocation.getExecutable().getSimpleName()
                : null;

        if (parentClass == null || targetClass == null || targetMethodName == null) {
            return;
        }
        if(clusteringInvocationMap ==null){
            clusteringInvocationMap = new HashMap<>();
        }
        if(!clusteringInvocationMap.containsKey(parentClass)) {
            clusteringInvocationMap.put(parentClass, new HashMap<>());
        }
        if(!clusteringInvocationMap.get(parentClass).containsKey(targetClass)) {
            clusteringInvocationMap.get(parentClass).put(targetClass, new HashMap<>());
        }
        HashMap<String, HashMap<String, Integer>> methodMap = clusteringInvocationMap.get(parentClass).get(targetClass);
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
        clusteringInvocationMap.get(parentClass).put(targetClass, methodMap);
    }

    //TODO Probleme ici ? à vérifier ne semble pas enregistrer le comptage
    public <T> void visitCtMethod(CtMethod<T> ctMethod) {
        super.visitCtMethod(ctMethod);
        if (clusteringParamsMap == null) {
            clusteringParamsMap = new HashMap<>();
        }
        List<CtParameter<?>> paramsList = ctMethod.getParameters();
        if (paramsList == null || paramsList.isEmpty()) {
            return;
        }
        String parentClass = ctMethod.getParent(CtClass.class)!=null?ctMethod.getParent(CtClass.class).getQualifiedName():null;
        if (parentClass == null) {
            return;
        }
        if (!clusteringParamsMap.containsKey(parentClass)) {
            clusteringParamsMap.put(parentClass, new HashMap<>());
        }
        HashMap<String, Integer> paramsMap = clusteringParamsMap.get(parentClass);
        for (CtParameter<?> param : paramsList) {
            String paramType = param.getType() != null ? param.getType().getQualifiedName() : "unknown";
            paramsMap.put(paramType, paramsMap.getOrDefault(paramType, 0) + 1);
        }
        clusteringParamsMap.put(parentClass, paramsMap);
    }

    public <T> void visitCtField(CtField<T> ctField) {
        super.visitCtField(ctField);

        if (clusteringFieldMap == null) {
            clusteringFieldMap = new HashMap<>();
        }
        String parentClass = ctField.getParent(CtClass.class).getQualifiedName();
        String fieldType = ctField.getType() != null ? ctField.getType().getQualifiedName() : "unknown";
        if (!clusteringFieldMap.containsKey(parentClass)) {
            clusteringFieldMap.put(parentClass, new HashMap<>());
        }
        HashMap<String, Integer> fieldMap = clusteringFieldMap.get(parentClass);
        fieldMap.put(fieldType, fieldMap.getOrDefault(fieldType, 0) + 1);
        clusteringFieldMap.put(parentClass, fieldMap);
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
    public HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> getClusteringInvocationMap() {
        return clusteringInvocationMap;
    }
    public HashMap<String, //Classe mere
            HashMap<String, //Classe fille (type du paramètre)
                    Integer>> getClusteringParamsMap() {
        return clusteringParamsMap;
    }

    public HashMap<String,//Classe mere (contenant l'attribut)
            HashMap<String,//classe fille (type de l'attribut)
                    Integer>> getClusteringFieldMap() {
        return clusteringFieldMap;
    }
}
