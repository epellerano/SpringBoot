package com.sigat.springboot.app.dto;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class PlanillaSobreturnoDTO {
	private Long id;
	// Esta anotación obliga a Spring a mandar la FECHA REAL como texto y no como la hora del sistema
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "America/Argentina/Buenos_Aires")
    private Date rangoFechaHora;
    private String pacienteApeNom;
    private String dni;
    private String observacion;
    private String tipo; // "TURNO" o "SOBRETURNO"
    
 // Constructor completo para mapear rápido
    public PlanillaSobreturnoDTO(Long id, Date rangoFechaHora, String pacienteApeNom, String dni, String observacion, String tipo) {
        this.id = id;
        this.rangoFechaHora = rangoFechaHora;
        this.pacienteApeNom = pacienteApeNom;
        this.dni = dni;
        this.observacion = observacion;
        this.tipo = tipo;
    }
    
  //getters and setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getRangoFechaHora() {
		return rangoFechaHora;
	}

	public void setRangoFechaHora(Date rangoFechaHora) {
		this.rangoFechaHora = rangoFechaHora;
	}

	public String getPacienteApeNom() {
		return pacienteApeNom;
	}

	public void setPacienteApeNom(String pacienteApeNom) {
		this.pacienteApeNom = pacienteApeNom;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
    
   
    
}