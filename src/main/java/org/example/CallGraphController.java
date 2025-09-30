package org.example;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import spoon.visitor.callGraphVisitor;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;
import spoon.Launcher;
import spoon.reflect.CtModel;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class CallGraphController {
    @GetMapping("/graph")
    public String showForm() {
        return "graph";
    }

    @PostMapping("/getGraph")
    @ResponseBody
    public Map<String, Object> getGraph(@RequestParam("directory") String directory) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(directory);
        launcher.getEnvironment().setNoClasspath(true);
        CtModel model = launcher.buildModel();
        callGraphVisitor visitor = new callGraphVisitor();
        model.getAllTypes().forEach(type -> type.accept(visitor));
        HashMap<String, ArrayList<String>> invocations = visitor.getInvocations();
        // Transformer en format D3.js
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
        HashMap<String, Object> result = new HashMap<>();
        result.put("nodes", nodes);
        result.put("links", links);
        return result;
    }
}
