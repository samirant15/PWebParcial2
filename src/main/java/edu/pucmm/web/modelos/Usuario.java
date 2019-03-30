package edu.pucmm.web.modelos;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Usuario implements Serializable {
    @Id
    private String usuario;
    private String nombre;
    private String contrasena;

    public Usuario() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
