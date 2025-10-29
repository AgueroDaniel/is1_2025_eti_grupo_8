package com.is1.proyecto.models;

//import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("personas") // si tu tabla se llama así; si no, cámbialo por el nombre correcto
public class Persona extends User {

    // 🔹 Getter y Setter de DNI
    public Integer getDni() {
        return getInteger("dni");
    }

    public void setDni(Integer dni) {
        set("dni", dni);
    }

    // 🔹 Getter y Setter de Teléfono
    public String getTelefono() {
        return getString("telefono");
    }

    public void setTelefono(String telefono) {
        set("telefono", telefono);
    }

    // 🔹 Getter y Setter de Dirección
    public String getDireccion() {
        return getString("direccion");
    }

    public void setDireccion(String direccion) {
        set("direccion", direccion);
    }

    // 🔹 Getter y Setter de Edad
    public Integer getEdad() {
        return getInteger("edad");
    }

    public void setEdad(Integer edad) {
        set("edad", edad);
    }

    // 🔹 Getter y Setter de Fecha de Nacimiento
    public String getFechaNacimiento() {
        return getString("fecha_nacimiento");
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        set("fecha_nacimiento", fechaNacimiento);
    }
}
