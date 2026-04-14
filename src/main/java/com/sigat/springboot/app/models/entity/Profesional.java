package com.sigat.springboot.app.models.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

// --- IMPORTS DE AUDITORÍA ---
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;
// ----------------------------

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@EntityListeners(AuditingEntityListener.class) // <--- ACTIVA AUDITORÍA
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "profesionales")
public class Profesional implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotEmpty
	private String nombre;
	@NotEmpty
	private String apellido;
	@NotEmpty
	@Email
	private String email;
	@NotEmpty
	private String telefono;
	@NotEmpty
	private String codigo;
	@NotEmpty
	private String matricula;
	@NotEmpty
	@Pattern(regexp = "\\d+", message = "El Dni debe ser un número")
	private String dni;

	// RESTAURADO: Nombre y Tipo original para no romper tus Queries
	@NotNull
	@CreatedDate // <--- Spring lo llena solo al crear
	@Column(name = "create_at", updatable = false)
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createAt;
    
	private String foto;	

	// ==========================================
	// NUEVOS CAMPOS DE AUDITORÍA (Sincronizados)
	// ==========================================
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
	// ==========================================

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "profesional")
	@JsonBackReference
	private List<Turno> turno;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "profesional")
	@JsonBackReference
	private List<Sobreturno> sobreturno;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "profesional")
	@JsonBackReference
	private List<Vinculacion> vinculacion;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "profesional")
	@JsonBackReference
	private List<Movimiento> movimiento;

	// Getters y Setters originales
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public String getApellido() { return apellido; }
	public void setApellido(String apellido) { this.apellido = apellido; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getTelefono() { return telefono; }
	public void setTelefono(String telefono) { this.telefono = telefono; }
	public String getCodigo() { return codigo; }
	public void setCodigo(String codigo) { this.codigo = codigo; }
	public String getMatricula() { return matricula; }
	public void setMatricula(String matricula) { this.matricula = matricula; }
	
    // RESTAURADO: Getter/Setter original
    public Date getCreateAt() { return createAt; }
	public void setCreateAt(Date createAt) { this.createAt = createAt; }

	public String getFoto() { return foto; }
	public void setFoto(String foto) { this.foto = foto; }
	public String getDni() { return dni; }
	public void setDni(String dni) { this.dni = dni; }

	public List<Turno> getTurno() { return turno; }
	public void setTurno(List<Turno> turno) { this.turno = turno; }
	public List<Sobreturno> getSobreturno() { return sobreturno; }
	public void setSobreturno(List<Sobreturno> sobreturno) { this.sobreturno = sobreturno; }
	public List<Vinculacion> getVinculacion() { return vinculacion; }
	public void setVinculacion(List<Vinculacion> vinculacion) { this.vinculacion = vinculacion; }	
	public List<Movimiento> getMovimiento() { return movimiento; }
	public void setMovimiento(List<Movimiento> movimiento) { this.movimiento = movimiento; }

	// Getters y Setters Auditoría
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
	
	private static final long serialVersionUID = 1L;
}
