package com.sigat.springboot.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sigat.springboot.app.models.entity.Paciente;
import com.sigat.springboot.app.models.entity.Profesional;

public interface IPacienteService {

	// listar sin paginacion
	public List<Paciente> findAll();

	// para listar con paginacion
	public Page<Paciente> findAll(Pageable pageable);

	// guardar - editar
	public void save(Paciente paciente);

	// buscar por id
	public Paciente findOne(Long id);

	// borrar
	public void delete(Long id);

	// para autocomplete en Turnos por nombre.
	public List<Paciente> findByNombre(String term);

	// para autocomplete en Turnos por documento y para Registro.html.
	public List<Paciente> findByDni(String term);

	// metodo Existe el Dni del paciente al insertar.
	public Paciente findByPacienteDni(String pacienteDni);

	// metodo Existe el nro Socio del paciente al insertar.
	public Paciente findByPacienteNsocio(String pacienteNsocio);

	// metodo Existe el Dni del paciente al actualizar.
	public Paciente findByPacienteDniUpdate(String pacienteDni, Long pacienteId);

	// metodo Existe el nro Socio del paciente al actualizar.
	public Paciente findByPacienteNsocioUpdate(String pacienteNsocio, Long pacienteId);

}
