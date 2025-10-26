package com.example.controllers;

import com.example.services.ClusteringServices;
import com.example.services.visitor.ClusteringVisitor;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import spoon.reflect.CtModel;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ClusteringController {

    @PostMapping("/analyze/clustering")
    @ResponseBody
    public Map<String, Object> analyzeClustering(@RequestParam("directory") String directory, @RequestParam(value = "cp", required = false, defaultValue = "0.5") float cp) {
        CtModel model = AnalysisUtils.buildModel(directory);
        Map<String, Object> result = new HashMap<>();
        if (model == null) {
            result.put("error", "Impossible de construire le modèle à partir du répertoire fourni.");
            return result;
        }
        ClusteringVisitor clusteringVisitor = new ClusteringVisitor();
        model.getAllTypes().forEach(type -> type.accept(clusteringVisitor));
        ClusteringServices clusteringServices = new ClusteringServices(clusteringVisitor, cp);
        clusteringServices.clusteringHierarchique();
        result.put("clusterGraphData", clusteringServices.getDendrogramDot());
        return result;
    }
}

