package com.sigat.springboot.app.models.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.Profesional;

public interface IProfesionalDao extends CrudRepository<Profesional, Long>, JpaRepository<Profesional, Long>,
		PagingAndSortingRepository<Profesional, Long> {

	/* PARA AUTOCOMPLETE */
	@Query("select p from Profesional p where p.apellido like %?1%")
	public List<Profesional> findByNombre(String term);

	// public List<Producto> findByNombreLikeIgnoreCase(String term);

	// Obtenemos la imagen del profesional para mostrarrla al seleccionar
	// profesional en Turnos Alta.
	@Query(value = "SELECT p.foto FROM profesionales p WHERE p.id = ?1", nativeQuery = true)
	public String findImagenBase64ById(Long profId);

	// metodo para detectar si Existe el Dni del profesional al insertar.
	@Query("select p from Profesional p where p.dni = ?1")
	Profesional findByProfesionalDni(String profDni);

	// metodo para detectar si Existe el Codigo del profesional al insertar.
	@Query("select p from Profesional p where p.codigo = ?1")
	Profesional findByProfesionalCodigo(String profCodigo);

	// metodo para detectar si Existe la Matricula del profesional al insertar.
	@Query("select p from Profesional p where p.matricula = ?1")
	Profesional findByProfesionalMatricula(String profMatricula);

	// metodo para detectar si Existe el Dni del profesional al actualizar menos
	// este prof_id.
	@Query("select p from Profesional p where p.dni = ?1 and p.id <> ?2")
	Profesional findByProfesionalDniUpdate(String profDni, Long profId);

	// metodo para detectar si Existe el Codigo del profesional al actualizar menos
	// este prof_id.
	@Query("select p from Profesional p where p.codigo = ?1 and p.id <> ?2")
	Profesional findByProfesionalCodigoUpdate(String profCodigo, Long profId);

	// metodo para detectar si Existe la Matricula del profesional al actualizar
	// menos este prof_id.
	@Query("select p from Profesional p where p.matricula = ?1 and p.id <> ?2")
	Profesional findByProfesionalMatriculaUpdate(String profMatricula, Long profId);
	
	//Para el buscador global
	@Query("select p from Profesional p where " +
		       "upper(concat(p.apellido, ' ', p.nombre)) like upper(concat('%', ?1, '%')) or " +
		       "upper(concat(p.nombre, ' ', p.apellido)) like upper(concat('%', ?1, '%')) or " +
		       "upper(p.codigo) like upper(concat('%', ?1, '%')) or " +
		       "p.dni like concat('%', ?1, '%')")
		Page<Profesional> findByNombreOrApellidoOrCodigo(String term, Pageable pageable);
	
	//Historia Clinica
	public Profesional findByCodigo(String codigo);


}
