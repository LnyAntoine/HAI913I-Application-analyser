package com.example.controllers;

import com.example.services.ClusteringServices;
import com.example.services.visitor.ClusteringVisitor;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import spoon.reflect.CtModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ClusteringController {

    @PostMapping("/analyze/clustering")
    @ResponseBody
    public Map<String, Object> analyzeClustering(@RequestParam("directory") ArrayList<String> directory, @RequestParam(value = "cp", required = false, defaultValue = "0.5") float cp,
                                                 @RequestParam(value = "excluded", required = false) ArrayList<String> excluded

    ) {
        CtModel model = AnalysisUtils.buildModel(directory);
        Map<String, Object> result = new HashMap<>();
        if (model == null) {
            result.put("error", "Impossible de construire le modèle à partir du répertoire fourni.");
            return result;
        }
        if (excluded == null) {
            excluded = new ArrayList<>();
        }
        ClusteringVisitor clusteringVisitor = new ClusteringVisitor();
        ArrayList<String> finalExcluded = excluded;
        model.getAllTypes().stream()
                .filter(ctType -> !finalExcluded.contains(ctType.getQualifiedName()))
                .forEach(type -> type.accept(clusteringVisitor));

        ClusteringServices clusteringServices = new ClusteringServices(clusteringVisitor, cp);
        clusteringServices.clusteringHierarchique();
        result.put("clusterGraphData", clusteringServices.getDendrogramDot());
        // Génération et ajout des modules (DOT)
        clusteringServices.generateModules();
        result.put("modulesGraphData", clusteringServices.getModulesDendogramDot());
        return result;
    }
}
