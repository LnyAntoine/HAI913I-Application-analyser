package com.example.services;

import com.example.services.visitor.CallingVisitor;

import java.util.ArrayList;
import java.util.HashMap;

public class CallingServices {
    private CallingVisitor visitor;
    private  HashMap<String, ArrayList<String>> invocations;

    public  CallingServices(CallingVisitor visitor) {
        this.visitor = visitor;
    }

    public void generateGraphFilter(ArrayList<String> filters) {
        if (filters == null || filters.isEmpty()) {
            generateFullGraph();
        }
        else {
            generateFullGraph();
        }
    }

    public void generateFullGraph(){
        this.invocations = visitor.getInvocations();
    }

    private String safeLabel(String in) {
        if (in == null) return "";
        return in.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public String getGraphAsDot(){
        StringBuilder dot = new StringBuilder();
        dot.append("digraph CallingGraph {\n");
        if (invocations == null || invocations.isEmpty()) {
            dot.append("}\n");
            return dot.toString();
        }
        // Ajout des noeuds
        for (String node : invocations.keySet()) {
            dot.append("  \"").append(safeLabel(node)).append("\";\n");
        }
        // Ajout des relations
        for (String from : invocations.keySet()) {

            ArrayList<String> targets = invocations.get(from);
            if (targets != null) {
                for (String to : targets) {
                    dot.append("  \"").append(safeLabel(from)).append("\" -> \"")
                            .append(safeLabel(to)).append("\";\n");
                }
            }
        }
        dot.append("}\n");
        return dot.toString();
    }
}
