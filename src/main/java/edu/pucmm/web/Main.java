package edu.pucmm.web;

import edu.pucmm.web.Servicios.UsuarioServicio;
import edu.pucmm.web.modelos.Usuario;
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

        if(UsuarioServicio.getInstancia().find("samirant15") == null){
            Usuario user = new Usuario();
            user.setNombre("Samir Comprés");
            user.setUsuario("samirant15");
            user.setContrasena("123123");
            UsuarioServicio.getInstancia().crear(user);
        }

        get("/", (request, response) -> {
            return renderThymeleaf(modelo, "/login");
        });
    }
}
