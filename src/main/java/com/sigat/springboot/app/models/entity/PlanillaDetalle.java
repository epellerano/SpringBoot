package com.sigat.springboot.app.models.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "planilladetalle")
public class PlanillaDetalle implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "rango_fechaHora")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Argentina/Buenos_Aires") // <--- Formato para tu JS
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date rangoFechaHora;	
	
	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "estado_id") // <--- REVISA QUE TENGA EL GUION BAJO
	private Estado estado;

	@NotEmpty
	private String Observacion;
	
	private String Box;

	@ManyToOne()	
	@JoinColumn(name = "planillacabecera_id")
	@JsonManagedReference
	private PlanillaCabecera planillacabecera;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "planilladetalle") 
	@JsonBackReference	  
	private List<Turno> turno;

	// AUDITORÍA UNIFICADA
	@CreatedBy
	@Access(AccessType.FIELD) // Esto obliga a buscar por el nombre del campo físico
	@Column(name = "creado_por", updatable = false)
	private String creadoPor;

	@CreatedDate
	@Access(AccessType.FIELD)
	@Column(name = "fecha_creacion", updatable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaCreacion;

	@LastModifiedBy
	@Access(AccessType.FIELD)
	@Column(name = "modificado_por")
	private String modificadoPor;

	@LastModifiedDate
	@Access(AccessType.FIELD)
	@Column(name = "fecha_modificacion")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaModificacion;

	public PlanillaDetalle() {}
	
	public PlanillaDetalle(@NotNull Date rangoFechaHora, @NotEmpty String observacion, String Box) {
		this.rangoFechaHora = rangoFechaHora;
		this.Observacion = observacion;
		this.Box = Box;
	}
	
	// Getters y Setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Date getRangoFechaHora() { return rangoFechaHora; }
	public void setRangoFechaHora(Date rangoFechaHora) { this.rangoFechaHora = rangoFechaHora; }
	public Estado getEstado() { return estado; }
	public void setEstado(Estado estado) { this.estado = estado; }
	public String getObservacion() { return Observacion; }
	public void setObservacion(String observacion) { this.Observacion = observacion; }
	public PlanillaCabecera getPlanillacabecera() { return planillacabecera; }
	public void setPlanillacabecera(PlanillaCabecera planillacabecera) { this.planillacabecera = planillacabecera; }
	public List<Turno> getTurno() { return turno; }
	public void setTurno(List<Turno> turno) { this.turno = turno; }
	public String getBox() { return Box; }
	public void setBox(String box) { this.Box = box; }

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