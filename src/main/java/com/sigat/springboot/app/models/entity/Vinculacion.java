package com.sigat.springboot.app.models.entity;

import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "vinculaciones")
public class Vinculacion implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotEmpty
	private String Observacion;

	/* @ManyToOne(fetch = FetchType.LAZY) */

	/*
	 * @OneToMany
	 * 
	 * @JoinColumn(name = "idProfesional") private List<Profesional> profesional;
	 * 
	 * @OneToMany
	 * 
	 * @JoinColumn(name = "idEspecialidad") private List<Especialidad> especialidad;
	 */
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idEspecialidad")
	private Especialidad especialidad;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idProfesional")
	private Profesional profesional;

	
	// getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getObservacion() {
		return Observacion;
	}

	public void setObservacion(String observacion) {
		Observacion = observacion;
	}

	public Especialidad getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(Especialidad especialidad) {
		this.especialidad = especialidad;
	}

	public Profesional getProfesional() {
		return profesional;
	}

	public void setProfesional(Profesional profesional) {
		this.profesional = profesional;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	// estandar de java
	private static final long serialVersionUID = 1L;


}
