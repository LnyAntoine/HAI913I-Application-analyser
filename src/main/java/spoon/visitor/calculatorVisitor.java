package spoon.visitor;

import spoon.reflect.declaration.*;
import spoon.reflect.visitor.CtScanner;

import java.lang.annotation.Annotation;
import java.util.HashMap;

public class calculatorVisitor extends CtScanner {

    private int totalClassesCounter = 0;
    private int totalFieldsCounter = 0;
    private int totalMethodsCounter = 0;
    private int totalMethodLineCounter = 0;
    private int maxParameterCounter = 0;
    private int totalLineCounter = 0;
    private int totalPackageCounter = 0;
    private final HashMap<String, Integer> classMethodCountMap = new HashMap<>();
    private final HashMap<String, Integer> classAttributeCountMap = new HashMap<>();
    private final HashMap<String, HashMap<String, Integer>> linePerMethodPerClass = new HashMap<>();

    @Override
    public <T> void visitCtClass(CtClass<T> ctClass) {
        super.visitCtClass(ctClass);
        totalClassesCounter++;
        if (ctClass.getPosition().isValidPosition()) {
            int start = ctClass.getPosition().getLine();
            int end = ctClass.getPosition().getEndLine();
            totalLineCounter += (end - start + 1);
        }
        classAttributeCountMap.put(ctClass.getSimpleName(), ctClass.getFields().size());
        classMethodCountMap.put(ctClass.getSimpleName(), ctClass.getMethods().size());
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
            if (!linePerMethodPerClass.containsKey(m.getParent(CtClass.class).getSimpleName())) {
                linePerMethodPerClass.put(m.getParent(CtClass.class).getSimpleName(), new HashMap<>());
            }
            linePerMethodPerClass.get(m.getParent(CtClass.class).getSimpleName()).
                    put(m.getSimpleName(),
                            m.getBody().getStatements().size());
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

    public int getTotalClassesCounter() {
        return totalClassesCounter;
    }

    public int getTotalFieldsCounter() {
        return totalFieldsCounter;
    }

    public int getTotalMethodsCounter() {
        return totalMethodsCounter;
    }

    public int getTotalMethodLineCounter() {
        return totalMethodLineCounter;
    }

    public int getMaxParameterCounter() {
        return maxParameterCounter;
    }

    public int getTotalLineCounter() {
        return totalLineCounter;
    }

    public int getTotalPackageCounter() {
        return totalPackageCounter;
    }

    public HashMap<String, Integer> getClassMethodCountMap() {
        return classMethodCountMap;
    }

    public HashMap<String, Integer> getClassAttributeCountMap() {
        return classAttributeCountMap;
    }

    public HashMap<String, HashMap<String, Integer>> getLinePerMethodPerClass() {
        return linePerMethodPerClass;
    }
}
