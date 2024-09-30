package com.sigat.springboot.app.models.entity;

import java.io.Serializable;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
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
@Table(name = "planillacabecera")
public class PlanillaCabecera implements Serializable{

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
	
	//@NotNull
	@NotEmpty
	@Column(name = "hora_inicialR1")
	//@Temporal(TemporalType.TIME)
	//@DateTimeFormat(pattern = "HH:mm")
	private String horaInicialR1;
	
	//@NotNull
	@NotEmpty
	@Column(name = "hora_finalR1")
	//@Temporal(TemporalType.TIME)
	//@DateTimeFormat(pattern = "HH:mm")
	private String horaFinalR1;
	
	@NotNull
	@Column(name = "intervalo_r1", nullable = false)
	private Integer intervaloR1;
	
	//@NotNull
	@NotEmpty
	@Column(name = "hora_inicialR2")
	//@Temporal(TemporalType.TIME)
	//@DateTimeFormat(pattern = "HH:mm")
	private String horaInicialR2;
	
	//@NotNull
	@NotEmpty
	@Column(name = "hora_finalR2")
	//@Temporal(TemporalType.TIME)
	//@DateTimeFormat(pattern = "HH:mm")
	private String horaFinalR2;
	
	@NotNull
	@Column(name = "intervalo_r2", nullable = false)
	private Integer intervaloR2;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idEstado", nullable = false)
	private Estado estado;
	
	@NotEmpty
	private String Observacion;	
	
	//Serial Version
	private static final long serialVersionUID = 1L;

	//Getters and Setters
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
