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
    @GetMapping("/")
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

        // Mapping des noms de méthodes vers les textes descriptifs
        Map<String, String> methodDescriptions = new HashMap<>();
        methodDescriptions.put("calculateNbClass", "1. Nombre de classes de l'application");
        methodDescriptions.put("calculateNbLine", "2. Nombre de lignes de code de l'application");
        methodDescriptions.put("calculateNbMethod", "3. Nombre total de méthodes de l'application");
        methodDescriptions.put("calculateNbPackage", "4. Nombre total de packages de l'application");
        methodDescriptions.put("calculateAverageMethodPerClass", "5. Nombre moyen de méthodes par classe");
        methodDescriptions.put("calculateAverageLinePerMethod", "6. Nombre moyen de lignes de code par méthode");
        methodDescriptions.put("calculateAverageAttributePerClass", "7. Nombre moyen d'attributs par classe");
        methodDescriptions.put("calculateTop10PercentNbMethodPerClass", "8. Les 10% des classes qui possèdent le plus grand nombre de méthodes");
        methodDescriptions.put("calculateTop10PercentNbAttributePerClass", "9. Les 10% des classes qui possèdent le plus grand nombre d'attributs");
        methodDescriptions.put("calculateTop10PercentNbAttributeMethodePerClass", "10. Les classes qui font partie en même temps des deux catégories précédentes");
        methodDescriptions.put("moreThanXMethods", "11. Les classes qui possèdent plus de " + x + " méthodes");
        methodDescriptions.put("calculateTop10PercentMethodPerLinPerClass", "12. Les 10% des méthodes qui possèdent le plus grand nombre de lignes de code (par classe)");
        methodDescriptions.put("maxParameterMethod", "13. Le nombre maximal de paramètres par rapport à toutes les méthodes de l'application");

        // Conversion des résultats avec les textes descriptifs
        Map<String, String> formattedResults = new HashMap<>();
        for (Map.Entry<String, String> entry : calculatorResults.entrySet()) {
            String descriptiveText = methodDescriptions.get(entry.getKey());
            if (descriptiveText != null) {
                formattedResults.put(descriptiveText, entry.getValue());
            }
        }

        // Préparation de la réponse
        HashMap<String, Object> result = new HashMap<>();
        result.put("nodes", nodes);
        result.put("links", links);
        result.put("calculatorResults", formattedResults);
        // Ajout de l'ordre des méthodes avec les textes descriptifs
        List<String> calculatorOrder = List.of(
            "1. Nombre de classes de l'application",
            "2. Nombre de lignes de code de l'application",
            "3. Nombre total de méthodes de l'application",
            "4. Nombre total de packages de l'application",
            "5. Nombre moyen de méthodes par classe",
            "6. Nombre moyen de lignes de code par méthode",
            "7. Nombre moyen d'attributs par classe",
            "8. Les 10% des classes qui possèdent le plus grand nombre de méthodes",
            "9. Les 10% des classes qui possèdent le plus grand nombre d'attributs",
            "10. Les classes qui font partie en même temps des deux catégories précédentes",
            "11. Les classes qui possèdent plus de " + x + " méthodes",
            "12. Les 10% des méthodes qui possèdent le plus grand nombre de lignes de code (par classe)",
            "13. Le nombre maximal de paramètres par rapport à toutes les méthodes de l'application"
        );
        result.put("calculatorOrder", calculatorOrder);
        return result;
    }
}
