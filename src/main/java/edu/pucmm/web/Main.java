package edu.pucmm.web;

import com.maxmind.geoip2.record.Location;
import edu.pucmm.web.Servicios.FormularioServicio;
import edu.pucmm.web.Servicios.LocationServicio;
import edu.pucmm.web.modelos.Formulario;
import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {

    static Map<String, Object> modelo = new HashMap<>();

    public static String renderThymeleaf(Map<String, Object> model, String templatePath) {
        return new ThymeleafTemplateEngine().render(new ModelAndView(model, templatePath));
    }

    public static void main(String[] args) {

        if(args.length > 0) {
            port(Integer.parseInt(args[0]));
        }

        staticFiles.location("/templates");
        JsonTransformer jsonTransformer = new JsonTransformer();


        get("/", (request, response) -> {
            return renderThymeleaf(modelo, "/index");
        });

        get("/encuesta", (request, response) -> {
//            response.redirect("encuesta.html"); return null;
            return renderThymeleaf(modelo, "/encuesta");
        });

        post("/encuesta", (request, response) -> {
//            response.redirect("encuesta.html"); return null;
            Formulario form = new Formulario();
            form.setNombre(request.queryParams("nombre"));
            System.out.println("Registrando el registro a nombre de :" + request.queryParams("nombre"));
            form.setSector(request.queryParams("sector"));
            form.setNivel_escolar(request.queryParams("nivel_escolar"));
            InetAddress ipAddress = InetAddress.getByName(request.queryParams("ip"));
            Location location = LocationServicio.getInstancia().locationReader.city(ipAddress).getLocation();
            form.setLatitud(location.getLatitude());
            form.setLongitud(location.getLongitude());
            FormularioServicio.getInstancia().crear(form);
            return "";
        });

        get("/encuestas", (request, response) -> {
            return FormularioServicio.getInstancia().findAll();
        }, jsonTransformer);

        get("/ver", (request, response) -> {
            modelo.put("encuestas", FormularioServicio.getInstancia().findAll());
            return renderThymeleaf(modelo, "/ver");
        });
    }
}
