package com.sigat.springboot.app.models.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

// --- IMPORTS DE AUDITORÍA ---
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;
// ----------------------------

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
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@EntityListeners(AuditingEntityListener.class) // <--- ACTIVA AUDITORÍA
@Table(name = "movimientos")
public class Movimiento implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_profesional")
	@JsonManagedReference
	private Profesional profesional;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idEspecialidad")
	private Especialidad especialidad;

	// RESTAURADO: Nombre y Tipo original para no romper tus Queries
	@NotNull
	@CreatedDate // <--- Ahora Spring lo llena solo
	@Column(name = "create_at", updatable = false)
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createAt;
	
	@NotEmpty
	@Column(name = "horaini_manana")
	private String horaIniManana;
	
	@NotEmpty
	@Column(name = "horafin_manana")
	private String horaFinManana;
	
	@NotEmpty
	@Column(name = "total_manana")
	private String totalManana;
	
	@NotEmpty
	@Column(name = "horaini_tarde")
	private String horaIniTarde;
	
	@NotEmpty
	@Column(name = "horafin_tarde")
	private String horaFinTarde;
	
	@NotEmpty
	@Column(name = "total_tarde")
	private String totalTarde;
	
	@NotEmpty
	@Column(name = "total_general")
	private String totalGeneral;

	// ==========================================
	// NUEVOS CAMPOS DE AUDITORÍA (No rompen nada)
	// ==========================================
	@CreatedBy
	@Column(name = "creado_por", updatable = false)
	private String creadoPor;

	@LastModifiedBy
	@Column(name = "modificado_por")
	private String modificadoPor;

	@LastModifiedDate
	@Column(name = "fecha_modificacion")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaModificacion;
	// ==========================================

	// Getters and Setters Originales
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Profesional getProfesional() { return profesional; }
	public void setProfesional(Profesional profesional) { this.profesional = profesional; }
	public Especialidad getEspecialidad() { return especialidad; }
	public void setEspecialidad(Especialidad especialidad) { this.especialidad = especialidad; }
	
	// RESTAURADO: Getter/Setter original para el PDF y Queries
	public Date getCreateAt() { return createAt; }
	public void setCreateAt(Date createAt) { this.createAt = createAt; }

	public String getHoraIniManana() { return horaIniManana; }
	public void setHoraIniManana(String horaIniManana) { this.horaIniManana = horaIniManana; }
	public String getHoraFinManana() { return horaFinManana; }
	public void setHoraFinManana(String horaFinManana) { this.horaFinManana = horaFinManana; }
	public String getTotalManana() { return totalManana; }
	public void setTotalManana(String totalManana) { this.totalManana = totalManana; }
	public String getHoraIniTarde() { return horaIniTarde; }
	public void setHoraIniTarde(String horaIniTarde) { this.horaIniTarde = horaIniTarde; }
	public String getHoraFinTarde() { return horaFinTarde; }
	public void setHoraFinTarde(String horaFinTarde) { this.horaFinTarde = horaFinTarde; }
	public String getTotalTarde() { return totalTarde; }
	public void setTotalTarde(String totalTarde) { this.totalTarde = totalTarde; }
	public String getTotalGeneral() { return totalGeneral; }
	public void setTotalGeneral(String totalGeneral) { this.totalGeneral = totalGeneral; }	

	// Getters y Setters de los campos de Auditoría nuevos
	public String getCreadoPor() { return creadoPor; }
	public void setCreadoPor(String creadoPor) { this.creadoPor = creadoPor; }
	public String getModificadoPor() { return modificadoPor; }
	public void setModificadoPor(String modificadoPor) { this.modificadoPor = modificadoPor; }
	public Date getFechaModificacion() { return fechaModificacion; }
	public void setFechaModificacion(Date fechaModificacion) { this.fechaModificacion = fechaModificacion; }

	private static final long serialVersionUID = 1L;
}