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
@Table(name = "movimientos")
public class Movimiento implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idProfesional")
	private Profesional profesional;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idEspecialidad")
	private Especialidad especialidad;

	// fecha de creacion
	@NotNull
	@Column(name = "create_at")
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
	
	
	//getters and setters
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


	public Date getCreateAt() {
		return createAt;
	}


	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}


	public String getHoraIniManana() {
		return horaIniManana;
	}


	public void setHoraIniManana(String horaIniManana) {
		this.horaIniManana = horaIniManana;
	}


	public String getHoraFinManana() {
		return horaFinManana;
	}


	public void setHoraFinManana(String horaFinManana) {
		this.horaFinManana = horaFinManana;
	}


	public String getTotalManana() {
		return totalManana;
	}


	public void setTotalManana(String totalManana) {
		this.totalManana = totalManana;
	}


	public String getHoraIniTarde() {
		return horaIniTarde;
	}


	public void setHoraIniTarde(String horaIniTarde) {
		this.horaIniTarde = horaIniTarde;
	}


	public String getHoraFinTarde() {
		return horaFinTarde;
	}


	public void setHoraFinTarde(String horaFinTarde) {
		this.horaFinTarde = horaFinTarde;
	}


	public String getTotalTarde() {
		return totalTarde;
	}


	public void setTotalTarde(String totalTarde) {
		this.totalTarde = totalTarde;
	}


	public String getTotalGeneral() {
		return totalGeneral;
	}


	public void setTotalGeneral(String totalGeneral) {
		this.totalGeneral = totalGeneral;
	}	
	
	private static final long serialVersionUID = 1L;
}
