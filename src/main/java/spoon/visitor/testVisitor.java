package spoon.visitor;

import spoon.reflect.declaration.*;
import spoon.reflect.visitor.CtScanner;

import java.lang.annotation.Annotation;
import java.util.HashMap;

public class testVisitor extends CtScanner {

    static int totalClassesCounter = 0;
    static int totalFieldsCounter = 0;
    static int totalMethodsCounter = 0;
    static int totalMethodLineCounter = 0;
    static int maxParameterCounter = 0;
    static int totalLineCounter = 0;
    static int totalPackageCounter = 0;
    static HashMap<String, Integer> classMethodCountMap = new HashMap<>();
    static HashMap<String, Integer> classAttributeCountMap = new HashMap<>();
    static HashMap<String,HashMap<String,Integer>> classMethodAttributeCountMap = new HashMap<>();


    @Override
    public <T> void visitCtClass(CtClass<T> ctClass) {
        super.visitCtClass(ctClass);
        totalClassesCounter++;
        classMethodCountMap.put(ctClass.getSimpleName(), ctClass.getMethods().size());
        if (ctClass.getPosition().isValidPosition()) {
            int start = ctClass.getPosition().getLine();
            int end = ctClass.getPosition().getEndLine();
            totalLineCounter += (end - start + 1);
        }
        classAttributeCountMap.put(ctClass.getSimpleName(), ctClass.getFields().size());
        HashMap<String,Integer> methodAttributeMap = new HashMap<>();
        ctClass.getMethods().forEach(method -> {
            methodAttributeMap.put(method.getSimpleName(), method.getParameters().size());
        });
        classMethodAttributeCountMap.put(ctClass.getSimpleName(), methodAttributeMap);
    }

    @Override
    public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
        super.visitCtEnum(ctEnum);
        if (ctEnum.getPosition().isValidPosition()) {
            int start = ctEnum.getPosition().getLine();
            int end = ctEnum.getPosition().getEndLine();
            totalLineCounter += (end - start + 1);
        }
    }

    @Override
    public <T> void visitCtInterface(CtInterface<T> intrface) {
        super.visitCtInterface(intrface);
        if (intrface.getPosition().isValidPosition()) {
            int start = intrface.getPosition().getLine();
            int end = intrface.getPosition().getEndLine();
            totalLineCounter += (end - start + 1);
        }
    }

    @Override
    public <A extends Annotation> void visitCtAnnotationType(CtAnnotationType<A> annotationType) {
        super.visitCtAnnotationType(annotationType);
        if (annotationType.getPosition().isValidPosition()) {
            int start = annotationType.getPosition().getLine();
            int end = annotationType.getPosition().getEndLine();
            totalLineCounter += (end - start + 1);
        }
    }



    @Override
    public <T> void visitCtField(CtField<T> f) {
        super.visitCtField(f);
        totalFieldsCounter++;
    }


    @Override
    public <T> void visitCtMethod(CtMethod<T> m) {
        super.visitCtMethod(m);
        totalMethodsCounter++;
        if (m.getBody() != null) {
            totalMethodLineCounter += m.getBody().getStatements().size();
        }
        if (m.getParameters().size() > maxParameterCounter) {
            maxParameterCounter = m.getParameters().size();
        }
    }

    @Override
    public void visitCtPackage(CtPackage ctPackage) {
        super.visitCtPackage(ctPackage);
        totalPackageCounter++;
    }


}
