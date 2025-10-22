package com.example.controllers;

import com.example.services.ClusteringServices;
import com.example.services.visitor.ClusteringVisitor;
import org.springframework.web.bind.annotation.*;
import com.example.services.CallingServices;
import com.example.services.CouplingServices;
import org.springframework.stereotype.Controller;
import com.example.services.visitor.CallingVisitor;

import java.util.*;

import spoon.Launcher;
import spoon.reflect.CtModel;
import com.example.services.StatisticCalculatorServices;
import com.example.services.visitor.StatisticsVisitor;
import com.example.services.visitor.CouplingVisitor;

@Controller
public class mainController {
    @GetMapping("/")
    public String showForm() {
        return "graph";
    }


    // Nouvelle méthode pour générer le graphe

    @PostMapping("/getGraph")
    @ResponseBody
    public Map<String, Object> getGraph(@RequestParam("directory") String directory, @RequestParam(value = "x", required = false, defaultValue = "0") int x) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(directory);
        launcher.getEnvironment().setNoClasspath(true);
        CtModel model = launcher.buildModel();

        HashMap<String, Object> resultTotal = new HashMap<>();

        ArrayList<String> callFilter = new  ArrayList<>();
        ArrayList<String> statisticFiler = new ArrayList<>();
        ArrayList<String> couplingFilter = new ArrayList<>();
        ArrayList<String> clusteringFilter = new ArrayList<>();
        resultTotal.put("callGraphData", this.getCallingGraphData(model,callFilter));
        resultTotal.put("couplingGraphData",this.getCouplingGraphData(model,couplingFilter));
        resultTotal.put("statisticData",this.getStatisticCalculatorData(model,x,statisticFiler));
        resultTotal.put("clusterGraphData",this.getClusterGraphData(model,clusteringFilter));
        return resultTotal;
    }

    public Object getCouplingGraphData(CtModel model,ArrayList<String> filters){
        CouplingVisitor couplingGraphVisitor = new CouplingVisitor();
        model.getAllTypes().forEach(type -> type.accept(couplingGraphVisitor));
        CouplingServices couplingGraphServices = new CouplingServices(couplingGraphVisitor);
        couplingGraphServices.generateGraphFilter(filters);
        return  couplingGraphServices.getGraphAsDot();
    }
    public Object getCallingGraphData(CtModel model,ArrayList<String> filters){
        CallingVisitor callingVisitor = new CallingVisitor();
        model.getAllTypes().forEach(type -> type.accept(callingVisitor));
        CallingServices callingServices = new CallingServices(callingVisitor);
        callingServices.generateGraphFilter(filters);
        return callingServices.getGraphAsDot();
    }

    public Object getClusterGraphData(CtModel model,ArrayList<String> filters){
        ClusteringVisitor clusteringVisitor = new ClusteringVisitor();
        model.getAllTypes().forEach(type ->{
            type.accept(clusteringVisitor);
        });
        ClusteringServices clusteringServices = new ClusteringServices(clusteringVisitor);
        clusteringServices.clusteringHierarchique();
        return clusteringServices.getDendrogramDot();
    }

    public Object getStatisticCalculatorData(CtModel model,int x,ArrayList<String> filters){
        StatisticsVisitor statisticsVisitor = new StatisticsVisitor();
        model.getAllTypes().forEach(type -> type.accept(statisticsVisitor));
        StatisticCalculatorServices statisticCalculatorServices = new StatisticCalculatorServices(statisticsVisitor);
        statisticCalculatorServices.setX(x);
        statisticCalculatorServices.calculateFilter(filters);
        Map<String, String> calculatorResults = new HashMap<>(statisticCalculatorServices.getMethodMessages());
        Map<String,Object> resultTotal = new HashMap<>();

        //TODO changer ça c'est dégueulasse
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

        resultTotal.put("result", formattedResults);
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
        resultTotal.put("order",calculatorOrder);
        return resultTotal;
    }


}
