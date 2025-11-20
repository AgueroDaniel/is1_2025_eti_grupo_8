package com.is1.proyecto.models;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;
@Table ("materia") //relaciona esta clase con la bdd
public class Materia extends Model {

    public Integer getEncargado(){
        return getInteger("encargado"); //retorna el valor de la columna encargado
    }
    public void setEncargado(Integer encargado){
        set("encargado", encargado); //le asigna encargado a la columna encargado
    }

    public String getNombreMateria(){
        return getString("nombreMateria"); //retorna el valor de la columna nombreMateria
    }
    public void setNombreMateria(String nombreMateria){
        set("nombreMateria", nombreMateria); //le asigna nombreMateria a la columna nombreMateria
    }
}
