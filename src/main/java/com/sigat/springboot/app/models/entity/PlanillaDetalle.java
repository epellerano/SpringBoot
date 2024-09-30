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
@Table(name = "planilladetalle")
public class PlanillaDetalle implements Serializable{
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idPlanillaCabecera")
	private PlanillaCabecera planillacabecera;
	
	@NotNull
	@Column(name = "rango_fechaHora")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date rangoFechaHora;	
	
	@NotEmpty	  
	@Column(name = "estado_id") private Integer estadoId;
	  
	@NotEmpty private String Observacion;
	 
	
	//Serial Version
	private static final long serialVersionUID = 1L;
	
	//Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PlanillaCabecera getPlanillacabecera() {
		return planillacabecera;
	}

	public void setPlanillacabecera(PlanillaCabecera planillacabecera) {
		this.planillacabecera = planillacabecera;
	}

	public Date getRangoFechaHora() {
		return rangoFechaHora;
	}

	public void setRangoFechaHora(Date rangoFechaHora) {
		this.rangoFechaHora = rangoFechaHora;
	}

	public Integer getEstadoId() {
		return estadoId;
	}

	public void setEstadoId(Integer estadoId) {
		this.estadoId = estadoId;
	}

	public String getObservacion() {
		return Observacion;
	}

	public void setObservacion(String observacion) {
		Observacion = observacion;
	}
	
	

}
