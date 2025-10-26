package com.example.controllers;

import com.example.services.CouplingServices;
import com.example.services.visitor.CouplingVisitor;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import spoon.reflect.CtModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CouplingController {

    @PostMapping("/analyze/coupling")
    @ResponseBody
    public Map<String, Object> analyzeCoupling(@RequestParam("directory") String directory) {
        CtModel model = AnalysisUtils.buildModel(directory);
        Map<String, Object> result = new HashMap<>();
        if (model == null) {
            result.put("error", "Impossible de construire le modèle à partir du répertoire fourni.");
            return result;
        }
        CouplingVisitor couplingVisitor = new CouplingVisitor();
        model.getAllTypes().forEach(type -> type.accept(couplingVisitor));
        CouplingServices couplingServices = new CouplingServices(couplingVisitor);
        couplingServices.generateGraphFilter(new ArrayList<>());
        result.put("couplingGraphData", couplingServices.getGraphAsDot());
        return result;
    }
}

