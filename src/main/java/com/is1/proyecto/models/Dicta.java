package com.is1.proyecto.models;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("dicta")
public class Dicta extends Model {

    
    public Integer getDniDocente() {
        return getInteger("dni_docente");
    }

    public void setDniDocente(Integer dniDocente) {
        set("dni_docente", dniDocente);
    }

    public Integer getIdMateria() {
        return getInteger("id_materia");
    }

    public void setIdMateria(Integer idMateria) {
        set("id_materia", idMateria);
    }

}