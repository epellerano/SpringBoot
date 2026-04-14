package com.sigat.springboot.app.models.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "turnos")
public class Turno implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@CreatedDate
	@Column(name = "fecha_creacion", updatable = false)
	private LocalDateTime fechaCreacion;
	
	private String Observacion;
	
	@ManyToOne(fetch = FetchType.LAZY)		  
	@JoinColumn(name = "planilladetalle_id")
	@JsonManagedReference 
	private PlanillaDetalle planilladetalle;		 
	
	@ManyToOne(fetch=FetchType.LAZY)		  
	@JoinColumn(name = "planillacabecera_id")		  
	@JsonManagedReference
	private PlanillaCabecera planillacabecera;
	
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "estado_id")
	private Estado estado;

	// AUDITORÍA EXTRA
	@CreatedBy
	@Column(name = "creado_por", updatable = false)
	private String creadoPor;

	@LastModifiedBy
	@Column(name = "modificado_por")
	private String modificadoPor;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@LastModifiedDate
	@Column(name = "fecha_modificacion")
	private LocalDateTime fechaModificacion;

	public Turno() {}

	// Getters y Setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public LocalDateTime getFechaCreacion() { return fechaCreacion; }
	public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
	public String getObservacion() { return Observacion; }
	public void setObservacion(String observacion) { this.Observacion = observacion; }
	public PlanillaCabecera getPlanillacabecera() { return planillacabecera; }
	public void setPlanillacabecera(PlanillaCabecera planillacabecera) { this.planillacabecera = planillacabecera; }
	public PlanillaDetalle getPlanilladetalle() { return planilladetalle; }
	public void setPlanilladetalle(PlanillaDetalle planilladetalle) { this.planilladetalle = planilladetalle; }
	public Paciente getPaciente() { return paciente; }
	public void setPaciente(Paciente paciente) { this.paciente = paciente; }	
	public Profesional getProfesional() { return profesional; }
	public void setProfesional(Profesional profesional) { this.profesional = profesional; }
	public Especialidad getEspecialidad() { return especialidad; }
	public void setEspecialidad(Especialidad especialidad) { this.especialidad = especialidad; }
	public Estado getEstado() { return estado; }
	public void setEstado(Estado estado) { this.estado = estado; }

	public String getCreadoPor() { return creadoPor; }
	public void setCreadoPor(String creadoPor) { this.creadoPor = creadoPor; }
	public String getModificadoPor() { return modificadoPor; }
	public void setModificadoPor(String modificadoPor) { this.modificadoPor = modificadoPor; }
	public LocalDateTime getFechaModificacion() { return fechaModificacion; }
	public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }

	private static final long serialVersionUID = 1L;
}