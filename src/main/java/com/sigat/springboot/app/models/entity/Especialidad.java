package com.sigat.springboot.app.models.entity;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "especialidades")
public class Especialidad implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotEmpty
	@Column(name = "especialidad_codigo")
	private String especialidadCodigo;
	@NotEmpty
	@Column(name = "especialidad_nombre")
	private String especialidadNombre;
	
	
	//getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEspecialidadCodigo() {
		return especialidadCodigo;
	}
	public void setEspecialidadCodigo(String especialidadCodigo) {
		this.especialidadCodigo = especialidadCodigo;
	}
	
	public String getEspecialidadNombre() {
		return especialidadNombre;
	}
	
	public void setEspecialidadNombre(String especialidadNombre) {
		this.especialidadNombre = especialidadNombre;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
	
	//serial version como estandard d ejava
	private static final long serialVersionUID = 1L;

}
