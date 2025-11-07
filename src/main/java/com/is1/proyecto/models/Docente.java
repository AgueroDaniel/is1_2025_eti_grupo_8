package com.is1.proyecto.models;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table ("docente") //relaciona esta clase con la bdd
public class Docente extends Model{

    public Integer getDni() {
        return getInteger("dni");  // Obtiene el valor de la columna 'dni'
    }
    
    public void setDni(Integer dni) {
        set("dni", dni); // establece el valor de dni para la columna dni
    }

    public String getDepartament(){
        return getString("departament"); //retorna el valor de la columna departamento
    }

    public void setDepartament(String departament){
        set("departament", departament); //le asigna departamento a la columna departamento
    }

    public String getCorreo(){
        return getString("correo"); //retorna el valor de la columna correo
    }

    public void setCorreo(String correo){
        set("correo", correo); //le asigna correo a la columna correo
    }
}