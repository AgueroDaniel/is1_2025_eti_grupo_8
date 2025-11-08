package com.is1.proyecto.models;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("carrera")
public class Carrera extends Model {
    
    public Integer getIdCarrera() {
        return getInteger("id_carrera");
    }

    public void setICarrera(Integer idCarrera) {
        set("id_carrera", idCarrera);
    }

    
    public String getNombre() {
        return getString("nombre");
    }

    public void setNombre(String nombre) {
        set("nombre", nombre);
    }

}