package edu.pucmm.web;

import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.staticFiles;

public class Main {
    static Map<String, Object> modelo = new HashMap<>();

    public static String renderThymeleaf(Map<String, Object> model, String templatePath) {
        return new ThymeleafTemplateEngine().render(new ModelAndView(model, templatePath));
    }

    public static void main(String[] args) {
        staticFiles.location("/public");

        get("/", (request, response) -> {
            return renderThymeleaf(modelo, "/login");
        });
    }
}
