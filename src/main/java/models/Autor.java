package models;

import java.time.LocalDate;

public class Autor {
    private int id;
    private String nombre;
    private String apellido;
    private String nacionalidad;
    private LocalDate fechaNacimiento;

    public Autor() {
    }
    public Autor(String nombre, String apellido, String nacionalidad, LocalDate fechaNacimiento) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.nacionalidad = nacionalidad;
        this.fechaNacimiento = fechaNacimiento;
    }
    public Autor(int id, String nombre, String apellido, String nacionalidad, LocalDate fechaNacimiento) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nacionalidad = nacionalidad;
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public String getApellido() {
        return apellido;
    }
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
    public String getNacionalidad() {
        return nacionalidad;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    @Override
    public String toString() {
        return "models.Autor{id=" + id + ", nombre='" + nombre + "', apellido='" + apellido +
                "', nacionalidad='" + nacionalidad + "', fechaNacimiento=" + fechaNacimiento + "}";
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
