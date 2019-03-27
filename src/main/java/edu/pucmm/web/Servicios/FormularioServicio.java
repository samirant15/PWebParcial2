package edu.pucmm.web.Servicios;

import edu.pucmm.web.modelos.Formulario;

public class FormularioServicio  extends GestionDb<Formulario>  {
    private static FormularioServicio instancia;

    private FormularioServicio() {
        super(Formulario.class);
    }

    public static FormularioServicio getInstancia(){
        if(instancia==null){
            instancia = new FormularioServicio();
        }
        return instancia;
    }
}