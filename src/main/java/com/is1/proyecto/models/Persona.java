package com.is1.proyecto.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("personas") // si tu tabla se llama así; si no, cámbialo por el nombre correcto
public class Persona extends User {

    // 🔹 Getter y Setter de DNI
    public Integer getDni() {
        return getInteger("dni");  // Obtiene el valor de la columna 'dni'
    }

    public void setDni(Integer dni) {
        set("dni", dni); // establece el valor de dni para la columna dni
    }

    public String getSurname(){
        return getString("surname"); //retorna el valor de la columna apellido
    }

    public void setSurname(String surname){
        set ("surname", surname); //le asigna apellido a la columna apellido
    }

}
