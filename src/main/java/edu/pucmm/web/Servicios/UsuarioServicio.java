package edu.pucmm.web.Servicios;

import edu.pucmm.web.modelos.Usuario;

public class UsuarioServicio extends GestionDb<Usuario>  {
    private static UsuarioServicio instancia;

    private UsuarioServicio() {
        super(Usuario.class);
    }

    public static UsuarioServicio getInstancia(){
        if(instancia==null){
            instancia = new UsuarioServicio();
        }
        return instancia;
    }
}
