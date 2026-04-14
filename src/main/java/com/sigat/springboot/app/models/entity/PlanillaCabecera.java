package com.sigat.springboot.app.models.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "planillacabecera")
public class PlanillaCabecera implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idProfesional", nullable = false)
	private Profesional profesional;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idEspecialidad", nullable = false)
	private Especialidad especialidad;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idDia", nullable = false)
	private Dia dia;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "planillacabecera")
	@JsonBackReference
	private Set<PlanillaDetalle> detalleList;

	@NotNull
	@Column(name = "fecha_inicio")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fechaInicio;

	@NotNull
	@Column(name = "fecha_final")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fechaFinal;

	private String horaInicialR1;
	private String horaFinalR1;
	private Integer intervaloR1;
	private String horaInicialR2;
	private String horaFinalR2;
	private Integer intervaloR2;

	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idEstado", nullable = false) // Cambiado de idEstado a id_estado
	private Estado estado;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "planillacabecera")
	@JsonBackReference
	private List<Turno> turno;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "planillacabecera")
	@JsonBackReference
	private List<Sobreturno> sobreturno;

	private String Observacion;

	@Column(name = "fecha_expira")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime fechaExpira;

	// AUDITORÍA UNIFICADA
	@CreatedBy
	@Column(name = "creado_por", updatable = false)
	private String creadoPor;

	@CreatedDate
	@Column(name = "fecha_creacion", updatable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaCreacion; // Asegúrate que NO diga _audit, solo fecha_creacion

	@LastModifiedBy
	@Column(name = "modificado_por")
	private String modificadoPor;

	@LastModifiedDate
	@Column(name = "fecha_modificacion")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaModificacion;

	public PlanillaCabecera() {
		detalleList = new HashSet<>();
	}

	// Getters y Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Profesional getProfesional() {
		return profesional;
	}

	public void setProfesional(Profesional profesional) {
		this.profesional = profesional;
	}

	public Especialidad getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(Especialidad especialidad) {
		this.especialidad = especialidad;
	}

	public Dia getDia() {
		return dia;
	}

	public void setDia(Dia dia) {
		this.dia = dia;
	}

	public Set<PlanillaDetalle> getDetalleList() {
		return detalleList;
	}

	public void setDetalleList(Set<PlanillaDetalle> detalleList) {
		this.detalleList = detalleList;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFinal() {
		return fechaFinal;
	}

	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	public String getHoraInicialR1() {
		return horaInicialR1;
	}

	public void setHoraInicialR1(String horaInicialR1) {
		this.horaInicialR1 = horaInicialR1;
	}

	public String getHoraFinalR1() {
		return horaFinalR1;
	}

	public void setHoraFinalR1(String horaFinalR1) {
		this.horaFinalR1 = horaFinalR1;
	}

	public Integer getIntervaloR1() {
		return intervaloR1;
	}

	public void setIntervaloR1(Integer intervaloR1) {
		this.intervaloR1 = intervaloR1;
	}

	public String getHoraInicialR2() {
		return horaInicialR2;
	}

	public void setHoraInicialR2(String horaInicialR2) {
		this.horaInicialR2 = horaInicialR2;
	}

	public String getHoraFinalR2() {
		return horaFinalR2;
	}

	public void setHoraFinalR2(String horaFinalR2) {
		this.horaFinalR2 = horaFinalR2;
	}

	public Integer getIntervaloR2() {
		return intervaloR2;
	}

	public void setIntervaloR2(Integer intervaloR2) {
		this.intervaloR2 = intervaloR2;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public String getObservacion() {
		return Observacion;
	}

	public void setObservacion(String observacion) {
		Observacion = observacion;
	}

	public LocalDateTime getFechaExpira() {
		return fechaExpira;
	}

	public void setFechaExpira(LocalDateTime fechaExpira) {
		this.fechaExpira = fechaExpira;
	}

	public List<Turno> getTurno() {
		return turno;
	}

	public void setTurno(List<Turno> turno) {
		this.turno = turno;
	}

	public List<Sobreturno> getSobreturno() {
		return sobreturno;
	}

	public void setSobreturno(List<Sobreturno> sobreturno) {
		this.sobreturno = sobreturno;
	}

	// Getters/Setters Auditoría
	public String getCreadoPor() {
		return creadoPor;
	}

	public void setCreadoPor(String creadoPor) {
		this.creadoPor = creadoPor;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public String getModificadoPor() {
		return modificadoPor;
	}

	public void setModificadoPor(String modificadoPor) {
		this.modificadoPor = modificadoPor;
	}

	public LocalDateTime getFechaModificacion() {
		return fechaModificacion;
	}

	public void setFechaModificacion(LocalDateTime fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}

	// metodo agregar la planilla detalle.
	public PlanillaCabecera addPlanillaDetalle(PlanillaDetalle planilladetalle) {
		detalleList.add(planilladetalle); // detalleList viene de setDetalleList.. setters and getters de arriba.
		planilladetalle.setPlanillacabecera(this); // this es el PlanillaCabecera.
		return this; // devuelve el objeto PlanillaCabecera.
	}

	// metodo eliminar planilla detalle.
	public void removePlanillaDetalle(PlanillaDetalle planilladetalle) {
		this.getDetalleList().remove(planilladetalle); // el this es PlanillaCabecera y getDetalleList de getters and
														// setters.
		planilladetalle.setPlanillacabecera(null); // eliminamos de planilladetalle la PlanillaCabecera.
	}

	private static final long serialVersionUID = 1L;
}