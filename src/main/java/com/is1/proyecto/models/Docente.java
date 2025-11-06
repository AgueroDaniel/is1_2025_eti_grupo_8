package com.is1.proyecto.models;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table ("docente") //relaciona esta clase con la bdd
public class Docente extends Persona{

    public String getDepartament(){
        return getString("departament"); //retorna el valor de la columna departamento
    }

    public void setDepartament(String departament){
        set("departament", departament); //le asigna departamento a la columna departamento
    }

    public String getCorreo(){
        return getString("Correo"); //retorna el valor de la columna correo
    }

    public void setCorreo(String correo){
        set("correo", correo); //le asigna departamento a la columna correo
    }

    public String getCurso(){
        return getString("Curso"); //retorna el valor de la columna correo
    }

    public void setCurso(String curso){
        set("curso", curso); //le asigna departamento a la columna correo
    } 
}