package com.sigat.springboot.app.models.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

// --- IMPORTS DE AUDITORÍA ---
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;
// ----------------------------

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "sobreturnos")
public class Sobreturno implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Column(name = "rango_fechaHora")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime rangoFechaHora;
	
	// RESTAURADO: Nombre original
	@CreatedDate
	@Column(name = "fecha_creacion", nullable = false, updatable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaCreacion;
	
	private String Observacion;
	
	@ManyToOne(fetch = FetchType.LAZY)	  
	@JoinColumn(name = "paciente_id")
	@JsonManagedReference
	private Paciente paciente;
	
	@ManyToOne(fetch = FetchType.LAZY)	  
	@JoinColumn(name = "profesional_id")
	@JsonManagedReference 
	private Profesional profesional;
	
	@ManyToOne(fetch = FetchType.LAZY)	  
	@JoinColumn(name = "especialidad_id")
	@JsonManagedReference
	private Especialidad especialidad;
	
	@ManyToOne(fetch=FetchType.LAZY)		  
	@JoinColumn(name = "planillacabecera_id")
	@JsonManagedReference
	private PlanillaCabecera planillacabecera;

	// NUEVOS CAMPOS AUDITORÍA
	@CreatedBy
	@Column(name = "creado_por", updatable = false)
	private String creadoPor;

	@LastModifiedBy
	@Column(name = "modificado_por")
	private String modificadoPor;

	@LastModifiedDate
	@Column(name = "fecha_modificacion")
	private LocalDateTime fechaModificacion;

	public Sobreturno() {}

	// Getters y Setters Originales
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public LocalDateTime getRangoFechaHora() { return rangoFechaHora; }
	public void setRangoFechaHora(LocalDateTime rangoFechaHora) { this.rangoFechaHora = rangoFechaHora; }
	public LocalDateTime getFechaCreacion() { return fechaCreacion; }
	public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
	public String getObservacion() { return Observacion; }
	public void setObservacion(String observacion) { this.Observacion = observacion; }
	public Paciente getPaciente() { return paciente; }
	public void setPaciente(Paciente paciente) { this.paciente = paciente; }
	public Profesional getProfesional() { return profesional; }
	public void setProfesional(Profesional profesional) { this.profesional = profesional; }
	public Especialidad getEspecialidad() { return especialidad; }
	public void setEspecialidad(Especialidad especialidad) { this.especialidad = especialidad; }	
	public PlanillaCabecera getPlanillacabecera() { return planillacabecera; }
	public void setPlanillacabecera(PlanillaCabecera planillacabecera) { this.planillacabecera = planillacabecera; }

	// Getters y Setters Auditoría
	public String getCreadoPor() { return creadoPor; }
	public void setCreadoPor(String creadoPor) { this.creadoPor = creadoPor; }
	public String getModificadoPor() { return modificadoPor; }
	public void setModificadoPor(String modificadoPor) { this.modificadoPor = modificadoPor; }
	public LocalDateTime getFechaModificacion() { return fechaModificacion; }
	public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }

	private static final long serialVersionUID = 1L;
}