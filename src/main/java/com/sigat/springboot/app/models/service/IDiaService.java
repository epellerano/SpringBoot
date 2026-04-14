package com.sigat.springboot.app.models.service;

import java.util.List;

import com.sigat.springboot.app.models.entity.Dia;

public interface IDiaService {

	// listar sin paginacion
	public List<Dia> findAll();
	
	// listar dias ordenados ascendente.
	public List<Dia> findAllDias();
}
