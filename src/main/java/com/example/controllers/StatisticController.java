package com.example.controllers;

import com.example.services.StatisticCalculatorServices;
import com.example.services.visitor.StatisticsVisitor;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import spoon.reflect.CtModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
public class StatisticController {

    @PostMapping("/analyze/statistics")
    @ResponseBody
    public Map<String, Object> analyzeStatistics(@RequestParam("directory") ArrayList<String> directory, @RequestParam(value = "x", required = false, defaultValue = "0") int x,
                                                 @RequestParam(value = "excluded", required = false) ArrayList<String> excluded) {
        CtModel model = AnalysisUtils.buildModel(directory);
        Map<String, Object> result = new HashMap<>();
        if (model == null) {
            result.put("error", "Impossible de construire le modèle à partir du répertoire fourni.");
            return result;
        }
        if (excluded == null) {
            excluded = new ArrayList<>();
        }
        StatisticsVisitor statisticsVisitor = new StatisticsVisitor();
        ArrayList<String> finalExcluded = excluded;
        model.getAllTypes().stream()
                .filter(ctType -> !finalExcluded.contains(ctType.getQualifiedName()))
                .forEach(type -> type.accept(statisticsVisitor));
        StatisticCalculatorServices statisticCalculatorServices = new StatisticCalculatorServices(statisticsVisitor);
        statisticCalculatorServices.setX(x);
        statisticCalculatorServices.calculateFilter(new ArrayList<>());
        Map<String, String> calculatorResults = new HashMap<>(statisticCalculatorServices.getMethodMessages());
        Map<String,Object> resultTotal = new HashMap<>();
        resultTotal.put("statisticData", Map.of("result", calculatorResults, "order", statisticCalculatorServices.getOrder()));
        return resultTotal;
    }
}
