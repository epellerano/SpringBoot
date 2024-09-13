package com.sigat.springboot.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sigat.springboot.app.models.entity.Paciente;

public interface IPacienteService {

	//listar sin paginacion
	public List<Paciente> findAll();		
	//para listar con paginacion
	public Page<Paciente> findAll(Pageable pageable);
	//guardar - editar
	public void save(Paciente paciente);
	//buscar por id
	public Paciente findOne(Long id);
	//borrar
	public void delete(Long id);
}
