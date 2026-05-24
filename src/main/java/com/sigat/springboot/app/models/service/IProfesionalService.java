package com.sigat.springboot.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.Profesional;

public interface IProfesionalService {

	// para la paginacion
	public Page<Profesional> findAll(Pageable pageable);

	public List<Profesional> findAll();

	public void save(Profesional profesional);

	public Profesional findOne(Long id);
	
	public Page<Profesional> findByNombreOrApellidoOrCodigo(String term, Pageable pageable);

	public void delete(Long id);

	// Obtenemos la imagen del profesional para mostrarrla al seleccionar
	// profesional en Turnos Alta.
	public String obtenerNombreArchivo(Long profId);

	// metodo Existe el Dni del profesional al insertar.
	public Profesional findByProfesionalDni(String profDni);

	// metodo Existe el Codigo del profesional al insertar.
	public Profesional findByProfesionalCodigo(String profCodigo);

	// metodo Existe la Matricula del profesional al insertar.
	public Profesional findByProfesionalMatricula(String profMatricula);

	// metodo Existe el Dni del profesional al actualizar menos este prof_id.
	public Profesional findByProfesionalDniUpdate(String profDni, Long profId);

	// metodo Existe el Codigo del profesional al actualizar menos este prof_id.
	public Profesional findByProfesionalCodigoUpdate(String profCodigo, Long profId);

	// metodo Existe la Matricula del profesional al actualizar menos este prof_id.
	public Profesional findByProfesionalMatriculaUpdate(String profMatricula, Long profId);
	
	//Historia Clinica
	public Profesional findByUsername(String username);

}
