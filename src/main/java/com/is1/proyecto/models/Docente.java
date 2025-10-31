package com.is1.proyecto.models;
import org.javalite.activejdbc.annotations.Table;

@Table ("docente") //relaciona esta clase con la bdd

public class Docente extends Persona{

    public String getDepartament(){
        return getString("departament"); //retorna el valor de la columna departamento
    }

    public void setDepartament(String departament){
        set("departament", departament); //le asigna departamento a la columna departamento
    }

    public String getCourse(){
        return getString("course"); //retorna el valor de la columna curso
    }

    public void setCourse(String course){
        set ("course", course); //le asigna curso a la columna curso
    }

}
