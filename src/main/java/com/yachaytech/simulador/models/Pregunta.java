package com.yachaytech.simulador.models;

import jakarta.persistence.*;

@Entity
@Table(name = "preguntas")
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fase;

    @Column(columnDefinition = "TEXT")
    private String enunciado;

    @Column(name = "opciona")
    private String opcionA;

    @Column(name = "puntajea")
    private Double puntajeA;

    @Column(name = "opcionb")
    private String opcionB;

    @Column(name = "puntajeb")
    private Double puntajeB;

    @Column(name = "opcionc")
    private String opcionC;

    @Column(name = "puntajec")
    private Double puntajeC;

    @Column(name = "opciond")
    private String opcionD;

    @Column(name = "puntajed")
    private Double puntajeD;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFase() { return fase; }
    public void setFase(String fase) { this.fase = fase; }

    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }

    public String getOpcionA() { return opcionA; }
    public void setOpcionA(String opcionA) { this.opcionA = opcionA; }

    public Double getPuntajeA() { return puntajeA; }
    public void setPuntajeA(Double puntajeA) { this.puntajeA = puntajeA; }

    public String getOpcionB() { return opcionB; }
    public void setOpcionB(String opcionB) { this.opcionB = opcionB; }

    public Double getPuntajeB() { return puntajeB; }
    public void setPuntajeB(Double puntajeB) { this.puntajeB = puntajeB; }

    public String getOpcionC() { return opcionC; }
    public void setOpcionC(String opcionC) { this.opcionC = opcionC; }

    public Double getPuntajeC() { return puntajeC; }
    public void setPuntajeC(Double puntajeC) { this.puntajeC = puntajeC; }

    public String getOpcionD() { return opcionD; }
    public void setOpcionD(String opcionD) { this.opcionD = opcionD; }

    public Double getPuntajeD() { return puntajeD; }
    public void setPuntajeD(Double puntajeD) { this.puntajeD = puntajeD; }
}