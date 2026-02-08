package mateolopez.taller.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reparacion")
public class Reparacion {

    //Atributos

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "coche_id")
    private Coche coche;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mecanico_id")
    private Mecanico mecanico;

    private LocalDate fecha;

    private String descripcion;

    private Integer horas;

    private Double precio;

    //Constructor 
    public Reparacion() {
    }

    public Reparacion(Coche coche, Mecanico mecanico, LocalDate fecha, String descripcion, Integer horas, Double precio) {
        this.coche = coche;
        this.mecanico = mecanico;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.horas = horas;
        this.precio = precio;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public Coche getCoche() {
        return coche;
    }

    public void setCoche(Coche coche) {
        this.coche = coche;
    }

    public Mecanico getMecanico() {
        return mecanico;
    }

    public void setMecanico(Mecanico mecanico) {
        this.mecanico = mecanico;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getHoras() {
        return horas;
    }

    public void setHoras(Integer horas) {
        this.horas = horas;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
