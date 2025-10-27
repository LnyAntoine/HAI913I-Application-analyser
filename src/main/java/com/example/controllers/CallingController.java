package com.example.controllers;

import com.example.services.CallingServices;
import com.example.services.visitor.CallingVisitor;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import spoon.reflect.CtModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CallingController {

    @PostMapping("/analyze/calling")
    @ResponseBody
    public Map<String, Object> analyzeCalling(@RequestParam("directory") ArrayList<String> directory,
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
        CallingVisitor callingVisitor = new CallingVisitor();
        ArrayList<String> finalExcluded = excluded;
        model.getAllTypes().stream()
                .filter(ctType -> !finalExcluded.contains(ctType.getQualifiedName()))
                .forEach(type -> type.accept(callingVisitor));
        CallingServices callingServices = new CallingServices(callingVisitor);
        callingServices.generateGraphFilter(new ArrayList<>());
        result.put("callGraphData", callingServices.getGraphAsDot());
        return result;
    }
}
