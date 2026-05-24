package com.sigat.springboot.app.models.entity;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "historias_clinicas")
public class HistoriaClinica implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date fecha;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "paciente_id")
	private Paciente paciente;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profesional_id")
	private Profesional profesional;

	// --- ALERTAS MÉDICAS ---
	@Column(columnDefinition = "TEXT")
	private String alergias; // Esto lo resaltaremos en el Dashboard del médico

	@Column(columnDefinition = "TEXT")
	private String antecedentes;

	// --- DESARROLLO DE LA CONSULTA ---
	@Column(columnDefinition = "TEXT")
	private String motivoConsulta;

	@Column(columnDefinition = "TEXT")
	private String examenFisico;

	@Column(columnDefinition = "TEXT")
	private String evolucionClinica;

	@Column(columnDefinition = "TEXT")
	private String diagnostico;

	@Column(columnDefinition = "TEXT")
	private String indicaciones;
	
	private String tipoAtencion; // Guardará "TURNO" o "SOBRETURNO"

	@PrePersist
	public void prePersist() {
		fecha = new Date();
	}

	// Getters y Setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Date getFecha() { return fecha; }
	public void setFecha(Date fecha) { this.fecha = fecha; }
	public Paciente getPaciente() { return paciente; }
	public void setPaciente(Paciente paciente) { this.paciente = paciente; }
	public Profesional getProfesional() { return profesional; }
	public void setProfesional(Profesional profesional) { this.profesional = profesional; }
	public String getAlergias() { return alergias; }
	public void setAlergias(String alergias) { this.alergias = alergias; }
	public String getAntecedentes() { return antecedentes; }
	public void setAntecedentes(String antecedentes) { this.antecedentes = antecedentes; }
	public String getMotivoConsulta() { return motivoConsulta; }
	public void setMotivoConsulta(String motivoConsulta) { this.motivoConsulta = motivoConsulta; }
	public String getExamenFisico() { return examenFisico; }
	public void setExamenFisico(String examenFisico) { this.examenFisico = examenFisico; }
	public String getEvolucionClinica() { return evolucionClinica; }
	public void setEvolucionClinica(String evolucionClinica) { this.evolucionClinica = evolucionClinica; }
	public String getDiagnostico() { return diagnostico; }
	public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
	public String getIndicaciones() { return indicaciones; }
	public void setIndicaciones(String indicaciones) { this.indicaciones = indicaciones; }
	public String getTipoAtencion() {return tipoAtencion;}

	public void setTipoAtencion(String tipoAtencion) {
		this.tipoAtencion = tipoAtencion;
	}

	private static final long serialVersionUID = 1L;
}

