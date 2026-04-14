package com.sigat.springboot.app.models.service;

import java.util.List;
import com.sigat.springboot.app.models.entity.Estado;

public interface IEstadoService {

	// listar sin paginacion
	public List<Estado> findAll();
	
	// listar estado solo activo o inactivo
	public List<Estado> findByEstadoActivoInactivo(String term, String term1);
	
	// listar estado solo activo
	public List<Estado> findByEstadoActivo(String term);
}
