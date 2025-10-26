package com.example.controllers;

import spoon.Launcher;
import spoon.reflect.CtModel;

public class AnalysisUtils {
    // Construit un CtModel à partir d'un répertoire source
    public static CtModel buildModel(String directory) {
        if (directory == null) return null;
        try {
            Launcher launcher = new Launcher();
            launcher.addInputResource(directory);
            // Eviter d'échouer sur le classpath complet
            launcher.getEnvironment().setNoClasspath(true);
            launcher.buildModel();
            return launcher.getModel();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

