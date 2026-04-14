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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@EntityListeners(AuditingEntityListener.class) // <--- ACTIVA EL MOTOR DE AUDITORÍA
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "vinculaciones")
public class Vinculacion implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "observacion", length = 500)
	private String Observacion;
	
	// RELACION CON PROFESIONAL
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_profesional")
	@JsonManagedReference
	private Profesional profesional;

	// RELACION CON ESPECIALIDAD
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_especialidad")
	@JsonManagedReference
	private Especialidad especialidad;

	// =========================================================================
	// CAMPOS DE AUDITORÍA AUTOMÁTICA
	// =========================================================================
	@CreatedBy
	@Column(name = "creado_por", updatable = false)
	private String creadoPor;

	@CreatedDate
	@Column(name = "fecha_creacion", updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaCreacion;

	@LastModifiedBy
	@Column(name = "modificado_por")
	private String modificadoPor;

	@LastModifiedDate
	@Column(name = "fecha_modificacion")
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime fechaModificacion;
	// =========================================================================

	// Getters and Setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getObservacion() { return Observacion; }
	public void setObservacion(String observacion) { Observacion = observacion; }

	public Especialidad getEspecialidad() { return especialidad; }
	public void setEspecialidad(Especialidad especialidad) { this.especialidad = especialidad; }

	public Profesional getProfesional() { return profesional; }
	public void setProfesional(Profesional profesional) { this.profesional = profesional; }

	// --- Getters y Setters para Auditoría ---
	public String getCreadoPor() { return creadoPor; }
	public void setCreadoPor(String creadoPor) { this.creadoPor = creadoPor; }

	public LocalDateTime getFechaCreacion() { return fechaCreacion; }
	public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

	public String getModificadoPor() { return modificadoPor; }
	public void setModificadoPor(String modificadoPor) { this.modificadoPor = modificadoPor; }

	public LocalDateTime getFechaModificacion() { return fechaModificacion; }
	public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }

	private static final long serialVersionUID = 1L;
}
