package com.sigat.springboot.app.models.service;

import java.util.List;
import com.sigat.springboot.app.models.entity.Estado;

public interface IEstadoService {

	// listar sin paginacion
	public List<Estado> findAll();
}
