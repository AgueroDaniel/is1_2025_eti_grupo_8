package com.is1.proyecto.models;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table ("materia") //relaciona esta clase con la bdd
public class Materia extends Model{

    public Integer getIdMateria() {
        return getInteger("id de materia");  // Obtiene el valor de la columna 'id_materia'
    }
    
    public void setIdMateria(Integer id_materia) {
        set("id_materia", id_materia); // establece el valor de id de materia para la columna id_materia
    }

    public String getNombre(){
        return getString("Nombre"); //retorna el valor de la columna nombre
    }

    public void setNombre(String nombre){
        set("departament", nombre); //le asigna nombre a la columna nombre
    }

    public Integer getIdCarrera(){
        return getInteger("Nombre"); //retorna el valor de la columna nombre
    }

    public void setIdCarrera(Integer id_carrera){
        set("id_carrera", id_carrera); //le asigna nombre a la columna nombre
    }
}