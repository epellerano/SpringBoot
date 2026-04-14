package com.sigat.springboot.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.sigat.springboot.app.models.entity.Paciente;
import com.sigat.springboot.app.models.entity.Profesional;

public interface IPacienteDao extends CrudRepository<Paciente, Long>, JpaRepository<Paciente, Long>,
		PagingAndSortingRepository<Paciente, Long> {

	@Query("select p from Paciente p where p.apellido like %?1%")
	public List<Paciente> findByNombre(String term);

	@Query("select p from Paciente p where p.dni like %?1%")
	public List<Paciente> findByDni(String term);

	// metodo para detectar si Existe el Dni del paciente al Insertar.
	@Query("select p from Paciente p where p.dni = ?1")
	Paciente findByPacienteDni(String pacienteDni);

	// metodo para detectar si Existe el Nº de Socio del paciente al Insertar.
	@Query("select p from Paciente p where p.numeroSocio = ?1")
	Paciente findByPacienteNsocio(String pacienteNsocio);

	// metodo para detectar si Existe el Dni del paciente al Actualizar.
	@Query("select p from Paciente p where p.dni = ?1 and p.id <> ?2")
	Paciente findByPacienteDniUpdate(String pacienteDni, Long pacienteId);

	// metodo para detectar si Existe el Nº de Socio del paciente al Actualizar.
	@Query("select p from Paciente p where p.numeroSocio = ?1 and p.id <> ?2")
	Paciente findByPacienteNsocioUpdate(String pacienteNsocio, Long pacienteId);
}
