package com.sigat.springboot.app.models.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

// --- IMPORTS DE AUDITORÍA ---
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;
// ----------------------------

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotEmpty;

@Entity
@EntityListeners(AuditingEntityListener.class) // <--- ACTIVA EL MOTOR DE AUDITORÍA
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "especialidades")
public class Especialidad implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotEmpty
	@Column(name = "especialidad_codigo")
	private String especialidadCodigo;

	@NotEmpty
	@Column(name = "especialidad_nombre")
	private String especialidadNombre;

	// =========================================================================
	// CAMPOS DE AUDITORÍA AUTOMÁTICA
	// =========================================================================
	@CreatedBy
	@Column(name = "creado_por", updatable = false)
	private String creadoPor;

	@CreatedDate
	@Column(name = "fecha_creacion", updatable = false)
	private LocalDateTime fechaCreacion;

	@LastModifiedBy
	@Column(name = "modificado_por")
	private String modificadoPor;

	@LastModifiedDate
	@Column(name = "fecha_modificacion")
	private LocalDateTime fechaModificacion;
	// =========================================================================

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "especialidad")
	@JsonBackReference
	private List<Turno> turno;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "especialidad")
	@JsonBackReference
	private List<Sobreturno> sobreturno;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "especialidad")
	@JsonBackReference
	private List<Vinculacion> vinculacion;

	// getters and setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getEspecialidadCodigo() { return especialidadCodigo; }
	public void setEspecialidadCodigo(String especialidadCodigo) { this.especialidadCodigo = especialidadCodigo; }

	public String getEspecialidadNombre() { return especialidadNombre; }
	public void setEspecialidadNombre(String especialidadNombre) { this.especialidadNombre = especialidadNombre; }

	public List<Turno> getTurno() { return turno; }
	public void setTurno(List<Turno> turno) { this.turno = turno; }

	public List<Sobreturno> getSobreturno() { return sobreturno; }
	public void setSobreturno(List<Sobreturno> sobreturno) { this.sobreturno = sobreturno; }		

	public List<Vinculacion> getVinculacion() { return vinculacion; }
	public void setVinculacion(List<Vinculacion> vinculacion) { this.vinculacion = vinculacion; }

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
