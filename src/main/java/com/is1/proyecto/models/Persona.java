package com.is1.proyecto.models;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("persona") //relaciona esta clase con la bdd
public class Persona extends Model {

    public String getDni() {
        return getString("dni"); // Obtiene el valor de la columna 'dni'
    }

    public void setDni(String dni) {
        set("dni", dni); // establece el valor de dni para la columna dni
    }

    public String getName(){
        return getString("name"); //retorna el valor de la columna nombre
    }

    public void setName(String name){
        set("name", name); //le asigna nombre a la columna nombre
    }

    public String getSurname(){
        return getString("surname"); //retorna el valor de la columna apellido
    }

    public void setSurname(String surname){
        set ("surname", surname); //le asigna apellido a la columna apellido
    }

}
