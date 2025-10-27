package com.example.controllers;

import com.example.services.CallingServices;
import com.example.services.CouplingServices;
import com.example.services.ClusteringServices;
import com.example.services.StatisticCalculatorServices;
import com.example.services.visitor.CallingVisitor;
import com.example.services.visitor.CouplingVisitor;
import com.example.services.visitor.ClusteringVisitor;
import com.example.services.visitor.StatisticsVisitor;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import spoon.reflect.CtModel;

import java.util.*;

@Controller
public class CombinedController {

    @PostMapping("/getGraph")
    @ResponseBody
    public Map<String, Object> getGraph(@RequestParam("directory") String directory, @RequestParam(value = "x", required = false, defaultValue = "0") int x,
                                        @RequestParam(value = "cp", required = false, defaultValue = "0.5") float cp,
                                        @RequestParam(value = "excluded", required = false) ArrayList<String> excluded) {
        CtModel model = AnalysisUtils.buildModel(directory);

        HashMap<String, Object> resultTotal = new HashMap<>();

        if (model == null) {
            resultTotal.put("error", "Impossible de construire le modèle à partir du répertoire fourni.");
            return resultTotal;
        }
        if (excluded == null) {
            excluded = new ArrayList<>();
        }

        ArrayList<String> callFilter = new ArrayList<>();
        ArrayList<String> statisticFiler = new ArrayList<>();
        ArrayList<String> couplingFilter = new ArrayList<>();
        ArrayList<String> clusteringFilter = new ArrayList<>();
        resultTotal.put("callGraphData", this.getCallingGraphData(model,callFilter, excluded));
        resultTotal.put("couplingGraphData",this.getCouplingGraphData(model,couplingFilter,excluded));
        resultTotal.put("statisticData",this.getStatisticCalculatorData(model,x,statisticFiler,excluded));

        // Récupère à la fois le dendrogramme du clustering et le dendrogramme des modules
        Map<String, String> clusterResults = this.getClusterGraphData(model,clusteringFilter,cp,excluded);
        resultTotal.put("clusterGraphData", clusterResults.get("dendrogram"));
        resultTotal.put("modulesGraphData", clusterResults.get("modules"));
        return resultTotal;
    }

    private Object getCouplingGraphData(CtModel model,ArrayList<String> filters,ArrayList<String> excluded){
        CouplingVisitor couplingGraphVisitor = new CouplingVisitor();
        if (excluded != null && !excluded.isEmpty())
            model.getAllTypes().stream()
                    .filter(ctType -> !excluded.contains(ctType.getQualifiedName()))
                    .forEach(type -> type.accept(couplingGraphVisitor));
        else
            model.getAllTypes().forEach(type -> type.accept(couplingGraphVisitor));
        CouplingServices couplingGraphServices = new CouplingServices(couplingGraphVisitor);
        couplingGraphServices.generateGraphFilter(filters);
        return  couplingGraphServices.getGraphAsDot();
    }
    private Object getCallingGraphData(CtModel model,ArrayList<String> filters,ArrayList<String> excluded){
        CallingVisitor callingVisitor = new CallingVisitor();
        if (excluded != null && !excluded.isEmpty())
            model.getAllTypes().stream()
                    .filter(ctType -> !excluded.contains(ctType.getQualifiedName()))
                    .forEach(type -> type.accept(callingVisitor));
        else
            model.getAllTypes().forEach(type -> type.accept(callingVisitor));
        CallingServices callingServices = new CallingServices(callingVisitor);
        callingServices.generateGraphFilter(filters);
        return callingServices.getGraphAsDot();
    }

    // Retourne un map contenant le DOT du dendrogramme et le DOT des modules (après génération)
    private Map<String, String> getClusterGraphData(CtModel model,ArrayList<String> filters,float CP,ArrayList<String> excluded){
        ClusteringVisitor clusteringVisitor = new ClusteringVisitor();
        if (filters != null && !filters.isEmpty())
            model.getAllTypes().stream()
                    .filter(ctType -> !excluded.contains(ctType.getQualifiedName()))
                    .forEach(type -> type.accept(clusteringVisitor));
        else
            model.getAllTypes().forEach(type -> type.accept(clusteringVisitor));
        ClusteringServices clusteringServices = new ClusteringServices(clusteringVisitor,CP);
        // Lance le clustering hiérarchique
        clusteringServices.clusteringHierarchique();
        String dendrogramDot = clusteringServices.getDendrogramDot();
        // Génère les modules à partir du cluster final
        clusteringServices.generateModules();
        String modulesDot = clusteringServices.getModulesDendogramDot();
        Map<String, String> result = new HashMap<>();
        result.put("dendrogram", dendrogramDot);
        result.put("modules", modulesDot);
        return result;
    }

    private Object getStatisticCalculatorData(CtModel model,int x,ArrayList<String> filters,ArrayList<String> excluded){
        StatisticsVisitor statisticsVisitor = new StatisticsVisitor();

        if (excluded != null && !excluded.isEmpty())
            model.getAllTypes().stream()
                    .filter(ctType -> !excluded.contains(ctType.getQualifiedName()))
                    .forEach(type -> type.accept(statisticsVisitor));
        else model.getAllTypes().forEach(type -> type.accept(statisticsVisitor));

        StatisticCalculatorServices statisticCalculatorServices = new StatisticCalculatorServices(statisticsVisitor);
        statisticCalculatorServices.setX(x);
        statisticCalculatorServices.calculateFilter(filters);
        Map<String, String> calculatorResults = new HashMap<>(statisticCalculatorServices.getMethodMessages());
        Map<String,Object> resultTotal = new HashMap<>();

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

        resultTotal.put("result", formattedResults);
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
