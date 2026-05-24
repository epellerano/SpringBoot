package com.sigat.springboot.app.dto;

import java.time.LocalDateTime;
import java.util.Date;

public class RecepcionDTO {
	private Long idOriginal;
    private Date fechaHora;     // Ahora coincide con Date de la DB
    private String paciente;
    private String dni;
    private String medico;
    private String especialidad;
    private String tipo;
    private Long estadoId;      // Cambiamos Integer por Long
    private String estadoNombre;
    
    private Long pacienteId; // Agregá este campo
    
 // --- NUEVOS CAMPOS PARA LOS BOTONES DE ACCIÓN ---
    private Long profesionalId;
    private Long especialidadId;
    
    //para mostrar el numero de box en la tabla
    private String box;
    
    private String observacion;
    
    
	public Long getIdOriginal() {
		return idOriginal;
	}
	public void setIdOriginal(Long idOriginal) {
		this.idOriginal = idOriginal;
	}
	public Date getFechaHora() {
		return fechaHora;
	}
	public void setFechaHora(Date fechaHora) {
		this.fechaHora = fechaHora;
	}
	public String getPaciente() {
		return paciente;
	}
	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}
	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}
	public String getMedico() {
		return medico;
	}
	public void setMedico(String medico) {
		this.medico = medico;
	}
	public String getEspecialidad() {
		return especialidad;
	}
	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Long getEstadoId() {
		return estadoId;
	}
	public void setEstadoId(Long estadoId) {
		this.estadoId = estadoId;
	}
	public String getEstadoNombre() {
		return estadoNombre;
	}
	public void setEstadoNombre(String estadoNombre) {
		this.estadoNombre = estadoNombre;
	}
	public Long getPacienteId() {
		return pacienteId;
	}
	public void setPacienteId(Long pacienteId) {
		this.pacienteId = pacienteId;
	}
	public Long getProfesionalId() {
		return profesionalId;
	}
	public void setProfesionalId(Long profesionalId) {
		this.profesionalId = profesionalId;
	}
	public Long getEspecialidadId() {
		return especialidadId;
	}
	public void setEspecialidadId(Long especialidadId) {
		this.especialidadId = especialidadId;
	}
	public String getBox() {
		return box;
	}
	public void setBox(String box) {
		this.box = box;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	
	
}
