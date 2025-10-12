package org.example;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.visitor.callGraphVisitor;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.Calculator;
import spoon.visitor.calculatorVisitor;

@Controller
public class CallGraphController {
    @GetMapping("/graph")
    public String showForm() {
        return "graph";
    }

    @PostMapping("/getGraph")
    @ResponseBody
    public Map<String, Object> getGraph(@RequestParam("directory") String directory, @RequestParam(value = "x", required = false, defaultValue = "0") int x) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(directory);
        launcher.getEnvironment().setNoClasspath(true);
        CtModel model = launcher.buildModel();
        callGraphVisitor visitor = new callGraphVisitor();
        model.getAllTypes().forEach(type -> type.accept(visitor));
        // Génération du graphe d'appels
        HashMap<String, ArrayList<String>> invocations = visitor.getInvocations();
        HashSet<String> nodeSet = new HashSet<>();
        ArrayList<Map<String, String>> links = new ArrayList<>();
        for (String source : invocations.keySet()) {
            nodeSet.add(source);
            for (String target : invocations.get(source)) {
                nodeSet.add(target);
                HashMap<String, String> link = new HashMap<>();
                link.put("source", source);
                link.put("target", target);
                links.add(link);
            }
        }
        ArrayList<Map<String, String>> nodes = new ArrayList<>();
        for (String node : nodeSet) {
            HashMap<String, String> n = new HashMap<>();
            n.put("id", node);
            nodes.add(n);
        }
        // Exécution de Calculator
        calculatorVisitor calcVisitor = new calculatorVisitor(); // ou adapter selon le constructeur
        model.getAllTypes().forEach(type -> type.accept(calcVisitor));
        Calculator calculator = new Calculator(calcVisitor);
        calculator.setX(x);
        calculator.calculateAll();
        Map<String, String> calculatorResults = new HashMap<>(calculator.getMethodMessages());
        // Préparation de la réponse
        HashMap<String, Object> result = new HashMap<>();
        result.put("nodes", nodes);
        result.put("links", links);
        result.put("calculatorResults", calculatorResults);
        // Ajout de l'ordre des méthodes du calculator
        List<String> calculatorOrder = List.of(
            "calculateNbClass",
            "calculateNbLine",
            "calculateNbMethod",
            "calculateNbPackage",
            "calculateAverageMethodPerClass",
            "calculateAverageLinePerMethod",
            "calculateAverageAttributePerClass",
            "calculateTop10PercentNbMethodPerClass",
            "calculateTop10PercentNbAttributePerClass",
            "calculateTop10PercentNbAttributeMethodePerClass",
            "moreThanXMethods",
            "calculateTop10PercentMethodPerLinPerClass",
            "maxParameterMethod"
        );
        result.put("calculatorOrder", calculatorOrder);
        return result;
    }
}
