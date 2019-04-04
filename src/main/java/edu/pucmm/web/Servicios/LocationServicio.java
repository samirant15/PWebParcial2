package edu.pucmm.web.Servicios;

import com.maxmind.geoip2.DatabaseReader;

import java.io.File;

public class LocationServicio {
    private static LocationServicio instancia;
    public static DatabaseReader locationReader;

    private LocationServicio() {
//        var database = getResourceAsStream(File.separator+"Location"+File.separator+"GeoLite2-City.mmdb");
        File database = new File("src"+File.separator+"main"+File.separator+"resources"+File.separator+"Location"+File.separator+"GeoLite2-City.mmdb");
//        getClass().getClassLoader().getResourceAsStream(File.separator+"Location"+File.separator+"GeoLite2-City.mmdb")
        try {locationReader = new DatabaseReader.Builder(database).build();}
        catch (Exception e) {System.out.println(e);}
    }

    public static LocationServicio getInstancia(){
        if(instancia==null){
            instancia = new LocationServicio();
        }
        return instancia;
    }
}