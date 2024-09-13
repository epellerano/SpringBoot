package com.sigat.springboot.app.models.entity;

import java.io.Serializable;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
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
	private String dni;	

	// fecha de creacion
	@NotNull
	@Column(name = "create_at")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createAt;
	private String foto;

	// Relacion PROFESIONAL/ESPECIALIDAD ----------------------------------------------------------------------------------------------------------------
	
	/*
	 * @ManyToMany(fetch = FetchType.EAGER)
	 * 
	 * @JoinTable(name = "Vinculacion", joinColumns = @JoinColumn(name =
	 * "idProfesional"), inverseJoinColumns = @JoinColumn(name = "idEspecialidad"))
	 * private List<Especialidad> especialidades;
	 * 
	 * // metodo helper que llene la lista perfiles para poder guardarlos public
	 * void agregar(Especialidad tempEspecialidad) { if (especialidades == null) {
	 * especialidades = new LinkedList<Especialidad>(); } // else
	 * especialidades.add(tempEspecialidad);
	 * 
	 * }
	 */
	//---------------------------------------------------------------------------------------------------------------------------------------------------

	
	// getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	
	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	/*
	 * public List<Especialidad> getEspecialidades() { return especialidades; }
	 * 
	 * public void setEspecialidades(List<Especialidad> especialidades) {
	 * this.especialidades = especialidades; }
	 */

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "Profesional [id=" + id + ", nombre=" + nombre + ", apellido=" + apellido + ", email=" + email
				+ ", telefono=" + telefono + ", codigo=" + codigo + ", matricula=" + matricula + ", dni=" + dni
				+ ", createAt=" + createAt + ", foto=" + foto + "]";
	}
}
