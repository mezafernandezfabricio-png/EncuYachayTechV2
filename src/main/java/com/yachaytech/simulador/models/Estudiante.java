package com.yachaytech.simulador.models;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "estudiantes")
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String correo;
    private String password;
    
    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private Timestamp fechaRegistro;
    
    private Boolean estado = true;

    @Column(name = "estado_simulacion")
    private String estadoSimulacion = "NO_INICIADO";

    @Column(name = "paso_actual")
    private Integer pasoActual = 1;

    @Column(name = "intentos_realizados")
    private Integer intentosRealizados = 0;

    @Column(name = "intentos_permitidos")
    private Integer intentosPermitidos = 1;

    @Column(name = "alerta_mostrada")
    private Boolean alertaMostrada = false;

    @Column(name = "puntaje_total")
    private Double puntajeTotal = 0.0;

    @Column(name = "resa")
    private String resA = "No respondido";
    @Column(name = "resb")
    private String resB = "No respondido";
    @Column(name = "resc")
    private String resC = "No respondido";

 // ... otros campos ...

    @Column(name = "res_hijo", length = 1000)
    private String resHijo = "No respondido";
    
    @Column(name = "res_padre", length = 1000)
    private String resPadre = "No respondido";

    @Column(name = "res_eje1", length = 500)
    private String resEje1 = "No respondido";
    
    @Column(name = "res_eje2", length = 500)
    private String resEje2 = "No respondido";
    
    @Column(name = "res_eje3", length = 500)
    private String resEje3 = "No respondido";

    // ... getters y setters ...

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
    public String getEstadoSimulacion() { return estadoSimulacion; }
    public void setEstadoSimulacion(String estadoSimulacion) { this.estadoSimulacion = estadoSimulacion; }
    public Integer getPasoActual() { return pasoActual; }
    public void setPasoActual(Integer pasoActual) { this.pasoActual = pasoActual; }
    public Integer getIntentosRealizados() { return intentosRealizados; }
    public void setIntentosRealizados(Integer intentosRealizados) { this.intentosRealizados = intentosRealizados; }
    public Integer getIntentosPermitidos() { return intentosPermitidos; }
    public void setIntentosPermitidos(Integer intentosPermitidos) { this.intentosPermitidos = intentosPermitidos; }
    public Boolean getAlertaMostrada() { return alertaMostrada; }
    public void setAlertaMostrada(Boolean alertaMostrada) { this.alertaMostrada = alertaMostrada; }
    public Double getPuntajeTotal() { return puntajeTotal; }
    public void setPuntajeTotal(Double puntajeTotal) { this.puntajeTotal = puntajeTotal; }
    public String getResA() { return resA; }
    public void setResA(String resA) { this.resA = resA; }
    public String getResB() { return resB; }
    public void setResB(String resB) { this.resB = resB; }
    public String getResC() { return resC; }
    public void setResC(String resC) { this.resC = resC; }
    public String getResHijo() { return resHijo; }
    public void setResHijo(String resHijo) { this.resHijo = resHijo; }
    public String getResPadre() { return resPadre; }
    public void setResPadre(String resPadre) { this.resPadre = resPadre; }
    public String getResEje1() { return resEje1; }
    public void setResEje1(String resEje1) { this.resEje1 = resEje1; }
    public String getResEje2() { return resEje2; }
    public void setResEje2(String resEje2) { this.resEje2 = resEje2; }
    public String getResEje3() { return resEje3; }
    public void setResEje3(String resEje3) { this.resEje3 = resEje3; }
}